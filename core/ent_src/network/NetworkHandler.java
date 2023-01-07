package network;

import com.esotericsoftware.kryo.Kryo;

import Coordinator.Coordinator;
import client.ClientImageHandler;
import network.packets.Packet;

public abstract class NetworkHandler {

	final ClientImageHandler ClImgHandle;
	
	public NetworkHandler(ClientImageHandler ClImgHandle) {
		this.ClImgHandle = ClImgHandle;
	}
	
	public abstract void init() throws Exception;
	public abstract boolean is_ready();
	public abstract void disconnect(int conid);
//	public abstract boolean is_connected(int conid);
//	public abstract boolean is_disconnected(int conid);
	public abstract void send_packet(int conid, Packet packet,byte type);
	public abstract void close();
	public abstract Kryo get_kryo();
	public abstract boolean is_error();
}
