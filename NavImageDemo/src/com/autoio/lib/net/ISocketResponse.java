package com.autoio.lib.net;

/**
* socket回调
* @author Hominlinx
*
*/
public interface ISocketResponse {
	//public abstract void onSocketResponse(String txt);
	public abstract void onSocketResponse(byte[] txt, int len);
}