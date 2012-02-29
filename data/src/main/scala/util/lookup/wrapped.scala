package nielinjie
package util.data

import data.Converter
import LookUp._
import scalaz._
import Scalaz._
//TODO WrappedLookingUp[K,A,B,W] 
class WrappedLookingUp[K, A, B, W[_]](exece: LookUpFunction[K, A, W[B], W])(implicit val f: Functor[W]) extends LookingUp[K, A, W[B], W](exece) {
  wlu =>
  def as[C <: B]: WrappedLookingUp[K, A, C, W] = {
    new WrappedLookingUp[K, A, C, W]({
      m: MapLike[K, A, W] =>
        wlu.exece(m).flatMap({
          result =>
            success(
              result.map {
                r => r.asInstanceOf[C]
              })
        })
    })
  }

  def to[C](implicit converter: Converter[B, C]): WrappedLookingUp[K, A, C, W] = {
    new WrappedLookingUp[K, A, C, W]({
      m: MapLike[K, A, W] =>
        wlu.exece(m).flatMap({
          result =>
            success(result.map {
              r => converter(r)
            })
        })
    })
  }
  


    def required(implicit fa:Foldable[W]): SimpleLookingUp[K, A, B, W] = {
      new SimpleLookingUp[K, A, B, W]({
        m: MapLike[K, A, W] =>
          wlu.exece(m).flatMap({
            result =>
              headO(result)(fa) match{
                case Some(a) => success(a)
                case None => failure("required, but not found")
              }
          })
      })
    }

    def default(d: B)(implicit fa:Foldable[W]): SimpleLookingUp[K, A, B, W] = {
      new SimpleLookingUp[K, A, B, W]({
        m: MapLike[K, A, W] =>
          wlu.exece(m).flatMap({
            result =>
              headO(result)(fa) match{
                case Some(a) => success(a)
                case None => success(d)
              }
          })
      })
    }

  def ensuring(condition: (B) => Boolean)(implicit fa:Foldable[W]): WrappedLookingUp[K, A, B, W] = ensuring(condition, "ensuring faild")(fa)

  def ensuring(condition: (B) => Boolean, message: String)(implicit fa:Foldable[W]): WrappedLookingUp[K, A, B, W] = {
    new WrappedLookingUp[K, A, B, W]({
      m: MapLike[K, A, W] =>
        wlu.exece(m).flatMap({
          result =>
            if (result.all(condition)) success(result) else failure(message)
          //            result match {
          //              case Some(a: B) => {
          //                if (condition(a)) success(Some(a)) else failure(message)
          //              }
          //              case None => failure("None")
          //            }
        })
    })(f)
  }
}
