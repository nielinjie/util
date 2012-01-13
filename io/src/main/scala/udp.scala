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

class Flasher(sendPort: Int, receivePort: Int) {
  lazy val localAddress = LocalAddress.getFirstNonLoopbackAddress(true, false)
  lazy val boardcastAddress = LocalAddress.getBoardcastAddress(localAddress)
  println(boardcastAddress)
  val str = "1"
  def flash = {
    println("flash")
    val ds = new DatagramSocket(sendPort, localAddress)
    val dp = new DatagramPacket(str.getBytes(), str.getBytes().length, boardcastAddress, receivePort)
    ds.send(dp)
    ds.close()
  }
  def watch: FlashMessage = {
    println("now")
    val ds = new DatagramSocket(receivePort)
    println("new")
    val buf: Array[Byte] = Array.ofDim[Byte](1024)
    val dp = new DatagramPacket(buf, 1024)
    println("recieving")
    ds.receive(dp)
    println("recieved")
    val re = FlashMessage(new String(dp.getData(), 0, dp.getLength()), dp.getAddress())
    println("recieved - %s".format(re))
    ds.close()
    re
  }
  def keepWatching=new {
    import Threads._
    var messages:List[FlashMessage]=List()
    def start= thread ({while(true){messages=watch::messages}}).start
  }
  def keepFlashing(intveral:Int)= new Timer().schedule(new TimerTask{
    override def run ={
      flash
    }
  }, intveral, intveral);

}
case class FlashMessage(message: String, origin: InetAddress)
case class Peer(address:InetAddress)

object Demo extends App {
  val flasher = new Flasher(3002, 3001)
//  val message = promise {
//    Iterator.continually({
//      flasher.watch
//    }).toList
//  }
//  message.map {
//    list =>
//      println(list)
//  }
//  Thread.sleep(5000)
//  flasher.flash
//  flasher.flash
//  println("try to get")
//  println(message())
  val watching=flasher.keepWatching
  watching.start
  flasher.flash
  Thread.sleep(3000)
  println(watching.messages)
}