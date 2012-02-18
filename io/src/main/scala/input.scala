package nielinjie
package util.io

import scalax.io._
import JavaConverters.asInputConverter
import JavaConverters.asOutputConverter
import reactive.Var
import reactive.Observing
import java.io.ByteArrayOutputStream

class CountingInput(input: Input) {
  val count = Var(0)
  val asInput = countingInput(input, { count() = _ })
  def countingInput(input: Input, countingFn: Int => Unit): Input = input.bytes.zipWithIndex.map {
    case (byte, index) =>
      countingFn(index+1)
      byte
  }.asInput
}
