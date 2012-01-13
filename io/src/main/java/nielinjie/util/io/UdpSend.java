package nielinjie.util.io;

import java.io.IOException;  
import java.net.DatagramPacket;  
import java.net.DatagramSocket;  
import java.net.InetAddress;  
import java.net.SocketException;  
import java.net.UnknownHostException;  
import java.text.SimpleDateFormat;  
import java.util.Date;  
import java.util.Timer;  

public class UdpSend {  
    public void sendData()throws SocketException,  
    UnknownHostException{  
        DatagramSocket ds = new DatagramSocket(3002,LocalAddress.getFirstNonLoopbackAddress(true, false));// 创建用来发送数据报包的套接字  
        String str = "1";  
        
        InetAddress boardcastAddress = LocalAddress.getBoardcastAddress(LocalAddress.getFirstNonLoopbackAddress(true,false));
        System.out.println(boardcastAddress.toString());
		DatagramPacket dp = new DatagramPacket(str.getBytes(),  
                str.getBytes().length,  
                boardcastAddress, 3001);  
        // 构造数据报包，用来将长度为 length 的包发送到指定主机上的指定端口号  
        try {  
            ds.send(dp);  
            System.out.println("sent");
        } catch (IOException e) {  
            e.printStackTrace();  
        }  
        ds.close();  
          
    }  
    public static void main(String[] args) {  
        Timer timer = new Timer();  
        timer.schedule(new MyTask(), 3000, 3000);
    }  
    static class MyTask extends java.util.TimerTask{   
        @Override  
        public void run() {   
            UdpSend tt = new UdpSend();  
//            Date d = new Date();  
//            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");  
//            String strdate = sdf.format(d);  
//            String[] classTime = {"17:18:00","17:19:00","17:20:00"};  
//            for(int i = 0;i<classTime.length;i++){  
//                 if(classTime[i].equals(strdate)){  
                     try {  
                        tt.sendData();  
                
                    } catch (SocketException e) {  
                        // TODO Auto-generated catch block  
                        e.printStackTrace();  
                    } catch (UnknownHostException e) {  
                        // TODO Auto-generated catch block  
                        e.printStackTrace();  
                    }  
////                 }  
//            }     
        }  
    }  
}  