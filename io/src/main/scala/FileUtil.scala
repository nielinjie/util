package nielinjie
package util.io

import java.io.File
import java.io.FileWriter
import scala.io.Source

object FileUtil {
  def makeSureFolderExist(folder: File): Unit = {
    if (folder.exists) {
      if (!folder.isDirectory)
        throw new IllegalStateException("file exist, but not a folder")
    } else {
      folder.mkdirs
    }
  }

  def toFile(text: String, file: File) {
    if (!file.exists) {
      makeSureFolderExist(file.getParentFile)
    }
    val fw = new FileWriter(file)
    try {
      fw.write(text)
    } catch {
      case e => e.printStackTrace()
    }
    finally {
      fw.close
    }

  }

  def fromFile(file: File): String = {
    Source.fromFile(file).mkString
  }
}
