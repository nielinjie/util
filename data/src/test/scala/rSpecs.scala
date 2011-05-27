package nielinjie
package util.data

import org.specs2.mutable._
import scalaz._

object RSepcs extends Specification {

  import Scalaz._
  import R._

  val map = Map("a" -> 1)
  "lookup" in {
    lookup("a")(map) must be equalTo ((1.some).success)
  }
  "required" in {
    ////    val k=((lookup("a"):Kleisli[PartialApply1Of2[Validation, String]#Apply,Map[String,Int],Option[Int]])
    ////      .>=>
    ////      (required():Kleisli[PartialApply1Of2[Validation, String]#Apply,Option[Int],Int]))
    //      //.apply(map) must equalTo(1.success)
    //
    //     val f = ☆((n: Int) => (if (n % 2 == 0) None else Some((n + 1).toString)).toSuccess("what?")):Kleisli[PartialApply1Of2[Validation, String]#Apply,Int,String]
    //    val g = ☆((s: String) => (if (List(5, 7) ∃ (_ == s.length)) None else Some("[" + s + "]")).toSuccess("kaka")):Kleisli[PartialApply1Of2[Validation, String]#Apply,String,String]
    //
    //    // Kleisli composition
    //    (List(7, 78, 98, 99, 100, 102, 998, 999, 10000) map (f >=> g apply _)) must notNull
    //      //equalTo (List(Some("[8]"), None, None, Some("[100]"), None, None, None, Some("[1000]"), None))

    val f = kleisli((n: Map[String, Int]) => n.get("a"))
    val g = kleisli((o: Int) => (o + 1).some)

    (f >=> g).apply(Map("a" -> 1)) must equalTo(Some(2))

    val f2 = kleisli((n: Map[String, Int]) => (n.get("a").toSuccess("not found")): Validation[String, Int])
    val g2 = kleisli((o: Int) => ((o + 1).success): Validation[String, Int])

    (f2 >=> g2).apply(Map("a" -> 1)) must equalTo((2).success)

    
    val f1 = kleisli[PartialApply1Of2[Validation, String]#Apply, Map[String, Int], Int]((n: Map[String, Int]) => (n.get("a").toSuccess("not found")): Validation[String, Int])
    val g1 = kleisli[PartialApply1Of2[Validation, String]#Apply, Int, Int]((o: Int) => ((o + 1).success): Validation[String, Int])

    (f1 >=> g1).apply(Map("a" -> 1)) must equalTo((2).success)

  }

}