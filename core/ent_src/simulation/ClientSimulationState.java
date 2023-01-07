package simulation;

import java.util.ArrayList;
import java.util.LinkedList;

import Coordinator.SystemPacket;
import handlers.basic.ClientCommand;
import handlers.basic.Timer;
import handlers.basic.entity.Entity;
import handlers.basic.entity.SyncedEntity;
import network.NetworkInfo;
import network.packets.Packet;

public class ClientSimulationState
{
	private int BASELINE_SEQUENCE;
	private int LATEST_RECIEVED_SEQUENCE,ACK_SEQUENCE;

	// server
	private int LAST_READ_COMMAND_ID=-1;
	Timer syncupSendTimer = new Timer();
	Timer syncupCycleTimer=new Timer();
	protected int syncupCount;
	
	//client
//	public ClientCommand newCommand = new ClientCommand();
	public ClientCommand unsentCommand = new ClientCommand();
	int PROCESSED_COMMAND_ID=-1;
	
	RequiredUpdateList reqUpdateList;
	
	private LinkedList<Packet> recievedQueue = new LinkedList<Packet>();
	private LinkedList<Packet> sendQueue = new LinkedList<Packet>();
	private Object retrieve_lock = new Object();
	
	private SyncedEntity clientEntity;
	
	private byte state;
	private boolean state_changed_flag=true;
	
	public long SEND_RATE;
	
	/********** SEQUENCE **********/
	
	public void update_sequence(Packet packet) {
		LATEST_RECIEVED_SEQUENCE = Math.max(LATEST_RECIEVED_SEQUENCE, packet.sequence);
		ACK_SEQUENCE = Math.max(ACK_SEQUENCE,packet.ack_sequence);
	}
	
	public int get_latest_recieved_sequence() {
		return LATEST_RECIEVED_SEQUENCE;
	}
	
	public int get_ack_sequence() {
		return ACK_SEQUENCE;
	}
	
	public int get_baseline_sequence() {
		return this.BASELINE_SEQUENCE;
	}
	
	private void set_latest_recieved_sequence(int seq) {
		this.LATEST_RECIEVED_SEQUENCE = seq;
	}
	
	protected void set_ack_sequence(int seq) {
		this.ACK_SEQUENCE = seq;
	}
	
	protected void set_baseline_sequence(int seq) {
		this.BASELINE_SEQUENCE = seq;
	}
	
	/********** SEND/RECIEVE PACKETS **********/
	
	public void recieve_packet(Packet packet) {
		synchronized(retrieve_lock) {
			if(packet.sequence <= LATEST_RECIEVED_SEQUENCE) {
				System.out.println("discarding packet: "+packet.sequence);
				return;
			}
			recievedQueue.add(packet);
			set_latest_recieved_sequence(packet.sequence);
		}
	}
	
	public void retrive_recieved_packets(LinkedList<Packet> systemQueue) {
		synchronized(retrieve_lock) {
			systemQueue.addAll(recievedQueue);
			recievedQueue.clear();
		}
	}
	
	public void add_to_send_queue(Packet packet) {
		sendQueue.add(packet);
	}
	
	public void get_send_queue(LinkedList<Packet> sq) {
		sq.addAll(sendQueue);
		sendQueue.clear();
	}
	
	/********** STATE METHODS **********/
	
	public void set_state(byte state) {
		if(this.state != state) {
			this.state = state;
			state_changed_flag=true;
		}
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
	
	/********** CLIENT COMMANDS **********/
	
	public int get_last_command_id() {
		return LAST_READ_COMMAND_ID;
	}
	
	public void set_last_command_id(int seq) {
		this.LAST_READ_COMMAND_ID = seq;
	}
	
	public int get_processed_command_id() {
		return PROCESSED_COMMAND_ID;
	}
	
	public void set_processed_command_id(int cmd) {
		this.PROCESSED_COMMAND_ID = cmd;
	}
	
	/********** CONFIGURATION **********/
	
//	public long get_send_rate() {
//		
//	}
	
	public void set_client_entity(SyncedEntity ent) {
		if(ent == null) {
			System.out.println("setting client entity to null");
		}
		this.clientEntity = ent;
	}
	
	public SyncedEntity get_client_entity() {
		return this.clientEntity;
	}
}

