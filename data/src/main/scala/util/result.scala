package nielinjie
package util.data


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