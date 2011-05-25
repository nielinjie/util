package nielinjie
package util.data

import scalaz._

object Params {

  import Scalaz._

  def lookUp[K](key: K) = {
    new WrappedLookingUp[K, Any]({
      x =>
        success(x.get(key))
    })
  }




  trait MapKind[K] {
    def get(key: K): Option[Any]
  }

  implicit def map2Kind[K](map: Map[K, Any]): MapKind[K] = new MapKind[K] {
    def get(key: K): Option[Any] = map.get(key)
  }


  type LookUpFunction[K, A] = (MapKind[K] => Validation[String,A])

  class LookingUp[K, A](val exece: LookUpFunction[K, A]) {
    import scalaz.Functor._
    def map[B](f: A => B): LookingUp[K, B] = {
      new LookingUp({
        m =>
          implicitly
           exece(m).map(f)
      })
    }

    def flatMap[B](f: A => LookingUp[K, B]): LookingUp[K, B] = {
      new LookingUp({

        m =>
          val result = exece(m)
          result.fold( failure(_),f(_).exece(m))
//          result match {
//            case Success(s) =>
//              f(s).exece(m)
//            case Failed(log) => Failed[B](log)
//          }
      })
    }

    def apply(map: MapKind[K]) = {
      exece(map)
    }

  }

  class SimpleLookingUp[K, A](exece: LookUpFunction[K, A]) extends LookingUp[K, A](exece) {
    def as[C]: SimpleLookingUp[K, C] = {
      new SimpleLookingUp[K, C]({
        m =>
          this.exece(m).flatMap({
            result =>
              success(result.asInstanceOf[C])
          })
      })
    }

    def to[C](implicit converter: Converter[A, C]): SimpleLookingUp[K, C] = {
      new SimpleLookingUp[K, C]({
        m =>
          this.exece(m).flatMap({
            result =>
              result match {
                case a: A => success(converter.convert(a))
                case _ => failure("type mismatch")
              }
          })
      })
    }

    def ensuring(condition: (A) => Boolean): SimpleLookingUp[K, A] = ensuring(condition, "ensuring faild")

    def ensuring(condition: (A) => Boolean, message: String): SimpleLookingUp[K, A] = {
      new SimpleLookingUp[K, A]({
        m =>
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

  class WrappedLookingUp[K, A](exece: LookUpFunction[K, Option[A]]) extends LookingUp[K, Option[A]](exece) {
    def as[C]: WrappedLookingUp[K, C] = {
      new WrappedLookingUp[K, C]({
        m =>
          this.exece(m).flatMap({
            result =>
              success(
                result.map {
                  r => r.asInstanceOf[C]
                })
          })
      })
    }


    def to[C](implicit converter: Converter[A, C]): WrappedLookingUp[K, C] = {
      new WrappedLookingUp[K, C]({
        m =>
          this.exece(m).flatMap({
            result =>
              result match {
                case a: Option[A] => success(result.map {
                  r => converter.convert(r)
                })
                case _ => failure("type mismatch")
              }
          })
      })
    }


    def required: SimpleLookingUp[K, A] = {
      new SimpleLookingUp[K, A]({
        m =>
          this.exece(m).flatMap({
            result =>
              result match {
                case Some(a) => success(a)
                case None => failure("required, but not found")
              }
          })
      })
    }

    def default(d: A): SimpleLookingUp[K, A] = {
      new SimpleLookingUp[K, A]({
        m =>
          this.exece(m).flatMap({
            result =>
              result match {
                case Some(a) => success(a)
                case None => success(d)
              }
          })
      })
    }

    def ensuring(condition: (A) => Boolean): WrappedLookingUp[K, A] = ensuring(condition, "ensuring faild")

    def ensuring(condition: (A) => Boolean, message: String): WrappedLookingUp[K, A] = {
      new WrappedLookingUp[K, A]({
        m =>
          this.exece(m).flatMap({
            result =>
              result match {
                case Some(a: A) => {
                  if (condition(a)) success(Some(a)) else failure(message)
                }
                case None => failure("None")
              }
          })
      })
    }
  }

}


