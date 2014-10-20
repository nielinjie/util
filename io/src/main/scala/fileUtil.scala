package nielinjie
package util.io

import java.io.File
import java.io.FileWriter
import scala.io.Source
import scalax.io.Resource
import scala.util.control.Exception._
import scalaz._
import Scalaz._
import scalax.io.Codec

object FileUtil {
  def makeSureFolderExist(folder: File): Unit = {
    if (folder.exists) {
      if (!folder.isDirectory)
        throw new IllegalStateException("file exist, but not a folder")
    } else {
      folder.mkdirs
    }
  }
  
  def needFile(file:File) : Validation[Throwable,Option[File]]={
    if (file.exists){
      file.some.success
    }else{
      validation(allCatch.either(makeSureFolderExist(file.getParentFile())).map(x=>None))
    }
  }
  //  /**
  //   * use scala-io
  //   * output.write("hello")
  //   */
  //  @deprecated("use scala-io","1.0")
  def toFile(text: String, file: File) {
    if (!file.exists) {
      makeSureFolderExist(file.getParentFile)
    }
    Resource.fromFile(file).write(text)(Codec.UTF8)
  }
  //  /**
  //   * use scala-io
  //   * input.slurpString(Codec.UTF8)
  //   */
  //  @deprecated("use scala-io","1.0")
  def fromFile(file: File): String = {
    Resource.fromFile(file).string(Codec.UTF8)
  }

  def recursiveListFiles(f: File): List[File] = {
    val these = f.listFiles.toList
    (these.filter(!_.isDirectory) ++ these.filter(_.isDirectory).flatMap(recursiveListFiles))
  }
  
  def home:Validation[Throwable,File] ={
    val p=System.getProperty("user.home")
    if(p != null) (new File(p)).success else ( new IllegalStateException("system property 'user.home' is null")).fail
  }
  
  def relativePath(base:File,file:File):String={
   base.toURI().relativize(file.toURI()).getPath()
  }

}
