package none.domain

import com.github.athieriot.{CleanAfterExample, EmbedConnection}
import com.mongodb.{ServerAddress, MongoClient}
import dispatch.classic.{Request, Http}
import name.nielinjie.common.UUIDSerializer
import name.nielinjie.common.domain.User
import name.nielinjie.util.test.MongoAndUnfiltered
import org.joda.time.DateTime
import org.json4s.JsonAST.JObject
import org.json4s.ext.JodaTimeSerializers
import org.json4s.{DefaultFormats, Extraction}
import org.json4s.native.Serialization
import org.specs2.mutable.Specification
import unfiltered.jetty.{Server}
import unfiltered.specs2.jetty.Served


import scala.collection.JavaConversions._


class PlansSpec extends Specification with PlanConfig with MongoAndUnfiltered {
  implicit val format = DefaultFormats + UUIDSerializer ++ JodaTimeSerializers.all

  override def setup: (Server) => Server = _.plan(allPlan)

  val http = new Http

  sequential

  def status(request: Request): Int = {
    http x (request as_str) {
      case (code, _, _, _) => code
    }
  }

  def body(request: Request): String = {
    http x (request as_str)
  }

  def json(data: AnyRef, request: Request): Request = {
    val string = Serialization.write(data)
    request << string <:< Map("Content-Type" -> "application/json;charset=UTF-8")
  }

  def withVendorId(request: Request): Request = {
    request <<? Map("vendorId" -> "godId")
  }

  "with user" should {
    "no vendorId" in {
      status(host / "datas/today") must_== 401
    }
    "vendorId" in {
      status(withVendorId(host / "datas/today")) must_== 200
    }
    "user" in {
      val user = Serialization.read[User](body(withVendorId(host / "user")))
      user.socialId must beEqualTo("vendorId:godId")
    }
  }
  "datas" should {
    "set" in {
      val user = Serialization.read[User](body(withVendorId(host / "user")))
      val data = Extraction.decompose(Map("type" -> "location", "xxx" -> "yyy")).asInstanceOf[JObject]
      val set = json(Measure(null, user.id, DateTime.now.getMillis, data), withVendorId(host / "datas"))
      status(set) must beEqualTo(201)
      val datas: Datas = config.inject[Datas](None)
      datas.repository.query(None) must have size (1)
    }
    "set and get" in {
      val user = Serialization.read[User](body(withVendorId(host / "user")))
      val data = Extraction.decompose(Map("type" -> "location", "xxx" -> "yyy")).asInstanceOf[JObject]
      val set = json(Measure(null, user.id, DateTime.now.getMillis, data), withVendorId(host / "datas"))
      val get = withVendorId(host / "datas/today")
      status(set) must beEqualTo(201)
      val mes: List[Measure] = Serialization.read[List[Measure]](body(get))
      mes must like {
        case x :: Nil => x must not beNull
      }

    }


  }


}