package nielinjie
package util.data

import org.specs2.mutable._
import Params._
import scalaz._
import org.specs2.internal.scalaz.Failure
import org.specs2.internal.scalaz.Digit._0
import scala.Either

object ParamsSpecs extends Specification {

  import Scalaz._

  "params looking up works" in {

    val theMap = Map("a" -> 1, "b" -> 2)
    val Map2 = Map("aa" -> 1)
    "param lookup with for comprehension" in {
      val lookingUp = for {
        a <- lookUp("a")
      } yield (a.get)
      lookingUp.apply(theMap) must equalTo(1.success)

    }
    "params lookup with for comprehension" in {
      val lookingUp = for {
        a <- lookUp("a");
        b <- lookUp("b")
      } yield (b.get)
      lookingUp.apply(theMap) must equalTo(2.success)
      val lookingUp2 = for {
        a <- lookUp("a");
        b <- lookUp("b")
      } yield (b.get, a.get)
      lookingUp2.apply(theMap) must equalTo((2, 1).success)
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
      lookingUp(theMap) must equalTo((1, Some(2), None).success)
      val lookingUp2 = for {
        a <- lookUp("a").required;
        b <- lookUp("b");
        c <- lookUp("c").required
      } yield (a, b, c)
      lookingUp2(theMap).isFailure must beTrue
    }
    "default works" in {
      val lookingUp = for {
        a <- lookUp("a").required;
        b <- lookUp("b").as[Int].default(1);
        c <- lookUp("c")
        d <- lookUp("d").as[Int].default(1)
      } yield (a, b, c, d)
      lookingUp(theMap) must equalTo((1, 2, None, 1).success)
    }
    "as Instance guard" in {
      import data._
      val lookingUp = for {
        a <- lookUp("a").required.as[Int].to[String];
        b <- lookUp("b").required.as[Int];
        c <- lookUp("c").as[Int]
      } yield (a, b, c)
      lookingUp(theMap) must equalTo(("1", 2, None).success)
    }
    "to other type Instance guard" in {
      import data._
      val lookingUp = for {
        a <- lookUp("a").required.as[Int].to[String];
        b <- lookUp("b").to[String]
      } yield (a, b)
      lookingUp(theMap) must equalTo(("1", Some("2")).success)
    }
    "ensuring guard" in {
      val lookingUp = for {
        a <- lookUp("a").required.ensuring(x => x != 1)
        b <- lookUp("b").as[Int].default(1);
        c <- lookUp("c")
        d <- lookUp("d").as[Int].default(1)
      } yield (a, b, c, d)
      lookingUp(theMap).isFailure must beTrue

      val lookingUp2 = for {
        a <- lookUp("a").required.ensuring(x => x == 1)
        b <- lookUp("b").as[Int].ensuring(x => x == 1)
        d <- lookUp("d").as[Int].default(1)
      } yield (a, b, d)
      lookingUp2(theMap).isFailure must beTrue

      val lookingUp3 = for {
        a <- lookUp("a").required.ensuring(x => x == 1)
        b <- lookUp("b").as[Int].ensuring(x => x != 1)
        d <- lookUp("d").as[Int].default(1)
      } yield (a, b, d)
      lookingUp3(theMap) must equalTo((1, Some(2), 1).success)
    }
  }
  "use as applicative functor, (not so useful)" in {
    val theMap = Map("a" -> 1, "b" -> 2)
    val lookingUp = for {
      a <- lookUp("a").required.ensuring(x => x != 1)
      b <- lookUp("b").as[Int].default(1);
      c <- lookUp("c")
      d <- lookUp("d").as[Int].default(1)
    } yield (a, b, c, d)
    ((lookUp("a").required)(theMap) |@|
      (lookUp("b").required)(theMap)).apply((_, _)) must equalTo((1, 2).success)
  }

  "value type safe" in {
    val theMap = Map("1" -> 10, "2" -> 20)
    //val theMap = Map("1" -> 10, "2" -> "20") this will no compile because value type safe

    val lookingUp = for {
      a <- lookUpFor[Int]("1").required
      b <- lookUpFor[Int]("2").required
    } yield (a, b)
    lookingUp(theMap) must equalTo((10, 20).success)
  }

  "lookup by projection function" in {

  }

//TODO todo?
//  "multiple lookuping" in {
//    val theMultipleMap = List("1" -> 10, "1" -> 100, "2" -> 20)
//    val multiLookingUp = for {
//      a <- lookUpFor[Int]("1").number(2)
//      b <- lookUpFor[Int]("2").one
//    } yield (a, b)
//    multiLookingUp(theMultipleMap) mst equalTo((List(10, 100), 20).success)
//  }

}
