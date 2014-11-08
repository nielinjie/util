package name.nielinjie.common.plan

import java.util.UUID

import com.escalatesoft.subcut.inject.{BindingModule, Injectable}
import name.nielinjie.common.UUIDSerializer
import name.nielinjie.common.baidu.auth.UserAware
import name.nielinjie.common.domain.{User, Users}
import org.json4s.{DefaultFormats, Extraction}
import unfiltered.filter.Plan
import unfiltered.filter.Plan._
import unfiltered.request.{GET, Path, Seg}
import unfiltered.response.{Json, NotFound, Ok}


class UserInfoPlan()(implicit val bindingModule: BindingModule) extends Plan with UserAware with Injectable{
  implicit val formats = DefaultFormats + UUIDSerializer
  val users = inject[Users]
  implicit def s2u(s: String): UUID = UUID.fromString(s)


  override def intent: Intent = {
    case r@(GET(Path(Seg("user" ::id:: Nil)))) => {
      users.get(id).map {
        u:User=>
        Ok ~> Json(Extraction.decompose(u))
      } .getOrElse(NotFound)
    }
    case r@(GET(Path(Seg("user" :: Nil)))) => {
      withUser(r){
        user:User=>
          Ok~> Json(Extraction.decompose(user))
      }
    }
  }
}

