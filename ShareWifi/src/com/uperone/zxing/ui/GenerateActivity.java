package com.uperone.zxing.ui;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.uperone.wifi.UserWifiInfo;
import com.uperone.wifi.WifiAdmin;
import com.uperone.zxing.R;
import com.uperone.zxing.decoding.DecodeFormatManager;

public class GenerateActivity extends BaseActivity {
	private String result = "";
	
	@Override
	public void setContentView() {
		setContentView( R.layout.activity_generate_layout );
	}

	@Override
	public void findViews() {
		mResultImg = ( ImageView )findViewById( R.id.generateImgId );
		mDecodeReslutTxt = ( TextView )findViewById( R.id.decodeReslutTxtId );
	}

	@Override
	public void getData() {
		
	}

	@Override
	public void showConent() {
		WifiAdmin wifiAdmin = new WifiAdmin( this );
		wifiAdmin.startScan( );
		WifiInfo connectWifiInfo = wifiAdmin.getWifiInfo( );
		System.out.println( "connectWifiInfo == " + connectWifiInfo.toString( ) );
		List<ScanResult> scanResultList = wifiAdmin.getWifiList( );
		if( null != scanResultList ){
			String ssid = connectWifiInfo.getSSID( );
			for( ScanResult scanResult : scanResultList ){
				System.out.println( "scanResult ssid == " + scanResult.SSID + " ssid == " + ssid );
				if( ssid.equals( "\"" + scanResult.SSID +"\"") ){
					//result += "errorcode";
					result += scanResult.SSID;
					result += "#";
					
					try {
						List<UserWifiInfo> userWifiInfos = wifiAdmin.getUserWifiInfoList( );
						if( null != userWifiInfos ){
							for( UserWifiInfo userWifiInfo : userWifiInfos ){
								if( ssid.equals( "\"" + userWifiInfo.Ssid +"\"") ){
									//result += userWifiInfo.Password;
									result += "errorcode";
									result += "#";
									break;
								}
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					String capabilites = scanResult.capabilities;
					Toast.makeText( this, capabilites, Toast.LENGTH_LONG ).show( );
					if( capabilites.contains( "wpa" ) && capabilites.contains( "wep" ) ){
						result += "1";
					}else if( capabilites.contains( "wep" ) ){
						result += "2";
					}else{
						result += "3";
					}
					
					if( TextUtils.isEmpty( result ) ){
						Toast.makeText( this, "wifi", Toast.LENGTH_LONG ).show( );
						finish( );
						return;
					}else{
						generate( result );
					}
					break;
				}
			}
		}else{
			Toast.makeText( this, "wifi", Toast.LENGTH_LONG ).show( );
			finish( );
			return;
		}
	}
	
	@SuppressLint("NewApi")
	@SuppressWarnings("deprecation")
	private void generate( String result ){
		if( TextUtils.isEmpty( result ) ){
			return;
		}
		
		try{
            //判断result合法性  
            if (result == null || "".equals(result) || result.length() < 1){
                return;
            }
            Hashtable<EncodeHintType, String> hints = new Hashtable<EncodeHintType, String>();
            hints.put(EncodeHintType.CHARACTER_SET, "utf-8");
            //图像数据转换，使用了矩阵转换  
            BitMatrix bitMatrix = new QRCodeWriter().encode(result, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT, hints);
            int[] pixels = new int[QR_WIDTH * QR_HEIGHT];
            //下面这里按照二维码的算法，逐个生成二维码的图片，  
            //两个for循环是图片横列扫描的结果  
            for (int y = 0; y < QR_HEIGHT; y++){
                for (int x = 0; x < QR_WIDTH; x++){
                    if (bitMatrix.get(x, y)){
                        pixels[y * QR_WIDTH + x] = 0xff000000;
                    }else{
                        pixels[y * QR_WIDTH + x] = 0xffffffff;
                    }
                }
            }
            //生成二维码图片的格式，使用ARGB_8888  
            Bitmap bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, QR_WIDTH, 0, 0, QR_WIDTH, QR_HEIGHT);
            //显示到一个ImageView上面 
            mResultImg.setBackground( new BitmapDrawable( bitmap ) );
            
            decodeBitmap( bitmap );
        }catch (WriterException e) {
            e.printStackTrace();
        }
	}
	
	private String decodeBitmap( Bitmap bitmap ){
		MultiFormatReader multiFormatReader = new MultiFormatReader();  
        // 解码的参数  
        Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>(2);  
        // 可以解析的编码类型  
        Vector<BarcodeFormat> decodeFormats = new Vector<BarcodeFormat>();  
        if (decodeFormats == null || decodeFormats.isEmpty()) {  
            decodeFormats = new Vector<BarcodeFormat>();  
  
            // 这里设置可扫描的类型，我这里选择了都支持  
            decodeFormats.addAll(DecodeFormatManager.ONE_D_FORMATS);  
            decodeFormats.addAll(DecodeFormatManager.QR_CODE_FORMATS);  
            decodeFormats.addAll(DecodeFormatManager.DATA_MATRIX_FORMATS);  
        }  
        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);  
  
        // 设置继续的字符编码格式为UTF8  
        // hints.put(DecodeHintType.CHARACTER_SET, "UTF8");  
  
        // 设置解析配置参数  
        multiFormatReader.setHints(hints);  
  
        // 开始对图像资源解码  
        try {  
            Result rawResult = multiFormatReader.decodeWithState(new BinaryBitmap(new HybridBinarizer( new com.uperone.zxing.decoding.BitmapLuminanceSource(bitmap))));  
            mDecodeReslutTxt.setText(new StringBuilder().append("内容：")  
                    .append(rawResult.getText()).append("\n编码方式：")  
                    .append(rawResult.getBarcodeFormat()).toString());  
        } catch (NotFoundException e) {  
            e.printStackTrace();  
        }  
          
        return null;  
    }  
	
	
	private static final int QR_HEIGHT = 640, QR_WIDTH = 640;
	private ImageView mResultImg = null;
	private TextView mDecodeReslutTxt = null;
}
