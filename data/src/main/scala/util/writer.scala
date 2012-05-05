package nielinjie
package util.data

class Writer[A](val result: A, val log: List[String]) {
  def map(f: A => A): Writer[A] = {
    new Writer(f(this.result), this.log)
  }
  def flatMap(f: A => Writer[A]): Writer[A] = {
    val newOne = f(this.result)
    new Writer(newOne.result, log ++ newOne.log)
  }
}
object Writer {
  def init[A](a: A) = {
    new Writer(a, List())
  }
}



