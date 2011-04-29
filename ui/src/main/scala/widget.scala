package nielinjie
package util.ui

import swing.{RadioButton, TextComponent, Component}

object WidgetUtil {
  def group(components: Component*) = new WidgetGroup(components)


  class WidgetGroup(val components: Seq[Component]) {
    def enable(e: Boolean) = {
      components.foreach(_.enabled = e)
    }

    def clean = {
      components.foreach {
        case t: TextComponent => t.text = ""
        case r: RadioButton => r.selected = false
      }
    }

    def ++(b: WidgetGroup): WidgetGroup = new WidgetGroup(this.components ++ b.components)

    def ++(components: Component*): WidgetGroup = ++(new WidgetGroup(components))
  }

}