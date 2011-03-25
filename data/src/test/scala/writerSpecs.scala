package nielinjie
package util.data

import org.specs2.mutable._

object WriterSpec extends Specification {
    def add(a:Int, b:Int, log:String)=new Writer(a+b, List(log))
    
    "one step with map" in {
        val result =add(0, 1, "add one").map{case p=>p }
        result.result must be equalTo(1)
        result.log must be equalTo(List("add one"))
    }
    "one step with for comprehension" in {
        val result = for (
            r <- add(0, 1, "add one")
        ) yield r
        result.result must be equalTo(1)
        result.log must be equalTo(List("add one"))
    }
    "three step with flatMap" in {
        val result= add(0, 1, "add one").flatMap{
                        case a => add(a,2,"add two").flatMap{
                            case b => add(a,3,"add three")                        }
                    }
        result.result must be equalTo(4)
        result.log must be equalTo(List("add one", "add two", "add three"))
    }
    "three step with for comprehension" in {
        val result = for (
            a <- add(0,1, "add one");
            b <- add(a, 2, "add two");
            c <- add (a, 3, "add three")
        ) yield c
        result.result must be equalTo(4)
        result.log must be equalTo(List("add one", "add two", "add three"))
    }
}
