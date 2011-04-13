package nielinjie
package util.ui
package event

import swing.{Table, Publisher, Component, ListView}
import reactive._
import swing.event.{Event, SelectionChanged}

object SelectSupport {

  implicit def componentWithSelectSupport2Component[A, T](componentWithSelectSupport: ComponentWithSingleSelectSupport[A, T]): T = componentWithSelectSupport.component

  implicit def componentWithSelectSupport2BindSelectedSupported[A, T](componentWithSelectSupport: ComponentWithSingleSelectSupport[A, T]): BindSelectedSupport[A, T] = new BindSelectedSupport(componentWithSelectSupport)
  implicit def component2GeneralEventStreamSupport(component:Component): GeneralEventStreamSupport = new GeneralEventStreamSupport(component)

  implicit def listView2SelectSupport[A](listView: ListView[A]) = new SelectableWrap[A, ListView[A]](listView) {
    def singleSelect = new ListViewWithSingleSelectSupport[A](listView)
  }

}

class ListViewWithSingleSelectSupport[A](val listView: ListView[A]) extends ComponentWithSingleSelectSupport[A, ListView[A]](listView) {
  listView.selection.intervalMode = ListView.IntervalMode.Single
  listView.selection.reactions += {
    case SelectionChanged(`listView`) =>
      if (!listView.selection.adjusting) {
        selectChanging.fire(SingleSelecting(listView.selection.items.headOption, updating))
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

  //  def onModelChange(seqDelta:SeqDelta){
  //    seqDelta match {
  //      case Update(index,_,newV) =>
  //
  //      case Remove(index,_)
  //      case Include(index,newV)
  //    }
  //  }
}

abstract class ComponentWithSingleSelectSupport[A, T](val component: T) {
  var updating = false
  val selectChanging: EventSource[SingleSelecting[A]] = new EventSource[SingleSelecting[A]]() {}
  val selectChangedEvent = selectChanging.filter({
    e =>
      !e.updating
  }).map({
    e =>
      e.selected
  })
  val selected: Signal[Option[A]] = selectChangedEvent.hold(None)

  def setSelectedValue(value: A)

  //  //TODO, refactory to set delta?
  //  def onModelChange(seqDelta:SeqDelta)
}

case class SingleSelecting[A](val selected: Option[A], val updating: Boolean)

class BindSelectedSupport[A, T](val componentWithSelectSupport: ComponentWithSingleSelectSupport[A, T]) extends Observing {

  def bindSelected(from: Option[A] => Unit, eventStream: EventStream[A]): ComponentWithSingleSelectSupport[A, T] = {
    componentWithSelectSupport.selectChangedEvent.foreach(from)
    eventStream.foreach(a => componentWithSelectSupport.setSelectedValue(a))
    componentWithSelectSupport
  }
  def bindSelected(from: Option[A] => Unit): ComponentWithSingleSelectSupport[A, T] = {
    componentWithSelectSupport.selectChangedEvent.foreach(from)
    componentWithSelectSupport
  }
}

abstract class SelectableWrap[A, T <: Component](val component: T) {
  def singleSelect: ComponentWithSingleSelectSupport[A, T]
}

class GeneralEventStreamSupport(val component:Component) {
  def eventStream[A](f:PartialFunction[Event,A])={
    val eventStream=new EventSource[A](){}
    component.reactions+=f.andThen(a => eventStream.fire(a))
    eventStream
  }
}
