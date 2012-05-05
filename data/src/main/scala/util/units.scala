package nielinjie
package util.data

object Units {
  implicit def toBytes(long: Long) = {
    new {
      def asBytes(): (Long, ByteUnit) = {
        val units = List(G, M, K)
        units.find(long >= _.value).map {
          u =>
            (long / u.value, u)
        }.getOrElse(long, B)
      }
    }
  }
}
trait ByteUnit {
  def value: Int
  def name: String
}
case object G extends ByteUnit {
  val value = 1024 * 1024 * 1024
  val name = "g"
}
case object M extends ByteUnit {
  val value = 1024 * 1024
  val name = "m"
}
case object K extends ByteUnit {
  val value = 1024
  val name = "k"
}
case object B extends ByteUnit {
  val value = 1
  val name = "b"
}