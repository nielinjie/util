package nielinjie
package util.data

import reactive.EventSource
import reactive.Observing

trait UnitedEvents[T] extends Observing {
  val event = new EventSource[T]() {}
  def addEventSource(eventS: EventSource[T]) {
    eventS >> event
  }
}