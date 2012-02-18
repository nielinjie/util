package nielinjie
package util.io

object Threads {
  def thread[F](f: => F) = (new Thread(new Runnable() { def run() { f } }))
  def start[F](f: => F) = thread(f).start
}