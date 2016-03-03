package wu.testandroidscreenshot;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;


import wu.lib.util.Client;
import wu.lib.util.Packet;
import wu.lib.util.Util;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class SendService extends Service  {

	MyApplication myApplication; 
	MainActivity mainActivity;
	Client user;
	MySendCommondThread mSendCommondThread;
	MySendFileThread mSendFileThread;
	
	 /**
	  * 图片开始的标识
	  */
	 private final String IMAGE_START = "image:";
	 /**
	  * 一条完整信息结束的标识
	  */
	 private final String MESSAGE_END = "over";
	 /**
	  * 文件名
	  */
	 private final String FILE_NAME = "/sdcard/my/test.jpg";

	
	public static final String TAG = "Hominlinx==>SendService";
	
	@Override  
	public void onCreate() {  
		super.onCreate();  
		myApplication = (MyApplication)getApplication();  
		mainActivity = myApplication.getMainAct();
		user = mainActivity.getClient();
		Log.d(TAG, "onCreate() executed");  
	}  

	@Override  
	public int onStartCommand(Intent intent, int flags, int startId) {  
		Log.d(TAG, "onStartCommand() executed"); 
		//mSendCommondThread = new MySendCommondThread();
		//mSendCommondThread.start();
		
		mSendFileThread = new MySendFileThread();
		mSendFileThread.start();
		
		return super.onStartCommand(intent, flags, startId);  
		
	}  

	@Override  
	public void onDestroy() {  
		super.onDestroy();  
		Log.d(TAG, "onDestroy() executed");  
	}  


	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	boolean quitFlag = false;
	class MySendCommondThread extends Thread{
		
		public void run() {
			while (!quitFlag) {
				Log.d(TAG, "sendcommond thread run");
				Packet packet=new Packet();
				packet.pack("test");
				
				user.send(packet);
				
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}		
		}
	}
	
	/**
	 * 发送文件线程
	 * 
	 */
    class MySendFileThread extends Thread{	
    	
    	public void run() {
    		while (!quitFlag) {
    			Log.d(TAG, "sendfile thread run");
    			//screenShot(FILE_NAME);
    			Util.testShot();
    			//sendImage();
    			try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    	}
    }
	
	
	 
	
	 public void sendImage()
	 {
		 try
		 {
			 File imageFile = new File(FILE_NAME);
			 InputStream is = new FileInputStream(imageFile);
			 long fileLength = imageFile.length();
			 Log.d(TAG, "sendImage, fileLength:" + fileLength);
			 
			 // 发送图片开始的标识，对应image_start
			 Packet packet = new Packet();
			 packet.pack(IMAGE_START);
			 user.send(packet);
			 
			 //发送图片文件的长度，对应image_file_length
			 byte[] bs = longToBytes(fileLength);
			 Packet lenPacket = new Packet();
			 lenPacket.pack(bs);
			 user.send(lenPacket);
			 
			 /*发送图片文件，对应image*/
			 int length;
			 byte[] b = new byte[1024];
			 while ((length = is.read(b)) > 0)
			 {
				// os.write(b, 0, length);
				 Packet imagePacket = new Packet();
				 imagePacket.pack(b);
				 user.send(imagePacket);
			 }

			 /*发送一条完整信息结束的标识，对应message_end*/
			 Packet overPacket = new Packet();
			 overPacket.pack(MESSAGE_END);
			 user.send(overPacket);
			 
		 } 
		 catch (Exception e) {
			 e.printStackTrace();
		 }
	 }

	 
	 
	 /**
	  * 将长整型转换为byte数组
	  * @param n
	  * @return
	  */
	 public static byte[] longToBytes(long n)
	 {
		 byte[] b = new byte[8];
		 b[7] = (byte) (n & 0xff);
		 b[6] = (byte) (n >> 8 & 0xff);
		 b[5] = (byte) (n >> 16 & 0xff);
		 b[4] = (byte) (n >> 24 & 0xff);
		 b[3] = (byte) (n >> 32 & 0xff);
		 b[2] = (byte) (n >> 40 & 0xff);
		 b[1] = (byte) (n >> 48 & 0xff);
		 b[0] = (byte) (n >> 56 & 0xff);
		 return b;
	 }
}
