package nielinjie
package util.data

object Params {
  def lookUp[K](key: K) = {
    new LookingUp[K, Option[Any]]({

      x =>
        x.get(key) match {
          case s: Some[Any] => Success(s)
          case None => Success(None)
        //case _ => Failed("type is not matched")

        }
    })
  }


  sealed trait LookUpResult[A] {
    def getOrFail(log2ResultF: String => A): A

    def map[B](f: A => B): LookUpResult[B]

    def flatMap[B](f: A => LookUpResult[B]): LookUpResult[B]

  }

  case class Success[A](result: A) extends LookUpResult[A] {
    def getOrFail(log2ResultF: String => A) = {
      result
    }

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
    def as[C]:LookingUp[K,C]={
      new LookingUp[K,C]({
        m =>
        this.exece(m).flatMap({
            result =>
              Success(result.asInstanceOf[C])
          })
      })
    }
    def to[C](implicit  converter: Converter[A,C]): LookingUp[K, C] = {
      new LookingUp[K, C]({
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
  }

  implicit def lookUpWithRequired[K, A](lookingUp: LookingUp[K, Option[A]]) = new LookingUp[K, Option[A]](lookingUp.exece) {
    def required: LookingUp[K, A] = {
      new LookingUp[K, A]({
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
  }

}
trait Converter[-A,C]{
  def convert(a:A):C
}
object Converters{
  implicit val anyToString=new Converter[Any,String]{
    def convert(a: Any) = a.toString()

  }
}
