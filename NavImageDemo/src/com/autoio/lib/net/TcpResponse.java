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
	ArrayList m_list = new ArrayList(); 
//	String m_recvData; // 接收到的数据
	int TCP_MINSIZE = 2; // block header: type
	int TCP_FRAMEREQ_LEN = 10;
	
	
	@Override
	public void onSocketResponse(byte[] txt, int len) {
		// TODO Auto-generated method stub
		Log.d(TAG,"onSocketResponse+++++++++" + m_list.size());

		for(int i = 0; i < len; ++i) {
			m_list.add(txt[i]);
		}
		//list.add(txt);
		Log.d(TAG,"XXXXXXXX+++++++++" + m_list.size() + "," + m_list.get(1));
		int totalLen = m_list.size();
		short typeId = 0;
		int ret = -1;
		while(totalLen > 0) {
			if ( totalLen < TCP_MINSIZE) {
				break;
			}
			typeId = Util.getShort(m_list, 0);
			Log.d(TAG, "onSocketResponse===========: id:" + typeId);
			switch(typeId) {
			case 0x03:	
				ret = handleFrameReq();
				break;
			default :
				ret = -2;
			}
	
			if (ret == -1) {
				break;
			}
			if (ret == -2) {
				m_list.clear();
				break;
			}
			for(int i = ret - 1; i >= 0; --i) {
				m_list.remove(i);
			}
			totalLen = m_list.size();
		}
		
		
		
       
//		byte t = (Byte) list.get(1);
//		if (len != TCP_FRAMEREQ_LEN) {
//			return;
//		}
//		if(txt[1] == 0x03) {
//			short w = Util.getShort(txt, 6);
//			short h = Util.getShort(txt, 8);
//			Log.d(TAG, "XXXXXXXXXXXXXXXXXXX:" + w + "," + h );
//			if (w > 0 && h > 0) {
//				Util.setWidthAndHeight(w, h);
//			}
//			
//		}
		
	}
	
	private int handleFrameReq() {
		int bufLen = m_list.size();
		if (bufLen < TCP_FRAMEREQ_LEN) {
			return -1;
		}
		short w = Util.getShort(m_list, 6);
		short h = Util.getShort(m_list, 8);
		Log.d(TAG, "XXXXXXXXXXXXXXXXXXX:" + w + "," + h );
		if (w > 0 && h > 0) {
			Util.setWidthAndHeight(w, h);
		}
		return TCP_FRAMEREQ_LEN;
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
