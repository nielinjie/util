package nielinjie
package util.ui
package event

import swing.ListView
import reactive.EventSource
import swing.event.SelectionChanged

object SelectSupport {
  def singleSelect[A](listView: ListView[A]): ComponentWithSelectSupport[ListView[A], A] =
    new ComponentWithSelectSupport[ListView[A], A](listView) {
      listView.selectIndices(0)
      listView.selection.intervalMode = ListView.IntervalMode.Single
      listView.listenTo(listView.selection)
      listView.reactions += {
        case SelectionChanged(`listView`) => {
          if (listView.selection.adjusting) {
            selectChangedEvent.fire(listView.selection.items(0))
          }
        }
      }
    }

}

class ComponentWithSelectSupport[T, A](val component: T) {
  val selectChangedEvent: EventSource[A] = new EventSource[A]() {}
}