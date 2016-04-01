package com.autoio.lib.net;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

import com.autoio.lib.util.Util;
import com.example.navimagedemo.SendService;




import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 
 * 接收udp广播 并 发送tcp 连接
 * 
 * @author hominlinx
 */
public class UdpReceiveAndTcpConnect extends Thread {
	private static final String TAG = "Hominlinx==>udpReceiveAndtcpConnect";
	DatagramPacket dp;
	Handler handler = new Handler();
	private Client user=null;

	public UdpReceiveAndTcpConnect(Handler handler, Client user) {
		this.handler = handler;
		this.user = user;
	}

	@Override
	public void run() {
		Message msg;
		String information;
		boolean quitFlag = false; 

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

				boolean ret = checkRecvData(data, quest_ip.substring(1), dp.getLength());
				//boolean ret = true; //FIXME
				if (ret) { // get valid UDP...
					user.open(quest_ip.substring(1), 32550); //tcp connect...

					while(user.isNeedConn()) {
						Log.d(TAG, "tcp need connect....");

						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} 
						user.reconn();
					}

					msg = new Message();
					msg.what = SendService.MSG_ID1;
					handler.sendMessage(msg);
					quitFlag = true;
				}
			}
		}
	}


	/*
	 * 检测接收UDP数据的合理性。
	 * 接收数据：2Byte(00 02) 4Byte( IP 地址的 16 进制表示) 1Byte( 00--Ready（or 01--Busy）)
	 */
	private boolean checkRecvData(byte[] data, String ip, int len) {

		if ( data.length < 9 || len != 9) {
			Log.d(TAG, "udp data's len is " + len + ", ERROR");
			return false;
		}

		// 将data组合为string
		String str = Util.bytesToHexString(data, len);
		
		//
		String validStr = getValidData(ip);
		Log.d(TAG, "checkRecvData getData:" + str + ",validStr:" + validStr);
		if (validStr.equals(str)) {
			return true;
		}

		return false;
	}


	public static String getValidData(String strIp) {
		byte[] validData = new byte[9];
		validData[0] = 0x00;
		validData[1] = 0x02;
		validData[2] = 0x06;
		byte[] ip = Util.convertIpTobytes(strIp);
		if (ip.length != 4) {
			return null;
		}
		for (int i = 0; i < 4; ++i) {
			validData[3+i] = ip[i];
		}
		validData[7] = 0x00;
		validData[8] = 0x01;

		return Util.bytesToHexString(validData, validData.length);
	}

}
