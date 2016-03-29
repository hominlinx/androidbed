package com.autoio.lib.net;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.autoio.lib.util.Util;


import android.util.Log;

/**
 *  
 * 发送udp多播
 * Discover 数据帧
 */
public class UdpBroadCast extends Thread {
	private static final String TAG = "Hominlinx==>udpBroadCast";
	static byte[] dataV1 = { 0x00, 0x01, 0x01, 0x01 };
	static int PORT = 10220;
	public boolean quitFlag = false;
	@Override
	public void run() {
		String host_ip = Util.modifyIP(Util.getLocalHostIp()); 
		Log.d(TAG, "ip:" + host_ip);

		while(!quitFlag) {
			try {  
				Thread.sleep(1000);  
			} catch (InterruptedException e) {  
				e.printStackTrace();  
			}  
			try {

				DatagramSocket dgSocket=new DatagramSocket(); 

				//DatagramPacket dgPacket=new DatagramPacket(data,data.length,InetAddress.getByName(host_ip),PORT);
				DatagramPacket dgPacket=new DatagramPacket(dataV1,dataV1.length,InetAddress.getByName("255.255.255.255"),PORT);


				dgSocket.send(dgPacket);  
				dgSocket.close();  
				Log.d(TAG, "udp broadcast ok....");

			} catch (Exception e) {  
				e.printStackTrace();  
			}  
		}

	}

}
