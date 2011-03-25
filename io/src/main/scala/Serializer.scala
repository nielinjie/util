package nielinjie
package util.io


import com.thoughtworks.xstream.XStream
abstract class Serializer{
  def serialize[T](obj:T):String
  def unSerialize[T](string:String):T
}
object Serializer{
  def apply():Serializer={new XStreamSerializer()}
}
class XStreamSerializer extends Serializer{
  val xStream=new XStream()
  override def serialize[T](obj:T)={
    xStream.toXML(obj)
  }
  override def unSerialize[T](string:String):T={
    xStream.fromXML(string).asInstanceOf[T]
  }
}
