package name.nielinjie.common.plan


import scalaz.{Failure, Success, Validation}

import name.nielinjie.common.UUIDSerializer
import name.nielinjie.common.repository.Repository
import org.json4s.JsonAST.{JArray, JField, JString}
import org.json4s._
import org.json4s.ext.JodaTimeSerializers
import org.json4s.native.JsonMethods._
import org.slf4j.{Logger, LoggerFactory}
import unfiltered.request._
import unfiltered.response._

import scala.util.control.Exception._

trait JsonAware {

  object JsonBody {
    implicit val formats = DefaultFormats ++ JodaTimeSerializers.all + UUIDSerializer

    def unapply[T](req: HttpRequest[T]): Option[JValue] =
      unfiltered.request.JsonBody(req)
  }

  def checkAcceptJson(req: HttpRequest[_])(body: => ResponseFunction[Any]): ResponseFunction[Any] = {
    req match {
      case Accepts.Json(_) => body
      case _ => BadRequest ~> ResponseString("You must accept application/json")
    }
  }

  def checkContentJson(req: HttpRequest[_])(body: => ResponseFunction[Any]): ResponseFunction[Any] = {

    req match {
      //TODO this is too hard
      case RequestContentType(s) if s.contains("json") && s.toUpperCase.contains("UTF-8") => body
      case _ => NotAcceptable ~> ResponseString("You must supply 'application/json;charset=UTF-8'")
    }
  }

  def withContentJson(req: HttpRequest[_])(body: JValue => ResponseFunction[Any]): ResponseFunction[Any] = {
    req match {
      //TODO this is too hard
      case RequestContentType(s) if s.contains("json") && s.toUpperCase.contains("UTF-8") => {
        req match {
          case JsonBody(c) => {
            body(c)
          }
          case _ => BadRequest ~> ResponseString("Invalid json data")
        }
      }
      case _ => NotAcceptable ~> ResponseString("You must supply 'application/json;charset=UTF-8'")
    }
  }
}


  //TODO is thread safe?
  trait JsonRestPlan extends unfiltered.filter.Plan with JsonAware {
    val log: Logger = LoggerFactory.getLogger(classOf[JsonRestPlan])
    val objName: String
    val collectionName: String
    val repository: Repository

    def validate(obj: JValue): Validation[String, JValue]


    def idQuery(id: String) = {
      JObject(List(JField("id", JString(id))))
    }

    def query(q: Option[JObject]) = Ok ~> Json(JArray(repository.query(q)))

    def queryOne(q: Option[JObject]) = {
      repository.query(q).headOption match {
        case Some(h) =>
          Ok ~> Json(h)
        case _ =>
          NotFound ~> ResponseString("not found - " + q.toString)
      }
    }

    object Query extends Params.Extract("query", Params.first)

    lazy val collectionPath = "/" + collectionName

    /**
     * POST -> create SUB RESOURCE, so, post to a collection is creating a object
     * GET -> get
     * PUT -> update
     * DELETE -> delete
     * @return
     */
    def intent = {
      case req@GET(Path(Seg(`objName` :: id :: Nil))) => {
        checkAcceptJson(req) {
          queryOne(Some(idQuery(id)))
        }
      }
      case req@PUT(Path(Seg(`objName` :: id :: Nil))) => {
        ???
      }
      case req@DELETE(Path(Seg(`objName` :: id :: Nil))) => {
        ???
      }
      case req@GET(Path(`collectionPath`)) => {
        checkAcceptJson(req) {
          req match {
            case Params(Query(qu)) => {
              allCatch.either {
                parse(qu).asInstanceOf[JObject]
              } match {
                case Right(q) => query(Some(q))
                case Left(t) => BadRequest ~> ResponseString("query is not cool -" + t.toString)
              }
            }
            case _ =>
              query(None)
          }
        }
      }
      case req@(POST(Path(`collectionPath`))) => {
        checkContentJson(req) {
          req match {
            case JsonBody(c) => {
              validate(c) match {
                case Success(d) => {
                  allCatch.either {
                    val uuid = repository.add(c.asInstanceOf[JObject])
                    Created ~> Json(JObject(JField("id", JString(uuid.toString))))
                  }.left.map {
                    l =>
                      InternalServerError ~> ResponseString(l.getMessage)
                  }.merge
                }
                case Failure(f) => BadRequest ~> ResponseString(f)
              }
            }
            case _ => BadRequest ~> ResponseString("Invalid json data")
          }
        }
      }
      //Collection has no PUT or DELETE
    }
  }