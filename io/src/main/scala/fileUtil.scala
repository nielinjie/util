package nielinjie
package util.io

import java.io.File
import java.io.FileWriter
import scala.io.Source
import scalax.io.Resource

object FileUtil {
  def makeSureFolderExist(folder: File): Unit = {
    if (folder.exists) {
      if (!folder.isDirectory)
        throw new IllegalStateException("file exist, but not a folder")
    } else {
      folder.mkdirs
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
    Resource.fromFile(file).write(text)
  }
  //  /**
  //   * use scala-io
  //   * input.slurpString(Codec.UTF8)
  //   */
  //  @deprecated("use scala-io","1.0")
  def fromFile(file: File): String = {
    Resource.fromFile(file).slurpString()
  }

  def recursiveListFiles(f: File): List[File] = {
    val these = f.listFiles.toList
    (these ++ these.filter(_.isDirectory).flatMap(recursiveListFiles))
  }

}
