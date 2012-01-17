package nielinjie.util
package io
import com.weiglewilczek.slf4s.Logging

import scalaz._
import Scalaz._

trait Log extends Logging {
  def debug[A: Show](any: => A) = logger.debug(any.shows)
  def info[A: Show](any: => A) = logger.info(any.shows)
  def warn[A: Show](any: => A) = logger.warn(any.shows)
  def error[A: Show](any: => A) = logger.error(any.shows)
}
trait Logger extends Logging