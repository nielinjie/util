package nielinjie
package util.ui

import swing.{ToggleButton, RadioButton, TextComponent, Component}

object WidgetUtil {
  def group(components: Component*) = new WidgetGroup(components)


  class WidgetGroup(val components: Seq[Component]) {
    def enable(e: Boolean) = {
      components.foreach(_.enabled = e)
    }

    def clean = {
      components.foreach {
        case t: TextComponent => t.text = ""
        case r: ToggleButton => r.selected = false
        case _ =>
      }
    }

    def ++(b: WidgetGroup): WidgetGroup = new WidgetGroup(this.components ++ b.components)

    def ++(components: Component*): WidgetGroup = ++(new WidgetGroup(components))
  }

}
//object SwingUtil {
//  impl
//}