package name.nielinjie.common.repository

import java.util.UUID

import org.json4s.JsonDSL._
import org.json4s._

trait Repository {
  def add(obj:JObject):UUID
  def query(query:Option[JObject]):List[JObject]
  def clear():Unit
  def update(id:UUID,obj:JObject):Unit
  def get(id:UUID):Option[JObject]
  def remove(query:JObject):Unit
}
class MapRepository extends Repository{
  private var repository: scala.collection.mutable.Map[UUID, JObject] = scala.collection.mutable.Map.empty
  def add(obj:JObject)={
    def uuid = java.util.UUID.randomUUID
    repository.put(uuid,obj ~ ("id"->uuid.toString))
    uuid
  }
  def query(query:Option[JObject]) ={
    query match {
      case None => repository.values.toList
      case _ => repository.toList.head._2::Nil
    }
  }
  def clear = {
    repository.clear()
  }

  override def update(id: UUID, obj: JObject): Unit = {
    repository.update(id,obj)
  }

  override def get(id: UUID): Option[JObject] = repository.get(id)

  override def remove(query: JObject): Unit = ???
}