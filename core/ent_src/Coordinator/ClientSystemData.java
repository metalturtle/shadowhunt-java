package Coordinator;

import java.util.LinkedList;
import java.util.Queue;

import handlers.basic.Timer;
import network.packets.Packet;

public class ClientSystemData {

	private Queue<SystemPacket> recievedQueue = new LinkedList<SystemPacket>();
//	private Queue<SystemPacket> sendQueue = new LinkedList<SystemPacket>();
	private int SENT_SEQUENCE,ACK_SEQUENCE,LATEST_RECIEVED_SEQUENCE;
	private Object retrieve_lock = new Object();
	private byte state;
	private boolean state_changed_flag=true;
	Timer disconnectTimer = new Timer();
	
	/********** SEQUENCE **********/
	
	public void update_sequence(Packet packet) {
		LATEST_RECIEVED_SEQUENCE = Math.max(LATEST_RECIEVED_SEQUENCE, packet.sequence);
		ACK_SEQUENCE = Math.max(ACK_SEQUENCE,packet.ack_sequence);
	}
	
	public int get_latest_recieved_sequence() {
		return LATEST_RECIEVED_SEQUENCE;
	}
	
	public int get_sequence() {
		return SENT_SEQUENCE;
	}
	
	public int get_ack_sequence() {
		return ACK_SEQUENCE;
	}
	
	/********** SEND/RECIEVE PACKETS **********/
	
	public void recieve_packet(SystemPacket packet) {
		synchronized(retrieve_lock) {
			recievedQueue.add(packet);
		}
	}
	
	public void retrive_recieved_packets(Queue<SystemPacket> systemQueue) {
		synchronized(retrieve_lock) {
			systemQueue.addAll(recievedQueue);
			recievedQueue.clear();
		}
	}
	
	public SystemPacket create_system_packet(byte type, byte code, String message) {
		SystemPacket systemPacket = new SystemPacket();
		SENT_SEQUENCE+=1;
		systemPacket.sequence = SENT_SEQUENCE;
		systemPacket.ack_sequence = LATEST_RECIEVED_SEQUENCE;
		systemPacket.type = type;
		systemPacket.code = code;
		systemPacket.message = message;
		System.out.println("sending packet: "+code);
//		sendQueue.add(systemPacket);
		return systemPacket;
	}
	
//	public void get_send_queue(Queue<SystemPacket> sq) {
//		sq.addAll(sendQueue);
//		sendQueue.clear();
//	}
	
	/********** STATE METHODS **********/
	
	public void set_state(byte state) {
		this.state = state;
		state_changed_flag=true;
	}
	
	protected boolean is_state_changed() {
		return state_changed_flag;
	}
	
	protected void off_state_change() {
		this.state_changed_flag = false;
	}
	
	public boolean state_equals(byte state) {
		return this.state == state;
	}
	
	public byte get_state() {
		return this.state;
	}
}
