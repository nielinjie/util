package none.domain

import scalaz._
import Scalaz._

import java.util.{UUID, Date}

import com.escalatesoft.subcut.inject.{BindingId, Injectable, BindingModule}
import name.nielinjie.common.UUIDSerializer
import name.nielinjie.common.domain.User
import name.nielinjie.common.repository.Repository
import org.joda.time.{DateTime, Interval}
import org.json4s.{Extraction, DefaultFormats}
import org.json4s.JsonAST.JObject
import org.json4s.ext.JodaTimeSerializers


trait Data {
  def id: UUID

  def userId: UUID

  def `type`: String

  def date: Long
}

object Data {
  def extract(j: JObject): Data = {
    implicit val format = DefaultFormats + UUIDSerializer ++ JodaTimeSerializers.all
    j.values.get("type") match {
      case Some("Measure") =>
        Extraction.extract[Measure](j)
      case Some("Action") =>
        Extraction.extract[Action](j)
      case t =>
        throw new IllegalArgumentException("Unknown type -" + t)
    }
  }
  implicit def dt2L(dt:DateTime):Long={
    dt.getMillis
  }
}

case class Measure(id: UUID, userId: UUID, date: Long, data: JObject, `type`: String = "Measure") extends Data

case class Action(id: UUID, userId: UUID, date: Long, data: JObject, `type`: String = "Action") extends Data


class Datas(implicit val bindingModule: BindingModule) extends Injectable {
  implicit val format = DefaultFormats + UUIDSerializer ++ JodaTimeSerializers.all
  val repository = inject[Repository](DatasRepositoryId)

  def set(data: Data)(implicit user: User): Validation[String, Unit] = {
    if (data.userId == user.id) {
      repository.add(Extraction.decompose(data).asInstanceOf[JObject])
      ().success
    } else {
      "only set own data".failure
    }
  }

  def find(userId: UUID, dateRange: Interval, types: List[String] = Nil)(implicit user: User): List[Data] = {
    //{"date": {"$gte": start, "$lt": end}}
    //TODO permissions
    val dateR = "date" -> Map("$gte" -> dateRange.getStart.getMillis, "$lt" -> dateRange.getEnd.getMillis)
    val query: JObject = Extraction.decompose(types match {
      case Nil => Map("userId" -> user.id, dateR)
      case _ => Map("userId" -> user.id, dateR, "type" -> Map("$in" -> types))
    }).asInstanceOf[JObject]
    repository.query(Some(query)).map {
      j: JObject =>
        Data.extract(j)
    }
  }
}

object DatasRepositoryId extends BindingId
