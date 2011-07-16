package nielinjie
package util.ui

import scalaz._
import Scalaz._

case class Bind[A](val push: (Validation[String, A] => Unit), val pull: (A => A)){
  def andThen(b:Bind[A])={
    Bind({va:Validation[String,A]=>this.push(va);b.push(va)},pull.andThen(b.pull)) //TODO pull is not support.
  }
}

object Bind {
  def apply[A](push: (Validation[String, A] => Unit)): Bind[A] = readOnly(push)


  //def readOnlyNoErrorHandle[A](push: A => Unit): Bind[A] = readOnly(noErrorHandle(push))

  //def noErrorHandle[A](push: A => Unit, pull: (A => A)): Bind[A] = Bind(noErrorHandle(push), pull)

  def readOnly[A](push: (Validation[String, A] => Unit)) = Bind(push, {
    a: A => a
  })


  def noErrorHandle[A](push: (A => Unit)): (Validation[String, A] => Unit) = {
    v: Validation[String, A] =>
      v.fold({
        e =>
      }, {
        a => push(a)
      })
  }

  def errorHandleAsOption[A](push: (Option[A] => Unit)): (Validation[String, A] => Unit) = {
    v: Validation[String, A] =>
      v.fold({
        e => push(None)
      }, {
        a => push(Some(a))
      })
  }
}