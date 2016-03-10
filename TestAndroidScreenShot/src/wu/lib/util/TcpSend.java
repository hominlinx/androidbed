package wu.lib.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.graphics.Bitmap;
import android.util.Log;


/**
 * 发送文件线程
 * 
 */
public class TcpSend extends Thread{	
	private static final String TAG = "Hominlinx===>TcpSend";
	private boolean quitFlag = false;
	Client user;
	private static int PACKSIZE = 2048;
	public void stopTcp() {
		quitFlag = true;
	}
	
	public TcpSend(Client user) {
		this.user = user;
	}
	
	public void run() {
		Log.d(TAG, "sendfile thread run");
		int i = 0;
		while (!quitFlag) {
			
			byte[] buf = getShot();
			sendFrameStart(buf.length);
			
			sendFrameData(buf);
			
			sendFrameEnd();
			
//			if (i == 1) {
//				quitFlag = true;
//			}
//			i++;
			//screenShot(FILE_NAME);
			//Util.testShot();
			//sendImage();
//			try {
//				Thread.sleep(1000);
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
		}
	}
	
	private byte[] getShot() {
		long start = System.currentTimeMillis();
		byte[] buf = null;
		try {
			Bitmap bm = Util.getClipScreenBitmap();
			//Util.saveMyBitmap(bm, outname);	
			ByteArrayOutputStream baos = null ;  
			try{  
				baos = new ByteArrayOutputStream();  
				bm.compress(Bitmap.CompressFormat.JPEG, 30, baos);  
	
			buf=baos.toByteArray();
			Log.d(TAG, "buf size::" + buf.length + ",cnt:" + baos.size());
			}finally{  
				try {  
					if(baos != null)  
						baos.close() ;  
				} catch (IOException e) {  
					e.printStackTrace();  
				}  
			}  
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		Log.i(TAG, "time cost:" + (end - start));
		
		return buf;
	}
	
	/*
	 * 
	 * Type(2 Bytes):00 03
	 * Length(4 Bytes):待传输的数据长度
	 */
	private void sendFrameStart(int len) {
		Packet packet = new Packet();
		byte[] buf = new byte[6];
		buf[0] = 0x00;
		buf[1] = 0x03;
		buf[2] = (byte) (len >> 24 & 0xff);
		buf[3] = (byte) (len >> 16 & 0xff);
		buf[4] = (byte) (len >> 8 & 0xff);
		buf[5] = (byte) (len & 0xff);
		
		packet.pack(buf);
		user.send(packet);
		byte[] temp = packet.getPacket();
		String outPut = Util.bytesToHexString(temp, temp.length);
		Log.d(TAG, "sendFrameStart, tempppp:" + temp.length + ", it is:" + outPut);
	}
	
	private void sendFrameData(byte[] buf) {
		int len = buf.length;
		int cnt = len / PACKSIZE;
		for(int i = 0; i < cnt; ++i) {
			byte[] tmpBuf = new byte[PACKSIZE];
			System.arraycopy(buf, i*PACKSIZE, tmpBuf, 0, PACKSIZE);
			Packet packet = new Packet();
			packet.pack(tmpBuf);
			user.send(packet);	
			byte[] temp = packet.getPacket();
			//String outPut = Util.bytesToHexString(temp, temp.length);
			//Log.d(TAG, "sendFrameData, AAAAAA:" + temp.length);
		}
		int rest = len % PACKSIZE;
		if (rest > 0) {
			byte[] restBuf = new byte[rest];
			System.arraycopy(buf, cnt*PACKSIZE, restBuf, 0, rest);
			Packet packet = new Packet();
			packet.pack(restBuf);
			user.send(packet);
		}
		Log.d(TAG, "sendFrameData, len:" + len + ",cnt:" + cnt + ",rest:" + rest);
	}
	
	static byte[] frameEnddata = { 0x00, 0x05, 0x41, 0x75, 0x74, 0x6F, 0x49, 0x4F, 0x43, 0x6F };
	private void sendFrameEnd() {
		Packet packet = new Packet();
		packet.pack(frameEnddata);
		user.send(packet);	
		
		byte[] temp = packet.getPacket();
		String outPut = Util.bytesToHexString(temp, temp.length);
		Log.d(TAG, "sendFrameEnd, data:" + temp.length + ", it is:" + outPut);
	}
}


