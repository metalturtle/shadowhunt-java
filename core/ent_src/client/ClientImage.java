package client;


import Coordinator.ClientSystemData;
import handlers.basic.ClientCommand;
import handlers.basic.Timer;
import network.NetworkInfo;
import network.packets.Packet;
import simulation.ClientSimulationState;


public class ClientImage {
	
	int CONNECTION_ID,local_id;
	
	private int state;
	Object state_change_lock=new Object();
	
	public Timer disconnectTimer = new Timer();
	
	class ClientState {
		public final static byte START=0,CONNECTED=1,DISCONNECT=2;
	}
	
	/********** CLIENT SYSTEM STATES **********/
	
	public ClientSystemData CSData=new ClientSystemData();
	public ClientSimulationState CSimState = new ClientSimulationState();
	
	/*****************************************/
	
	public ClientImage(int CONNECTION_ID,int local_id)
	{
		this.CONNECTION_ID = CONNECTION_ID;
		this.local_id = local_id;
	}
	
	/********** ID **********/
	
	public int get_local_id() {return local_id;}
	
	public int get_con_id() {
		return CONNECTION_ID;
	}
	
	/********** STATE **********/
	
	long ERROR_TIMESTAMP;
	final long ERROR_DURATION=5000;
	String disconnect_message="";

	public void set_connect() {
		this.state = ClientState.CONNECTED;
	}
	
	public void set_disconnect() {
		this.state = ClientState.DISCONNECT;
		disconnectTimer.set(5000);
	}
	
	public boolean is_connected() {
		return this.state == ClientState.CONNECTED;
	}
	
	public boolean is_disconnected() {
		return this.state == ClientState.DISCONNECT;
	}
	
	/********** CONNECTION STATISTICS **********/
	volatile short tcpRTT,udpRTT;
	
	public long get_tcp_rtt() {
		return tcpRTT;
	}
	
	public long get_udp_rtt() {
		return udpRTT;
	}
	
	public void set_udp_rtt(short rtt) {
		this.udpRTT = rtt;
	}
}
