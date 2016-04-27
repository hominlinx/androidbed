package com.autoio.lib.wifi;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;
import android.util.Log;
import android.widget.Toast;

public class WifiAdmin2 {
	Context context;
	// 定义一个WifiManager对象
	private WifiManager mWifiManager;
	private ConnectivityManager mConnManager;
	// 扫描出的网络连接列表
	private List<ScanResult> mWifiList = new ArrayList<ScanResult>();
	// 网络配置列表
	private List<WifiConfiguration> mWifiConfigurations = new ArrayList<WifiConfiguration>();
	WifiLock mWifiLock;

	public WifiAdmin2(Context context) {
		this.context = context;
		// 取得WifiManager对象
		mWifiManager = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		mConnManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		mWifiLock = mWifiManager.createWifiLock("lock");
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
			Toast.makeText(context, "请先开启WIFI服务!!！", Toast.LENGTH_SHORT).show();
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
	public void addNetWork(WifiConfiguration configuration) {
		int wcgId = mWifiManager.addNetwork(configuration);
		if (mWifiManager.enableNetwork(wcgId, true)){
//			Intent intent=new Intent(context,LoadingActivity.class);
//			context.startActivity(intent);
			if(isConnected()){
				Toast.makeText(context, "网络连接成功！", Toast.LENGTH_SHORT).show();
			}
		}
		else {
			Toast.makeText(context, "网络连接失败！", Toast.LENGTH_SHORT).show();
		}
	}

	// 断开指定ID的网络
	public void disConnectionWifi(int netId) {
		mWifiManager.disableNetwork(netId);
		mWifiManager.disconnect();
	}
	
	public boolean isConnected(){
		return mConnManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected();
	}
	// 断线重连
	public void reConnection() {
		mWifiManager.reconnect();
	}

	public WifiConfiguration CreateWifiInfo(String SSID, String Password,String Identity,
			int Type) {
		String mSsid="\"" + SSID + "\"";
		String mPassword="\"" + Password + "\"";
		String mIdentity="\"" + Identity + "\"";
		WifiConfiguration config = new WifiConfiguration();
		config.allowedAuthAlgorithms.clear();
		config.allowedGroupCiphers.clear();
		config.allowedKeyManagement.clear();
		config.allowedPairwiseCiphers.clear();
		config.allowedProtocols.clear();
		config.SSID = mSsid;
		// WIFICIPHER_NOPASS
		if (Type == 0) {
			config.wepKeys[0] = "";
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		}
		// WIFICIPHER_WEP
		if (Type == 1) {
			config.hiddenSSID = false;
			config.wepKeys[0] = mPassword;
			config.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.SHARED);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
			config.wepTxKeyIndex = 0;
		}
		// WIFICIPHER_WPA
		if (Type == 2) {
			config.hiddenSSID = false;
			config.status = WifiConfiguration.Status.ENABLED;
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
			config.allowedAuthAlgorithms
					.set(WifiConfiguration.AuthAlgorithm.OPEN);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
			config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
			config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);

			config.preSharedKey = mPassword;
		}
		if(Type == 3){
			config.hiddenSSID = false;
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
			config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
			config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
			config.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
			config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_EAP);
			config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.IEEE8021X);
			
			if(android.os.Build.VERSION.SDK_INT<18){
				
				try {
					final String INT_PRIVATE_KEY = "private_key";
					final String INT_PHASE2 = "phase2";
					final String INT_PASSWORD = "password";
					final String INT_IDENTITY = "identity";
					final String INT_EAP = "eap";
					final String INT_CLIENT_CERT = "client_cert";
					final String INT_CA_CERT = "ca_cert";
					final String INT_ANONYMOUS_IDENTITY = "anonymous_identity";
					final String INT_ENTERPRISEFIELD_NAME ="android.net.wifi.WifiConfiguration$EnterpriseField";
					boolean noEnterpriseFieldType = true;
					
					final String ENTERPRISE_EAP = "PEAP";
					final String ENTERPRISE_PHASE2 = "MSCHAPV2";
			       
			        Class[] wcClasses = WifiConfiguration.class.getClasses();
			        Class wcEnterpriseField = null;

			        for (Class wcClass : wcClasses)
			            if (wcClass.getName().equals(INT_ENTERPRISEFIELD_NAME)) 
			            {
			                wcEnterpriseField = wcClass;
			                noEnterpriseFieldType = false;
			                break;
			            }
			        
			        Field wcefAnonymousId = null, 
			        	wcefCaCert = null,
			        	wcefClientCert = null,
			        	wcefEap = null, 
			        	wcefIdentity = null, 
			        	wcefPassword = null, 
			        	wcefPhase2 = null, 
			        	wcefPrivateKey = null;
			        Field[] wcefFields = WifiConfiguration.class.getFields();
			        for (Field wcefField : wcefFields) 
			        {
			            if (wcefField.getName().equals(INT_ANONYMOUS_IDENTITY))
			                wcefAnonymousId = wcefField;
			            else if (wcefField.getName().equals(INT_CA_CERT))
			                wcefCaCert = wcefField;
			            else if (wcefField.getName().equals(INT_CLIENT_CERT))
			                wcefClientCert = wcefField;
			            else if (wcefField.getName().equals(INT_EAP))
			                wcefEap = wcefField;
			            else if (wcefField.getName().equals(INT_IDENTITY))
			                wcefIdentity = wcefField;
			            else if (wcefField.getName().equals(INT_PASSWORD))
			                wcefPassword = wcefField;
			            else if (wcefField.getName().equals(INT_PHASE2))
			                wcefPhase2 = wcefField;
			            else if (wcefField.getName().equals(INT_PRIVATE_KEY))
			                wcefPrivateKey = wcefField;
			        }


			        Method wcefSetValue = null;
			        if(!noEnterpriseFieldType){
			        for(Method m: wcEnterpriseField.getMethods())
			             if(m.getName().trim().equals("setValue"))
			                wcefSetValue = m;
			        }
			        //EAP Method
			        if(!noEnterpriseFieldType){
			            wcefSetValue.invoke(wcefEap.get(config), ENTERPRISE_EAP);
			        }
			        //EAP Phase 2 Authentication
			        if(!noEnterpriseFieldType){
			            wcefSetValue.invoke(wcefPhase2.get(config), ENTERPRISE_PHASE2);
			        }
			        //EAP Identity
			        if(!noEnterpriseFieldType){
			            wcefSetValue.invoke(wcefIdentity.get(config), mIdentity);
			        }
			        //EAP Password
			        if(!noEnterpriseFieldType){
			            wcefSetValue.invoke(wcefPassword.get(config), mPassword);
			        }

			    } catch (Exception e){
			        // TODO Auto-generated catch block
			    	return null;
			    }

			}
			
			else{
				/*WifiEnterpriseConfig enterpriseConfig = new WifiEnterpriseConfig(); 
				enterpriseConfig.setIdentity(Identity);
				enterpriseConfig.setPassword(Password);
				enterpriseConfig.setEapMethod(WifiEnterpriseConfig.Eap.PEAP);
				enterpriseConfig.setPhase2Method(WifiEnterpriseConfig.Phase2.MSCHAPV2); 
				config.enterpriseConfig = enterpriseConfig;*/
			}
			
		}
		mWifiConfigurations.add(config);
		return config;
	}

}
