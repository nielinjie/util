package nielinjie
package util.ui
package event

import reactive.EventSource
import swing.event.SelectionChanged
import swing.{Publisher, Component, ListView}

object SelectSupport {
  implicit def listViewSelectable[A] = new Selectable[A, ListView] {
    def wrap(listView: ListView[A]) = new ComponentWithSelectSupport[A, ListView[A]](listView) {
      listView.selection.intervalMode = ListView.IntervalMode.Single
      listView.reactions += {
        case SelectionChanged(`listView`) =>
          if (!listView.selection.adjusting) {
            selectChangedEvent.fire(listView.selection.items(0))
          }
      }
    }
  }


  def singleSelect[A, C[A] <: Component](component: C[A])(implicit selectable: Selectable[A, C]): ComponentWithSelectSupport[A, C[A]] =
    selectable.wrap(component)

  implicit def componentWithSelectSupport2Component[A, T](componentWithSelectSupport: ComponentWithSelectSupport[A, T]): T = componentWithSelectSupport.component
//  implicit def component2SelectSupport[A,C[A]<: Component](component:C[A])(implicit selectable: Selectable[A, C])=
//    new componentWrap(component,selectable)
////  class componentWrap[A, C[A] <: Component](val component:C[A],val selectable: Selectable[A, C]){
////    import SelectSupport._
////    def singleSelect(component: C[A])=selectable.wrap(component)
////  }
}

class ComponentWithSelectSupport[A, T](val component: T) {
  val selectChangedEvent: EventSource[A] = new EventSource[A]() {}
}

trait Selectable[A, C[A] <: Component] {
  def wrap(component: C[A]): ComponentWithSelectSupport[A, C[A]]
}
