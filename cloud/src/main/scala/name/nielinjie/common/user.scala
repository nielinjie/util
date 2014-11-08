package name.nielinjie.common
package domain

import java.util.UUID

import com.escalatesoft.subcut.inject.{BindingId, BindingModule, Injectable}
import name.nielinjie.common.repository.Repository
import org.json4s.JsonAST.JObject
import org.json4s.JsonDSL._
import org.json4s.{DefaultFormats, Extraction}


object UsersRepositoryId extends BindingId

case class User(id: UUID, socialId:String, name: String, media: String = "unknown", imgs: Imgs = List())

case class Imgs(tiny: String, head: String, main: String)

object Imgs {
  implicit def list(l: List[String]): Imgs = {
    l match {
      case Nil => Imgs(null, null, null)
      case x :: Nil => Imgs(x, null, null)
      case x :: x2 :: Nil => Imgs(x, x2, null)
      case x :: x2 :: x3 :: _ => Imgs(x, x2, x3)
    }
  }
}


class Users(implicit val bindingModule: BindingModule) extends Injectable{
  implicit val format = DefaultFormats + UUIDSerializer


  val repository = inject[Repository](UsersRepositoryId)


  def fromSocialId(user:User):User ={
    val obj:JObject=("socialId"->user.socialId)
    repository.query(Some(obj)) match {
      case Nil =>{
        val u=user.copy(id=UUID.randomUUID())
        repository.update(u.id,Extraction.decompose(u).asInstanceOf[JObject])
        u
      }
      case x::_ => {
        Extraction.extract[User](x)
      }
    }
  }

  def get(id:UUID):Option[User]={
    repository.get(id).map{
      Extraction.extract[User](_)
    }
  }

}
object Users{

  implicit def s2u(s: String): UUID = UUID.fromString(s)
  val unknownUser = User("c04036ab-993f-465b-bede-ed03b99d477e","0","UNKNOWN")
  val god=User("c8fed344-cd3e-4421-b846-03b307169029","0","SYSTEM")
}