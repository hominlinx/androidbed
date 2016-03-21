package wu.lib.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;

import org.apache.http.conn.util.InetAddressUtils;

import wu.testjpeg.R;

import ShellUtils.ShellUtils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.text.format.Formatter;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.telephony.CellLocation;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;

/**
 * 工具类，比如：
 * 通过读取文件 /dev/graphics/fb0获取屏幕截图
 * 截图获取bitmap只需要100ms左右
 *
 * @author hominlinx
 * @data 2016-3-1
 */
public class Util {
	
	private static final String TAG = "Hominlinx===>Util:Screenshot";
	private static final String TELEPHONY_SERVICE = null;
	
	
	


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
		result.append("cpu型号：").append(cpuInfo[0]).append("\n").append("cup频率：").append(cpuInfo[1]);
		

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
