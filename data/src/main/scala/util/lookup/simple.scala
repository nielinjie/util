package nielinjie
package util.data

import data.Converter
import LookUp._
import scalaz._
import Validation._
import scalaz.std.either._
import scala.util.control.Exception._


//Use ID(ID[A]==A) here?
class SimpleLookingUp[K, A, B, W[_]](exece: LookUpFunction[K, A, B, W]) extends LookingUp[K, A, B, W](exece) {
  
   def fmap[C](f: B => C): SimpleLookingUp[K, A, C, W] = {
      new SimpleLookingUp({
        m: MapLike[K, A, W] =>
          exece(m).map(f)
      })
    }
  
  def as[C <: B ]: SimpleLookingUp[K, A, C, W] = {
    new SimpleLookingUp[K, A, C, W]({
      m: MapLike[K, A, W] =>
        this.exece(m).flatMap({
          result =>
            fromTryCatch(result.asInstanceOf[C]).leftMap(_.getMessage)
        })
    })
  }

  def to[C](implicit converter: Converter[B, C]): SimpleLookingUp[K, A, C, W] = {
    new SimpleLookingUp[K, A, C, W]({
      m: MapLike[K, A, W] =>
        this.exece(m).flatMap({
          result =>
            fromTryCatch(converter(result)).leftMap(_.getMessage)
        })
    })
  }

  def ensuring(condition: (B) => Boolean): SimpleLookingUp[K, A, B, W] = ensuring(condition, "ensuring faild")

  def ensuring(condition: (B) => Boolean, message: String): SimpleLookingUp[K, A, B, W] = {
    new SimpleLookingUp[K, A, B, W]({
      m: MapLike[K, A, W] =>
        this.exece(m).flatMap({
          result =>
            fromTryCatch(condition(result)) match {
              case Success(true) =>success(result)
              case Success(false) => failure(message)
              case Failure(e) => failure(e.getMessage)
            }
        })
    })
  }
}
