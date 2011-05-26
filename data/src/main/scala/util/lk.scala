package nielinjie
package util.data

import scalaz._

object R {
  import Scalaz._
  type SLK[A] = ReaderT[PartialApply1Of2[Validation, String]#Apply,Map[String,A],A]
  def slk[A](f:(Map[String,A])=>Validation[String,A]) = ReaderT[PartialApply1Of2[Validation, String]#Apply,Map[String,A],A](f)

}
