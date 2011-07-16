package nielinjie
package util.ui



import scalaz._
import Scalaz._


case class Selection[A](var value: Option[A], var index: Option[Int]) {
  def saveToMaster(master: List[A]): List[A] = {
    index.map {
      i =>
        master.updated(i, value.get)
    }.getOrElse(master)
  }

  def selected = !(this.index.isEmpty)
}




