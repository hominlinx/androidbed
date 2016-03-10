package wu.testandroidscreenshot;

import wu.lib.util.TcpSend;
import wu.lib.util.udpReceiveAndtcpConnect;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class ScreenTestService extends Service {
	public static final String TAG = "Hominlinx==>ScreenTestService";
	
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override  
	public void onCreate() {  
		super.onCreate();  
		Log.d(TAG, "onCreate() executed");  
	}  

	@Override  
	public int onStartCommand(Intent intent, int flags, int startId) {  
		Log.d(TAG, "onStartCommand() executed"); 		
		return super.onStartCommand(intent, flags, startId);  
		
	}  

	@Override  
	public void onDestroy() {  
		super.onDestroy();  
		Log.d(TAG, "onDestroy() executed");  
	}  
}
