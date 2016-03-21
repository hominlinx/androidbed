package wu.testjpeg;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Pattern;

import ShellUtils.ShellUtils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
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
import android.telephony.TelephonyManager;
import android.text.format.Formatter;
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
import android.widget.TextView;

import wu.lib.util.*;

public class MainActivity extends Activity  implements OnClickListener {
	private static final String TAG = "Hominlinx==>MainActivity";
    private MyApplication myApplication;  
  
    private TextView phoneInfo;
    private TextView test1;
    private Button btn1;
    private Button btn2;
    private Button btn3;
    static InputStream in;
    private static String mtype;
    private int cnt = 1;
    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	
		myApplication = (MyApplication)getApplication();  
		myApplication.setMainAct(this);  
		phoneInfo = (TextView) this.findViewById(R.id.phoneInfo );
		test1 = (TextView)this.findViewById(R.id.testView1);
		btn1 = (Button)this.findViewById(R.id.button1);
		btn2 = (Button)this.findViewById(R.id.button2);
		btn3 = (Button)this.findViewById(R.id.button3);
		btn1.setOnClickListener(this); 
		btn2.setOnClickListener(this); 
		btn3.setOnClickListener(this); 
		
		TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		mtype = android.os.Build.MODEL; // 手机型号  

		phoneInfo.setText(getCpuInfo());
		Log.d(TAG, "CCCCCCCCCCCCCc");
		//得到资源中的Raw数据流  
		
		String fileName = "test.jpg"; //文件名字   
		
		try {

			in = getResources().getAssets().open(fileName);
			int length = in.available();  
			//test1.setText(length);
			Log.d(TAG, "YYYYYYYYYYYYYYYYYYY" + length);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			Log.d(TAG, "XXXXXXXXXXXXXXXX");
			e1.printStackTrace();
		}   

		
	}
	
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		StringBuilder result = new StringBuilder();
		switch (v.getId()) {  
		case R.id.button1:
	
			//Util.getCpuInfo();
			String outFile = "/sdcard/mytest30.jpg";
			long temp = 0;
			for(int i = 0; i < cnt; ++i) {
				temp += compressBitmap(outFile, 30);
			}
			temp = temp / cnt;
			result.append("test1 using:").append(temp).append("\n");
			test1.setText(result);
			Log.d(TAG, "start");
	
			break;
		case R.id.button2:
			String outFile2 = "/sdcard/mytest50.jpg";
			long temp2 = compressBitmap(outFile2, 50);
			result.append("test2 using:").append(temp2).append("\n");
			//test1.setText("test1 using : " + temp2 + "ms");
			test1.setText(result);
			Log.d(TAG, "stop");
			break;
		case R.id.button3:
			String outFile3 = "/sdcard/mytest70.jpg";
			long temp3 = compressBitmap(outFile3, 70);
			
			result.append("test3 using:").append(temp3).append("\n");
			test1.setText(result);
			Log.d(TAG, "testStart");
			break;
		}
	}
	
	
	 // 压缩 缩放 到指定文件中
	public static long compressBitmap(String outFile, int quailty) {

		
		
		Bitmap bitmap = BitmapFactory.decodeStream(in); //

		FileOutputStream b = null;
		try {
			b = new FileOutputStream(outFile);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		long start = System.currentTimeMillis();
		
		bitmap.compress(Bitmap.CompressFormat.JPEG,quailty, b);

		long end = System.currentTimeMillis();
		long temp = end - start;
		Log.i(TAG, "time cost:" + (end - start));
		
		try {
			b.flush();
			b.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return temp;
	}
	
	
	
	
	// 实时获取CPU当前频率（单位KHZ）
	public static String getMaxCpuFreq() {
		String result = "";
		ProcessBuilder cmd;
		try {
			String[] args = { "/system/bin/cat",
			"/sys/devices/system/cpu/cpu0/cpufreq/cpuinfo_max_freq" };
			cmd = new ProcessBuilder(args);
			Process process = cmd.start();
			InputStream in = process.getInputStream();
			byte[] re = new byte[24];
			while (in.read(re) != -1) {
				result = result + new String(re);
			}
			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
			result = "N/A";
		}
		return result.trim();
	}

	/**
	* Gets the number of cores available in this device, across all processors.
	* Requires: Ability to peruse the filesystem at "/sys/devices/system/cpu"
	* @return The number of cores, or 1 if failed to get result
	*/
	private static int getNumCores() {
		//Private Class to display only CPU devices in the directory listing
		class CpuFilter implements FileFilter {
			@Override
			public boolean accept(File pathname) {
				//Check if filename is "cpu", followed by a single digit number
				if(Pattern.matches("cpu[0-9]", pathname.getName())) {
					return true;
				}
				return false;
			}
		}

		try {
			//Get directory containing CPU info
			File dir = new File("/sys/devices/system/cpu/");
			//Filter to only list the devices we care about
			File[] files = dir.listFiles(new CpuFilter());
			//Return the number of cores (virtual CPU devices)
			return files.length;
		} catch(Exception e) {
			//Default to return 1 core
			return 1;
		}
	}
	
	/**
	 * 获取手机CPU信息
	 * <p/>
	 * 和内存信息同理，cpu信息可通过读取/proc/cpuinfo文件来得到，其中第一行为cpu型号，第二行为cpu频率。
	 *
	 * @return
	 */
	public static String getCpuInfo() {
		String str1 = "/proc/cpuinfo";
		
		String str2 = "";
		String[] cpuInfo = {"", ""}; // 1-cpu型号 //2-cpu频率
		String[] arrayOfString;
		try {
			FileReader fr = new FileReader(str1);
			BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
			str2 = localBufferedReader.readLine();
			arrayOfString = str2.split("\\s+");
			for (int i = 2; i < arrayOfString.length; i++) {
				cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";
			}
			str2 = localBufferedReader.readLine();
			arrayOfString = str2.split("\\s+");
			cpuInfo[1] += arrayOfString[2];
			localBufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		StringBuilder result = new StringBuilder();
		result.append("cpu型号：").append(cpuInfo[0]).append("\n");
		result.append("cpu核数：").append(getNumCores()).append("\n");
		result.append("cpu频率：").append(getMaxCpuFreq()).append("KHZ");
		

		long mTotalMem = 0; 
		String str3 = "/proc/meminfo";
		String[] memOfString;
		try {
			

			FileReader localFileReader = new FileReader(str3);  
			BufferedReader localBufferedReader = new BufferedReader(localFileReader, 8192);  
			str2 = localBufferedReader.readLine();  
			memOfString = str2.split("\\s+");  
			mTotalMem = Integer.valueOf(memOfString[1]).intValue() * 1024;  
			localBufferedReader.close();  

		} catch (IOException e) {
			e.printStackTrace();
		}
		result.append("\n").append(str2);
		
		Log.w("CpuInfoUtil", result.toString());
		return result.toString();
	}

	
}
	