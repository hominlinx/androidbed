package wu.testandroidscreenshot;

import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import ShellUtils.ShellUtils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import wu.lib.util.*;

public class MainActivity extends Activity  implements OnClickListener {
	private static final String TAG = "Hominlinx==>MainActivity";
    private MyApplication myApplication;  
  
    Button startService;
    Button stopService;
    Button startTest;
    Button stopTest;
    
    private Client user=null;
	private EditText ip,port,sendContent,recContent;
	ScreenshotThread mScreenshotThread;
	boolean screenshotQuit = false;

	public Client getClient() {
		return user;
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		
		user=new Client(this.getApplicationContext(),socketListener);
		
		initView();
		
		myApplication = (MyApplication)getApplication();  
		myApplication.setMainAct(this);  
				
		startService = (Button) findViewById(R.id.start);
		stopService = (Button) findViewById(R.id.stop);
		startService.setOnClickListener(this);  
		stopService.setOnClickListener(this); 		
		startService.setEnabled(true);
		stopService.setEnabled(false);
		
		startTest = (Button) findViewById(R.id.testStart);
		stopTest = (Button) findViewById(R.id.testStop);
		startTest.setOnClickListener(this);  
		stopTest.setOnClickListener(this);
		startTest.setEnabled(true);
		stopTest.setEnabled(false);
		

	}
	
	private void initView()
	{
		Log.d(TAG, "initView");
//		findViewById(R.id.open).setOnClickListener(listener);
//		findViewById(R.id.close).setOnClickListener(listener);
//		findViewById(R.id.reconn).setOnClickListener(listener);
//		findViewById(R.id.send).setOnClickListener(listener);
//		findViewById(R.id.clear).setOnClickListener(listener);
//		findViewById(R.id.start).setOnClickListener(listener);
//		findViewById(R.id.stop).setOnClickListener(listener);
		
//		ip=(EditText) findViewById(R.id.ip);
//		port=(EditText) findViewById(R.id.port);
//		sendContent=(EditText) findViewById(R.id.sendContent);
//		recContent=(EditText) findViewById(R.id.recContent);
//		
//		ip.setText("172.10.11.42");
//		port.setText("60000");
		
		Util.initScreen(this);
		
		//Util.testShot();
	}
	
	
//	private OnClickListener listener=new OnClickListener() {
//
//		@Override
//		public void onClick(View v) {
//			switch(v.getId())
//			{
//				case R.id.open:
////					user.open();
//					//user.open(ip.getText().toString(), Integer.valueOf(port.getText().toString()));				
//					
//					break;
//					
//				case R.id.close:
//					user.close();
//					break;
//					
//				case R.id.reconn:
//					user.reconn();
//					break;
//					
//				case R.id.send:
//					Packet packet=new Packet();
//					packet.pack(sendContent.getText().toString());
//					user.send(packet);
//					sendContent.setText("");
//					break;
//					
//				case R.id.clear:
//					recContent.setText("");
//					break;
//					
////				case R.id.start:
////					Log.d(TAG, "startttt");
////					Intent mIntent = new Intent();  
////					//mIntent.setAction("XXX.XXX.XXX");//你定义的service的action  
////					mIntent.setPackage(getPackageName());//这里你需要设置你应用的包名  
////					startService(mIntent); 
////					
////					//Intent startIntent = new Intent(MainActivity.class, SendService.class);  
////					//startService(startIntent);  
////					break;
////					
////				case R.id.stop:
////					Intent stopIntent = new Intent();  
////					stopService(stopIntent); 
////					break;
//			}
//		}
//		
//	};
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK)
		{
			android.os.Process.killProcess(android.os.Process.myPid());
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private ISocketResponse socketListener=new ISocketResponse() {

		@Override
		public void onSocketResponse(final String txt) {
			runOnUiThread(new Runnable() {
				public void run() {
					recContent.getText().append(txt).append("\r\n");
				}
			});
		}
	};

	
//	@Override  
//	public void onClick(View v) {  
//		switch (v.getId()) {  
////		case R.id.startbtn:  
////			Intent startIntent = new Intent(this, SendService.class);  
////			startService(startIntent);  
////			break;  
////		case R.id.stopbtn:  
////			Intent stopIntent = new Intent(this, SendService.class);  
////			stopService(stopIntent);  
////			break;  
//		default:  
//			break;  
//		}  
//	}  
	

	udpBroadCast udp = null;
	public void startUDP() {
		if(udp != null) {
			udp.start();
		}
		
	}
	public void stopUDP() {
		if (udp != null) {
			udp.quitFlag = true;
		}
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {  
		case R.id.start:
			udp = new udpBroadCast();
			startUDP();
			Intent startIntent = new Intent(this, SendService.class);  
			startService(startIntent); 
			startService.setEnabled(false);
			stopService.setEnabled(true);
			 
			Log.d(TAG, "start");
	
			break;
		case R.id.stop:
			stopUDP();
			Intent stopIntent = new Intent(this, SendService.class);  
			stopService(stopIntent); 
			startService.setEnabled(true);
			stopService.setEnabled(false);
			Log.d(TAG, "stop");
			break;
		case R.id.testStart:
			mScreenshotThread = new ScreenshotThread();
			screenshotQuit = false;
			mScreenshotThread.start();
			
//			Intent startIntent1 = new Intent(this, ScreenTestService.class);  
//			startService(startIntent1); 
			
			startTest.setEnabled(false);
			stopTest.setEnabled(true);
			Log.d(TAG, "testStart");
			break;
		case R.id.testStop:
			screenshotQuit = true;
//			Intent stopIntent1 = new Intent(this, ScreenTestService.class);  
//			stopService(stopIntent1); 
			
			startTest.setEnabled(true);
			stopTest.setEnabled(false);
			Log.d(TAG, "testStop");
			break;
		}
//			case R.id.startbtn:  
//				Intent startIntent = new Intent(this, SendService.class);  
//				startService(startIntent);  

		
	}
	
	/**
	 * 保存截屏文件线程
	 * 
	 */
	public class ScreenshotThread extends Thread{	
		
		public void run() {
			Log.d(TAG, "ScreenshotThread thread run");
			
			while (!screenshotQuit) {	
				Log.d(TAG, "Util  testShot");
				Util.testShot();			
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
	