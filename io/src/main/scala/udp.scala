package nielinjie
package util.io
import java.net.DatagramSocket
import java.net.DatagramPacket
import java.net.InetAddress
import scala.actors.Futures
import scalaz._
import Scalaz._
import java.util.Timer
import java.util.TimerTask
import reactive.Val
import reactive.SeqSignal
import reactive.Var
import reactive.Observing

class Flasher(sendPort: Int, receivePort: Int, message: String) extends Logger {
  lazy val localAddress = LocalAddress.getFirstNonLoopbackAddress(true, false)
  lazy val boardcastAddress = LocalAddress.getBoardcastAddress(localAddress)
  logger.debug(boardcastAddress.toString)
  //val str = "1"
  def flash = {
    logger.debug("flash")
    val ds = new DatagramSocket(sendPort, localAddress)
    val dp = new DatagramPacket(message.getBytes(), message.getBytes().length, boardcastAddress, receivePort)
    ds.send(dp)
    ds.close()
  }
  def watch: FlashMessage = {
    //TODO move new socket to keepWatching start?
    val ds = new DatagramSocket(receivePort)
    val buf: Array[Byte] = Array.ofDim[Byte](1024)
    val dp = new DatagramPacket(buf, 1024)
    logger.debug("recieving")
    ds.receive(dp)
    logger.debug("recieved")
    val re = FlashMessage(new String(dp.getData(), 0, dp.getLength()), dp.getAddress())
    logger.debug("recieved - %s".format(re))
    ds.close()
    re
  }
  def keepWatching = new {
    import Threads._
    val msges = Var(List[FlashMessage]())
    val messages = SeqSignal(msges)
    def start = thread({ while (true) { msges() = watch :: msges.now } }).start
  }
  def keepFlashing(intveral: Int) = new Timer().schedule(new TimerTask {
    override def run = {
      flash
    }
  }, intveral, intveral);

}
case class FlashMessage(message: String, origin: InetAddress)
case class Peer(address: InetAddress)

object Demo extends App with Logger with Observing {
  val flasher = new Flasher(3002, 3001, "1")
  val watching = flasher.keepWatching
  watching.messages.deltas.foreach {
    case d => {
      logger.debug(d.toString)
    }
  }
  watching.start
  Thread.sleep(1000)
  flasher.flash

}