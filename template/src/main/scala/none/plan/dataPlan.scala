package none.plan

import com.escalatesoft.subcut.inject.{Injectable, BindingModule}
import name.nielinjie.common.UUIDSerializer
import name.nielinjie.common.baidu.auth.UserAware
import name.nielinjie.common.domain.User
import name.nielinjie.common.plan.{PlanHelper, JsonAware}
import org.joda.time.DateTime
import org.json4s.{JObject, JValue, Extraction, DefaultFormats}
import org.json4s.ext.JodaTimeSerializers
import org.slf4j.LoggerFactory
import unfiltered.filter.Plan
import unfiltered.request.{Seg, GET, POST, Path}
import unfiltered.response._
import none.domain.{Data, Datas}

import scalaz.{Success, Failure}


class DatasPlan(implicit val bindingModule: BindingModule) extends Plan with JsonAware with UserAware with PlanHelper with Injectable {
  implicit val format = DefaultFormats + UUIDSerializer ++ JodaTimeSerializers.all
  val logger = LoggerFactory.getLogger(classOf[DatasPlan])

  val datas: Datas = inject[Datas]

  import com.github.nscala_time.time.Implicits._

  override def intent = {
    case req@(POST(Path(Seg("datas"::Nil)))) => {
      withContentJson(req) {
        c: JValue =>
          withKnownUser(req) {
            implicit u: User =>
              dealAllException {
                datas.set(Data.extract(c.asInstanceOf[JObject])) match {
                  case Success(_)=>Created
                  case Failure(s)=>InternalServerError~>ResponseString(s)
                }
              }
          }
      }
    }
    case req@(GET(Path(Seg("datas" :: "today" :: Nil)))) => {
      withKnownUser(req) {
        implicit u: User =>
          dealAllException {
            val today = (DateTime.now - 12.hours) to (DateTime.now + 1.minute)
            val ds = datas.find(u.id, today)
            Ok ~> Json(Extraction.decompose(ds))
          }
      }
    }
  }


}