package com.uperone.wifi;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;

public class WifiAdmin {
    public WifiAdmin(Context context) {   
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);   
        mWifiInfo = mWifiManager.getConnectionInfo();   
    }   
   
    // ��WIFI    
    public void openWifi() {   
        if (!mWifiManager.isWifiEnabled()) {   
            mWifiManager.setWifiEnabled(true);   
        }   
    }   
   
    // �ر�WIFI    
    public void closeWifi() {   
        if (mWifiManager.isWifiEnabled()) {   
            mWifiManager.setWifiEnabled(false);   
        }   
    }   
   
    // ��鵱ǰWIFI״̬    
    public int checkState() {   
        return mWifiManager.getWifiState();   
    }   
   
    // ��WifiLock    
    public void acquireWifiLock() {   
        mWifiLock.acquire();   
    }   
   
    // ����WifiLock    
    public void releaseWifiLock() {   
        // �ж�ʱ����    
        if (mWifiLock.isHeld()) {   
            mWifiLock.acquire();   
        }   
    }   
   
    // ����һ��WifiLock    
    public void creatWifiLock() {   
        mWifiLock = mWifiManager.createWifiLock("Test");   
    }   
   
    // �õ����úõ�����    
    public List<WifiConfiguration> getConfiguration() {   
        return mWifiConfiguration;   
    }   
   
    // ָ�����úõ������������    
    public void connectConfiguration(int index) {   
        // ����������úõ����������    
        if (index > mWifiConfiguration.size()) {   
            return;   
        }   
        // �������úõ�ָ��ID������    
        mWifiManager.enableNetwork(mWifiConfiguration.get(index).networkId,   
                true);   
    }   
   
    public void startScan() {   
        mWifiManager.startScan();   
        // �õ�ɨ����    
        mWifiList = mWifiManager.getScanResults();   
        // �õ����úõ���������    
        mWifiConfiguration = mWifiManager.getConfiguredNetworks();   
    }   
   
    // �õ������б�    
    public List<ScanResult> getWifiList() {   
        return mWifiList;   
    }   
   
    // �鿴ɨ����    
    @SuppressLint("UseValueOf")
	public StringBuilder lookUpScan() {   
    	StringBuilder stringBuilder = new StringBuilder();   
        for (int i = 0; i < mWifiList.size(); i++) {   
        	stringBuilder.append("Index_" + new Integer(i + 1).toString() + ":");   
            // ��ScanResult��Ϣת����һ���ַ�� ���аѰ�����BSSID��SSID��capabilities��frequency��level    
            stringBuilder.append((mWifiList.get(i)).toString());   
            stringBuilder.append("/n");   
        }   
        return stringBuilder;   
    }  
   
    // �õ�MAC��ַ    
    public String getMacAddress() {   
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();   
    }   
   
    // �õ�������BSSID    
    public String getBSSID() {   
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();   
    }   
   
    // �õ�IP��ַ    
    public int getIPAddress() {   
        return (mWifiInfo == null) ? 0 : mWifiInfo.getIpAddress();   
    }   
   
    // �õ����ӵ�ID    
    public int getNetworkId() {   
        return (mWifiInfo == null) ? 0 : mWifiInfo.getNetworkId();   
    }   
    
    // �õ�WifiInfo��������Ϣ��    
    public WifiInfo getWifiInfo() {   
        return mWifiInfo;   
    }
   
    // ���һ�����粢����    
    public void addNetwork(WifiConfiguration wcg) {   
     int wcgID = mWifiManager.addNetwork(wcg);   
     boolean b =  mWifiManager.enableNetwork(wcgID, true);   
     System.out.println("a--" + wcgID);  
     System.out.println("b--" + b);  
    }   
   
    // �Ͽ�ָ��ID������    
    public void disconnectWifi(int netId) {   
        mWifiManager.disableNetwork(netId);   
        mWifiManager.disconnect();   
    }   
   
	// Ȼ����һ��ʵ��Ӧ�÷�����ֻ��֤��û������������
	// ��Ϊ���������1û������2��wep����3��wpa����
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
      
	private WifiConfiguration IsExsits(String SSID) {
		List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
		if (null == existingConfigs) {
			return null;
		}
		for (WifiConfiguration existingConfig : existingConfigs) {
			System.out.println("existingConfig == " + existingConfig.toString());
			if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
				System.out.println("existingConfig.SSID == " + existingConfig.SSID + " SSID == " + SSID);
				return existingConfig;
			}
		}
		
		return null;
	}
    
    public List<UserWifiInfo> getUserWifiInfoList( ) throws Exception {  
        List<UserWifiInfo> wifiInfos=new ArrayList<UserWifiInfo>();  
      
        Process process = null;  
        DataOutputStream dataOutputStream = null;  
        DataInputStream dataInputStream = null;  
        StringBuffer wifiConf = new StringBuffer();  
        try {  
            process = Runtime.getRuntime().exec("su");  
            dataOutputStream = new DataOutputStream(process.getOutputStream());  
            dataInputStream = new DataInputStream(process.getInputStream());  
            dataOutputStream.writeBytes("cat /data/misc/wifi/*.conf\n");  
            dataOutputStream.writeBytes("exit\n");  
            dataOutputStream.flush();  
            InputStreamReader inputStreamReader = new InputStreamReader(  
                    dataInputStream, "UTF-8");  
            BufferedReader bufferedReader = new BufferedReader(  
                    inputStreamReader);  
            String line = null;  
            while ((line = bufferedReader.readLine()) != null) {  
                wifiConf.append(line);  
            }  
            bufferedReader.close();  
            inputStreamReader.close();  
            process.waitFor();  
        } catch (Exception e) {  
            throw e;  
        } finally {  
            try {  
                if (dataOutputStream != null) {  
                    dataOutputStream.close();  
                }  
                if (dataInputStream != null) {  
                    dataInputStream.close();  
                }  
                process.destroy();  
            } catch (Exception e) {  
                throw e;  
            }  
        }     
      
          
        Pattern network = Pattern.compile("network=\\{([^\\}]+)\\}", Pattern.DOTALL);  
        Matcher networkMatcher = network.matcher(wifiConf.toString() );  
        while (networkMatcher.find() ) {  
            String networkBlock = networkMatcher.group();  
            Pattern ssid = Pattern.compile("ssid=\"([^\"]+)\"");  
            Matcher ssidMatcher = ssid.matcher(networkBlock);  
              
            if (ssidMatcher.find() ) {     
                UserWifiInfo wifiInfo=new UserWifiInfo();  
                wifiInfo.Ssid=ssidMatcher.group(1);  
                Pattern psk = Pattern.compile("psk=\"([^\"]+)\"");  
                Matcher pskMatcher = psk.matcher(networkBlock);  
                if (pskMatcher.find() ) {  
                    wifiInfo.Password=pskMatcher.group(1);  
                } else {  
                    wifiInfo.Password="";  
                }          
                wifiInfos.add(wifiInfo);  
            }  
              
        }  
  
        return wifiInfos;  
    }
    
    private WifiManager mWifiManager = null;   
    private WifiInfo mWifiInfo = null;   
    private List<ScanResult> mWifiList = null;   
    private List<WifiConfiguration> mWifiConfiguration = null;   
    private WifiLock mWifiLock = null; 
}  
