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
    "operator?" in {
      val &(Foo.fromString(f), Bar.fromString(b)) = "foobar"
      f must equalTo(Foo("fooName"))
      b must equalTo(Bar("barName"))

      val (Foo.fromString(f1) & Bar.fromString(b1)) = "foobar"
      f1 must equalTo(Foo("fooName"))
      b1 must equalTo(Bar("barName"))
    }
  }

}

case class Foo(name: String)

object Foo {

  object fromString {
    def unapply(string: String): Option[Foo] = {
      string match {
        case s if (s.contains("foo")) => Some(Foo("fooName"))
        case _ => None
      }
    }
  }

}

case class Bar(name: String)

object Bar {

  object fromString {
    def unapply(string: String): Option[Bar] = {
      string match {
        case s if (s.contains("bar")) => Some(Bar("barName"))
        case _ => None
      }
    }
  }

}

object & {
  def unapply(a: Any): Option[(Any, Any)] = Some(a, a)
}
