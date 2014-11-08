package name.nielinjie.common.repository

import java.util.UUID

import com.escalatesoft.subcut.inject.{BindingModule, Injectable}
import com.mongodb.ServerAddress
import com.mongodb.casbah._
import name.nielinjie.common.UUIDSerializer
import org.json4s.JsonDSL._
import org.json4s.mongo.JObjectParser
import org.json4s.{JField, JString, _}
import org.slf4j.LoggerFactory

import scala.util.control.Exception._


case class MongoConfig(host: String, port: Int, db: String, auth: Option[(String, String)])

class Mongo(implicit val bindingModule: BindingModule) extends Injectable {
  val logger = LoggerFactory.getLogger(classOf[Mongo])
  val config: MongoConfig = inject[MongoConfig]
  val MongoConfig(host, port, dbName, auth) = config
  val server = new ServerAddress(host, port)
  val options = MongoClientOptions(autoConnectRetry = true)
  val mongoClient =
    auth match {
      case Some((user, pass)) =>
        val credential = MongoCredential(user, dbName, pass.toCharArray)
        MongoClient(server, List(credential), options)
      case None =>
        MongoClient(server, options)
    }
  val db = mongoClient(dbName)
}

class MongoRepository(val collectionName: String)(implicit val bindingModule: BindingModule) extends Repository with Injectable {
  implicit val formats = DefaultFormats + UUIDSerializer
  val logger = LoggerFactory.getLogger(classOf[MongoRepository])
  val mongo = inject[Mongo]

  object db3 {

    var _collection: MongoCollection = null


    def withCollection[T](body: MongoCollection => T): T = {
      def reco = {

        _collection = mongo.db(collectionName)
      }
      if (_collection == null) {
        logger.info("new collection")
        reco
      }
      allCatch either body(_collection) match {
        case Left(e) => {
          logger.error(e.getMessage, e)
          logger.info("retry")
          reco
          val re = body(_collection)
          re
        }
        case Right(t) => {
          t
        }
      }
    }
  }

  import db3.withCollection

  def idQuery(id: String) = {
    JObjectParser.parse(JObject(List(JField("id", JString(id)))))
  }

  def add(obj: JObject): UUID = {
    obj.values.get("id") match {
      case Some(a) if a != null =>
        throw new IllegalArgumentException("no id allowed")
      case _ => {
        def uuid = java.util.UUID.randomUUID
        val dbo: JObject = obj ~ ("id" -> uuid.toString)
        withCollection(c => c.insert(JObjectParser.parse(dbo)))
        uuid
      }
    }


  }

  def query(query: Option[JObject]) = {
    query match {
      case None => withCollection({
        c =>
          val cu = c.find
          val re = cu.toList.map {
            JObjectParser.serialize(_).asInstanceOf[JObject]
          }
          cu.close()
          re
      })
      case Some(jobj) => withCollection({
        c =>
          assume(c != null)
          val cu = c.find(JObjectParser.parse(jobj))
          val re = cu.toList.map {
            JObjectParser.serialize(_).asInstanceOf[JObject]
          }
          cu.close()
          re
      })
    }
  }

  def clear = {
    withCollection(c => c.drop())
  }

  override def update(id: UUID, obj: JObject): Unit = {
    get(id) match {
      case Some(_) =>
        withCollection(c => c.update(idQuery(id.toString), JObjectParser.parse(obj)))
      case None => withCollection(c => c.insert(JObjectParser.parse(obj)))
    }
  }

  override def get(id: UUID): Option[JObject] = {
    withCollection({
      c =>
        val v = c.find(idQuery(id.toString))
        val re = if (v.hasNext)
          Some(JObjectParser.serialize(v.next).asInstanceOf[JObject])
        else
          None
        v.close
        re
    })

  }

  override def remove(query: JObject): Unit = {
    withCollection({
      c =>
        c.remove(JObjectParser.parse(query))
    })
  }
}