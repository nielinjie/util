package nielinjie
package util.ui

import collection.mutable.ListBuffer

class Model[A](value_ : A, val bind: Option[Bind[A]]) extends Changable[A] {
  private var value: A = value_

  def set(f: A => A) = {
    val oldValue = this.value
    val newValue = f(this.value)
    this.value = newValue
    this.bind.foreach {
      b =>
        b.push(Option(this.value))
    }
    notifyChangeListener(oldValue, this.value)
  }

  def get = {
    this.bind.foreach {
      b =>
        this.value = b.pull(this.value)
    }
    this.value
  }

}

case class Bind[A](val push: (Option[A] => Unit), val pull: (A => A))

case class Selection[A](var value: Option[A], var index: Option[Int]) {
  def saveToMaster(master: List[A]): List[A] = {
    index.map {
      i =>
        master.updated(i, value.get)
    }.getOrElse(master)
  }

  def selected = !(this.index.isEmpty)
}


class MasterDetail[A](_master: List[A]) {
  private var master: List[A] = _master

  def check = {
    println(master)
    println(selection)
  }

  var selection: Selection[A] = new Selection[A](None, None)
  var detailBind: Option[Bind[A]] = None
  var masterBind: Option[Bind[List[A]]] = None

  def selectNone() = {
    //TODO support none selection binding
    this.selection.value = None
    this.selection.index = None
  }


  def select(index: Option[Int]): Unit = {

    val oldSelection = this.selection

    saveDetail
   
    this.selection.index = index
    this.selection.value = index.map(master.apply(_))
    this.detailBind.foreach {
      b =>
        b.push(this.selection.value)
    }

  }


  def saveDetail = {
    this.detailBind.foreach {
      b =>
        if (this.selection.selected) {
          this.selection.value = Some(b.pull(this.selection.value.get))
        }
    }
    this.master = this.selection.saveToMaster(this.master)
    masterBind.foreach {
      b =>
        b.push(Option(this.master))
    }
  }

  def setMaster(list: List[A]) = {
//    println("befor setMaster's saveDetail")
//    this.check
    saveDetail
//    println("after setMaster's saveDetail")
//    this.check
    this.selectNone
    this.master = list
    masterBind.foreach {
      b =>
//        println("in setmaster's for each")
        b.push(Option(this.master))
    }
  }
  def getMaster=this.master
}

trait Changable[T] {
  val onChanges: ListBuffer[(T, T) => Unit] = new ListBuffer()

  def notifyChangeListener(oldValue: T, newValue: T): Unit = {
    onChanges.foreach {
      f =>
        f(oldValue, newValue)
    }
  }

  def onChange(f: (T, T) => Unit) = {
    this.onChanges.append(f)
  }

}

import scala.swing._
import reactive._

object SwingSupport extends Observing {
  def bindToListView[A](masterDetail: MasterDetail[A], listView: ListView[A]) = {
    import event.SelectSupport._
    listView.listData = masterDetail.getMaster
    val selectSupport = listView.singleSelect
    masterDetail.masterBind = Some(Bind({
      olist => selectSupport.updateKeepingSelect(olist.get)
    }, {
      list=>Nil
    }))

    selectSupport.selectIndexChangedEvent.foreach {
      selIndex =>
        masterDetail.select(selIndex)
    }
  }
}


