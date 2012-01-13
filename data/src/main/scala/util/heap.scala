package nielinjie
package util.data

import scala.concurrent.stm._
import reactive._


import scalaz._
import Scalaz._

class Heap[T](obj: T) {
  import Helper._
  val ref = Ref(obj)
  def findAndTransform[S](findCond: S => Boolean, transformFun: S => S)(implicit setter: Setter[T, S], descriptor: Descriptor[S]): Unit = {
    val transF = {
      s: S =>
        transformFun(s).doto {
          case s => changeEvents.fire("changed - " + descriptor.descript(s))
        }
    }
    atomic {
      implicit txn =>
        ref.transform({
          heap =>
            setter.setit(heap, findCond, transF)
        })
    }

  }
  def transform(f: T => T)(implicit descriptor: Descriptor[T]) = {
    val transF = {
      s: T =>
        f(s).doto {
          case s => changeEvents.fire("changed - " + descriptor.descript(s))
        }
    }
    ref.single.transform(transF)
  }
  def get: T = {
    ref.single.get
  }
  val changeEvents = new EventSource[String]() {}

  case class ChangedEvent(obj: String)
}
//TODO Setter can be replaced by lens.
trait Setter[T, S] {
  def setit(heapObj: T, findCond: S => Boolean, transFun: S => S): T
}


trait Descriptor[O] {
  def descript(obj: O): String
}
