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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

	private static int height = 0;
	private static int width = 0;
	private static int screenHeight = 0;
	private static int screenWidth = 0;
	private static int deepth = 0;
	final static String FB0FILE1 = "/dev/graphics/fb0";
	final static String OUTFILE = "/sdcard/my/test.jpg";
	static File fbFile;
	static FileInputStream graphics = null;
	static DataInputStream dStream=null;
	static byte[] piex=null;
	static int colorSize = 0;
	
	
	/**
	 * 测试截图
	 */
	@SuppressLint("SdCardPath")
	public static void testShot() {
		long start = System.currentTimeMillis();
	
		SimpleDateFormat sdf = new SimpleDateFormat(
				"yyyy-MM-dd_HH-mm-ss", Locale.US); 
		String outname = "/sdcard/my/" + sdf.format(new Date()) + ".jpg";
		
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
//		try {
//
//			Runtime.getRuntime().exec(
//
//					new String[] { "su", "-c","chmod 777 /dev/graphics/fb0" });
//
//		} catch (IOException e) {
//
//			e.printStackTrace();
//
//		}
		// 获取屏幕大小：
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager WM = (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
		Display display = WM.getDefaultDisplay();
		display.getMetrics(metrics);

		height = metrics.heightPixels; // 屏幕高
		width = metrics.widthPixels; // 屏幕的宽

		Log.d(TAG, "================ height:" + height + ",width:" + width);
		
		PixelFormat localPixelFormat1 = new PixelFormat();

		@SuppressWarnings("deprecation")
		int pixelformat = display.getPixelFormat();  
		PixelFormat.getPixelFormatInfo(pixelformat, localPixelFormat1);

		deepth = localPixelFormat1.bytesPerPixel;// 位深
		Log.d(TAG, "================pixelformat:" + pixelformat + ",deepth:" + deepth);
		screenHeight = height;
		screenWidth = width + 8;
		colorSize = screenHeight * screenWidth  ;
		piex = new byte[colorSize  * deepth]; // Just for meizu-note3
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
		
		long start = System.currentTimeMillis();
		
		try {
			graphics = new FileInputStream(fbFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		DataInputStream dStream = new DataInputStream(graphics);
		dStream.readFully(piex);
		dStream.close();

		int[] colors = new int[colorSize]; // 这个只适用于魅族note2.
		// 将rgba转为色值
		for (int m = 0; m < screenHeight ; m++ ) {
			for (int n = 0; n < screenWidth; n++) {
				int r = (piex[(m * screenWidth + n) *4] & 0xFF);
				int g = (piex[(m * screenWidth + n) *4 + 1] & 0xFF);
				int b = (piex[(m * screenWidth + n) *4 + 2] & 0xFF);
				int a = (piex[(m * screenWidth + n) *4 + 3] & 0xFF);
				//colors[m* screenWidth + n] = (a << 24) + (r << 16) + (g << 8) + b;
				colors[(screenHeight - 1 -m )* screenWidth + ( screenWidth - 1 - n)] = (a << 24) + (r << 16) + (g << 8) + b;
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
		long end = System.currentTimeMillis();
		Log.i(TAG, " shot screen time cost:" + (end - start));
		return Bitmap.createBitmap(colors, (width + 8), height,
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


}
