package nielinjie
package util.ui

import scala.swing._
import scala.swing.Swing._
import net.miginfocom.layout._
import net.miginfocom.swing._

import scalaz._
import Scalaz._

class MigPanel(layoutc: String, colc: String, rowc: String) extends Panel with LayoutContainer {
  def this(layoutc: String, colc: String) = this(layoutc, colc, null)

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

object Mig {
  def fill = new SimpleConstraint("fill")
  def fill(int: Int): Constraint = fill + prefer(int)
  def wrap = new SimpleConstraint("wrap")
  def debug = new SimpleConstraint("debug")
  def none = new SimpleConstraint("")
  def prefer(size: Int) = Sizes(None, size.some, None)

  case class Sizes(min: Option[Int], prefer: Option[Int], max: Option[Int]) extends Constraint {
    def asString = List(min, prefer, max).map {
      size =>
        size.map {
          i =>
            i.toString
        }.getOrElse("")
    }.mkString(":")
  }

  class SimpleConstraint(val string: String) extends Constraint {
    def asString = string
  }

  implicit def cons2String(constraint: Constraint) = constraint.asString

  trait Constraint {
    def asString: String
    def +(b: Constraint) = {
      val a = this
      new Constraint {
        def asString = a.asString + "," + b.asString
      }
    }
  }
  //suggers
  class ConstraintList(val cons: List[Constraint]) {
    def | = this
    def |(con: Constraint) = new ConstraintList(cons :+ con)
    def ||(con: Constraint) = this | none | con
    def |||(con: Constraint) = this || none | con
    def ||||(con: Constraint) = this ||| none | con
    def asString = cons.map {
      con =>
        "[%s]".format(con.asString)
    }.mkString
  }
  implicit def con2conList(con: Constraint) = new ConstraintList(List(con))
  implicit def cons2String(constraintl: ConstraintList) = constraintl.asString
}

