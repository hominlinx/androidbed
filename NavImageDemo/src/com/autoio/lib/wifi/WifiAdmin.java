package com.autoio.lib.wifi;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.util.Log;
import android.widget.Toast;

public abstract class WifiAdmin {
	
	private static final String TAG = "Hominlinx===>WifiAdmin";  
	
	// 定义一个WifiManager对象
	private WifiManager mWifiManager;
	private ConnectivityManager mConnManager;
	// 扫描出的网络连接列表
	private List<ScanResult> mWifiList = new ArrayList<ScanResult>();
	// 网络配置列表
	private List<WifiConfiguration> mWifiConfigurations = new ArrayList<WifiConfiguration>();
	WifiLock mWifiLock;

	 private Context mContext = null; 
	 private WifiInfo mWifiInfo;  
	
	public abstract Intent myRegisterReceiver(BroadcastReceiver receiver, IntentFilter filter);  

	public abstract void myUnregisterReceiver(BroadcastReceiver receiver);  

	public abstract void onNotifyWifiConnected();  

	public abstract void onNotifyWifiConnectFailed();  

	
	public WifiAdmin(Context context) {
		mContext = context;
		// 取得WifiManager对象
		mWifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		// 取得WifiInfo对象  
		 mWifiInfo = mWifiManager.getConnectionInfo();  
		 
		mConnManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		mWifiLock = mWifiManager.createWifiLock("lock");
		Log.v(TAG, "getIpAddress = " + mWifiInfo.getIpAddress());
	}

	// 打开wifi
	public void openWifi() {
		if (!mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(true);
		}
	}

	// 关闭wifi
	public void closeWifi() {
		if (mWifiManager.isWifiEnabled()) {
			mWifiManager.setWifiEnabled(false);
		}
	}

	// 检查当前wifi状态
	public int checkState() {
		return mWifiManager.getWifiState();
	}

	// 锁定wifiLock
	public void acquireWifiLock() {
		mWifiLock.acquire();
	}

	// 解锁wifiLock
	public void releaseWifiLock() {
		// 判断是否锁定
		if (mWifiLock.isHeld()) {
			mWifiLock.release();
		}
	}

	// 得到配置好的网络
	public List<WifiConfiguration> getConfiguration() {
		return mWifiConfigurations;
	}

	// 指定配置好的网络进行连接
	public boolean connectWifi(ScanResult myitem) {
		String myssid = "\"" + myitem.SSID + "\"";
		List<WifiConfiguration> configurationList = getConfiguration();
		for (WifiConfiguration conf : configurationList) {
			if (conf.SSID.equals(myssid)) {
				connetionConfiguration(conf.networkId);
//				Intent intent=new Intent(context,LoadingActivity.class);
//				context.startActivity(intent);
				return true;
			}
		}
		return false;
	}

	private void connetionConfiguration(int networkId) {
		mWifiManager.enableNetwork(networkId, true);
	}

	public void clear() {
		mWifiConfigurations.clear();
		mWifiList.clear();
	}

	public void startScan() {
		Log.d(TAG, "startScan:" + checkState());
		if (WifiManager.WIFI_STATE_ENABLED == checkState()) {
			mWifiManager.startScan();
			// 得到扫描结果
			mWifiList.clear();
			mWifiList = mWifiManager.getScanResults();
			if (!mWifiList.isEmpty()){
				 for (int i = 0; i < mWifiList.size() - 1; i++) {
					for (int j = mWifiList.size() - 1; j > i; j--) {
					   if (mWifiList.get(j).SSID.equals(mWifiList.get(i).SSID)) {
					    	 mWifiList.remove(j);
					   }
					}
				 }
				Collections.sort(mWifiList, new Comparator<ScanResult>() {
					@Override
					public int compare(ScanResult lhs, ScanResult rhs) {
						// TODO Auto-generated method stub
						if(lhs.level>rhs.level) return -1;
						else if(lhs.level==rhs.level) return 0;
						else return 1;
					}
				});
        	}
		} else {
			Toast.makeText(mContext, "请先开启WIFI服务!！", Toast.LENGTH_SHORT).show();
		}
	}

	// 得到网络列表
	public List<ScanResult> getWifiList() {
		return mWifiList;
	}

	// 查看扫描结果
	public StringBuffer lookUpScan() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < mWifiList.size(); i++) {
			sb.append("Index_" + new Integer(i + 1).toString() + ":");
			// 将ScanResult信息转换成一个字符串包
			// 其中把包括：BSSID、SSID、capabilities、frequency、level
			sb.append((mWifiList.get(i)).toString()).append("\n");
		}
		return sb;
	}

	public String getMacAddress() {
		WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
	}

	public String getBSSID() {
		WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
	}
	
	public String getSSID() {
		WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.getSSID();
	}

	public int getIpAddress() {
		WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
		return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();
	}

	// 得到连接的ID
	public int getNetWordId() {
		WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
		return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();
	}

	// 得到wifiInfo的所有信息
	public String getWifiInfo() {
		WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
		return (mWifiInfo == null) ? "NULL" : mWifiInfo.toString();
	}

	// 添加一个网络并连接
		public void addNetWork2(WifiConfiguration configuration) {
			int wcgId = mWifiManager.addNetwork(configuration);
			if (mWifiManager.enableNetwork(wcgId, true)){
				Toast.makeText(mContext, "网络连接成功！", Toast.LENGTH_SHORT).show();
//				if(isConnected()){
//					Toast.makeText(mContext, "网络连接成功！", Toast.LENGTH_SHORT).show();
//				}
			}
			else {
				Toast.makeText(mContext, "网络连接失败！", Toast.LENGTH_SHORT).show();
			}
		}

	// 添加一个网络并连接  
    public void addNetwork(WifiConfiguration wcg) {  
          
        register();  
          
        WifiApAdmin.closeWifiAp(mContext);  
          
        int wcgID = mWifiManager.addNetwork(wcg);  
        boolean b = mWifiManager.enableNetwork(wcgID, true);  
        if(b) {
        	Toast.makeText(mContext, "网络连接成功！", Toast.LENGTH_SHORT).show();
        }
        else {
        	Toast.makeText(mContext, "网络连接失败！", Toast.LENGTH_SHORT).show();
        }
    }  

		private boolean isConnected() {
			// TODO Auto-generated method stub
			return mConnManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();

		}

	public static final int TYPE_NO_PASSWD = 0x1;  
    public static final int TYPE_WEP = 0x2;  
    public static final int TYPE_WPA = 0x3;  
      
    public void addNetwork(String ssid, String passwd, int type) {  
        if (ssid == null || passwd == null || ssid.equals("")) {  
            Log.e(TAG, "addNetwork() ## nullpointer error!");  
            return;  
        }  
          
//        if (type != TYPE_NO_PASSWD && type != TYPE_WEP && type != TYPE_WPA) {  
//            Log.e(TAG, "addNetwork() ## unknown type = " + type);  
//        }  
//          
        stopTimer();  
        unRegister();  
          
        addNetwork(CreateWifiInfo(ssid, passwd, type));  
    }  
	// 断开指定ID的网络
	public void disConnectionWifi(int netId) {
		mWifiManager.disableNetwork(netId);
		mWifiManager.disconnect();
	}
	
//	public boolean isConnected(){
//		return mConnManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
//	}
	// 断线重连
	public void reConnection() {
		mWifiManager.reconnect();
	}

//	public WifiConfiguration CreateWifiInfo(String SSID, String Password,String Identity,
//			int Type) {
//		String mSsid="\"" + SSID + "\"";
//		String mPassword="\"" + Password + "\"";
//		String mIdentity="\"" + Identity + "\"";
//		WifiConfiguration config = new WifiConfiguration();
//		config.allowedAuthAlgorithms.clear();
//		config.allowedGroupCiphers.clear();
//		config.allowedKeyManagement.clear();
//		config.allowedPairwiseCiphers.clear();
//		config.allowedProtocols.clear();
//		config.SSID = mSsid;
//		// WIFICIPHER_NOPASS
//		if (Type == 0) {
//			config.wepKeys[0] = "";
//			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//			config.wepTxKeyIndex = 0;
//		}
//		// WIFICIPHER_WEP
//		if (Type == 1) {
//			config.hiddenSSID = false;
//			config.wepKeys[0] = mPassword;
//			config.allowedAuthAlgorithms
//					.set(WifiConfiguration.AuthAlgorithm.SHARED);
//			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
//			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
//			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
//			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
//			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
//			config.wepTxKeyIndex = 0;
//		}
//		// WIFICIPHER_WPA
//		if (Type == 2) {
//			config.hiddenSSID = false;
//			config.status = WifiConfiguration.Status.ENABLED;
//			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
//			config.allowedAuthAlgorithms
//					.set(WifiConfiguration.AuthAlgorithm.OPEN);
//			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
//			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
//			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
//			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
//			config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
//			config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
//
//			config.preSharedKey = mPassword;
//		}
//		if(Type == 3){
//			config.hiddenSSID = false;
//			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
//			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
//			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
//			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
//			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
//			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
//			config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
//			config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
//			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
//			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.IEEE8021X);
//			
//			if(android.os.Build.VERSION.SDK_INT<18){
//				
//				try {
//					final String INT_PRIVATE_KEY = "private_key";
//					final String INT_PHASE2 = "phase2";
//					final String INT_PASSWORD = "password";
//					final String INT_IDENTITY = "identity";
//					final String INT_EAP = "eap";
//					final String INT_CLIENT_CERT = "client_cert";
//					final String INT_CA_CERT = "ca_cert";
//					final String INT_ANONYMOUS_IDENTITY = "anonymous_identity";
//					final String INT_ENTERPRISEFIELD_NAME ="android.net.wifi.WifiConfiguration$EnterpriseField";
//					boolean noEnterpriseFieldType = true;
//					
//					final String ENTERPRISE_EAP = "PEAP";
//					final String ENTERPRISE_PHASE2 = "MSCHAPV2";
//			       
//			        Class[] wcClasses = WifiConfiguration.class.getClasses();
//			        Class wcEnterpriseField = null;
//
//			        for (Class wcClass : wcClasses)
//			            if (wcClass.getName().equals(INT_ENTERPRISEFIELD_NAME)) 
//			            {
//			                wcEnterpriseField = wcClass;
//			                noEnterpriseFieldType = false;
//			                break;
//			            }
//			        
//			        Field wcefAnonymousId = null, 
//			        	wcefCaCert = null,
//			        	wcefClientCert = null,
//			        	wcefEap = null, 
//			        	wcefIdentity = null, 
//			        	wcefPassword = null, 
//			        	wcefPhase2 = null, 
//			        	wcefPrivateKey = null;
//			        Field[] wcefFields = WifiConfiguration.class.getFields();
//			        for (Field wcefField : wcefFields) 
//			        {
//			            if (wcefField.getName().equals(INT_ANONYMOUS_IDENTITY))
//			                wcefAnonymousId = wcefField;
//			            else if (wcefField.getName().equals(INT_CA_CERT))
//			                wcefCaCert = wcefField;
//			            else if (wcefField.getName().equals(INT_CLIENT_CERT))
//			                wcefClientCert = wcefField;
//			            else if (wcefField.getName().equals(INT_EAP))
//			                wcefEap = wcefField;
//			            else if (wcefField.getName().equals(INT_IDENTITY))
//			                wcefIdentity = wcefField;
//			            else if (wcefField.getName().equals(INT_PASSWORD))
//			                wcefPassword = wcefField;
//			            else if (wcefField.getName().equals(INT_PHASE2))
//			                wcefPhase2 = wcefField;
//			            else if (wcefField.getName().equals(INT_PRIVATE_KEY))
//			                wcefPrivateKey = wcefField;
//			        }
//
//
//			        Method wcefSetValue = null;
//			        if(!noEnterpriseFieldType){
//			        for(Method m: wcEnterpriseField.getMethods())
//			             if(m.getName().trim().equals("setValue"))
//			                wcefSetValue = m;
//			        }
//			        //EAP Method
//			        if(!noEnterpriseFieldType){
//			            wcefSetValue.invoke(wcefEap.get(config), ENTERPRISE_EAP);
//			        }
//			        //EAP Phase 2 Authentication
//			        if(!noEnterpriseFieldType){
//			            wcefSetValue.invoke(wcefPhase2.get(config), ENTERPRISE_PHASE2);
//			        }
//			        //EAP Identity
//			        if(!noEnterpriseFieldType){
//			            wcefSetValue.invoke(wcefIdentity.get(config), mIdentity);
//			        }
//			        //EAP Password
//			        if(!noEnterpriseFieldType){
//			            wcefSetValue.invoke(wcefPassword.get(config), mPassword);
//			        }
//
//			    } catch (Exception e){
//			        // TODO Auto-generated catch block
//			    	return null;
//			    }
//
//			}
//			
//			else{
//				/*WifiEnterpriseConfig enterpriseConfig = new WifiEnterpriseConfig(); 
//				enterpriseConfig.setIdentity(Identity);
//				enterpriseConfig.setPassword(Password);
//				enterpriseConfig.setEapMethod(WifiEnterpriseConfig.Eap.PEAP);
//				enterpriseConfig.setPhase2Method(WifiEnterpriseConfig.Phase2.MSCHAPV2); 
//				config.enterpriseConfig = enterpriseConfig;*/
//			}
//			
//		}
//		mWifiConfigurations.add(config);
//		return config;
//	}

	public WifiConfiguration CreateWifiInfo(String SSID, String password, int type) {
		WifiConfiguration config = new WifiConfiguration();
		config.allowedAuthAlgorithms.clear();
		config.allowedGroupCiphers.clear();
		config.allowedKeyManagement.clear();
		config.allowedPairwiseCiphers.clear();
		config.allowedProtocols.clear();
		config.SSID = "\"" + SSID + "\"";

		WifiConfiguration tempConfig = this.IsExsits(SSID);
		if (tempConfig != null) {
			mWifiManager.removeNetwork(tempConfig.networkId);
		}

		if (type == 1) {
			// WIFICIPHER_NOPASS
			config.wepKeys[0] = "";
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		}
		if (type == 2) {
			// WIFICIPHER_WEP
			config.hiddenSSID = true;
			config.wepKeys[0] = "\"" + password + "\"";
			config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		}
		if (type == 3) {
			// WIFICIPHER_WPA
			config.preSharedKey = "\"" + password + "\"";
			config.hiddenSSID = true;
			config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
			// config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.status = WifiConfiguration.Status.ENABLED;
		}
		
		return config;
	} 
	 
	 public static final int WIFI_CONNECTED = 0x01;  
	    public static final int WIFI_CONNECT_FAILED = 0x02;  
	    public static final int WIFI_CONNECTING = 0x03;  
	    /** 
	     * 判断wifi是否连接成功,不是network 
	     *  
	     * @param context 
	     * @return 
	     */  
	    public int isWifiContected(Context context) {  
	        ConnectivityManager connectivityManager = (ConnectivityManager) context  
	                .getSystemService(Context.CONNECTIVITY_SERVICE);  
	        NetworkInfo wifiNetworkInfo = connectivityManager  
	                .getNetworkInfo(ConnectivityManager.TYPE_WIFI);  
	          
	        Log.v(TAG, "isConnectedOrConnecting = " + wifiNetworkInfo.isConnectedOrConnecting());  
	        Log.d(TAG, "wifiNetworkInfo.getDetailedState() = " + wifiNetworkInfo.getDetailedState());  
	        if (wifiNetworkInfo.getDetailedState() == DetailedState.OBTAINING_IPADDR  
	                || wifiNetworkInfo.getDetailedState() == DetailedState.CONNECTING) {  
	            return WIFI_CONNECTING;  
	        } else if (wifiNetworkInfo.getDetailedState() == DetailedState.CONNECTED) {  
	            return WIFI_CONNECTED;  
	        } else {  
	            Log.d(TAG, "getDetailedState() == " + wifiNetworkInfo.getDetailedState());  
	            return WIFI_CONNECT_FAILED;  
	        }  
	    }  
	      
	    private WifiConfiguration IsExsits(String SSID) {  
	        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();  
	        for (WifiConfiguration existingConfig : existingConfigs) {  
	            if (existingConfig.SSID.equals("\"" + SSID + "\"") /*&& existingConfig.preSharedKey.equals("\"" + password + "\"")*/) {  
	                return existingConfig;  
	            }  
	        }  
	        return null;  
	    }  
	  
	private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {  
		  
        @Override  
        public void onReceive(Context context, Intent intent) {  
            // TODO Auto-generated method stub  
        	Log.d(TAG, "BroadcastReceiver onReceive," + intent.getAction());
        	
            int i = isWifiContected(mContext);
            Log.d(TAG, "isWifiContected i," + i);
        	final String action = intent.getAction();
        	switch (i) {
        	case WIFI_CONNECTED:
        		Log.d(TAG, "wifi connected");
        		stopTimer();  
        		onNotifyWifiConnected();  
        		unRegister();  
        		break;
        	case WIFI_CONNECT_FAILED:
        		Log.d(TAG, "wifi failed");
        		break;
        	case WIFI_CONNECTING:
        		Log.d(TAG, "wifi connecting");

        		break;
        
        	default:
        		Log.d(TAG, "wifi default");
        		break;
        	}
//        	if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
//        		int st = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
//        		Log.d(TAG, "BroadcastReceiver onReceive:" + st);
//        		
//        		switch (st) {
//        		case WifiManager.WIFI_STATE_ENABLED:
//        		Log.d(TAG, "wifi enabled");
//        		
//        		break;
//        		case WifiManager.WIFI_STATE_ENABLING:
//        		Log.d(TAG, "wifi enabling");
//        		break;
//        		case WifiManager.WIFI_STATE_DISABLED:
//        		Log.d(TAG, "wifi disabled");
//        		
//        		break;
//        		case WifiManager.WIFI_STATE_DISABLING:
//        		Log.d(TAG, "wifi disabling");
//        		break;
//        		default:
//        		break;
//        		}
//        	}
//   
//            if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {  
//                Log.d(TAG, "RSSI changed");  
//                  
//                //有可能是正在获取，或者已经获取了  
//                Log.d(TAG, " intent is " + WifiManager.RSSI_CHANGED_ACTION);  
//                  
//                if (isWifiContected(mContext) == WIFI_CONNECTED) {  
//                	Log.d(TAG, " WIFI_CONNECTED ");
//                    stopTimer();  
//                    onNotifyWifiConnected();  
//                    unRegister();  
//                } else if (isWifiContected(mContext) == WIFI_CONNECT_FAILED) {  
//                	Log.d(TAG, " WIFI_CONNECT_FAILED ");
//                    stopTimer();  
//                    closeWifi();  
//                    onNotifyWifiConnectFailed();  
//                    unRegister();  
//                } else if (isWifiContected(mContext) == WIFI_CONNECTING) {  
//                	Log.d(TAG, " WIFI_CONNECTING ");
//                }  
//            }  
        }  
    };
    
    private final int STATE_REGISTRING = 0x01;  
    private final int STATE_REGISTERED = 0x02;  
    private final int STATE_UNREGISTERING = 0x03;  
    private final int STATE_UNREGISTERED = 0x04;  
      
    private int mHaveRegister = STATE_UNREGISTERED;  
    private synchronized void register() {  
        Log.v(TAG, "register() ##mHaveRegister = " + mHaveRegister);  
  
        if (mHaveRegister == STATE_REGISTRING   
                || mHaveRegister == STATE_REGISTERED) {  
            return ;  
        }  
          
        mHaveRegister = STATE_REGISTRING;  
        //配置的网络标识已被更改。 
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        
        myRegisterReceiver(mBroadcastReceiver, intentFilter);  
        mHaveRegister = STATE_REGISTERED;  
          
        startTimer();  
    }  
    private synchronized void unRegister() {  
        Log.v(TAG, "unRegister() ##mHaveRegister = " + mHaveRegister);  
          
        if (mHaveRegister == STATE_UNREGISTERED   
                || mHaveRegister == STATE_UNREGISTERING) {  
            return ;  
        }  
          
        mHaveRegister = STATE_UNREGISTERING;  
        myUnregisterReceiver(mBroadcastReceiver);  
        mHaveRegister = STATE_UNREGISTERED;  
    }  
      
    private Timer mTimer = null;  
    private MyTimerTask mTimerTask = null;
    private void startTimer() {  
    	Log.d(TAG, "startTimer!");  
        if (mTimer != null) {  
        	Log.d(TAG, "startTimer!  time is not null"); 
            stopTimer(); 
         
        }  
        if (mTimerTask != null){
        	mTimerTask.cancel();  //将原任务从队列中移除
        }
        mTimerTask = new MyTimerTask(); 
        mTimer = new Timer(true);  
//      mTimer.schedule(mTimerTask, 0, 20 * 1000);// 20s  
        mTimer.schedule(mTimerTask, 30 * 1000);  
    }  
    
   
    class MyTimerTask extends TimerTask {
    	 @Override  
         public void run() {  
             // TODO Auto-generated method stub  
             Log.e(TAG, "timer out!");  
             onNotifyWifiConnectFailed();  
             unRegister();  
         }  
    };
   
      
    private void stopTimer() {  
    	 Log.d(TAG, "stopTimer!");  
        if (mTimer != null) {  
            mTimer.cancel();  
            mTimer = null;  
        }  
    }  
      
}

