package none.domain

import java.util.Date

import com.github.athieriot.{CleanAfterExample, EmbedConnection}
import org.joda.time.{Period, DateTime}
import org.json4s.JsonAST.JObject
import org.specs2.mutable.Specification


class DataSpec extends Specification with EmbedConnection with CleanAfterExample with UsersConfig{
  import com.github.nscala_time.time.Implicits._
  import Data._
  sequential
  "data" should {
    "can set" in {
      val data = Measure(null, a.id, DateTime.now, JObject())
      implicit val user=a
      datas.set(data)
      success
    }
    "can find" in {
      val data = Measure(null, a.id, DateTime.now, JObject())
      implicit val user=a
      datas.set(data)
      val fromT:DateTime = DateTime.now - Period.hours(12)
      val toT:DateTime = DateTime.now + Period.hours(12)
      val re=datas.find(a.id,(fromT to toT) )
      re must like{
        case x::Nil => x must equalTo(data.copy(id=x.id))
      }
    }
    "don find too much" in {
      val data = Measure(null, a.id, DateTime.now, JObject())
      val data2 = Measure(null, a.id, (DateTime.now- Period.days(2)), JObject())
      val data3 = Measure(null, b.id, DateTime.now, JObject())
      implicit val user=a
      datas.set(data)
      datas.set(data2)
      datas.set(data3)
      val fromT:DateTime = DateTime.now - Period.hours(12)
      val toT:DateTime = DateTime.now + Period.hours(12)
      val re=datas.find(a.id,(fromT to toT) )
      re must like{
        case x::Nil => x must equalTo(data.copy(id=x.id))
      }
    }
    "not find too less" in {
      val data = Measure(null, a.id, DateTime.now, JObject())
      val data2 = Measure(null, a.id, (DateTime.now- Period.hours(2)), JObject())
      val data3 = Measure(null, b.id, DateTime.now, JObject())
      implicit val user=a
      datas.set(data)
      datas.set(data2)
      datas.set(data3)
      val fromT:DateTime = DateTime.now - Period.hours(12)
      val toT:DateTime = DateTime.now + Period.hours(12)
      val re=datas.find(a.id,(fromT to toT) )
      re must like{
        case x::y::Nil => {
          x must equalTo(data.copy(id=x.id))
          y must equalTo(data2.copy(id=y.id))
        }
      }
    }
    "actions is data too" in {
      val data = Measure(null, a.id, DateTime.now, JObject())
      val data2 = Action(null, a.id, (DateTime.now- Period.hours(2)), JObject())
      val data3 = Measure(null, b.id, DateTime.now, JObject())
      implicit val user=a
      datas.set(data)
      datas.set(data2)
      datas.set(data3)
      val fromT:DateTime = DateTime.now - Period.hours(12)
      val toT:DateTime = DateTime.now + Period.hours(12)
      val re=datas.find(a.id,(fromT to toT) )
      re must like{
        case x::y::Nil => {
          x must equalTo(data.copy(id=x.id))
          y must equalTo(data2.copy(id=y.id))
        }
      }
    }
  }
}
