package nielinjie
package util.ui
package event


import scalaz._
import Scalaz._
import swing.event.SelectionChanged
import reactive.{EventStream, Observing, Signal, EventSource}
import swing.{Component, ListView}


object SelectSupport {

  implicit def singleSelectSupport2Component[A, T](singleSelectSupport: SingleSelectEventSupport[A, T]): T = singleSelectSupport.component

  implicit def listView2SelectSupport[A](listView: ListView[A]) = new SelectEventWrap[A, ListView[A]](listView) {
    def singleSelectionEvents = new ListViewSingleSelectSupport[A](listView)
  }

  class ListViewSingleSelectSupport[A](val listView: ListView[A]) extends SingleSelectEventSupport[A, ListView[A]](listView) {
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

}


abstract class SingleSelectEventSupport[A, T](val component: T) {
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


abstract class SelectEventWrap[A, T <: Component](val component: T) {
  def singleSelectionEvents: SingleSelectEventSupport[A, T]
}