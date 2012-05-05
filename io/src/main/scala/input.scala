package nielinjie
package util.io

import scalax.io._
import JavaConverters.asInputConverter
import JavaConverters.asOutputConverter
import reactive.Var
import reactive.Observing
import java.io.ByteArrayOutputStream
import java.io.InputStream
import org.apache.commons.io.input.CountingInputStream
import reactive.EventSource
class CountingInput(input: InputStream) extends Logger{
  val countChange=new EventSource[Long] {}
  val counting=new CountingInputStream(input) {
    override def afterRead(n:Int)={
      super.afterRead(n)
      countChange.fire(super.getByteCount)
    }
  }
  def asInput=counting.asInput
}
