package nielinjie
package util.data

import java.io.File

trait Converter[-A, C] {
  def convert(a: A): C
}

object Converters {
  implicit val anyToString = new Converter[Any, String] {
    def convert(a: Any) = a.toString()

  }
  implicit val stringToFile = new Converter[String, File] {
    def convert(a: String) = new File(a)
  }
  //TODO How to reuse converters in Predef?
  implicit val stringToInt = new Converter[String, Int] {
    def convert(a: String) = a.toInt
  }
  implicit val stringToBoolean = new Converter[String, Boolean] {
    def convert(a: String) = a.toBoolean
  }
}