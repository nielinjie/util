package name.nielinjie.common.baidu.auth

import scalaz._
import Scalaz._


import dispatch.classic._
import name.nielinjie.common.domain.{Imgs, User}
import nielinjie.util.data.LookUp._
import org.json4s.JsonAST.JValue
import org.json4s._
import org.json4s.native.JsonMethods._
import org.slf4j.LoggerFactory


case class Err(code:String,msg:String)
object BaiduSocial {
  val errorL=for {
    code <- lookUp("error_code").required.as[String]
    msg <- lookUp("error_msg").required.as[String]
  } yield (Err(code,msg))
  val logger = LoggerFactory.getLogger(BaiduSocial.getClass)
  implicit def projectionFunction(jv:JValue, key:String):Option[Any] ={
    jv.asInstanceOf[JObject].values.get(key)
  }
//  def lookup(code:String):Option[(String,User)] ={
//    /*
//    https://openapi.baidu.com/social/oauth/2.0/token?
//	  grant_type=authorization_code&
//	  code=ANXxSNjwQDugOnqeikRMu2bKaXCdlLxn&
//	  client_id=Va5yQRHlA4Fq4eR3LT0vuXV4&
//	  client_secret=0rDSjzQ20XUj5itV7WRtznPQSzr5pVw2&
//	  redirect_uri=http%3A%2F%2Fwww.example.com%2Fsocial_oauth_redirect
//     */
//    val baidu=url("https://openapi.baidu.com/social/oauth/2.0/token") <<? Map(
//      "grant_type" -> "authorization_code",
//      "code" -> code,
//      "client_id" -> "FqfGOgMrlc72Ovc9yYNeUO9i",
//      "client_secret" -> "3t7C3qiiS18ZGKP2oWg2QS28WDG8Fz7z",
//      "redirect_uri" -> "http://lifethread.duapp.com/auth/callback"
//    )
//    val userL=for {
//      name <- lookUp("name").required.as[String]
//      id <- lookUp("social_uid").required.as[String]
//      token <- lookUp("access_token").required.as[String]
//    } yield ((token,User(id,name)))
//    val handle = (baidu) >- {
//      json:String =>
//        val j = parse(json)
//        userL(j) match {
//          case Success(u) => Some(u)
//          case Failure(_) =>
//            errorL(j) match {
//              case Success(e) => logger.error(e.toString)
//              case Failure(_) => logger.error(s"unknown error - ${json}")
//            }
//            None
//        }
//    }
//    new Http().apply(handle)
//  }

  /*

  {
    username: "Jerry",
    sex: "2",
    birthday: "1990-01-09",
    tinyurl: "http://logo.kaixin001.com.cn/logo/8/27/50_152082706_1.jpg",
    headurl: "http://logo.kaixin001.com.cn/logo/8/27/100_152082706_1.jpg",
    mainurl: "http://logo.kaixin001.com.cn/logo/8/27/200_152082706_1.jpg",
    hometown_location: "北京",
    work_history: "百度",
    university_history: "北京大学",
    hs_history: "北京中学",
    province: "北京",
    city: "北京",
    is_verified: "0",
    media_uid: "152082706",
    media_type: "kaixin",
    social_uid: 282335
}
   */
  def getUser(token:String):Option[User]={
    val baidu = url("https://openapi.baidu.com/social/api/2.0/user/info")
    val userL=for {
      name <- lookUp("username").required.as[String]
      id <- lookUp("social_uid").required.as[BigInt]
      media <- lookUp("media_type").required.as[String]
      tiny <- lookUp("tinyurl").required.as[String]
      head <- lookUp("headurl").required.as[String]
      main <- lookUp("mainurl").required.as[String]
    } yield (User(null,id.toString,name,media,Imgs(tiny,head,main)))

    val handle = (baidu <<? Map("access_token"->token)) >- {
      json:String =>
        val j = parse(json)
        userL(j) match {
          case Success(u) => Some(u)
          case Failure(_) =>
            errorL(j) match {
              case Success(e) => logger.error(e.toString)
              case Failure(_) => logger.error(s"unknown error - ${json}")
            }
            None
        }
    }
    new Http().apply(handle)
  }
}