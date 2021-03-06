package wu.lib.util;

import wu.lib.util.AtomicIntegerUtil;

public class Packet {
	
	private int id=AtomicIntegerUtil.getIncrementID();
	private byte[] data;

	public int getId() {
		return id;
	}

	public void pack(String txt)
	{
		data=txt.getBytes();
	}
	
	public void pack(byte[] temp)
	{
		data = temp;
	}

	public byte[] getPacket()
	{
		return data;
	}

	
}
