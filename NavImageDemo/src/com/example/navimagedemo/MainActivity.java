package com.example.navimagedemo;


import com.autoio.lib.net.Client;
import com.autoio.lib.net.ISocketResponse;
import com.autoio.lib.net.TcpResponse;
import com.autoio.lib.net.UdpBroadCast;
import com.autoio.lib.util.Util;
import com.autoio.lib.wifi.WifiAdmin;
import com.autoio.lib.wifi.WifiAdmin2;

import com.autoio.lib.zxing.activity.CaptureActivity;

import android.os.Bundle;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity implements OnClickListener  {
	private static final String TAG = "Hominlinx==>MainActivity";
    private MyApplication myApplication;  
    private Context mContext = null; 
  
    Button startService;
    Button stopService;
    Button startTest;
    Button stopTest;
    Button wifiTest;
    private TextView resultTextView;
    TcpResponse socketListener =  null;
    
    private Client user=null;
	UdpBroadCast udp = null;

    ScreenshotThread mScreenshotThread;
	boolean screenshotQuit = false;
	
	WifiAdmin wifiAdmin = null;
	WifiAdmin2 wifiAdmin2 = null;

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

		socketListener = new TcpResponse();
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
		
		wifiTest = (Button)findViewById(R.id.testwifi);
		wifiTest.setOnClickListener(this);
		
		resultTextView = (TextView) this.findViewById(R.id.textView2);
		
		mContext = this;
//		wifiAdmin = new WifiAdmin(mContext);
		wifiAdmin = new WifiAdmin(mContext) {

			@Override
			public Intent myRegisterReceiver(BroadcastReceiver receiver,
					IntentFilter filter) {
				// TODO Auto-generated method stub
				MainActivity.this.registerReceiver(receiver, filter);  
				return null;
			}

			@Override
			public void myUnregisterReceiver(BroadcastReceiver receiver) {
				// TODO Auto-generated method stub
				MainActivity.this.unregisterReceiver(receiver); 
			}

			@Override
			public void onNotifyWifiConnected() {
				// TODO Auto-generated method stub
				Log.v(TAG, "have connected success!");  
                Log.v(TAG, "###############################"); 
                //发送AUTH:由 Master 发送,用于传输加密的 SSID_m、PWD_m
			}

			@Override
			public void onNotifyWifiConnectFailed() {
				// TODO Auto-generated method stub
				 Log.v(TAG, "have connected failed!");  
                 Log.v(TAG, "###############################");  
			}
			
		};
		wifiAdmin.openWifi();
		
		wifiAdmin2 = new WifiAdmin2(mContext);
		wifiAdmin2.openWifi();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
//	private ISocketResponse socketListener=new ISocketResponse() {
//
//		@Override
//		public void onSocketResponse(final String txt) {
//			runOnUiThread(new Runnable() {
//				public void run() {
//					Log.d(TAG, "++++++++++++++++++++++=" + txt.length());
//				}
//			});
//		}
//	};
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {  
		case R.id.start:
			udp = new UdpBroadCast(this);
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
			
		case R.id.testwifi:
			Log.d(TAG, "testWififf");
			//打开扫描界面扫描条形码或二维码
			Intent openCameraIntent = new Intent(MainActivity.this,CaptureActivity.class);
			startActivityForResult(openCameraIntent, 0);
			// wifiAdmin.addNetWork(wifiAdmin.CreateWifiInfo("mytest", "yua", null, 2));
//			if (ret == true) {
//				Log.d(TAG, "testWifi connect ok!");
//			} else {
//				Log.d(TAG, "testwifi connect error!");
//			}
			break;
		}
		
	}
	
	/*
	 * receive scanner activity result in my activity.
	 * 处理扫描结果（在界面上显示）
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent)
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			Bundle bundle = data.getExtras();
			String scanResult = bundle.getString("result");
			resultTextView.setText(scanResult);
			connectWifi(scanResult);
		}
	}

	private void connectWifi(String str) {
		System.out.println( "connectWifi  == " + str );
		String[] results = str.split( "#" );
		System.out.println( "connectWifi == " + results.length + "," + results[0] );
		Log.d(TAG, "connectWifi:" + str);
		if (results.length != 3) {
			resultTextView.append("\n扫描错误。");
			return;
		}
		String user = results[ 0 ];
		String password = results[ 1 ];
		int type = Integer.parseInt( results[ 2 ] );
		wifiAdmin.addNetwork(user, password, type);  
		//wifiAdmin.addNetwork(wifiAdmin.CreateWifiInfo(user, password, null, 2));
		//wifiAdmin.addNetWork2(wifiAdmin.CreateWifiInfo(user, password, null, 2));
		//wifiAdmin2.addNetWork(wifiAdmin2.CreateWifiInfo(user, password, null, type));
//		if (ret == false) {
//			resultTextView.append("\nwifi连接失败。");
//		}
		
		//resultTextView.setText(results.length);
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
