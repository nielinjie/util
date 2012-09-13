package nielinjie
package util.data

import scalaz._

import scala.util.control.Exception._
object LookUp {

  import Scalaz._
  import data._
  
  

  def headO[M[_], A](m: M[A])(implicit fa: Foldable[M]): Option[A] = {
    fa.foldLeft(m, None, {
      (listLike: Option[A], element: A) =>
        listLike match {
          case Some(s) => listLike
          case None => Option(element)
        }
    })
  }
  def lookUp[K](key: K) = {
    new WrappedLookingUp[K, Any, Any, Option]({
      x: MapLike[K, Any, Option] =>
        allCatch.either(x.get(key))
    })
  }

  class LP[V] {
    def apply[K](key: K) = new WrappedLookingUp[K, V, V, Option]({
      x: MapLike[K, V, Option] =>
//        success(x.get(key))
        allCatch.either(x.get(key))
    })
  }

  def lookUpFor[V] = new LP[V]

  class LPM[V] {
    def apply[K](key: K) = new WrappedLookingUp[K, V, V, List]({
      x =>
        allCatch.either(x.get(key))
    })
  }
  def lookUpMoreFor[V] = new LPM[V]

  trait MapLike[K, V, W[_]] {
    def get(key: K): W[V]
  }

  implicit def mapProjectFunction[K, A]: (Map[K, A], K) => Option[A] = {
    (m, k) => m.get(k)
  }

  

  type LookUpFunction[K, A, B, W[_]] = (MapLike[K, A, W] => Validation[String, B])
  
  
  /**
   * 
   */
  class LookingUp[K, A, B, W[_]](val exece: LookUpFunction[K, A, B, W]) {
    lu =>
      
    def map[C](f: B => C): LookingUp[K, A, C, W] = {
      new LookingUp({
        m: MapLike[K, A, W] =>
          exece(m).map(f)
      })
    }

    def flatMap[C](f: B => LookingUp[K, A, C, W]): LookingUp[K, A, C, W] = {
      new LookingUp({

        m: MapLike[K, A, W] =>
          val result = lu.exece(m)
          result.fold(failure(_), f(_).exece(m))
      })
    }

    def apply[M](m: M)(implicit projectionFunction: (M, K) => W[A]) = {
      val map = new MapLike[K, A, W] {
        def get(key: K): W[A] = projectionFunction(m, key)
      }
      exece(map)
    }
  }

}


