package handlers.basic.entity;

import java.util.ArrayList;
import java.util.Iterator;

import basic.Rectangle;
import basic.RoundQueue;
import basic.Vector;
import handlers.basic.ClientCommand;
import handlers.basic.PacketFieldInfo;
import handlers.basic.packets.EntityState;
import network.NetworkInfo;
import simulation.ClientSimulationState;

public abstract class SyncedEntity<E extends EntityState> extends Entity {
	
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
	public ClientSimulationState CSimState;
	
	public int state_send_ids[][];
	public boolean state_flag[];
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

	public void set_processed_command_id(int cmdid) {
		this.processed_command_id = cmdid;
	}
	
	public int get_processed_command_id() {
		return this.processed_command_id;
	}

	///////////////////////////HISTORY///////////////////////////////////
	public EntityState[] get_updates(long timestamp) {
		EntityState entupdate[] = new EntityState[2];
		for(Iterator <EntityState> iterator = entityHistory.get_iterator(false);iterator.hasNext(); )
		{
			entupdate[0] = entupdate[1];
			entupdate[1]  = iterator.next();
			if(entupdate[1].get_timestamp()>timestamp) {
				break;
			}
		}
		return entupdate;
	}
	
///////////////////////////SEND HISTORY//////////////////////////////
	
	public EntityState get_server_output(long sequence) {
		if(sequence==0)
			return null;
		EntityState base_packet=null;
		for(Iterator <EntityState> iterator = serverOutput.get_iterator(false);iterator.hasNext(); )
		{
			EntityState entupdate = iterator.next();
			
			if(entupdate.get_search_sequence() == sequence) {
				base_packet = entupdate;
			}
			if(entupdate.get_search_sequence() > sequence) {
				break;
			}
		}
		return base_packet;
	}
	
///////////////////////////HANDLE///////////////////////////////////
	
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
	
///////////////////////////SYNCED///////////////////////////////////
	
	abstract E create_state();
	abstract void copy_state(E e1, E e2);
	
//	protected E create_game_state()
//	{	
//		E ent_packet = (E)entStateArray.get(k);
//		
//		E hist_packet = (E)entityHistory.get_last_element();
//		
//		if(hist_packet != null) {
//			copy_state(ent_packet,hist_packet);
//		} else {
//			copy_state(ent_packet,this.defaultState);
//		}
//	}
	
	private void create_delta_packet(E epacket, E base,int ack_baseline) {
		int one_read=0;
		RoundQueue hist = serverOutput;
		E last_packet = null;
		if(hist.size() >1) {
			int i=0;
			for(Iterator it = hist.get_iterator(true);it.hasNext();) {
				last_packet = (E)it.next();
				if(i==1)
					break;
				i+=1;
			}
		}
		boolean req[] = epacket.required_fields;
		for(int i = 0; i < req.length; i++) {
			req[i]=false;
		}
		
		PacketFieldInfo packet_fields[] = 
		
		for(int i = 0; i < packet_fields.length; i++) {
			PacketFieldInfo pinfo = packet_fields[i];
			get_packet_field(epacket,eField1,i);
			get_packet_field(base,eField2,i);
			if(pinfo.get_send_type() == PacketFieldInfo.ALWAYS) {
				req[i]=true;
			}
			if(pinfo.get_send_type() == PacketFieldInfo.NOT_EMPTY) {
				if(!eField1.equals(emptyField)) {
					req[i]=true;
				}
			}
			if(pinfo.get_send_type() == PacketFieldInfo.ON_CHANGE) {
				if(!eField1.equals(eField2)) {
					req[i]=true;
				}
			}
			
			if(pinfo.get_send_type() == PacketFieldInfo.ON_CHANGE_ONE_READ) {
				if(last_packet != null) {
					get_packet_field(last_packet,eField3,i);
					if(!eField1.equals(eField3)) {
						req[i]=true;
					} else if (ent.state_send_ids[one_read][1] > ack_baseline) {
						req[i]=true;
					}
				}
				one_read+=1;
			}
			if(pinfo.get_send_type() == PacketFieldInfo.BASELINE) {
				if(!eField1.equals(eField2)) {
					req[i]=true;
				}
			}
		}
		
		one_read=0;
		for(int i = 0; i < packet_fields.length; i++) {
			PacketFieldInfo pinfo = packet_fields[i];
			get_packet_field(epacket,eField1,i);
			if(req[i]==true) {
				if(pinfo.get_send_type() == PacketFieldInfo.ON_CHANGE_ONE_READ) {
					if(last_packet != null) {
						get_packet_field(last_packet,eField3,i);
						if(!eField1.equals(eField3)) {
							ent.state_send_ids[one_read][0]+=1;
							ent.state_send_ids[one_read][1] = ack_baseline;
						}
						epacket.state_send_ids[one_read]=ent.state_send_ids[one_read][0];
					}
					one_read+=1;
				}
				if(pinfo.get_send_type() == PacketFieldInfo.BASELINE) {
					get_packet_field(epacket,eField1,i);
					get_packet_field(base,eField2,i);
					eField1.sub(eField2);
					set_packet_field(epacket,eField1,i);
				}
			}
		}
	}
}
