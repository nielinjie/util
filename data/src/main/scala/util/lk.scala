package nielinjie
package util.data

import scalaz._

object R {
  import Scalaz._
  //type SLK[A] = ReaderT[PartialApply1Of2[Validation, String]#Apply,Map[String,A],A]
//  def kle[A,B](f:A => Validation[String,B])=
//    kleisli[PartialApply1Of2[Validation, String]#Apply,A,B](f)
//  def slk[A](f:(Map[String,A])=>Validation[String,A]) =
//    kleisli[PartialApply1Of2[Validation, String]#Apply,Map[String,A],A](f)
//  def oslk[A](f:(Map[String,A])=>Validation[String,Option[A]]) =
//    kleisli[PartialApply1Of2[Validation, String]#Apply,Map[String,A],Option[A]](f)
  def lookup(key:String)= kleisli[PartialApply1Of2[Validation, String]#Apply,Map[String,Int],Option[Int]]({map:Map[String,Int] => map.get(key).success})
  def required() =  kleisli[PartialApply1Of2[Validation, String]#Apply,Option[Int],Int]({a:Option[Int] => a.toSuccess("required")})
  //def required = slk()
}
