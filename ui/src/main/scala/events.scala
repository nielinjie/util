package nielinjie
package util.ui
package event

import reactive._
import swing._
import event._

object EventSupport {
  implicit def component2EventWrap(component: Component): GeneralEventStreamWrap = new GeneralEventStreamWrap(component)

  implicit def eventWrap2Component(eventWrap: GeneralEventStreamWrap): Component = eventWrap.component

  implicit def buttonToEvent(button: Button) = new GeneralEventStreamWrap(button) {
    val clicked = eventStream({
      case ActionEvent(source) => source
    })
  }

  implicit def toggleBToEvent(toggleB: ToggleButton) = new GeneralEventStreamWrap(toggleB) {
    val toggled = eventStream({
      case ActionEvent(source) => toggleB.selected
    })
  }
}

trait WindowEventSupport {
  self: MainFrame =>
  reactions += {
    case WindowClosing(w) => {
      onClosing(w)
    }
  }

  def onClosing(w: Window): Unit
}



class GeneralEventStreamWrap(val component: Component) {
  def eventStream[A](f: PartialFunction[Event, A]) = {
    val eventStream = new EventSource[A]() {}
    component.listenTo(component)
    component.reactions += f.andThen({
      a =>
        eventStream.fire(a)
    })
    eventStream
  }
}
