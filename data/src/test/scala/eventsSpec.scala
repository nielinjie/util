package nielinjie
package util.data

import org.specs2.mutable._
import reactive.EventSource
import reactive.Observing

object EventsSpec extends Specification with Observing {
  "simpile" in {
    var ints = List[Int]()
    val source = new EventSource[Int]() {}
    source.foreach({
      case i: Int => ints = ints :+ i
    })
    source.fire(1)
    source.fire(2)
    ints must equalTo(List(1, 2))
  }
  "composite" in {
    var ints = List[Int]()
    val small = new EventSource[Int]() {}
    val big = new EventSource[Int]() {}
    val composite = small | big
    composite.foreach({
      case i: Int => ints = ints :+ i
    })
    small.fire(1)
    small.fire(2)
    big.fire(10)
    ints must equalTo(List(1, 2, 10))
    val bb = new EventSource[Int]() {}
    (composite | bb).foreach {
      case i: Int => ints = ints :+ i
    }
    bb.fire(100)
    ints must equalTo(List(1, 2, 10, 100))
  }
  "united after listening" in {
    var ints = List[Int]()

    val oneEvent = new UnitedEvents[Int]() {}
    oneEvent.event.foreach({
      case i: Int => ints = ints :+ i
    })
    val small = new EventSource[Int]() {}
    oneEvent.addEventSource(small)

    small.fire(1)
    small.fire(2)
    ints must equalTo(List(1, 2))
    val big = new EventSource[Int]() {}
    oneEvent.addEventSource(big)
    big.fire(10)
    small.fire(3)
    ints must equalTo(List(1, 2, 10, 3))
  }
}