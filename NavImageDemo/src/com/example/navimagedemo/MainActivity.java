package com.example.navimagedemo;








import com.autoio.lib.net.Client;
import com.autoio.lib.net.ISocketResponse;
import com.autoio.lib.net.UdpBroadCast;
import com.autoio.lib.util.Util;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener  {
	private static final String TAG = "Hominlinx==>MainActivity";
    private MyApplication myApplication;  
  
    Button startService;
    Button stopService;
    Button startTest;
    Button stopTest;
    
    private Client user=null;
	UdpBroadCast udp = null;

    ScreenshotThread mScreenshotThread;
	boolean screenshotQuit = false;

	public Client getClient() {
		return user;
	}

	public void startUDP() {
		Log.d(TAG, "startUDP");
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
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		user=new Client(this.getApplicationContext(),socketListener);

		Util.initScreen(this);
		
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
	private ISocketResponse socketListener=new ISocketResponse() {

		@Override
		public void onSocketResponse(final String txt) {
			runOnUiThread(new Runnable() {
				public void run() {
					
				}
			});
		}
	};
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {  
		case R.id.start:
			udp = new UdpBroadCast();
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
	
			startTest.setEnabled(false);
			stopTest.setEnabled(true);
			Log.d(TAG, "testStart");
			break;
		case R.id.testStop:
			screenshotQuit = true;
			startTest.setEnabled(true);
			stopTest.setEnabled(false);
			Log.d(TAG, "testStop");
			break;
		}
		
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
