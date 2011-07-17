package nielinjie
package util.ui

import scalaz._
import Scalaz._

case class SingleSelection[A](var value: Option[A], var index: Option[Int]) {
  def saveToMaster(master: List[A], filtered: Option[List[(A, Int)]]): List[A] = {
    //println(master)
    //println(filtered)
    val re = index.map {
      i =>
        master.updated(filtered.map(_.apply(i)._2).getOrElse(i), value.get)
    }.getOrElse(master)
    //println(re)
    re
  }

  def selected = !(this.index.isEmpty)
}


class MasterDetail[A](_master: List[A]) {
  private var master: List[A] = _master

  //  private var filter: Option[Filter[A]] = None
  //  private var filteredMaster:List[A]=_master
  class FilterAndFiltered(filter: Filter[A]) {

    def filtered: List[(A, Int)] = filter.apply(master)

    def filteredMaster: List[A] = filtered.map(_._1)
  }

  private var filterAndFiltered: Option[FilterAndFiltered] = None

  def check = {
    println(master)
    println(selection)
  }

  var selection: SingleSelection[A] = new SingleSelection[A](None, None)
  var detailBind: Option[Bind[Option[A]]] = None
  var masterBind: Option[Bind[List[A]]] = None

  def selectNone() = {
    //TODO support none selection binding
    select(None)
  }


  def select(index: Option[Int]): Unit = {

    val oldSelection = this.selection
    if (this.selection.selected) {
      saveDetail
    }
    this.selection.index = index
    this.selection.value = index.map(filteredMaster.apply(_))
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
    this.master = this.selection.saveToMaster(this.master, this.filterAndFiltered.map(_.filtered))
    fireMasterBind()
  }

  def setMaster(list: List[A]) = {
    //    println("befor setMaster's saveDetail")
    //    this.check
    saveDetail
    //    println("after setMaster's saveDetail")
    //    this.check
    this.selectNone
    this.master = list

    fireMasterBind()
  }

  private def fireMasterBind() = {
    masterBind.foreach {
      b =>
        b.push(filteredMaster.success)
    }
  }

  private def filteredMaster = this.filterAndFiltered.map(_.filteredMaster).getOrElse(this.master)

  def setFilter(filter: Option[Filter[A]]) = {
    saveDetail
    this.selectNone
    this.filterAndFiltered = filter.map(new FilterAndFiltered(_))
    fireMasterBind()
  }

  def getMaster = this.master
}

trait Filter[A] {
  /**
   * @return list of (object, originIndex)
   */
  self =>
  def filter(a: A): Boolean

  def apply(master: List[A]): List[(A, Int)] = {
    master.zipWithIndex.filter({
      aI: (A, Int) =>
        filter(aI._1)
    })
  }

  def add(b: Filter[A]) = {
    new Filter[A] {
      def filter(obj: A) = self.filter(obj) && b.filter(obj)
    }
  }
}

import scala.swing._
import reactive._

object SwingSupport extends Observing {
  def bindToListView[A](masterDetail: MasterDetail[A], listView: ListView[A]) = {
    import event.SelectSupport._
    listView.listData = masterDetail.getMaster
    val selectSupport = listView.singleSelectionEvents
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
