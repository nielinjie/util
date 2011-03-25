package nielinjie
package util.io

import java.util.UUID
import java.io._
import scala.xml._
import org.htmlcleaner._

object HtmlToXML{
  def findCharset(input:InputStream):String ={
  "gb2312"
  }
  def toXML(input:InputStream):Elem={
    val cleaner = new HtmlCleaner
    val charset=findCharset(input)
    val props = cleaner.getProperties()
        // customize cleaner's behaviour with property setters
        //props.setXXX(...);
    val  node = cleaner.clean(input,charset)
    XML.loadString(new SimpleXmlSerializer(props).getXmlAsString(node,charset))
  }
  }
object XMLUtil{
  def findByAttr(node:Elem, nodeName:String, attrName:String, attrValue:String)={
    (node \\ nodeName).filter({
      n:Node=>
      (n \ ("@"+attrName)).text==attrValue
    })
  }
  def findById(node:Elem, nodeName:String, id:String)={
    findByAttr(node,nodeName,"id",id)
  }
}
object UrlUtil{
  import java.net._
  def resovleRelative(url:String,relative:String)={
    new URL(new URL(url),relative)
  }
}


