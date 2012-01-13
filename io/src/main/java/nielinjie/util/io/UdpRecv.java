package nielinjie.util.io;  
import java.net.DatagramPacket;  
import java.net.DatagramSocket;  
public class UdpRecv {  
    public static void main(String[] args) throws Exception {  
//        DatagramSocket ds = new DatagramSocket(3001,LocalAddress.getFirstNonLoopbackAddress(true,false));// 创建接收数据报套接字并将其绑定到本地主机上的指定端口  
        DatagramSocket ds = new DatagramSocket(3001);// 创建接收数据报套接字并将其绑定到本地主机上的指定端口
        byte[] buf = new byte[1024];  
        DatagramPacket dp = new DatagramPacket(buf, 1024);  
        ds.receive(dp);  
        System.out.println("received");
        String strRecv = new String(dp.getData(), 0, dp.getLength()) + " from "  
                + dp.getAddress().getHostAddress() + ":" + dp.getPort();  
        System.out.println(strRecv);  
        ds.close();  
    }  
}  