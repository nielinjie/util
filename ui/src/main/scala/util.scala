package nielinjie
package util.ui


import scala.swing._
import scala.swing.Swing._
import net.miginfocom.layout._
import net.miginfocom.swing._


class MigPanel(layoutc: String, colc: String, rowc: String) extends Panel with LayoutContainer {
  def this(layoutc: String, colc: String) = this (layoutc, colc, null)

  def layoutManager = peer.getLayout.asInstanceOf[MigLayout]

  override lazy val peer = new javax.swing.JPanel(new MigLayout(layoutc, colc, rowc)) with SuperMixin

  type Constraints = String

  protected def constraintsFor(comp: Component) =
    layoutManager.getComponentConstraints(comp.peer).asInstanceOf[String]

  protected def areValid(c: Constraints): (Boolean, String) = (true, "")

  protected def add(c: Component, l: Constraints) {
    peer.add(c.peer, l)
  }
}