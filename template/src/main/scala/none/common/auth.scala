package none.common

import com.escalatesoft.subcut.inject.{BindingModule, Injectable}
import name.nielinjie.common.baidu.auth.{BaiduSocial, Author}
import name.nielinjie.common.domain.{Users, User}
import unfiltered.request.{Params, HttpRequest}

import scala.collection.mutable


class VendorIdAuthor(implicit val bindingModule: BindingModule) extends Author with Injectable {

  object NonEmptyVendorId extends Params.Extract(
    "vendorId",
    Params.first ~> Params.nonempty
  )

  val usersCache: mutable.Map[String, User] = mutable.Map()
  val users = inject[Users]

  override def auth(req: HttpRequest[_]): Option[User] = {
    req match {
      case Params(NonEmptyVendorId(vendorId)) =>
        usersCache.get(vendorId) match {
          case None => {
            val vendorUser = User(null, "vendorId:" + vendorId, "", "vendorId")
            //RULE use socialId as a general 'external' id.
            val user = users.fromSocialId(vendorUser)
            usersCache += (vendorId -> user)
            Some(user)
          }
          case u => u
        }
      case _ => {
        None
      }
    }
  }
}