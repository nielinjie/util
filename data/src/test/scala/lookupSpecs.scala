package nielinjie
package util.data

import org.specs2.mutable._
import Params._

object ParamsSpecs extends Specification {
  "params looking up works" in {

    val theMap = Map("a" -> 1, "b" -> 2)
    val Map2 = Map("aa" -> 1)
    "param lookup with map" in {
      val lookingUp = lookUp("a").map {
        case a => (a.get, a.get)
      }
      lookingUp.apply(theMap) must equalTo(Success((1, 1)))

      //lookingUp.apply(Map2).getOrFail(_ => (2, 2)) must equalTo((2, 2))
    }
    "param lookup with for comprehension" in {
      val lookingUp = for {
        a <- lookUp("a")
      } yield (a.get)
      lookingUp.apply(theMap) must equalTo(Success(1))

    }
    "params lookup with flatMap" in {
      val lookingUp = lookUp("a").flatMap {
        case a => lookUp("b").map {
          case b => (b.get)
        }
      }
      lookingUp.apply(theMap) must equalTo(Success(2))
      val lookingUp2 = lookUp("a").flatMap {
        case a => lookUp("b").map {
          case b => (a.get, b.get)
        }
      }
      lookingUp2.apply(theMap) must equalTo(Success((1, 2)))

    }
    "params lookup with for comprehension" in {
      val lookingUp = for {
        a <- lookUp("a");
        b <- lookUp("b")
      } yield (b.get)
      lookingUp.apply(theMap) must equalTo(Success(2))
      val lookingUp2 = for {
        a <- lookUp("a");
        b <- lookUp("b")
      } yield (b.get, a.get)
      lookingUp2.apply(theMap) must equalTo(Success((2, 1)))
    }
  }
  "guards" in {

    val theMap = Map("a" -> 1, "b" -> 2)
    "required guards" in {
      val lookingUp = for {
        a <- lookUp("a").required;
        b <- lookUp("b");
        c <- lookUp("c")
      } yield (a, b, c)
      lookingUp(theMap) must equalTo(Success(1, Some(2), None))
      val lookingUp2 = for {
        a <- lookUp("a").required;
        b <- lookUp("b");
        c <- lookUp("c").required
      } yield (a, b, c)
      lookingUp2(theMap) must haveClass[Failed[_]]
    }
    "as Instance guard" in {
      import Converters._
      val lookingUp = for {
        a <- lookUp("a").required.as[Int].to[String];
        b <- lookUp("b").required.as[Int];
        c <- lookUp("c")
      } yield (a ,b)
      lookingUp(theMap) must equalTo(Success(("1",2)))
    }
  }
}
