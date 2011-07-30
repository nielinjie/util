package nielinjie.util
package data

import org.specs2.mutable.Specification

object HelpersSpecs extends Specification {
  "doto" in {
    import data._
    val i = "what"
    var change: Boolean = false
    i.doto {
      x =>
        change = true
    } must equalTo("what")
    change must beTrue
    i.applyTo {
      x =>
        x + "?"
    } must equalTo("what?")
  }
}
