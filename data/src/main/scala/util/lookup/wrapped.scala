package nielinjie
package util.data

import data.Converter
import LookUp._
import scalaz._
import Validation._
//TODO WrappedLookingUp[K,A,B,W]
class WrappedLookingUp[K, A, B, W[_]](exece: LookUpFunction[K, A, W[B], W])(implicit val f: Functor[W]) extends LookingUp[K, A, W[B], W](exece) {
  wlu =>

  def fmap[C](fu: B => C): WrappedLookingUp[K, A, C, W] = {
      new WrappedLookingUp({
        m: MapLike[K, A, W] =>
          exece(m).map(f.map(_)(fu))
      })(f)
    }

  def as[C <: B]: WrappedLookingUp[K, A, C, W] = {
    new WrappedLookingUp[K, A, C, W]({
      m: MapLike[K, A, W] =>
        wlu.exece(m).flatMap({
          result =>
            success(
              f.map (result){
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
            success(f.map(result) {
              r =>
                converter(r)
            })
        })
    })
  }

  def required(implicit fa: Foldable[W]): SimpleLookingUp[K, A, B, W] = {
    new SimpleLookingUp[K, A, B, W]({
      m: MapLike[K, A, W] =>
        wlu.exece(m).flatMap({
          result =>
            headO(result)(fa) match {
              case Some(a) => success(a)
              case None => failure("required, but not found")
            }
        })
    })
  }

  def default(d: B)(implicit fa: Foldable[W]): SimpleLookingUp[K, A, B, W] = {
    new SimpleLookingUp[K, A, B, W]({
      m: MapLike[K, A, W] =>
        wlu.exece(m).flatMap({
          result =>
            headO(result)(fa) match {
              case Some(a) => success(a)
              case None => success(d)
            }
        })
    })
  }

  def ensuring(condition: (B) => Boolean)(implicit fa: Foldable[W]): WrappedLookingUp[K, A, B, W] = ensuring(condition, "ensuring faild")(fa)

  def ensuring(condition: (B) => Boolean, message: String)(implicit fa: Foldable[W]): WrappedLookingUp[K, A, B, W] = {
    new WrappedLookingUp[K, A, B, W]({
      m: MapLike[K, A, W] =>
        wlu.exece(m).flatMap({
          result =>
            if (fa.all(result)(condition)) success(result) else failure(message)
        })
    })(f)
  }
}
