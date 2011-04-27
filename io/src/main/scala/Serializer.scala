package nielinjie
package util.io


import com.thoughtworks.xstream.XStream
abstract class Serializer[T]{
  def serialize(obj:T):String
  def unSerialize(string:String):T
}
object Serializer{
  def apply():Serializer[Any]={new XStreamSerializer[Any]()}
}
class XStreamSerializer[T] extends Serializer[T]{
  val xStream=new XStream()
  override def serialize(obj:T)={
    xStream.toXML(obj)
  }
  override def unSerialize(string:String):T={
    xStream.fromXML(string).asInstanceOf[T]
  }
}
