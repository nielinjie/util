package nielinjie
package util.ui

import org.specs2.mutable._

object MigSpec extends Specification {
  import Mig._
  val simple = "[][:100:]"
  val oneCol = "[:100:]"
  val more = "[fill,:400:][][][fill,:400:][][][]"
  "miglayout dsl" in {
    fill.asString must equalTo("fill")
    (fill + wrap).asString must equalTo("fill,wrap")
    //implicit val debug=true
    (fill + wrap + Mig.debug).asString must equalTo("fill,wrap,debug")
    prefer(1).asString must equalTo(":1:")
  }
  "dsl sugger" in {
    ((prefer(100))|).asString must equalTo(oneCol)
    (Mig.none | prefer(100)).asString must equalTo(simple)
    (fill(400) ||| fill(400) ||| Mig.none).asString must equalTo(more)
  }
}