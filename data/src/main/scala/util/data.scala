package nielinjie
package util.data

import java.io.File

package object data {
  type Converter[-A, C] = Function[A, C]
  implicit val any2String = {
    x: Any => x.toString()
  }
  implicit val string2File = {
    x: String => new File(x)
  }
  implicit val string2Int = {
    x: String => x.toInt
  }
  implicit val string2Boolean = {
    x: String => x.toBoolean
  }

}

object Helper {
  class Wrapped[A](val obj: A) {
    def doto(f: A => Unit): A = {
      val re = obj
      f(re)
      re
    }

    def applyTo[B](f: A => B) = {
      f(obj)
    }
  }
  implicit def helpersWrap[A](obj: A) = new Wrapped(obj)
}





