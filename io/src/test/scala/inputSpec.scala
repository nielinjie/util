package nielinjie
package util.io

import org.specs2.mutable._
import scalax.io._
import JavaConverters.asInputConverter
import JavaConverters.asOutputConverter
import java.io.ByteArrayOutputStream
import reactive.Observing

object InputSpec extends Specification with Observing{
  "counting input" in {
    val in = List[Byte](1, 2, 3, 4, 5, 6)
    val countIn = new CountingInput(in.asInput)
    val array = new ByteArrayOutputStream
    var counting = List[Int]()
    countIn.count.change.foreach(x => counting =  counting :+ x)

    countIn.asInput.copyDataTo(array.asOutput)
    "counting cool" in {
      counting must equalTo(0 to 5)
    }
    "copy work" in {
      array.toByteArray.toList must equalTo(in)
    }
  }
}