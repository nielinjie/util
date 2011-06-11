package nielinjie
package util.data

import scalaz._

object Params {

  import Scalaz._
  import data._

  def lookUp[K](key: K) = {
    new WrappedLookingUp[K, Any, Any]({
      x =>
        success(x.get(key))
    })
  }
  class LP[V] {
    def apply[K](key:K)=new WrappedLookingUp[K,V, V]({
        x =>
          success(x.get(key))
      })
  }
  def lookUpFor[V] = new LP[V]

  trait MapLike[K, V] {
    def get(key: K): Option[V]
  }

  implicit def map2MapLike[K, A](map: Map[K, A]): MapLike[K, A] = new MapLike[K, A] {
    def get(key: K): Option[A] = map.get(key)
  }


  type LookUpFunction[K, A, B] = (MapLike[K, A] => Validation[String, B])

  class LookingUp[K, A, B](val exece: LookUpFunction[K, A, B]) {
    lu =>
    def map[C](f: B => C): LookingUp[K, A, C] = {
      new LookingUp({
        m: MapLike[K, A] =>
          exece(m).map(f)
      })
    }

    def flatMap[C](f: B => LookingUp[K, A, C]): LookingUp[K, A, C] = {
      new LookingUp({

        m: MapLike[K, A] =>
          val result = lu.exece(m)
          result.fold(failure(_), f(_).exece(m))
      })
    }

    def apply(map: MapLike[K, A]) = {
      exece(map)
    }

  }

  class SimpleLookingUp[K, A, B](exece: LookUpFunction[K, A, B]) extends LookingUp[K, A, B](exece) {
    def as[C <: B]: SimpleLookingUp[K, A, C] = {
      new SimpleLookingUp[K, A, C]({
        m: MapLike[K, A] =>
          this.exece(m).flatMap({
            result =>
              success(result.asInstanceOf[C])
          })
      })
    }

    def to[C](implicit converter: Converter[B, C]): SimpleLookingUp[K, A, C] = {
      new SimpleLookingUp[K, A, C]({
        m: MapLike[K, A] =>
          this.exece(m).flatMap({
            result =>
              result match {
                case a: B => success(converter(a))
                case _ => failure("type mismatch")
              }
          })
      })
    }

    def ensuring(condition: (A) => Boolean): SimpleLookingUp[K, A, A] = ensuring(condition, "ensuring faild")

    def ensuring(condition: (A) => Boolean, message: String): SimpleLookingUp[K, A, A] = {
      new SimpleLookingUp[K, A, A]({
        m: MapLike[K, A] =>
          this.exece(m).flatMap({
            result =>
              result match {
                case a: A => {
                  if (condition(a)) success(a) else failure(message)
                }
                case _ => failure("type mismatch")
              }
          })
      })
    }
  }

  class WrappedLookingUp[K, A, B](exece: LookUpFunction[K, A, Option[B]]) extends LookingUp[K, A, Option[B]](exece) {
    wlu =>
    def as[C <: A]: WrappedLookingUp[K, A, C] = {
      new WrappedLookingUp[K, A, C]({
        m: MapLike[K, A] =>
          wlu.exece(m).flatMap({
            result =>
              success(
                result.map {
                  r => r.asInstanceOf[C]
                })
          })
      })
    }


    def to[C](implicit converter: Converter[B, C]): WrappedLookingUp[K, A, C] = {
      new WrappedLookingUp[K, A, C]({
        m: MapLike[K, A] =>
          wlu.exece(m).flatMap({
            result =>
              result match {
                case a: Option[B] => success(result.map {
                  r => converter(r)
                })
                case _ => failure("type mismatch")
              }
          })
      })
    }


    def required: SimpleLookingUp[K, A, B] = {
      new SimpleLookingUp[K, A, B]({
        m: MapLike[K, A] =>
          wlu.exece(m).flatMap({
            result =>
              result match {
                case Some(a) => success(a)
                case None => failure("required, but not found")
              }
          })
      })
    }

    def default(d: B): SimpleLookingUp[K, A, B] = {
      new SimpleLookingUp[K, A, B]({
        m: MapLike[K, A] =>
          wlu.exece(m).flatMap({
            result =>
              result match {
                case Some(a) => success(a)
                case None => success(d)
              }
          })
      })
    }

    def ensuring(condition: (B) => Boolean): WrappedLookingUp[K, A,B] = ensuring(condition, "ensuring faild")

    def ensuring(condition: (B) => Boolean, message: String): WrappedLookingUp[K, A, B] = {
      new WrappedLookingUp[K, A, B]({
        m: MapLike[K, A] =>
          wlu.exece(m).flatMap({
            result =>
              result match {
                case Some(a: B) => {
                  if (condition(a)) success(Some(a)) else failure(message)
                }
                case None => failure("None")
              }
          })
      })
    }
  }

}


