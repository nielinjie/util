package nieinjie
package util.ui


case class Bind[A](val push: (Option[A] => Unit), val pull: (A => A))

object Bind {
  def readOnly[A](push:(Option[A]=>Unit))=Bind(push,{a:A => a})

}