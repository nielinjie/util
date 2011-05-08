package nielinjie
package util.data

import org.specs2.mutable.Specification

object ExtractorSpecs extends Specification {
  "extractor" in {
    "override?" in {
      val Foo.fromString(foo) = "foo"
      foo must equalTo(Foo("fooName"))
    }
    "case match" in {
      "foo" match {
        case Foo.fromString(foo) => foo must equalTo(Foo("fooName"))
        case _ => failure("faile")
      }
      "noFoo" match {
        case Foo.fromString(foo) => failure("should not extracted")
        case _ => success
      }
    }
    "nested" in {
      val Foo.fromString(Foo(fName)) = "foo"
      fName must equalTo("fooName")
    }
  }

}

case class Foo(name: String)

object Foo {

  object fromString {
    def unapply(string: String): Option[Foo] = {
      string match {
        case s if (s == "foo") => Some(Foo("fooName"))
        case _ => None
      }
    }
  }


}
