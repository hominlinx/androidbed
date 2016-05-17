package com.autoio.lib.net;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

import com.autoio.lib.util.Util;
import com.example.navimagedemo.SendService;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class UdpPairReceive extends Thread {
	private static final String TAG = "Hominlinx==>udpPairReceive";
	static byte[] dataV1 = { 0x55, 0x02, 0x41, 0x75, 0x74, 0x6F, 0x49, 0x4F, 0x43, 0x6F };
	String validData = Util.bytesToHexString(dataV1, 10);
	public final static int MSG_ID2 = 0x223;
	DatagramPacket dp;
	Handler handler = new Handler();
	public UdpPairReceive(Handler handler) {
		this.handler = handler;
	}
	
	@Override
	public void run() {
		Message msg;
		String information;
		boolean quitFlag = false; 
		Log.d(TAG, "XXXXXXXXXXXXXX:::");
		byte[] data = new byte[256];
		DatagramSocket dgSocket = null;
		try {
			dgSocket = new DatagramSocket(10220);
		} catch (SocketException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		while (!quitFlag) {
			try {


				dp =new DatagramPacket(data, data.length); 
				if (dgSocket != null) {
					dgSocket.receive(dp);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (dp.getAddress() != null) {
				final String quest_ip = dp.getAddress().toString();

				String host_ip = Util.getLocalHostIp(); 



				/* 若udp包的ip地址 是 本机的ip地址的话，丢掉这个包(不处理)*/

				if( (!host_ip.equals(""))  && host_ip.equals(quest_ip.substring(1)) ) {
					continue;
				}

				System.out.println("host_ip:  --------------------  " + host_ip);
				System.out.println("quest_ip: --------------------++++  " + quest_ip);
				System.out.println("quest_ip: --------------------  " + quest_ip.substring(1));

				Log.d(TAG, "hostip:" + host_ip + ",questip:" + quest_ip.substring(1));

		
				String strData = Util.bytesToHexString(data, dp.getLength());
				Log.d(TAG, "XXXXXXXXXXXXXX:::" + strData);
				if (validData.equals(strData)) {
					msg = new Message();
					msg.what = MSG_ID2;
					handler.sendMessage(msg);
					dgSocket.close();
					quitFlag = true;
				}
			
			}
		}
	}
	
	
	
}
