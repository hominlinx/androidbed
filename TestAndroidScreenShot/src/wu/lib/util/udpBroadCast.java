package wu.lib.util;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import android.util.Log;

/**
 *  
 * 发送udp多播
 * Discover 数据帧
 */

public class udpBroadCast extends Thread {
	private static final String TAG = "Hominlinx==>udpBroadCast";
	static byte[] data = { 0x00, 0x01, 0x41, 0x75, 0x74, 0x6F, 0x49, 0x4F, 0x43, 0x6F };
	static int PORT = 10220;
	public boolean quitFlag = false;
	@Override
	public void run() {
		while(!quitFlag) {
			try {  
                Thread.sleep(1000);  
            } catch (InterruptedException e) {  
                e.printStackTrace();  
            }  
			try {
				DatagramSocket dgSocket=new DatagramSocket(); 
				DatagramPacket dgPacket=new DatagramPacket(data,data.length,InetAddress.getByName("255.255.255.255"),PORT);
				dgSocket.send(dgPacket);  
	            dgSocket.close();  
	            Log.d(TAG, "udp broadcast ok....");
	            
			} catch (Exception e) {  
                e.printStackTrace();  
            }  
		}
		
	}

}
