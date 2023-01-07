package simulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import basic.InputPacketProcessor;
import basic.OutputPacketProcessor;
import basic.Rectangle;
import basic.RoundQueue;
import basic.Vector;
import handlers.EntityStateField;
import handlers.basic.ClientCommand;
import handlers.basic.Command;
import handlers.basic.PacketFieldInfo;
import handlers.basic.PacketFieldInfo.Interpolation;
import handlers.basic.entity.Entity;
import handlers.basic.entity.SyncedEntity;
import handlers.basic.packets.EntityState;
import network.NetworkInfo;
import simulation.handlers.EntityHandler;
import util.GameInfo;
import util.Logger;


public abstract class SyncedEntityHandler<E extends EntityState,T extends SyncedEntity> extends EntityHandler<T>
{
	String name;
	public boolean IS_SERVER_FLAG;
	String [] COMMAND_DEFINATION;
	
	int SYNCED_HANDLER_ID;
	
	private HashMap <T,Integer> EntIDTranslate = new HashMap<T,Integer>();
	private HashMap <Integer,T> ReverseEntIDTranslate = new HashMap<Integer,T>();

	int SERVER_ENTITY_INDEX;
	
	LinkedList <Integer> disabled_entities = new LinkedList<Integer>();;
	ArrayList <T> entities;
	PacketFieldInfo packet_fields[];
	ArrayList <EntityState> entStateList=new ArrayList<EntityState>();
//	EntityState entStateArray[] = new EntityState[0],deltaStateArray[] = new EntityState[0];
	ArrayList<EntityState> entStateArray = new ArrayList<EntityState>(),deltaStateArray = new ArrayList<EntityState>();
	E interpolState;
	EntityStateField eField1 = new EntityStateField(),eField2=new EntityStateField(),eField3 = new EntityStateField(),emptyField=new EntityStateField();
	int ENTITY_MANUAL_UPDATES_NO,STATE_GUARANTEED_ONE_READ;
	E defaultState;
	
	public SyncedEntityHandler(SimulationCoordinator SimCoord)
	{
		super(SimCoord);
		this.IS_SERVER_FLAG = SimCoord.is_server();
		
		
		this.packet_fields = get_packet_field_info();
		this.defaultState = create_entity_state();
		this.interpolState = create_entity_state();
		
		for(int i = 0; i < this.packet_fields.length; i++) {
			if(this.packet_fields[i].get_send_type() == PacketFieldInfo.MANUAL) {
				ENTITY_MANUAL_UPDATES_NO+=1;
			}
			if(this.packet_fields[i].get_send_type() == PacketFieldInfo.ON_CHANGE_ONE_READ) {
				STATE_GUARANTEED_ONE_READ+=1;
			}
		}
	}
	
	public boolean is_server() {
		return IS_SERVER_FLAG;
	}
	
	public boolean is_network_sending() {
//		return NetworkInfo.is_network_sending();
		return SimCoord.sendTimer.check();
	}
	
	////////////////////////////////////STATE AND COMMAND DEFINATION//////////////////////////////////////////////
	
	int get_command_def_id(String command)
	{
		for(int i=0;i<COMMAND_DEFINATION.length;i++)
			if(command == COMMAND_DEFINATION[i])
				return i;
		return -1;
	}
	
	////////////////////////////////////Entity Handler functions///////////////////////////////////
	
	public void set_synced_handler_id(int handler_id)
	{
		this.SYNCED_HANDLER_ID = handler_id;
	}
	
	public int get_synced_handler_id() {
		return this.SYNCED_HANDLER_ID;
	}
	
	////////////////////////////////////// translate dictionary functions//////////////////////////
	
	private void add_translate_id(T ent,int id) {
		EntIDTranslate.put(ent,id);
		ReverseEntIDTranslate.put(id,ent);
	}
	
	private void remove_translate_id(T ent) {
		int id = EntIDTranslate.get(ent);
		ReverseEntIDTranslate.remove(id);
		EntIDTranslate.remove(ent);
	}
	
	public T get_entity_from_translate_id(int id) {
		return ReverseEntIDTranslate.get(id);
	}
	
	public int get_translate_id_from_entity(T ent) {
		return EntIDTranslate.get(ent);
	}
	
	public boolean check_translate_id_exists(int id) {
		return ReverseEntIDTranslate.containsKey(id);
	}
	
	@Override
	public void reassign_resource_object_id(T obj, int new_id) {
//		int id = EntIDTranslate.get(obj);
//		remove_translate_id(obj);
//		EntIDTranslate.put(obj,id);
//		ReverseEntIDTranslate.put(id,obj);
	}
	
	////////////////////////////////////ADDING ENTITY//////////////////////////////////////////////
	
	abstract public T create_new_element();
	abstract public E create_entity_state_raw() ;
	
	public E create_entity_state() {
		E ent_packet = create_entity_state_raw();
		int no_fields = packet_fields.length;
		if(ent_packet.required_fields == null || ent_packet.required_fields.length != no_fields) {
			ent_packet.required_fields = new boolean [no_fields];
		}
		ent_packet.state_send_ids = new int[STATE_GUARANTEED_ONE_READ];
		return ent_packet;
	}
	
	EntityState[] create_queue_data(int size) {
		EntityState[] arr = new EntityState[size];
		for(int i=0;i<size;i++)
		{
			E entupdate = create_entity_state();
			arr[i] = entupdate;
		}
		return arr;
	}
	
	public T add_resource_object(Vector pos, Rectangle bound, String params) {
		T ent = super.add_resource_object(pos,bound,params);
		entStateList.add(create_entity_state());
		ent.set_handler_id(HANDLER_ID);
		ent.entityHistory = new RoundQueue<EntityState>(create_queue_data(4),true);
		
		if(is_server()) {
			add_translate_id(ent,SERVER_ENTITY_INDEX);
			ent.set_sync_id(SERVER_ENTITY_INDEX);
			SERVER_ENTITY_INDEX+=1;
		}

		ent.serverOutput = new RoundQueue<EntityState>(create_queue_data(NetworkInfo.NETWORK_RATE),true);
		
		ent.state_send_ids = new int[STATE_GUARANTEED_ONE_READ][2];
		ent.state_flag = new boolean[STATE_GUARANTEED_ONE_READ];
		entStateArray.add(this.create_entity_state());
		deltaStateArray.add(this.create_entity_state());
		return ent;
	}

	@Override
	protected void disable_resource_object(T ent) {
		super.disable_resource_object(ent);
		ent.reset();
		ent.entityHistory.clear();
		ent.serverOutput.clear();
		disabled_entities.add(get_translate_id_from_entity(ent));
		remove_translate_id(ent);
		int s = entStateArray.size()-1;
		entStateArray.remove(s);
		deltaStateArray.remove(s);
	}
	
	@Override
	public void disable_resource_object(int local_id) {
		T ent = get_resource_object(local_id);
		disable_resource_object(ent);
	}
	
	////////////////////////////////////GAMESTATE SENDING FUNCTIONS////////////////////////////////
	
	public abstract Class[] init_packets();
	abstract protected String get_params_from_packet(E ent_packet);
	
	public void create_gamestate_packets(int baseline_sequence) {
		int k=0;
		for(Iterator <T> iterator = get_iterator(null);iterator.hasNext();) {
			T ent = iterator.next();

			E ent_packet = (E)entStateArray.get(k);

			E hist_packet = (E)ent.entityHistory.get_last_element();
			if(hist_packet != null) {
				copy_packet(ent_packet,hist_packet);
			} else {
				copy_packet(ent_packet,this.defaultState);
			}
			
			ent_packet.actid = get_translate_id_from_entity(ent);
			E epacket = (E)ent.serverOutput.add_mset_element();
			
			copy_packet(epacket,ent_packet);
			epacket.set_search_sequence(baseline_sequence);
			
			ent_packet.delta = false;
			int no_fields = packet_fields.length;
			for(int i=0;i<no_fields;i++) {
				ent_packet.required_fields[i] = true;
			}
			create_delta_packet(ent,ent_packet,defaultState,0);
			k+=1;
		}
	}
	
	public ArrayList<EntityState> server_send_gamestate_packets(int last_client_recieved_sequence,boolean delta) {
		int k=0;
		for(Iterator <T> iterator = get_iterator(null);iterator.hasNext();) {
			T ent = iterator.next();

			E ent_packet = (E)deltaStateArray.get(k);
			copy_packet(ent_packet,(E)entStateArray.get(k));

			
			E old_update = (E)ent.get_server_output(last_client_recieved_sequence);
			if(!delta || (last_client_recieved_sequence == 0 || old_update == null)) {
				ent_packet.delta = false;
			} else {
				ent_packet.delta = true;
				create_delta_packet(ent,ent_packet,old_update,last_client_recieved_sequence);
			}
			k+=1;
		}
		return deltaStateArray;
	}
	
	
	private void create_delta_packet(T ent, E epacket, E base,int ack_baseline) {
		int one_read=0;
		RoundQueue hist = ent.serverOutput;
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
//		modify_state_send_fields(epacket);
		
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
	
	private void read_delta_packet(T ent,E packet, E base) {
		boolean req[] = packet.required_fields;
		int one_read=0;
		for(int i = 0; i < packet_fields.length; i++) {
			PacketFieldInfo pinfo = packet_fields[i];
			get_packet_field(packet,eField1,i);
			get_packet_field(base,eField2,i);
				
			if(req[i] == true) {
				if(pinfo.get_send_type() == PacketFieldInfo.NOT_EMPTY) {
				}
				if(pinfo.get_send_type() == PacketFieldInfo.BASELINE) {
					eField1.add(eField2);
					set_packet_field(packet,eField1,i);
				}
				if(pinfo.get_send_type() == PacketFieldInfo.ON_CHANGE_ONE_READ) {
					if(packet.state_send_ids[one_read] > ent.state_send_ids[one_read][0]) {
						ent.state_send_ids[one_read][0] = packet.state_send_ids[one_read];
					} else {
						req[i]=false;
					}
					one_read+=1;
				}
			} else {
				if(pinfo.get_send_type() != PacketFieldInfo.NOT_EMPTY)
					set_packet_field(packet,eField2,i);
			}
		}
	}
	
	public void serialize_entity_state(E epacket,OutputPacketProcessor output) {
		int one_read=0;
		boolean req[] = epacket.required_fields;
		for(int i = 0; i < packet_fields.length; i++) {
			PacketFieldInfo pinfo = packet_fields[i];
			if(epacket.required_fields[i]) {
				if(pinfo.get_send_type() == PacketFieldInfo.ON_CHANGE_ONE_READ) {
					output.write_int(epacket.state_send_ids[one_read]);
					one_read+=1;
				}
				get_packet_field(epacket,output,i);
			}
		}
	}
	
	public void deserialize_entity_state(EntityState estate, InputPacketProcessor input) {
		int one_read=0;
		for(int i = 0; i < packet_fields.length;i++) {
			PacketFieldInfo pinfo = packet_fields[i];
			if(estate.required_fields[i]) {
				if(pinfo.get_send_type() == PacketFieldInfo.ON_CHANGE_ONE_READ) {
					estate.state_send_ids[one_read]=input.read_int();
					one_read+=1;
				}
				set_packet_field((E)estate,input,i);
			}
		}
	}
	
	public void copy_packet(E packet1, E packet2) {
		packet1.actid = packet2.actid;
		for(int i = 0; i < packet_fields.length;i++) {
			get_packet_field(packet2,eField1,i);
			set_packet_field(packet1,eField1,i);
		}
		for(int i = 0; i < packet_fields.length; i++) {
			packet1.required_fields[i] = packet2.required_fields[i];
		}
	}
	
	public boolean is_disabled_list_empty() {
		return disabled_entities.isEmpty();
	}
	
	public void retrieve_disabled_entities(LinkedList <Integer> dislist) {
		dislist.addAll(disabled_entities);
//		disabled_entities.clear();
	}
	
	public void clear_disabled_entities() {
		disabled_entities.clear();
	}
	
	public void client_disable_entity(int syncid) {
//		System.out.println("client disabled entity: "+check_translate_id_exists(syncid));
		if(check_translate_id_exists(syncid)) {
			T ent_to_disable = get_entity_from_translate_id(syncid);
			disable_resource_object(ent_to_disable);
		}
	}
	
	public boolean client_read_gamestate_packet(ArrayList<EntityState> ent_packets,int baseline_sequence, int new_sequence) {

		boolean return_flag=true;
		long current_timestamp = GameInfo.get_time_millis();
		T ent;
		for(int i=0;i<ent_packets.size();i++) {
			E ent_packet = (E)ent_packets.get(i);
			
			if(!check_translate_id_exists(ent_packet.actid)) {
				ent = add_resource_object(null,null,get_params_from_packet((E)ent_packet));
				this.add_translate_id(ent, ent_packet.actid);
				ent.set_sync_id(ent_packet.actid);
				}
			else {
				ent = get_entity_from_translate_id(ent_packet.actid);
			}
			if(ent_packet.delta) {	
				E base_packet=null;
				
				int k = 0;
				for(Iterator <EntityState> iterator = ent.serverOutput.get_iterator(false);iterator.hasNext(); ) {
					EntityState entupdate = iterator.next();
					k+=1;
					if(entupdate.get_search_sequence() == baseline_sequence) {
						base_packet = (E)entupdate;
						break;
					}
				}
//				System.out.println("delta packet");
				if(base_packet == null) {
					System.out.println("baseline is not found");
					return_flag = false;
					continue;
				}
				
				for(int s = 0; s < k-1; s++) {
					ent.serverOutput.remove_first_element();
				}
				
				read_delta_packet(ent,ent_packet,base_packet);
			} else {
//				System.out.println("full packet");
				read_delta_packet(ent,ent_packet,defaultState);
			}
			ent_packet.set_search_sequence(new_sequence);
			ent_packet.set_timestamp(current_timestamp);

			E old_update = (E)ent.get_server_output(new_sequence);
			if(old_update == null ) {
				E spacket = (E)ent.serverOutput.add_mset_element();
				copy_packet(spacket,ent_packet);
				spacket.set_search_sequence(new_sequence);
			} else {
				copy_packet(old_update,ent_packet);
			}
			E hpacket = (E)ent.entityHistory.add_mset_element();
			copy_packet(hpacket,ent_packet);
			hpacket.set_timestamp(current_timestamp);

			this.read_entity_state(ent_packet, ent);
		}
		return return_flag;
	}
	
	public void cleanup_entity_server_output(T ent, int sequence) {
		int k = 0;
		for(Iterator <EntityState> iterator = ent.serverOutput.get_iterator(false);iterator.hasNext(); ) {
			EntityState entupdate = iterator.next();
			
			if(entupdate.get_search_sequence() >= sequence) {
				break;
			}
			k+=1;
		}
		while(k>0) {
			ent.serverOutput.remove_first_element();
			k-=1;
		}
	}

	public abstract void modify_state_send_fields(E epacket);
	
	public abstract void write_entity_state(E ent_packet, T ent);
	public abstract void read_entity_state(E ent_packet, T ent);
	
	abstract public PacketFieldInfo[] get_packet_field_info();
	abstract public void get_packet_field(E ent_packet,OutputPacketProcessor output,int id);
	abstract public void set_packet_field(E ent_packet,InputPacketProcessor oututProcessor,int id);
//	public Object server_send_state_change() {return null;};
//	public void client_read_state_change(Object obj) {};
	
	////////////////////////////////////RUNNING LOOP//////////////////////////////////////////////
	
	static public long get_interpolation_time(long time) {
		return time - NetworkInfo.CLIENT_INTERPOLATION_DELAY;
	}
	
	protected EntityState[] interpolate_client_entity(T ent) {
		long ent_interpol_time = get_interpolation_time(GameInfo.get_time_millis());
		EntityState entupdates[] = ent.get_updates( ent_interpol_time);
		if(entupdates[0] == null || entupdates[1] ==null)
			return null;
		
		E prevupdate = (E)entupdates[0],curupdate = (E)entupdates[1];
		if(ent_interpol_time > curupdate.get_timestamp()) {
			read_entity_state((E)curupdate,ent);
		}
		float a = (ent_interpol_time-prevupdate.get_timestamp());
		float diff = Math.abs(curupdate.get_timestamp()-prevupdate.get_timestamp());
		a = a/diff;
		
		copy_packet(interpolState,defaultState);
		for(int i = 0; i < packet_fields.length;i++) {
			PacketFieldInfo pinfo = packet_fields[i];
			get_packet_field(prevupdate,eField1,i);
			get_packet_field(curupdate,eField2,i);
			
			if(pinfo.get_interpolation_type() == Interpolation.PREVIOUS) {
				set_packet_field(interpolState,eField1,i);
				interpolState.required_fields[i]=prevupdate.required_fields[i];
			}
			if(pinfo.get_interpolation_type() == Interpolation.NEXT) {
				set_packet_field(interpolState,eField2,i);
				interpolState.required_fields[i]=curupdate.required_fields[i];
			}
			if(pinfo.get_interpolation_type() == Interpolation.INTERPOLATE) {
				if(!eField1.equals(eField2)) {
					interpolState.required_fields[i]=true;
				}
				eField1.interpolate(eField2, a);
				set_packet_field(interpolState,eField1,i);
			}
			if(pinfo.get_interpolation_type() == Interpolation.ANGLE) {
				float prevang = prevupdate.angle;
				float currang = curupdate.angle;
				if(prevang<0)
					prevang+=360;
				if(currang<0)
					currang+=360;
				float ang1 = Math.abs(currang-prevang);
				float ang2 = 360-ang1;
				float angle=0;
				if(ang2 < ang1) {
					if(prevang>currang)	{
						angle= (prevang*(1-a)+(currang+360)*(a))%360;		
					}
					else if(prevang<currang) {
						angle= ((prevang+360)*(1-a)+(currang)*(a))%360;
					}
				}
				else {
					angle= prevang*(1-a)+currang*(a);
				}
				interpolState.angle = angle;
			}
		}
		
		read_entity_state(interpolState,ent);
		return entupdates;
	}
	
	public void run_client() {
		for(Iterator<T> iterator = get_iterator(null);iterator.hasNext();) {
			T ent = iterator.next();
			client_handle_entity(ent);
		}
	}
	
	abstract protected void client_handle_entity(T ent);
	

	protected void run_entity_commands(T ent) {
		ClientCommand clientCommand = ent.CSimState.unsentCommand;
		int k = clientCommand.get_start_id();
//		System.out.println("rum ent cmd: "+ent.CSimState.get_processed_command_id());
		for(Iterator<Command> it = clientCommand.get_command_queue().get_iterator(false);it.hasNext();) {
			Command command = it.next();
			if(k <= ent.CSimState.get_processed_command_id()) {
//				System.out.println("skipping: "+k);
				k+=1;
				continue;
			}
//			System.out.println("run: "+k);
			handle_entity_command(ent,command);
			
			k+=1;
//			command.set_read_flag(true);
		}
	}
	
	protected void handle_entity_command(T ent, Command command) {}
	
	public void run_server () {
		for(Iterator<T> iterator = get_iterator(null);iterator.hasNext();) {
			T ent = iterator.next();
			server_handle_entity(ent);
			
			E ent_packet = (E)ent.entityHistory.add_mset_element();
			write_entity_state(ent_packet,ent);
			boolean req[] = ent_packet.required_fields;
			for(int i = 0; i < req.length;i++) {
				req[i] = true;
			}
		}
	}
	
	abstract protected void server_handle_entity(T ent);
	

}
