package name.nielinjie.common.baidu.auth

import com.escalatesoft.subcut.inject._
import name.nielinjie.common.domain.{User, Users}
import unfiltered.request._
import unfiltered.response._

import scala.collection.mutable


trait Author {
  def auth(req: HttpRequest[_]): Option[User]
}

object NonEmptyUName extends Params.Extract(
  "uname",
  Params.first ~> Params.nonempty
)

object NonEmptyCode extends Params.Extract(
  "code",
  Params.first ~> Params.nonempty
)

object NonEmptyAcToken extends Params.Extract(
  "acToken",
  Params.first ~> Params.nonempty
)


class BaiduAuth(implicit val bindingModule: BindingModule) extends Author with Injectable {
  //TODO How about when token timeout?
  // token -> user
  val usersCache: mutable.Map[String, User] = mutable.Map()
  val users=inject[Users]

  override def auth(req: HttpRequest[_]): Option[User] = {
    req match {
      case Params(NonEmptyAcToken(token)) =>
        usersCache.get(token) match {
          case None =>
            BaiduSocial.getUser(token) match {
              case Some(bu) => {
                val user=users.fromSocialId(bu)
                usersCache += (token -> user)
                Some(user)
              }
              case None => None
            }
          case u => u
        }

      case _ => {
        None
      }
    }
  }
}



trait UserAware extends Injectable {
  val author:Author=inject[Author]
  def withUser[T](req: HttpRequest[_])(body: User => ResponseFunction[T]): ResponseFunction[T]={
    author.auth(req) match {
      case Some(user)=>body(user)
      case None=>body(Users.unknownUser)
    }
  }
  def withKnownUser[T](req: HttpRequest[_])(body: User => ResponseFunction[T]): ResponseFunction[T]={
    author.auth(req) match {
      case Some(user)=>body(user)
      case None=> Unauthorized
    }
  }
}

