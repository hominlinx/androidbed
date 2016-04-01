package com.autoio.lib.net;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;

import com.autoio.lib.util.Util;

import android.util.Log;

public class TcpResponse implements ISocketResponse {
	private static final String TAG = "Hominlinx==>TcpResponse";

//	StringBuilder m_buffer = new StringBuilder(""); // 缓存上一次或多次的未处理的数据, 这个用来处理，重新粘包
//	
//	String m_recvData; // 接收到的数据
	int TCP_MINSIZE = 2; // block header: type
	int TCP_FRAMEREQ_LEN = 10;
	
	@Override
	public void onSocketResponse(byte[] txt, int len) {
		// TODO Auto-generated method stub
		
		ArrayList list = new ArrayList(); 
		for(int i = 0; i < len; ++i) {
			list.add(txt[i]);
		}
		//list.add(txt);
		Log.d(TAG,"XXXXXXXX+++++++++" + list.size() + "," + list.get(1));
		byte t = (Byte) list.get(1);
		if (len != TCP_FRAMEREQ_LEN) {
			return;
		}
		if(txt[1] == 0x03) {
			short w = Util.getShort(txt, 6);
			short h = Util.getShort(txt, 8);
			Log.d(TAG, "XXXXXXXXXXXXXXXXXXX:" + w + "," + h );
			if (w > 0 && h > 0) {
				Util.setWidthAndHeight(w, h);
			}
			
		}
		
	}
	
//	private int handleFrameReq() {
//		int buflen = m_buffer.length();
//		if (buflen < TCP_FRAMEREQ_LEN) {
//			return -1;
//		}
//		
//		String sub = m_buffer.substring(6);
//		//
//		Log.d(TAG, "handleFrameReq , sub:" + sub.length());
//		if (sub.length() == 4) {
//			short w = Util.getShort(sub.getBytes(), 0);
//			short h = Util.getShort(sub.getBytes(), 2);
//			Util.setWidthAndHeight(w, h);
//			Log.d(TAG, "XXXXXXXXXXXXXXXXXXX:" + w + "," + h );
//		}
//		return TCP_FRAMEREQ_LEN;
//		
//	}
}
