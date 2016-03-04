package wu.lib.util;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

import wu.testandroidscreenshot.SendService;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 
 * 接收udp广播 并 发送tcp 连接
 * 
 * @author hominlinx
 *
 */
public class udpReceiveAndtcpConnect extends Thread {
	private static final String TAG = "Hominlinx==>udpReceiveAndtcpConnect";
	DatagramPacket dp;
	Handler handler = new Handler();
	private Client user=null;
	
	public udpReceiveAndtcpConnect(Handler handler, Client user) {
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

				String host_ip = getLocalHostIp();

				System.out.println("host_ip:  --------------------  " + host_ip);
				System.out.println("quest_ip: --------------------++++  " + quest_ip);
				System.out.println("quest_ip: --------------------  " + quest_ip.substring(1));

				/* 若udp包的ip地址 是 本机的ip地址的话，丢掉这个包(不处理)*/

				if( (!host_ip.equals(""))  && host_ip.equals(quest_ip.substring(1)) ) {
					continue;
				}


				boolean ret = checkRecvData(data, quest_ip.substring(1), dp.getLength());
				if (ret) { // get valid UDP...
					user.open(quest_ip.substring(1), 32550); //tcp connect...
					if (user.isNeedConn()) {
						Log.d(TAG, "tcp need connect....");
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
	
	
	private String getLocalHostIp() {
        String ipaddress = "";
        try {
            Enumeration<NetworkInterface> en = NetworkInterface
                    .getNetworkInterfaces();
            // 遍历所用的网络接口
            while (en.hasMoreElements()) {
                NetworkInterface nif = en.nextElement();// 得到每一个网络接口绑定的所有ip
                Enumeration<InetAddress> inet = nif.getInetAddresses();
                // 遍历每一个接口绑定的所有ip
                while (inet.hasMoreElements()) {
                    InetAddress ip = inet.nextElement();
                    if (!ip.isLoopbackAddress()
                            && InetAddressUtils.isIPv4Address(ip
                            .getHostAddress())) {
                        return ip.getHostAddress();
                    }
                }
            }
        }
        catch(SocketException e)
        {
            Log.e(TAG, "获取本地ip地址失败");
            e.printStackTrace();
        }
        return ipaddress;
    }
	
	/*
	 * 检测接收UDP数据的合理性。
	 * 接收数据：2Byte(00 02) 4Byte( IP 地址的 16 进制表示) 1Byte( 00--Ready（or 01--Busy）)
	 */
	private boolean checkRecvData(byte[] data, String ip, int len) {
		
		if ( data.length < 7 && len != 7) {
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
		byte[] validData = new byte[7];
		validData[0] = 0x00;
		validData[1] = 0x02;
		byte[] ip = convertIpTobytes(strIp);
		if (ip.length != 4) {
			return null;
		}
		for (int i = 0; i < 4; ++i) {
			validData[2+i] = ip[i];
		}
		validData[6] = 0x00;

		return Util.bytesToHexString(validData, validData.length);
	}
	public static byte[] convertIpTobytes(String strIp) {
		if (strIp == null) {
			return null;
		}
		byte[] ip = new byte[4];
		String [] ipb=strIp.split("\\.");
		ip[0]=(byte)Integer.parseInt(ipb[0]);
		ip[1] = (byte)Integer.parseInt(ipb[1]);
		ip[2] = (byte)Integer.parseInt(ipb[2]);
		ip[3] = (byte)Integer.parseInt(ipb[3]);
		return ip;

	}
}
