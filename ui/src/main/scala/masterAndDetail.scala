package nielinjie
package util.ui

import scalaz._
import Scalaz._

class MasterDetail[A](_master: List[A]) {
  private var master: List[A] = _master

  def check = {
    println(master)
    println(selection)
  }

  var selection: Selection[A] = new Selection[A](None, None)
  var detailBind: Option[Bind[Option[A]]] = None
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
        b.push(this.selection.value.success)
    }

  }


  def saveDetail = {
    this.detailBind.foreach {
      b =>
        if (this.selection.selected) {
          this.selection.value = b.pull(this.selection.value)
        }
    }
    this.master = this.selection.saveToMaster(this.master)
    masterBind.foreach {
      b =>
        b.push((this.master).success)
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
        b.push(this.master.success)
    }
  }

  def getMaster = this.master
}



import scala.swing._
import reactive._

object SwingSupport extends Observing {
  def bindToListView[A](masterDetail: MasterDetail[A], listView: ListView[A]) = {
    import event.SelectSupport._
    listView.listData = masterDetail.getMaster
    val selectSupport = listView.singleSelect
    masterDetail.masterBind = Some(Bind(({
      //TODO add some logic to handle error?
      olist => olist.fold({
        x =>
      }, selectSupport.updateKeepingSelect(_))
    }), {
      list => Nil
    }))

    selectSupport.selectIndexChangedEvent.foreach {
      selIndex =>
        masterDetail.select(selIndex)
    }
  }
}
