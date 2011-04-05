package nielinjie
package util.ui
package example

import swing._
import event.{EditDone, SelectionChanged}

object Tables extends SimpleSwingApplication with ListAndText {
  def top = new MainFrame {
    title = "First Swing App"
    contents = new MigPanel("fill,debug", "[fill,:300:]", "[fill,:400:][]") {
      add(listView, "wrap")
      add(textBox, "wrap")
    }
  }

}

trait ListAndText {
  self: Tables.type =>

  import UISupport._

  val strings = List("a", "b", "c")
  val masterAndD = new MasterDetail(Model(strings))

  val listView = masterAndD.bindMasterTo(new ListView[String]())
  //masterAndD.supported(BindMaster(listView), )

  val textBox = new TextField()
  masterAndD.bindDetail {
    textBox.text = _
  }
  masterAndD.detailBack(textBox, {
    case (EditDone(`textBox`)) =>
      textBox.text
  })
}