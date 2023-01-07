package simulation;

import handlers.basic.Timer;
import network.packets.Packet;

public class GameFlowPacket extends Packet
{
	public byte id;
	public String message;
	public byte type;
//	public Timer timer;
	public void set(String msg,byte type)
	{
		this.message = msg;
		this.type = type;
//		this.timer = timer;
	}
}