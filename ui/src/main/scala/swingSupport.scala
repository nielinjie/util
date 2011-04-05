package nielinjie
package util.ui

import swing.{Component, ListView}
import swing.event.{Event, SelectionChanged}


object UISupport {
  implicit def masterDetailToBindable[A](masterAndDetail: MasterDetail[A]): CanBindToList[A] = {
    new CanBindToList(masterAndDetail)
  }
}

//class Supported {
//  def supported(support:Support)
//}
//trait Support{
//  def support
//}
//case class BindMaster(val component)

class CanBindToList[A](val masterAndDetail: MasterDetail[A]) {
  def bindMasterTo(listView: ListView[A]) = {
    listView.listData = masterAndDetail.master.value
    listView.listenTo(listView.selection)
    listView.reactions += {
      case SelectionChanged(`listView`) => {
        if (listView.selection.adjusting) {
          masterAndDetail.select(listView.selection.items(0))
        }
      }
    }
    listView.selectIndices(0)
    listView.selection.intervalMode = ListView.IntervalMode.Single
    listView
  }

  def bindDetail(f: A => Unit) {
    masterAndDetail.onChange {
      (oldV, newV) =>
        newV.foreach {
          sel =>
            f(sel.value.get)
        }
    }
  }

  def detailBack(component: Component, reactions: PartialFunction[Event, Unit]*) {
    reactions.foreach {
      component.reactions += _
    }
  }
}