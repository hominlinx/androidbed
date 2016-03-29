package com.example.navimagedemo;

import com.autoio.lib.net.Client;
import com.autoio.lib.net.TcpSend;
import com.autoio.lib.net.UdpReceiveAndTcpConnect;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

public class SendService extends Service {
	MyApplication myApplication; 
	MainActivity mainActivity;
	public static final String TAG = "Hominlinx==>SendService";
	public final static int MSG_ID1 = 0x222;
	TcpSend tcpSend;
	Client user;
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

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

		new UdpReceiveAndTcpConnect(handler_for_udpReceiveAndtcpSend, user).start();
		return super.onStartCommand(intent, flags, startId);  
		
	}  

	@Override  
	public void onDestroy() {  
		super.onDestroy();  
		tcpSend.stopTcp();
		Log.d(TAG, "onDestroy() executed");  
	}  
	
	Handler handler_for_udpReceiveAndtcpSend = new Handler() {
    	@Override
    	public void handleMessage(Message msg) {
    		super.handleMessage(msg);
    		if (msg.what == MSG_ID1) { // connect ok...
    			Log.d(TAG, "Connect ok....");
    			mainActivity.stopUDP();
    			tcpSend.start();
             }
    		
    	}
    };	 

}
