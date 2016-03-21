package wu.lib.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
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

import ShellUtils.ShellUtils;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.graphics.Point;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

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

	private static int height = 0; // bitmap 的高度
	private static int width = 0;  // bitmap 的宽度
	private static int screenHeight = 0; // 屏幕的高度
	private static int screenWidth = 0;  // 屏幕的宽度
	/*
	 * 从屏幕中心点向x，y进行扩展。扩展宽度为ex_width, 扩展高度为ex_height.（注意这个是一个扩展方向）
	 */
	private static int center_x = 0;
	private static int center_y = 0;
	//private static int ex_width = 335;
	//private static int ex_height = 200;
	private static int ex_width = 350;
	private static int ex_height = 200;
	private static int min_x = 0;
	private static int max_x = 0;
	private static int min_y = 0;
	private static int max_y = 0;
	private static int deepth = 0;
	final static String FB0FILE1 = "/dev/graphics/fb0";
	final static String OUTFILE = "/storage/sdcard1/my/test.jpg";
	static File fbFile;
	static FileInputStream graphics = null;
	static DataInputStream dStream=null;
	static byte[] piex=null;
	static int colorSize = 0;
	static int offset = 16; //xiaomi
	static boolean bRotate = false;
	//static int offset = 8; //meizu
	//static boolean bRotate = true;
	/**
	 * 测试截图
	 */
	@SuppressLint("SdCardPath")
	public static void testShot() {
		long start = System.currentTimeMillis();
	
		SimpleDateFormat sdf = new SimpleDateFormat(
				"yyyy-MM-dd_HH-mm-ss", Locale.US); 
		String outname = "/storage/sdcard1/my/" + sdf.format(new Date()) + ".jpg";
		
		try {
			Bitmap bm = getScreenBitmap();
			saveMyBitmap(bm, outname);
		} catch (IOException e) {
			e.printStackTrace();
		}
		long end = System.currentTimeMillis();
		Log.i(TAG, "time cost:" + (end - start));
	}
	
	/**
	* 保存bitmap到文件
	*
	* @param bitmap
	* @param bitName
	* @throws IOException
	*/
	public static void saveMyBitmap(Bitmap bitmap, String bitName)
			throws IOException {
		long start = System.currentTimeMillis();
		
	//	Bitmap b = rotate(bitmap, 180);
		File f = new File(bitName);
		f.createNewFile();
		FileOutputStream fOut = new FileOutputStream(f);
		
//		ByteArrayOutputStream baos = null ;  
//		try{  
//			baos = new ByteArrayOutputStream();  
//			bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);  
//
//		}finally{  
//			try {  
//				if(baos != null)  
//					baos.close() ;  
//			} catch (IOException e) {  
//				e.printStackTrace();  
//			}  
//		}  
		bitmap.compress(Bitmap.CompressFormat.JPEG, 30, fOut);
		fOut.flush();
		fOut.close();
		
		long end = System.currentTimeMillis();
		Log.i(TAG, " save bitmap time cost:" + (end - start) + "...");
	}
	

	public static Bitmap rotate(Bitmap b, int degrees) {
		if (degrees != 0 && b != null) {
			Matrix m = new Matrix();
			m.setRotate(degrees,
					(float) b.getWidth() / 2, (float) b.getHeight() / 2);
			try {
				Bitmap b2 = Bitmap.createBitmap(
						b, 0, 0, b.getWidth(), b.getHeight(), m, true);
				if (b != b2) {
					b.recycle();  //Android开发网再次提示Bitmap操作完应该显示的释放
					b = b2;
				}
			} catch (OutOfMemoryError ex) {
				// Android123建议大家如何出现了内存不足异常，最好return 原始的bitmap对象。.
			}
		}
		return b;
	}

	public static void initScreen(Activity activity) {
		////
		Log.d(TAG, "==============================");
		fbFile = new File(FB0FILE1);
		// 初始化事件文件的权限
		//
		ShellUtils.execCommand("chmod 777 /dev/graphics/fb0", true);
		// 获取屏幕大小：
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager WM = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
		Display display = WM.getDefaultDisplay();
		display.getMetrics(metrics);
		center_x = metrics.widthPixels / 2;
		center_y = metrics.heightPixels / 2;
		max_x = center_x + ex_width;
		min_x = center_x - ex_width;
		max_y = center_y + ex_height;
		min_y = center_y - ex_height;
		width = 2 * ex_width;
		height = 2 * ex_height;	

		Log.d(TAG, "screen's width:" + metrics.widthPixels + ",height:" + metrics.heightPixels);
		Log.d(TAG, "bitmap's width:" + width + ", height:" + height);
		Log.d(TAG, "min_x:" + min_x + ", max_x:" + max_x + ", min_y:" + min_y + ", max_y:" + max_y);
		
		PixelFormat localPixelFormat1 = new PixelFormat();

		@SuppressWarnings("deprecation")
		int pixelformat = display.getPixelFormat();  
		PixelFormat.getPixelFormatInfo(pixelformat, localPixelFormat1);

		deepth = localPixelFormat1.bytesPerPixel;// 位深
		Log.d(TAG, "screen's pixelformat:" + pixelformat + ",deepth:" + deepth);
		screenHeight = metrics.heightPixels;
		screenWidth = metrics.widthPixels + offset;
		
		colorSize = width * height;
		//colorSize = screenHeight * screenWidth  ;
		piex = new byte[screenHeight *  screenWidth * deepth]; // Just for meizu-note3
	}
	
	/**

	 * 获取当前屏幕截图，一定要先init（只有一次）。

	 * @param activity

	 * @return
	 * @throws IOException 

	 */
	@SuppressLint("NewApi")
	public static Bitmap getScreenBitmap() throws IOException {

		// 获取屏幕大小：
		// 获取显示方式
		
		//long start = System.currentTimeMillis();
		
		try {
			graphics = new FileInputStream(fbFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		DataInputStream dStream = new DataInputStream(graphics);
		dStream.readFully(piex);
		dStream.close();
		
		int[] colors = new int[screenHeight *  screenWidth]; // 
		// 将rgba转为色值
		for (int m = 0; m < screenHeight ; m++ ) {
			for (int n = 0; n < screenWidth; n++) {
				int r = (piex[(m * screenWidth + n) *4] & 0xFF);
				int g = (piex[(m * screenWidth + n) *4 + 1] & 0xFF);
				int b = (piex[(m * screenWidth + n) *4 + 2] & 0xFF);
				int a = (piex[(m * screenWidth + n) *4 + 3] & 0xFF);
				if (bRotate) {
					colors[(screenHeight - 1 -m )* screenWidth + ( screenWidth - 1 - n)] = (a << 24) + (r << 16) + (g << 8) + b;
				} else {
					colors[m* screenWidth + n] = (a << 24) + (r << 16) + (g << 8) + b;
					
				}
				
				//colors[(screenHeight - 1 -m )* screenWidth + ( screenWidth - 1 - n)] = (a << 24) + (r << 16) + (g << 8) + b;
			}
		}
		// 保存图片
		// piex生成Bitmap
		//long end = System.currentTimeMillis();
		//Log.i(TAG, " shot screen time cost:" + (end - start));
		return Bitmap.createBitmap(colors, (screenWidth ), screenHeight,
				Bitmap.Config.ARGB_8888);
	}
	
	
	/**

	 * 获取当前屏幕截图，一定要先init（只有一次）。

	 * @param activity

	 * @return
	 * @throws IOException 

	 */
	@SuppressLint("NewApi")
	public static Bitmap getClipScreenBitmap() throws IOException {

		// 获取屏幕大小：
		// 获取显示方式
		
		//long start = System.currentTimeMillis();
		
		try {
			graphics = new FileInputStream(fbFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		DataInputStream dStream = new DataInputStream(graphics);
		dStream.readFully(piex);
		dStream.close();
		
		int[] colors = new int[colorSize]; // 
		// 将rgba转为色值
//		for (int m = 0; m < screenHeight ; m++ ) {
//			for (int n = 0; n < screenWidth; n++) {
//				int r = (piex[(m * screenWidth + n) *4] & 0xFF);
//				int g = (piex[(m * screenWidth + n) *4 + 1] & 0xFF);
//				int b = (piex[(m * screenWidth + n) *4 + 2] & 0xFF);
//				int a = (piex[(m * screenWidth + n) *4 + 3] & 0xFF);
//				colors[m* screenWidth + n] = (a << 24) + (r << 16) + (g << 8) + b;
//				//colors[(screenHeight - 1 -m )* screenWidth + ( screenWidth - 1 - n)] = (a << 24) + (r << 16) + (g << 8) + b;
//			}
//		}
		
		for (int m = min_y; m < max_y; m++) {
			for (int n = min_x; n < max_x; n++) {
				int r = (piex[(m * screenWidth + n) * 4] & 0xFF);
				int g = (piex[(m * screenWidth + n) *4 + 1] & 0xFF);
				int b = (piex[(m * screenWidth + n) *4 + 2] & 0xFF);
				int a = (piex[(m * screenWidth + n) *4 + 3] & 0xFF);
				//colors[m* screenWidth + n] = (a << 24) + (r << 16) + (g << 8) + b;
				if(bRotate) {
					colors[(max_y - 1 -m )* width + ( max_x - 1 - n)] = (a << 24) + (r << 16) + (g << 8) + b;
				} else {
					colors[(m - min_y) * width + (n - min_x)] = (a << 24) + (r << 16) + (g << 8) + b;
				}
				
				
			}
		}
//		for (int m = 0; m < colors.length; m++) {
//			int r = (piex[m * 4] & 0xFF);
//			int g = (piex[m * 4 + 1] & 0xFF);
//			int b = (piex[m * 4 + 2] & 0xFF);
//			int a = (piex[m * 4 + 3] & 0xFF);
//			colors[m] = (a << 24) + (r << 16) + (g << 8) + b;
//		}
		// 保存图片
		// piex生成Bitmap
		//long end = System.currentTimeMillis();
		//Log.i(TAG, " shot screen time cost:" + (end - start));
		return Bitmap.createBitmap(colors, (width ), height,
				Bitmap.Config.ARGB_8888);
	}

	public void screenShot(String path){
		Process process = null;
		try{
			process = Runtime.getRuntime().exec("su");
			PrintStream outputStream = null;
			try {
				outputStream = new PrintStream(new BufferedOutputStream(process.getOutputStream(), 8192));
				outputStream.println("screencap  " + path);
				outputStream.flush();
			}catch(Exception e){
				e.printStackTrace();
			} finally {
				if (outputStream != null) {
					outputStream.close();
				}
			}
			process.waitFor();
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			if(process != null){
				process.destroy();
			}
		}
	}

	/**
	  * 将长整型转换为byte数组
	  * @param n
	  * @return
	  */
	 public static byte[] longToBytes(long n)
	 {
		 byte[] b = new byte[8];
		 b[7] = (byte) (n & 0xff);
		 b[6] = (byte) (n >> 8 & 0xff);
		 b[5] = (byte) (n >> 16 & 0xff);
		 b[4] = (byte) (n >> 24 & 0xff);
		 b[3] = (byte) (n >> 32 & 0xff);
		 b[2] = (byte) (n >> 40 & 0xff);
		 b[1] = (byte) (n >> 48 & 0xff);
		 b[0] = (byte) (n >> 56 & 0xff);
		 return b;
	 }
	 
	 public static byte[] intToBytes(int n)
	 {
		 byte[] b = new byte[4];
		 b[3] = (byte) (n & 0xff);
		 b[2] = (byte) (n >> 8 & 0xff);
		 b[1] = (byte) (n >> 16 & 0xff);
		 b[0] = (byte) (n >> 24 & 0xff);
		 return b;
	 }


	 public static String bytesToHexString(byte[] src, int len){  
			StringBuilder stringBuilder = new StringBuilder("");  
			if (src == null || src.length <= 0) { 
				Log.d(TAG, "XXXXX" + src + ",len" + src.length);
				return null;  
			}  
			for (int i = 0; i < len; i++) {  
				int v = src[i] & 0xFF;  
				String hv = Integer.toHexString(v); 
				
				if (hv.length() < 2) {  
					stringBuilder.append(0);  
				}  				
				stringBuilder.append(hv);  

			}  

			return stringBuilder.toString();  
		}  
	 
	public static String getLocalHostIp() {
	        String ipaddress = "";
	        try {
	            Enumeration<NetworkInterface> en = NetworkInterface
	                    .getNetworkInterfaces();
	            // 遍历所用的网络接口
	            while (en.hasMoreElements()) {
	                NetworkInterface nif = en.nextElement();// 得到每一个网络接口绑定的所有ip
	                Enumeration<InetAddress> inet = nif.getInetAddresses();
	                // 遍历每一个接口绑定的所有ip
	                while (inet.hasMoreElements()) {
	                    InetAddress ip = inet.nextElement();
	                    if (!ip.isLoopbackAddress()
	                            && InetAddressUtils.isIPv4Address(ip
	                            .getHostAddress())) {
	                        return ip.getHostAddress();
	                    }
	                }
	            }
	        }
	        catch(SocketException e)
	        {
	            Log.e(TAG, "获取本地ip地址失败");
	            e.printStackTrace();
	        }
	        return ipaddress;
	    }
	
	public static byte[] convertIpTobytes(String strIp) {
		if (strIp == null) {
			return null;
		}
		byte[] ip = new byte[4];
		String [] ipb=strIp.split("\\.");
		ip[0]=(byte)Integer.parseInt(ipb[0]);
		ip[1] = (byte)Integer.parseInt(ipb[1]);
		ip[2] = (byte)Integer.parseInt(ipb[2]);
		ip[3] = (byte)Integer.parseInt(ipb[3]);
		
//		ip[3]=(byte)Integer.parseInt(ipb[0]);
//		ip[2] = (byte)Integer.parseInt(ipb[1]);
//		ip[1] = (byte)Integer.parseInt(ipb[2]);
//		ip[0] = (byte)Integer.parseInt(ipb[3]);
		return ip;

	}
	
	public static String modifyIP(String strIp) {
		if (strIp == null) {
			return null;
		}
		byte[] ip = new byte[4];
		String [] ipb=strIp.split("\\.");
		ip[0]=(byte)Integer.parseInt(ipb[0]);
		ip[1] = (byte)Integer.parseInt(ipb[1]);
		ip[2] = (byte)Integer.parseInt(ipb[2]);
		//ip[3] = (byte)Integer.parseInt(ipb[3]);
		ip[3] = (byte)255;
		
		StringBuilder stringBuilder = new StringBuilder("");  
		if (ip == null || ip.length <= 0) { 
			
			return null;  
		}  
		for (int i = 0; i < ip.length; i++) {  
			int v = ip[i] & 0xFF;  
			String hv = Integer.toString(v); 
			
//			if (hv.length() < 2) {  
//				stringBuilder.append(0);  
//			}  			
			if ( i != 0) {
				stringBuilder.append("."); 
			}
			stringBuilder.append(hv); 
			

		}  

		return stringBuilder.toString();  
	}
	
}
