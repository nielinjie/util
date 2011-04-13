package nielinjie
package util.ui
package example

import swing._
import event.{ButtonClicked, WindowClosed, EditDone, SelectionChanged}
import util.ui.event.{SelectSupport}
import reactive.{Var, Observing}

object Tables extends SimpleSwingApplication with ListAndText {

  import SelectSupport._

  def top = new MainFrame {
    title = "First Swing App"
    contents = new MigPanel("fill,debug", "[fill,:300:]", "[fill,:400:][][]") {
      add(listView, "wrap")
      add(textBox, "wrap")
      add(button, "wrap")
    }

  }

  reactions += {

    case WindowClosed(top) => {
      println("what?")
      System.exit(0)
    }
  }
  //  listenTo(top)

}

trait ListAndText extends Observing {
  self: Tables.type =>

  import SelectSupport._

  val model = Var(List("A", "B", "C"))
  val listView = new ListView[String](model.value).singleSelect
  model.change.foreach(listView.listData = _)
  listView.selectChanging.foreach {
    x =>
      println(x.toString)
  }
  listView.selected.change.foreach {
    x =>
      println(x.toString)
  }

  val textBox = new TextField()
  val button = new Button("Change")
  val buttonStream = button.eventStream({
    case ButtonClicked(_) =>
      "foo"
  })
  listView.bindSelected({
    x =>
      textBox.text = x.toString
  }, buttonStream)

  //  {
  //
  //    reactions += {
  //      case ButtonClicked(_) =>
  //      //        model()=List("foo","bar")
  //        listView.setSelectedValue("foo")
  //    }
  //
  //  }
}