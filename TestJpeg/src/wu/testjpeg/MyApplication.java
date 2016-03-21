package wu.testjpeg;


import android.app.Application;
import android.util.Log;

public class MyApplication extends Application {
	private static final String TAG = "Hominlinx==>MyApplication";
	MainActivity mainActivity;


	public MainActivity getMainAct() {  
		return mainActivity;  
	}  

	public void setMainAct(MainActivity mainAct) {  
		this.mainActivity = mainAct;  
	}  


	@Override  
	public void onCreate() {  
		super.onCreate();  
		Log.v(TAG, "onCreate");  
	}  
	  
}
