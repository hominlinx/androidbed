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
import wu.lib.util.udpReceiveAndtcpConnect;
import wu.lib.util.TcpSend;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class SendService extends Service  {

	MyApplication myApplication; 
	MainActivity mainActivity;
	Client user;
	
	//MySendFileThread mSendFileThread;
	TcpSend tcpSend;
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
	 
	 public final static int MSG_ID1 = 0x222;
	 

	
	public static final String TAG = "Hominlinx==>SendService";
	
	
	@Override  
	public void onCreate() {  
		super.onCreate();  
		myApplication = (MyApplication)getApplication();  
		mainActivity = myApplication.getMainAct();
		user = mainActivity.getClient();
		tcpSend = new TcpSend(user);
		Log.d(TAG, "onCreate() executed");  
	}  

	@Override  
	public int onStartCommand(Intent intent, int flags, int startId) {  
		Log.d(TAG, "onStartCommand() executed"); 
		//mSendCommondThread = new MySendCommondThread();
		//mSendCommondThread.start();
		
		new udpReceiveAndtcpConnect(handler_for_udpReceiveAndtcpSend, user).start();
//		mSendFileThread = new MySendFileThread();
//		mSendFileThread.start();
		
		
		
		return super.onStartCommand(intent, flags, startId);  
		
	}  

	@Override  
	public void onDestroy() {  
		super.onDestroy();  
		//quitFlag = true;
		tcpSend.stopTcp();
		
		Log.d(TAG, "onDestroy() executed");  
	}  


	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	Handler handler_for_udpReceiveAndtcpSend = new Handler() {
    	@Override
    	public void handleMessage(Message msg) {
    		super.handleMessage(msg);
    		if (msg.what == MSG_ID1) { // connect ok...
    			Log.d(TAG, "Connect ok....");
    			mainActivity.stopUDP();
    			tcpSend.start();
    			//mSendFileThread = new MySendFileThread();	
                //mSendFileThread.start();
             }
    		
    	}
    };	 
	 
}
