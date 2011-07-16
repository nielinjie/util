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
  val xStream={
   val  x=new XStream()
    ListConverter.configureXStream(x)
    x
  }
  override def serialize(obj:T)={
    xStream.toXML(obj)
  }
  override def unSerialize(string:String):T={
    xStream.fromXML(string).asInstanceOf[T]
  }
}

import com.thoughtworks.xstream.converters._
import com.thoughtworks.xstream.converters.collections._
import com.thoughtworks.xstream._
import com.thoughtworks.xstream.mapper._
import com.thoughtworks.xstream.io._

class ListConverter( _mapper : Mapper )  extends AbstractCollectionConverter(_mapper) {
  def canConvert( clazz: Class[_]) = {
    // "::" is the name of the list class, also handle nil
    classOf[::[_]] == clazz || classOf[scala.collection.immutable.Nil$] == clazz
  }

  def marshal( value: Any, writer: HierarchicalStreamWriter, context: MarshallingContext) = {
    val list = value.asInstanceOf[List[_]]
    for ( item <- list ) {
      writeItem(item, context, writer)
    }
  }

  def unmarshal( reader: HierarchicalStreamReader, context: UnmarshallingContext ) = {
    var list : List[_] = Nil
    while (reader.hasMoreChildren()) {
      reader.moveDown();
      val item = readItem(reader, context, list);
      list = list ::: List(item) // be sure to build the list in the same order
      reader.moveUp();
    }
    list
  }
}

object ListConverter {
  def configureXStream( stream: XStream ) = {
    stream.alias("list", classOf[::[_]])
    stream.alias("list", classOf[scala.collection.immutable.Nil$])
    stream.registerConverter( new ListConverter(stream.getMapper) )
  }
}
