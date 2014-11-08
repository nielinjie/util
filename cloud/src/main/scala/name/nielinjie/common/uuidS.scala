
package name.nielinjie.common

import java.util.UUID

import org.json4s.CustomSerializer
import org.json4s.JsonAST.{JNull, JString}

object JavaTypesSerializers {
  val all = List(UUIDSerializer)
}

case object UUIDSerializer extends CustomSerializer[UUID](format => ( {
  case JString(s) => UUID.fromString(s)
  case JNull => null
}, {
  case x: UUID => JString(x.toString)
}
  )
)
