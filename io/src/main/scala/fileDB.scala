package nielinjie
package util.io


import java.io.File

class FileDB[A, I](val rootDir: File)(implicit hasId: HasId[A, I], serializer: Serializer[A]) {

  import FileUtil._

  makeSureFolderExist(rootDir)

  def get(id: I): Option[A] = {
    val file = new File(rootDir, id.toString)
    if (file.exists)
      Some(serializer.unSerialize(fromFile(file)))
    else
      None
  }

  def put(a: A) = {
    val file= new File(rootDir, hasId.getId(a).toString)
    toFile(serializer.serialize(a),file)
  }
}

trait HasId[A, I] {
  def getId(a: A): I
}