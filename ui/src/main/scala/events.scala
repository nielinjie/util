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

object SelectSupport {

  implicit def singleSelectSupport2Component[A, T](singleSelectSupport: SingleSelectSupport[A, T]): T = singleSelectSupport.component

  implicit def listView2SelectSupport[A](listView: ListView[A]) = new SelectEventWrap[A, ListView[A]](listView) {
    def singleSelect = new ListViewSingleSelectSupport[A](listView)
  }

}

class ListViewSingleSelectSupport[A](val listView: ListView[A]) extends SingleSelectSupport[A, ListView[A]](listView) {
  listView.selection.intervalMode = ListView.IntervalMode.Single
  listView.selection.reactions += {
    case SelectionChanged(`listView`) =>
      if (!listView.selection.adjusting) {
        selectChanging.fire(SingleSelecting(listView.selection.items.headOption, listView.selection.indices.headOption, updating))
      }
  }

  def setSelectedValue(value: A) = {
    updating = true
    val selectedIndex = listView.selection.indices.headOption
    selectedIndex.foreach {
      index =>
        listView.listData = listView.listData.updated(index, value)
        listView.selectIndices(index)
    }
    updating = false
  }

  def updateKeepingSelect(list: List[A]) = {
    updating = true
    listView.listData = list
    val selectedIndex = listView.selection.indices.headOption
    selectedIndex.foreach {
      index =>
        listView.selectIndices(index)
    }
    updating = false
  }
}

abstract class SingleSelectSupport[A, T](val component: T) extends BindSelectedSupport[A, T] {
  var updating = false
  val selectChanging: EventSource[SingleSelecting[A]] = new EventSource[SingleSelecting[A]]() {}
  val selectChangedEvent = selectChanging.filter({
    e =>
      !e.updating
  }).map({
    e =>
      e.selected
  })
  val selectIndexChangedEvent = selectChanging.filter({
    e =>
      !e.updating
  }).map({
    e =>
      e.selectedIndex
  })
  val selected: Signal[Option[A]] = selectChangedEvent.hold(None)

  def setSelectedValue(value: A)

  def updateKeepingSelect(list: List[A])
}

case class SingleSelecting[A](val selected: Option[A], val selectedIndex: Option[Int], val updating: Boolean)

trait BindSelectedSupport[A, T] extends Observing {
  componentWithSelectSupport: SingleSelectSupport[A, T] =>
  def bindSelected(from: Option[A] => Unit, eventStream: EventStream[A]): SingleSelectSupport[A, T] = {
    componentWithSelectSupport.selectChangedEvent.foreach(from)
    eventStream.foreach(a => componentWithSelectSupport.setSelectedValue(a))
    componentWithSelectSupport
  }

  def bindSelected(from: Option[A] => Unit): SingleSelectSupport[A, T] = {
    componentWithSelectSupport.selectChangedEvent.foreach(from)
    componentWithSelectSupport
  }
}

abstract class SelectEventWrap[A, T <: Component](val component: T) {
  def singleSelect: SingleSelectSupport[A, T]
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
