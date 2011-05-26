package nielinjie
package util.data

import scalaz._

object R {
  import Scalaz._
  //type SLK[A] = ReaderT[PartialApply1Of2[Validation, String]#Apply,Map[String,A],A]
  def slk[A](f:(Map[String,A])=>Validation[String,A]) =
    kleisli[PartialApply1Of2[Validation, String]#Apply,Map[String,A],A](f)
  def oslk[A](f:(Map[String,A])=>Validation[String,Option[A]]) =
    kleisli[PartialApply1Of2[Validation, String]#Apply,Map[String,A],Option[A]](f)
  def lookup(key:String)=oslk({map:Map[String,_] => map.get(key).success})
  //def required = slk()
}
