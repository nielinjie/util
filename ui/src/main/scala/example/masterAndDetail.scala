package nielinjie
package util.ui
package example

import swing._
import event._
import reactive._

object MasterAndDetail extends SimpleSwingApplication with Observing {
  def top = new MainFrame {
    title = "First Swing App"
    contents = new MigPanel("fill,debug", "[fill,:300:][fill,:300:]", "[fill,:400:]") {
      add(new MigPanel("fill,debug", "[fill,:300:]", "[fill,:400:][][fill,:400:][][]") {
        add(listPerson, "wrap")
        add(textAge, "wrap")
        add(listName, "wrap")
        add(textF, "wrap")
        add(textL, "")
      }, "")
      add(new MigPanel("fill,debug", "[fill,:300:]", "[fill,:400:][fill,:400:][][]") {

      }, "")
    }

  }

  reactions += {

    case WindowClosed(top) => {
      println("closing")
      System.exit(0)
    }
  }

  case class Person(age: Int, names: List[Name])

  case class Name(first: String, last: String)

  val persons = List(Person(32, List(Name("nie", "jason"), Name("NIE", "JASON"))), Person(27, List(Name("xu", "elsa"), Name("XU", "234"))))
  val mdPerson = new MasterDetail(persons)

  import Bind._

  mdPerson.detailBind = Some(Bind(noErrorHandle({
    a: Option[Person] => a match {
      case o@Some(aa) => {
        mdName.setMaster(aa.names)
        textAge.text = aa.age.toString
      }
      //TODO logic when None
      case o@None =>
    }
  })
  , {
    person => {
      mdName.saveDetail
      Some(Person(textAge.text.toInt, mdName.getMaster))
    }
  }
  ))

  val mdName = new MasterDetail[Name](Nil)
  mdName.detailBind = Some(Bind(noErrorHandle({
    o: Option[Name] => o match {
      case Some(n) => {
        textF.text = n.first
        textL.text = n.last
      }
      case None => {
        //TODO disable when nothe selected
      }
    }
  })
  , {
    name => Some(Name(textF.text, textL.text))
  }))

  val textAge = new TextField("age")
  val listPerson = new ListView[Person]()
  val listName = new ListView[Name]()

  val textF = new TextField("f")
  val textL = new TextField("l")


  import SwingSupport._

  bindToListView(mdPerson, listPerson)
  bindToListView(mdName, listName)
}