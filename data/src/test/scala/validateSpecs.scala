package nielinjie
package util.data

import org.specs2.mutable._
import LookUp._
import scalaz._
import std.option._

object ValidateSpecs extends Specification {
  "validate" in {
    val theMap = Map("a" -> 1, "b" -> 2)
    "use lookup to validate" in {
      val lookingUp = for {
        a <- lookUp("a").required
      } yield (theMap)
      lookingUp.apply(theMap) must equalTo(Success(theMap))
      val lookingUp2 = for {
        a <- lookUp("a").required
        c <- lookUp("c").required
      } yield (theMap)
      lookingUp2.apply(theMap).isFailure must beTrue
    }
    "validate wrapper" in {
      val v = lookUp("a").required.flatMap({
        case a =>
          lookUp("b").required
      }).foreach {
        case _ =>
      }
      v.asValidate.apply(theMap) must equalTo(Success(theMap))
      val v2 = lookUp("a").required.flatMap({
        case a =>
          lookUp("c").required
      }).foreach {
        case (a, c) => Unit
      }
      v2.asValidate.apply(theMap).isFailure must beTrue
    }
    "validate syntax" in {
      val v = for {
        a <- lookUp("a").required
        b <- lookUp("b").required
      } yield Unit
      v.asValidate.apply(theMap) must equalTo(Success(theMap))
      val v2 = for {
        a <- lookUp("a").required
        c <- lookUp("c").required
      } yield Unit
      v2.asValidate.apply(theMap).isFailure must beTrue

    }
    //TODO validate syntax is not cool. it is really no better than using lookingup directly. maybe we have to use macro here.
  }
}