package nielinjie
package util.data

import data.Converter
import LookUp._
import scalaz._
import Scalaz._

class SimpleLookingUp[K, A, B, W[_]](exece: LookUpFunction[K, A, B, W]) extends LookingUp[K, A, B, W](exece) {
  def as[C <: B ]: SimpleLookingUp[K, A, C, W] = {
    new SimpleLookingUp[K, A, C, W]({
      m: MapLike[K, A, W] =>
        this.exece(m).flatMap({
          result =>
            success(result.asInstanceOf[C])
        })
    })
  }

  def to[C](implicit converter: Converter[B, C]): SimpleLookingUp[K, A, C, W] = {
    new SimpleLookingUp[K, A, C, W]({
      m: MapLike[K, A, W] =>
        this.exece(m).flatMap({
          result =>
            success(converter(result))
        })
    })
  }

  def ensuring(condition: (B) => Boolean): SimpleLookingUp[K, A, B, W] = ensuring(condition, "ensuring faild")

  def ensuring(condition: (B) => Boolean, message: String): SimpleLookingUp[K, A, B, W] = {
    new SimpleLookingUp[K, A, B, W]({
      m: MapLike[K, A, W] =>
        this.exece(m).flatMap({
          result =>
            if (condition(result)) success(result) else failure(message)
        })
    })
  }
}
