package nielinjie
package util.data

import org.specs2.mutable._
import scalaz._

object RSepcs extends Specification {

  import Scalaz._
  import R._

  "slk" in {
    val map = Map("a" -> 1)
    val look = slk[Int]({
      map => (map.getOrElse("a", 2)).success
    })
    look(map) must be equalTo((1).success)
  }
}