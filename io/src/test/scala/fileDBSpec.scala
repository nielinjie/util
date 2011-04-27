package nielinjie
package util.io

import org.specs2.mutable._
import java.io.File
object FileDBSpec extends Specification {
  "simple file db just work" in {
    case class Person(name: String, age: Int)
    implicit val ser = new XStreamSerializer[Person]

    implicit object personId extends HasId[Person, String] {
      def getId(person: Person) = person.name
    }

    val db = new FileDB[Person,String](new File("./testdb"))
    val person = Person("Jason", 32)
    db.put(person)
    val jason = db.get("Jason")
    jason must equalTo(Some(person))
  }

}