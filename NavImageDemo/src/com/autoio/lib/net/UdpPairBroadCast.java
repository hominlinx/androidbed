package com.autoio.lib.net;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import com.autoio.lib.util.Util;

import android.content.Context;
import android.util.Log;

public class UdpPairBroadCast extends Thread {
	private static final String TAG = "Hominlinx==>udpPairBroadCast";
	static byte[] dataV1 = { 0x55, 0x01, 0x01, 0x01 };
	static int PORT = 10220;
	
	String ssid="AndroidAP2";
	String separator = "\t";
	String pwd = "yzkj123456";
	
	public boolean quitFlag = false;
	Context m_context;
	public UdpPairBroadCast(Context context, String ssid, String pwd) {
		m_context = context;
		this.ssid = ssid;
		this.pwd = pwd;
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

		Log.d(TAG, "ssid:" + ssid.length() + "," + pwd.length());
		
    	
		byte[] tmp1 = ssid.getBytes();
		byte[] tmp2 = separator.getBytes();
		byte[] tmp3 = pwd.getBytes();
		int size = ssid.length() + separator.length() +  pwd.length();
		Log.d(TAG, "ssiddd:" + tmp1.length + "," + tmp2.length);
		byte[] tmpBuf = new byte[ size + 6];
		tmpBuf[0] = 0x55;
		tmpBuf[1] = 0x01;
		tmpBuf[2] = (byte) (size >> 24 & 0xff);
		tmpBuf[3] = (byte) (size >> 16 & 0xff);
		tmpBuf[4] = (byte) (size >> 8 & 0xff);
		tmpBuf[5] = (byte) (size & 0xff);
		System.arraycopy(tmp1, 0, tmpBuf, 6, ssid.length());
		System.arraycopy(tmp2, 0, tmpBuf, 6 + ssid.length(), separator.length());
		System.arraycopy(tmp3, 0, tmpBuf, 6+ssid.length() + separator.length(), pwd.length());
		Log.d(TAG, "XXXXXXXXXx, size:" + size);
		Log.d(TAG, "XXXXXXXXXXXXXXXX" + Util.bytesToHexString(tmpBuf, tmpBuf.length));
		
		while(!quitFlag) {
			try {  
				Thread.sleep(1000);  
			} catch (InterruptedException e) {  
				e.printStackTrace();  
			}  
			try {
				
				DatagramSocket dgSocket=new DatagramSocket(); 
				
				//DatagramPacket dgPacket=new DatagramPacket(dataV1,dataV1.length,InetAddress.getByName(host_ip),PORT);
				DatagramPacket dgPacket=new DatagramPacket(tmpBuf,tmpBuf.length,InetAddress.getByName("255.255.255.255"),PORT);
				dgSocket.send(dgPacket);  
				dgSocket.close();  
				Log.d(TAG, "udp pair broadcast ok....");

			} catch (Exception e) {  
				e.printStackTrace();  
			}  
		}
		
	}
}
