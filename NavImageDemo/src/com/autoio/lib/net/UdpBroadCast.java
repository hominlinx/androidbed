package com.autoio.lib.net;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.autoio.lib.util.Util;


import android.content.Context;
import android.os.Handler;
import android.os.Message;
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
	Context m_context;
	UdpReceiveAndTcpConnect udpReceive = null;
	public UdpBroadCast(Context context) {
		m_context = context;
//		udpReceive =  new UdpReceiveAndTcpConnect(handler_for_udpReceiveAndtcpSend, null);
//		udpReceive.start();
	}
	@Override
	public void run() {
		String host_ip = "";
		if (Util.isApEnabled(m_context)) {
			host_ip = "192.168.43.255";
		} else {
			host_ip = Util.modifyIP(Util.getLocalHostIp());
		}
		Log.d(TAG, "ap:" + Util.isApEnabled(m_context));
		
		Log.d(TAG, "ip:" + host_ip);

		while(!quitFlag) {
			try {  
				Thread.sleep(1000);  
			} catch (InterruptedException e) {  
				e.printStackTrace();  
			}  
			try {

				Log.d(TAG, "app:" + Util.isApEnabled(m_context));
				
				Log.d(TAG, "ipp:" + host_ip);
				
				DatagramSocket dgSocket=new DatagramSocket(); 

				DatagramPacket dgPacket=new DatagramPacket(dataV1,dataV1.length,InetAddress.getByName(host_ip),PORT);
				//DatagramPacket dgPacket=new DatagramPacket(dataV1,dataV1.length,InetAddress.getByName("255.255.255.255"),PORT);
				dgSocket.send(dgPacket);  
				dgSocket.close();  
				Log.d(TAG, "udp broadcast ok......");

				
				
			} catch (Exception e) {  
				e.printStackTrace();  
			}  
		}

	}

//	 public final static int MSG_ID1 = 0x222;
//	    Handler handler_for_udpReceiveAndtcpSend = new Handler() {
//	    	@Override
//	    	public void handleMessage(Message msg) {
//	    		super.handleMessage(msg);
//	    		if (msg.what == MSG_ID1) { // connect ok...
//	    			Log.d(TAG, "Connect ok....");
//	    			//stopUDP();
//	    			quitFlag  = true;
//	    			//tcpSend.start();
//	             }
//	    		
//	    	}
//	    };	 
}
