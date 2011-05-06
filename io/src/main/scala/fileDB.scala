package nielinjie
package util.io


import java.io.File

class FileDB[A](val rootDir: File)(implicit hasId: HasId[A], serializer: Serializer[A]) {

  import FileUtil._

  makeSureFolderExist(rootDir)

  def get(id: String): Option[A] = {
    val file = new File(rootDir, id)
    if (file.exists)
      Some(serializer.unSerialize(fromFile(file)))
    else
      None
  }

  def put(a: A) = {
    val file= new File(rootDir, hasId.getId(a))
    toFile(serializer.serialize(a),file)
  }
}

trait HasId[A] {
  def getId(a: A): String
}