package nielinjie
package util.data

import java.io.File

object Params {
  def lookUp[K](key: K) = {
    new WrappedLookingUp[K, Any]({
      x =>
        Success(x.get(key))
    })
  }


  sealed trait LookUpResult[A] {
    def getOrFail(log2ResultF: String => A): A

    def getOrFail(default: => A): A

    def toOption: Option[A]

    def map[B](f: A => B): LookUpResult[B]

    def flatMap[B](f: A => LookUpResult[B]): LookUpResult[B]

  }

  case class Success[A](result: A) extends LookUpResult[A] {
    def getOrFail(log2ResultF: String => A) = {
      result
    }

    def toOption = Some(result)

    def getOrFail(default: => A): A = result

    def map[B](f: A => B): LookUpResult[B] = {
      Success(f(this.result))
    }

    def flatMap[B](f: A => LookUpResult[B]): LookUpResult[B] = {

      f(this.result) match {
        case Success(r) => Success(r)
        case f: Failed[_] => f

      }
    }

  }

  case class Failed[A](log: String) extends LookUpResult[A] {
    def getOrFail(log2ResultF: String => A) = {
      log2ResultF(log)
    }

    def toOption = Option.empty[A]

    def getOrFail(default: => A): A = default

    def map[B](f: A => B): LookUpResult[B] = Failed[B](this.log)

    def flatMap[B](f: A => LookUpResult[B]): LookUpResult[B] = {
      Failed[B](this.log)
    }
  }

  trait MapKind[K] {
    def get(key: K): Option[Any]
  }

  implicit def map2Kind[K](map: Map[K, Any]): MapKind[K] = new MapKind[K] {
    def get(key: K): Option[Any] = map.get(key)
  }


  type LookUpFunction[K, A] = (MapKind[K] => LookUpResult[A])

  class LookingUp[K, A](val exece: LookUpFunction[K, A]) {
    def map[B](f: A => B): LookingUp[K, B] = {
      new LookingUp({

        m =>
          exece(m).map(f)
      })
    }

    def flatMap[B](f: A => LookingUp[K, B]): LookingUp[K, B] = {
      new LookingUp({

        m =>
          val result = exece(m)
          result match {
            case Success(s) =>
              f(s).exece(m)
            case Failed(log) => Failed[B](log)
          }
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
              Success(result.asInstanceOf[C])
          })
      })
    }

    def to[C](implicit converter: Converter[A, C]): SimpleLookingUp[K, C] = {
      new SimpleLookingUp[K, C]({
        m =>
          this.exece(m).flatMap({
            result =>
              result match {
                case a: A => Success(converter.convert(a))
                case _ => Failed("type mismatch")
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
                  if (condition(a)) Success(a) else Failed(message)
                }
                case _ => Failed("type mismatch")
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
              Success(
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
                case a: Option[A] => Success(result.map {
                  r => converter.convert(r)
                })
                case _ => Failed("type mismatch")
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
                case Some(a) => Success(a)
                case None => Failed("required, but not found")
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
                case Some(a) => Success(a)
                case None => Success(d)
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
                  if (condition(a)) Success(Some(a)) else Failed(message)
                }
                case None => Failed("None")
              }
          })
      })
    }
  }

}

trait Converter[-A, C] {
  def convert(a: A): C
}

object Converters {
  implicit val anyToString = new Converter[Any, String] {
    def convert(a: Any) = a.toString()

  }
  implicit val stringToFile = new Converter[String, File] {
    def convert(a: String) = new File(a)
  }
  //TODO How to reuse converters in Predef?
  implicit val stringToInt = new Converter[String, Int] {
    def convert(a: String) = a.toInt
  }
  implicit val stringToBoolean = new Converter[String, Boolean] {
    def convert(a: String) = a.toBoolean
  }
}
