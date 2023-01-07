package Coordinator;

import network.packets.Packet;

public class SystemPacket extends Packet {
	public byte type;
	public int code;
	public String message;
	public static final byte STATE_CHANGE=0;
}
