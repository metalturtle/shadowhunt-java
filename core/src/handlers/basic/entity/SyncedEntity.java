package handlers.basic.entity;

import java.util.ArrayList;
import java.util.Iterator;

import basic.Rectangle;
import basic.RoundQueue;
import basic.Vector;
import handlers.basic.ClientCommand;
import handlers.basic.packets.EntityState;
import network.NetworkInfo;
import simulation.ClientSimulationState;

public abstract class SyncedEntity extends Entity {
	
	public static class ServerType {
		public static final int INTERPOLATION,CLIENT_USING;
		static {
			int val=0;
			INTERPOLATION=val<<1;
			CLIENT_USING =val<<1;
		}
	}
	int sync_id,state_send_id;
	int server_flags;
	public RoundQueue<EntityState> entityHistory,serverOutput;
	public EntityState currentState;
//	public ClientCommand clientCommand = new ClientCommand(),predictCommand = new ClientCommand();.
	public ClientSimulationState CSimState;
	public int state_send_ids[][];
	public boolean state_flag[];
	public boolean recieved_history;
	int processed_command_id;
	
	public SyncedEntity(){
		super(new Vector(0,0),new Rectangle(0,0,0,0));
		set_angle(0);
		speed=0;
		state = (byte)0;
	}
	
	public SyncedEntity(Vector p,Rectangle bound){
			super(p,bound);
	}
	
	public void set_sync_id(int sync_id) {
		this.sync_id = sync_id;
	}
	
	public int get_sync_id() {
		return sync_id;
	}
	
	///////////////////////////COMMANDS///////////////////////////////////
	
//	public void set_command_id(int cmd_id) {
//		this.client_command_id = cmd_id;
//	}
//	
//	public int get_command_id() {
//		return this.client_command_id;
//	}
	
	
	public void set_processed_command_id(int cmdid) {
		this.processed_command_id = cmdid;
	}
	
	public int get_processed_command_id() {
		return this.processed_command_id;
	}

	///////////////////////////HISTORY///////////////////////////////////
//	
//	protected ArrayList<EntityState> add_history_array(int size) {
//		ArrayList <EntityState> arr = new ArrayList <EntityState>();
//		for(int i=0;i<size;i++)
//		{
//			EntityState entupdate = create_entity_state();
////			entupdate.entity = this.clone();
//			arr.add(entupdate);
//		}
//			
//		return arr;
//	}
	
//	public void init_history(int size) {
//		
////		entityHistory.set_data(add_history_array(size));
//	}
	
//	public void add_history(EntityState entupdate) {
//		entityHistory.add_element(entupdate);
//	}
	
	public EntityState[] get_updates(long timestamp) {
		EntityState entupdate[] = new EntityState[2];
		boolean breaking = false;
		for(Iterator <EntityState> iterator = entityHistory.get_iterator(false);iterator.hasNext(); )
		{
			entupdate[0] = entupdate[1];
			entupdate[1]  = iterator.next();
			if(entupdate[1].get_timestamp()>timestamp) {
				breaking = true;
				break;
			}
		}
//		if(!breaking)
//			 System.out.println("getting last ent update");
		return entupdate;
	}
	
///////////////////////////SEND HISTORY//////////////////////////////
	
//	public void init_server_output() {
//		serverOutput = new RoundQueue<T>(NetworkInfo.NETWORK_RATE,true);
//		serverOutput.set_data(add_history_array(NetworkInfo.NETWORK_RATE));
//	}
	
//	public void add_server_output(EntityState entupdate) {
//		serverOutput.add_element(entupdate);
//	}
	
	public EntityState get_server_output(long sequence) {
		if(sequence==0)
			return null;
		EntityState base_packet=null;
		for(Iterator <EntityState> iterator = serverOutput.get_iterator(false);iterator.hasNext(); )
		{
			EntityState entupdate = iterator.next();
			
			if(entupdate.get_search_sequence() == sequence) {
				base_packet = entupdate;
//				System.out.println("base_packet : "+entupdate.get_search_sequence());
//				System.out.println("found base packet: "+base_packet.get_search_sequence());
//				break;
			}
			if(entupdate.get_search_sequence() > sequence) {
				break;
			}
		}
		return base_packet;
//		EntityState base_packet=null;
//		for(Iterator <EntityState> iterator = serverOutput.get_iterator(false);iterator.hasNext(); )
//		{
//			EntityState item = iterator.next();
//			System.out.println("client reading ack: "+sequence+" "+item.get_search_sequence());
//			if(item.get_search_sequence() == sequence) {
//				base_packet = item;
//			} else {
//				break;
//			}
//		}
//		return base_packet;
	}
	
///////////////////////////HANDLE///////////////////////////////////
	
//	public void enable_interpolated(boolean flag) {
//		this.interpolated = flag;
//	}
//	
//	public boolean get_interpolated() {
//		return interpolated;
//	}
	
	public int get_state_send_id() {
		return this.state_send_id;
	}
	
	public void inc_state_send_id() {
		this.state_send_id+=1;
	}
	
	public void set_state_send_id(int id) {
		this.state_send_id = id;
	}
	
///////////////////////////FLAGS///////////////////////////////////
	
	public boolean check_flags(int flag) {
		return (this.server_flags&flag)>0;
	}
	
	public void set_flag(int flag) {
		this.server_flags |= flag;
	}
}
