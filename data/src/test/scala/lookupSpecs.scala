package nielinjie
package util.data

import org.specs2.mutable._
import LookUp._
import scalaz._
import std.option._
import std.list._

object LookUpSpecs extends Specification {

  import syntax.validation._

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
        a <- lookUp("a").as[Int].required;
        b <- lookUp("b");
        c <- lookUp("c")
      } yield (a, b, c)
      lookingUp(theMap) must equalTo((1, Some(2), None).success)
      val lookingUp2 = for {
        a <- lookUp("a").as[Int].required;
        b <- lookUp("b");
        c <- lookUp("c").as[Int].required
      } yield (a, b, c)
      lookingUp2(theMap).isFailure must beTrue
    }
    "default works" in {
      val lookingUp = for {
        a <- lookUp("a").as[Int].required;
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
    "fmap " in {
      val lookingUp3 = for {
        a <- lookUp("a").required.as[Int].fmap(_ + 1)
        b <- lookUp("b").as[Int].fmap(_ + 1)
        d <- lookUp("d").as[Int].fmap(_ + 1)
      } yield (a, b, d)
      lookingUp3(theMap) must equalTo((2, Some(3), None).success)
    }
  }
  // TODO: applicative (and orther type class?)
//  "use as applicative functor, (not so useful)" in {
//    val theMap = Map("a" -> 1, "b" -> 2)
//    val lookingUp = for {
//      a <- lookUp("a").as[Int].required.ensuring(x => x != 1)
//      b <- lookUp("b").as[Int].default(1);
//      c <- lookUp("c")
//      d <- lookUp("d").as[Int].default(1)
//    } yield (a, b, c, d)
//    ((lookUp("a").required) |@|
//      (lookUp("b").required)).apply((_, _)) must equalTo((1, 2).success)
//  }

  "value type safe" in {
    val theMap = Map("1" -> 10, "2" -> 20)
    //val theMap = Map("1" -> 10, "2" -> "20") this will no compile because value type safe

    val lookingUp = for {
      a <- lookUpFor[Int]("1").required
      b <- lookUpFor[Int]("2").required
    } yield (a, b)
    lookingUp(theMap) must equalTo((10, 20).success)
  }

  "lookup in xml with projection function" in {
    import xml._
    val x = <a>
              <b attr="c">bText</b>
            </a>
    implicit def xmlProjection: (Elem, Elem => NodeSeq) => Option[String] = {
      (x, f) =>
        f(x) match {
          case ns if !(ns.isEmpty) => Some(ns.text)
          case _ => None
        }
    }
    def lookUpXml(f: Elem => NodeSeq) = lookUpFor[String](f)
    val lookingUp = for {
      b <- lookUpXml(_ \ "b").required
      a <- lookUpXml(_ \ "b" \ "@attr").required
      c <- lookUpXml(_ \ "c")
      d <- lookUpXml(_ \ "d")
    } yield (b, a, c, d)
    lookingUp(x) must equalTo(("bText", "c", None, None).success)

  }

  "multiple lookuping" in {
    implicit def mapMoreProjectFunction[K, A]: (Map[K, List[A]], K) => List[A] = {
      (m, k) => m.get(k).toList.flatten
    }
    val theMultipleMap = Map("1" -> List(10, 100), "2" -> List(20))
    "simple" in {
      val multiLookingUp = for {
        a <- lookUpMoreFor[Int]("1")
        b <- lookUpMoreFor[Int]("2")
      } yield (a, b)
      multiLookingUp(theMultipleMap) must equalTo((List(10, 100), List(20)).success)
    }
    "required" in {
      val multiLookingUp = for {
        a <- lookUpMoreFor[Int]("1").required
        b <- lookUpMoreFor[Int]("2")
        c <- lookUpMoreFor[Int]("3")
      } yield (a, b, c)
      multiLookingUp(theMultipleMap) must equalTo((10, List(20), List()).success)
      ((for {
        a <- lookUpMoreFor[Int]("1").required
        b <- lookUpMoreFor[Int]("2")
        c <- lookUpMoreFor[Int]("3").required
      } yield (a, b, c))(theMultipleMap)).isFailure must beTrue
    }
    "default" in {
      val multiLookingUp = for {
        a <- lookUpMoreFor[Int]("1").required
        b <- lookUpMoreFor[Int]("2")
        c <- lookUpMoreFor[Int]("3").default(3)
      } yield (a, b, c)
      multiLookingUp(theMultipleMap) must equalTo((10, List(20), 3).success)
    }
    "ensuring" in {
      val multiLookingUp = for {
        a <- lookUpMoreFor[Int]("1").ensuring(_ >= 10)
        b <- lookUpMoreFor[Int]("2")
      } yield (a, b)
      multiLookingUp(theMultipleMap) must equalTo((List(10, 100), List(20)).success)
      val multiLookingUp2 = for {
        a <- lookUpMoreFor[Int]("1").ensuring(_ >= 100)
        b <- lookUpMoreFor[Int]("2")
      } yield (a, b)
      multiLookingUp2(theMultipleMap).isFailure must beTrue
    }
  }
  "exception in project function " in {
    implicit def mapProjectFunction[K, A]: (Map[K, A], K) => Option[A] = {
      (m, k) => throw new RuntimeException("fake exception")
    }
    val theMap = Map("a" -> 1, "b" -> 2)
    "param lookup with for comprehension" in {
      val lookingUp = for {
        a <- lookUp("a")
      } yield (a.get)
      lookingUp.apply(theMap).isFailure must beTrue

    }
  }

}
