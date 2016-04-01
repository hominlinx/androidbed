package com.autoio.lib.net;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.autoio.lib.util.Util;



import android.graphics.Bitmap;
import android.util.Log;

/**
 * 发送文件线程
 * 
 */
public class TcpSend extends Thread {
	private static final String TAG = "Hominlinx===>TcpSend";
	private boolean quitFlag = false;
	Client user;
	private static int PACKSIZE = 2048;
	private static int INTERVAL = 250; // 250ms 发送一帧
	public void stopTcp() {
		quitFlag = true;
		user.close();
	}
	
	public TcpSend(Client user) {
		this.user = user;
	}
	public void run() {
		Log.d(TAG, "sendfile thread run");
		while (!quitFlag) {
			long start = System.currentTimeMillis();
			byte[] buf = getShot();  //  修改这里。。
			
			///////////////////////////////////
			//save2File(buf);
			///////////////////////////////////
			sendFrameStart(buf.length);
			
			sendFrameData(buf);
			
			sendFrameEnd();
			long end1 = System.currentTimeMillis();
			//screenShot(FILE_NAME);
			//Util.testShot();
			//sendImage();
			try {
				long temp = end1 - start;
				if ( temp < INTERVAL) {
					Thread.sleep(INTERVAL - temp);
				}
				//Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			long end = System.currentTimeMillis();
			//Log.i(TAG, "time cost:" + (end - start));
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
			//Log.d(TAG, "buf size::" + buf.length + ",cnt:" + baos.size());
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
		//Log.i(TAG, "time cost:" + (end - start));
		
		return buf;
	}
	
	private void save2File(byte[] buf)
	{
		SimpleDateFormat sdf = new SimpleDateFormat(
				"yyyy-MM-dd_HH-mm-ss", Locale.US); 
		//String outname = "/storage/sdcard1/my/" + sdf.format(new Date()) + ".jpg";
		String outname = "/storage/sdcard1/my/test"+ ".jpg";
		File f = new File(outname);
		try {
			f.createNewFile();
			FileOutputStream fOut = new FileOutputStream(f);
			fOut.write(buf);
			fOut.flush();
			fOut.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*
	 * 
	 * Type(2 Bytes):00 04
	 * Length(4 Bytes):待传输的数据长度
	 */
	private void sendFrameStart(int len) {
		Packet packet = new Packet();
		byte[] buf = new byte[6];
		buf[0] = 0x00;
		buf[1] = 0x04;
		buf[2] = (byte) (len >> 24 & 0xff);
		buf[3] = (byte) (len >> 16 & 0xff);
		buf[4] = (byte) (len >> 8 & 0xff);
		buf[5] = (byte) (len & 0xff);
		
		packet.pack(buf);
		user.send(packet);
		byte[] temp = packet.getPacket();
		String outPut = Util.bytesToHexString(temp, temp.length);
		//Log.d(TAG, "sendFrameStart, tempppp:" + temp.length + ", it is:" + outPut);
	}
	
	private void sendFrameData(byte[] buf) {
	
		int len = buf.length;
		int cnt = len / PACKSIZE;
		for(int i = 0; i < cnt; ++i) {
			byte[] tmpBuf = new byte[PACKSIZE + 6];
			tmpBuf[0] = 0x00;
			tmpBuf[1] = 0x05;
			tmpBuf[2] = (byte) (PACKSIZE >> 24 & 0xff);
			tmpBuf[3] = (byte) (PACKSIZE >> 16 & 0xff);
			tmpBuf[4] = (byte) (PACKSIZE >> 8 & 0xff);
			tmpBuf[5] = (byte) (PACKSIZE & 0xff);
			
			System.arraycopy(buf, i*PACKSIZE, tmpBuf, 6, PACKSIZE);
			Packet packet = new Packet();
			packet.pack(tmpBuf);
			user.send(packet);	
			//byte[] temp = packet.getPacket();
			//String outPut = Util.bytesToHexString(temp, temp.length);
			//Log.d(TAG, "sendFrameData, AAAAAA:" + temp.length);
		}
		int rest = len % PACKSIZE;
		if (rest > 0) {
			byte[] restBuf = new byte[rest + 6];
			restBuf[0] = 0x00;
			restBuf[1] = 0x05;
			restBuf[2] = (byte) (rest >> 24 & 0xff);
			restBuf[3] = (byte) (rest >> 16 & 0xff);
			restBuf[4] = (byte) (rest >> 8 & 0xff);
			restBuf[5] = (byte) (rest & 0xff);
			
			System.arraycopy(buf, cnt*PACKSIZE, restBuf, 6, rest);
			Packet packet = new Packet();
			packet.pack(restBuf);
			user.send(packet);
		}
		//Log.d(TAG, "sendFrameData, len:" + len + ",cnt:" + cnt + ",rest:" + rest);
	}
	
	static byte[] frameEnddata = { 0x00, 0x06, 0x41, 0x75, 0x74, 0x6F, 0x49, 0x4F, 0x43, 0x6F };
	private void sendFrameEnd() {
		Packet packet = new Packet();
		packet.pack(frameEnddata);
		user.send(packet);	
		
		byte[] temp = packet.getPacket();
		String outPut = Util.bytesToHexString(temp, temp.length);
		//Log.d(TAG, "sendFrameEnd, data:" + temp.length + ", it is:" + outPut);
	}
}
