package nielinjie
package util.data

import java.io.File

package object data {
  type Converter[-A, C] = Function[A, C]
  implicit val any2String = {x : Any => x.toString()}
  implicit val string2File = {x : String => new File(x)}
  implicit val string2Int = {x:String => x.toInt}
  implicit val string2Boolean = {x: String => x.toBoolean}
}
