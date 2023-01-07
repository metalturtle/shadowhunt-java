package simulation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import Coordinator.ClientSystemData;
import Coordinator.Coordinator;
import Coordinator.HandlerInitRunner;
import Coordinator.SystemPacket;
import Coordinator.Coordinator.SystemState;
import basic.InputState;
import basic.RoundQueue;
import basic.Vector;
import client.ClientImage;
import client.ClientImageHandler;
import game.synced.ActorStatePacket;
import game.synced.ShadowActorHandler;
import handlers.basic.ClientCommand;
import handlers.basic.Command;
import handlers.basic.Timer;
import handlers.basic.entity.SyncedEntity;
import handlers.basic.packets.EntityState;
import network.NetworkDataType;
import network.NetworkHandler;
import network.NetworkInfo;
import network.packets.Packet;
import simulation.RequiredUpdateList.RequiredUpdate;
import simulation.handlers.EntityHandler;
import util.GameInfo;


public class SimulationCoordinator {
	
	public static class State {
		public static final byte INIT,IDLE,SYNCUP,RUN,CLOSE;
		static {
			byte val=0;
			INIT = val++;
			IDLE=val++;
			RUN=val++;
			SYNCUP=val++;
			CLOSE=val++;
		}
	}
	
	static class Info {
		final static long FULL_PACKET_RATE = 500;
		final static long MAX_SYNCUP_TIME = 1000*30;
		final static int MAX_SYNCUPS = 5;
	}
	
	NetworkHandler NetworkHandle;
	public ClientImageHandler CIHandle;
	public SimulationHandler SimHandle;
	SimulationFlowHandler SimFlowHandle;
	
	int RL_SIZE,RL_RESERVED_SIZE=2;
	boolean BUFFER_OVERFLOW;
	
//	ClientGameFlowHandler ClientGameFlowHandle;
	
	ArrayList <SyncedEntityHandler> entityHandlers;
	ServerGameStatePacket svGameStatePacket = new ServerGameStatePacket();
	ClientGameStatePacket clCommandPacket = new ClientGameStatePacket();
	
	ArrayList<EntitySerializer> EntSerializers;
	
	LinkedList <Integer> disabledEntList[];
	RequiredUpdateList globalUpdates;
	LinkedList<Packet> recvQueue = new LinkedList<Packet>();
	
	ServerSerializer servSerializer;
	ClientSerializer clientSerializer;
	
	Coordinator coordinator;
	
	ClientGameFlowHandler ClientGameFlowHandle;
//	Command commands[];
	
	public Timer sendTimer = new Timer();
	long sendTime;
	
	class SentSequence {
		Vector pos;
		int seq;
		long sendTime;
		SentSequence(int seq,Vector pos,long sendTime) {
			this.seq = seq;
			this.pos = new Vector(pos);
			this.sendTime = sendTime;
		}
	}
	
	RoundQueue<SentSequence> sentSeqQueue;
	
	
	private static class Net {
		private static int SENT_SEQUENCE=1,SIM_SEQUENCE=1;
		
		public static int get_sent_sequence() {
			return SENT_SEQUENCE;
		}
		
		public static void inc_sent_sequence() {
			SENT_SEQUENCE+=1;
		}
		
		public static int get_sim_sequence() {
			return SIM_SEQUENCE;
		}
		
		public static void inc_sim_sequence() {
			SIM_SEQUENCE+=1;
		}
	}

	public SimulationCoordinator(Coordinator coordinator,HandlerInitRunner InitRunner,boolean server)
	{
		this.coordinator = coordinator;
		this.NetworkHandle = coordinator.NetworkHandle;
		this.CIHandle = coordinator.CIHandle;
		this.SimHandle = new SimulationHandler(coordinator.loader.MRenderer.RenderEntityMainHandle);
//		this.SimHandle.SimNetManage = this;
		
		InitRunner.add_render_entity_handlers(this.SimHandle.RendEntMainHandle);
		InitRunner.add_handlers(this);
		
		if(server)
		SimFlowHandle = InitRunner.add_simflow_handler(this, CIHandle);
		else {
			SimFlowHandle = new ClientGameFlowHandler(this);
			ClientGameFlowHandle = (ClientGameFlowHandler)SimFlowHandle;
		}
		
		entityHandlers = SimHandle.get_synced_handlers();

//		if(NetworkInfo.is_server() == false) {
//			ClientGameFlowHandle = new ClientGameFlowHandler(this);
//		}

		EntSerializers = new ArrayList<EntitySerializer>();
		RL_SIZE = entityHandlers.size()+RL_RESERVED_SIZE;
		
		for(SyncedEntityHandler ent_handle: entityHandlers) {
			EntitySerializer EntSerializer = new EntitySerializer(ent_handle.get_packet_field_info(),ent_handle,SimHandle);
			EntSerializers.add(EntSerializer);
		}
		
		globalUpdates = new RequiredUpdateList(RL_SIZE);
		
		Command commands[] = new Command[NetworkInfo.COMMAND_BUFFER_SIZE];
		for(int i = 0; i < NetworkInfo.COMMAND_BUFFER_SIZE; i++) {
			commands[i] = new Command((short)0);
		}
		
		clCommandPacket.commands = commands;
		
		SentSequence sentSeqArr[] = new SentSequence[20];
		for(int i = 0; i < 20; i++) {
			sentSeqArr[i] = new SentSequence(0,new Vector(),0);
		}
		sentSeqQueue = new RoundQueue<SentSequence>(sentSeqArr,true);
		
		svGameStatePacket.ent_packets = new ArrayList[entityHandlers.size()];
		for(int i = 0; i < entityHandlers.size(); i++) {
			svGameStatePacket.ent_packets[i] = new ArrayList<EntityState>();
		}
		
		svGameStatePacket.reqUpdateList= new RequiredUpdateList(RL_SIZE);
		
		servSerializer = new ServerSerializer(this);
		clientSerializer = new ClientSerializer();
	    NetworkHandle.get_kryo().register(ServerGameStatePacket.class,servSerializer);
	    NetworkHandle.get_kryo().register(ClientGameStatePacket.class,clientSerializer);
	}
	
	public void load_from_level_file(JSONArray miscarray) {
		this.SimFlowHandle.level_load_entities(miscarray);
	}
	
	public boolean is_server() {
		return coordinator.is_server();
	}
	
	public void add_entity_handler(EntityHandler EntHandle, String name) {
		SimHandle.add_entity_handler(EntHandle, name);
	}
	
	
	
	protected void server_prepare_common_state()
	{
		svGameStatePacket.sequence = Net.get_sent_sequence();
		svGameStatePacket.sim_sequence = Net.get_sim_sequence();
		Net.inc_sent_sequence();
		Net.inc_sim_sequence();
		
		svGameStatePacket.reqUpdateList.clear();
//		LinkedList<Integer> newDisabledList = null;
		boolean disabled_ent_flag = false;
		for(int e = 0; e < entityHandlers.size(); e++) {
			
			SyncedEntityHandler entHandler = entityHandlers.get(e);
			entHandler.create_gamestate_packets(svGameStatePacket.sim_sequence);
		}
		
//		disabledEntList.clear();
//		for(int e = 0; e < entityHandlers.size(); e++) {
//			disabledEntList[e].clear();
//		}

		for(int e = 0; e < entityHandlers.size(); e++) {
			if(!entityHandlers.get(e).is_disabled_list_empty()) {
				disabledEntList = new LinkedList[entityHandlers.size()];
				for(int i = 0; i < entityHandlers.size(); i++) {
					disabledEntList[i] = new LinkedList<Integer>();
				}
				disabled_ent_flag=true;
				break;
			}
		}
		
		for(int e = 0; e < entityHandlers.size(); e++) {
			SyncedEntityHandler entHandler = entityHandlers.get(e);
			if(!entHandler.is_disabled_list_empty()) {
				entHandler.retrieve_disabled_entities(disabledEntList[e]);
				entHandler.clear_disabled_entities();
			}
		}
		
		if(disabled_ent_flag) {
			globalUpdates.add_update(0, disabledEntList, svGameStatePacket.sequence);
		}
	}
	
	protected ServerGameStatePacket server_prepare_packet(ClientImage clientImage, boolean delta)
	{
		ClientSimulationState CSimState = clientImage.CSimState;
		svGameStatePacket.ack_sequence = CSimState.get_latest_recieved_sequence();
		svGameStatePacket.state = CSimState.get_state();
		svGameStatePacket.baseline_sim_sequence = CSimState.get_baseline_sequence();
		
//		if(delta)
//			svGameStatePacket.baseline_sequence = CSimState.get_baseline_sequence();
//		else 
//			svGameStatePacket.baseline_sequence = Net.get_baseline_sequence();

		byte k=0;
		
		for(SyncedEntityHandler ent_handler:entityHandlers) {
			svGameStatePacket.ent_packets[k] = ent_handler.server_send_gamestate_packets(CSimState.get_baseline_sequence(),delta);
			k+=1;
		}
		
		for( int s = 0; s < RL_SIZE ; s++ ) {
			LinkedList<RequiredUpdate> glUpd = globalUpdates.get(s);
			LinkedList<RequiredUpdate> clUpd = CSimState.reqUpdateList.get(s);
			
			for(Iterator <RequiredUpdate> it = glUpd.iterator(); it.hasNext();) {
				RequiredUpdate reqUpdate = it.next();
				CSimState.reqUpdateList.add_update(s,reqUpdate.updObj,reqUpdate.sequence);
			}
			
			for(Iterator<RequiredUpdate> it = clUpd.iterator(); it.hasNext();) {
				RequiredUpdate requpd = it.next();
//				System.out.println("last requied update: "+requpd.send_id);
				svGameStatePacket.reqUpdateList.get(s).add(requpd);
			}
		}
		
		svGameStatePacket.command_id = CSimState.get_last_command_id();
		return svGameStatePacket;
	}
	
	protected void server_cleanup() {
		globalUpdates.clear();
	}
	
	protected void server_read_packet(ClientImage clientImage, PlaceholderPacket plPacket)
	{
		ClientSimulationState CSimState = clientImage.CSimState;
		clientSerializer.read_command_stream(clCommandPacket,plPacket, NetworkHandle.get_kryo());
		
		if(CSimState.state_equals(State.RUN)) {
			if(CSimState.syncupCycleTimer.check()) {
				CSimState.syncupCycleTimer.set(Info.MAX_SYNCUP_TIME);
				CSimState.syncupCount = 0;
			}
		}
		
		if((Net.get_sim_sequence() - clCommandPacket.ack_baseline_sequence) >= NetworkInfo.MAX_BUFFER_SIZE) {
			
			if(CSimState.syncupCount >= Info.MAX_SYNCUPS) {
				CSimState.set_state(State.CLOSE);
				
				coordinator.disconnect(clientImage, "ERROR: Server disconnected due to high latency");
				return;
			}
			CSimState.set_state(State.SYNCUP);
			return;
		}
		
		for(int i = 0; i < RL_SIZE;i++) {
			CSimState.reqUpdateList.remove_update(i,clCommandPacket.ack_sequence);
		}
		
		
		CSimState.update_sequence(clCommandPacket);
		CSimState.set_baseline_sequence(clCommandPacket.ack_baseline_sequence);
		
		int max_cmd_id = clCommandPacket.start_command_id+clCommandPacket.array_size-1;
		if(max_cmd_id<= CSimState.get_last_command_id()) {
			return;
		}
		
		int arr_start = CSimState.get_last_command_id()-clCommandPacket.start_command_id+1;
		arr_start = Math.max(0, arr_start);
		
//		if(clientImage.get_con_id() == 1)
//			System.out.println("\nsim coord adding commands: "+clCommandPacket.sequence+" "+(CSimState.get_last_command_id()+1)+" ");
		int k = CSimState.get_last_command_id()+1;
		for(int i=arr_start;i<clCommandPacket.array_size;i++) {
//			if(clientImage.get_con_id() == 1)
//				System.out.println("cmd: "+k);
			CSimState.unsentCommand.add_command(clCommandPacket.commands[i]);
			k+=1;
		}
		CSimState.set_last_command_id( max_cmd_id );
		
	}
	
	
	/////////////////linked list////////////////
	
	protected void client_read_packet(ClientImage clientImage,PlaceholderPacket plPacket)
	{
		ClientSimulationState CSimState = clientImage.CSimState;
		
		for(int i = 0; i < svGameStatePacket.ent_packets.length; i++) {
			svGameStatePacket.ent_packets[i].clear();
		}
		svGameStatePacket.reqUpdateList.clear();
		
		servSerializer.read_entity_stream(svGameStatePacket, plPacket, NetworkHandle.get_kryo());
		ArrayList<EntityState> ent_packets[] = svGameStatePacket.ent_packets;

		
//		System.out.println("client read packet: "+svGameStatePacket.command_id);
		/////////////////// disabling entities/////////////////////////////////////////

		RequiredUpdateList clUpdList = CSimState.reqUpdateList;
		RequiredUpdateList servUpdList = svGameStatePacket.reqUpdateList;
		LinkedList<RequiredUpdate> servReqUpdates = servUpdList.get(0);
		RequiredUpdate clReqUpdate;
		
		while(!servReqUpdates.isEmpty()) {
			RequiredUpdate reqUpd = servReqUpdates.removeFirst();
			System.out.println("Recieved required update: "+reqUpd);
			if(clUpdList.get(0).get(0).get_send_id() < reqUpd.get_send_id()) {
				LinkedList<Integer> disabledList[] = (LinkedList<Integer>[])reqUpd.updObj;
				
				for(int i = 0; i < disabledList.length; i++) {
//					System.out.println("disabled list: "+disabledList[i].size());
					while(!disabledList[i].isEmpty()) {
						int val = disabledList[i].removeFirst();
						if(CSimState.get_client_entity() != null && val == CSimState.get_client_entity().get_sync_id()) {
							System.out.println("calling disconnect client");
							disconnect_client(clientImage);
						}
						
						entityHandlers.get(i).client_disable_entity(val);
					}
				}
			}
		}
		
		/////////////////////////////Handlers reading states//////////////////////////////////////////
		boolean baseline_flag=true;
		int k=0;
		for(SyncedEntityHandler ent_handler: entityHandlers) {
//			baseline_flag &= ent_handler.client_read_gamestate_packet(ent_packets[k],0,svGameStatePacket.baseline_sequence);
			baseline_flag &= ent_handler.client_read_gamestate_packet(ent_packets[k],svGameStatePacket.baseline_sim_sequence,svGameStatePacket.sim_sequence);
		}
		if(baseline_flag) {
			CSimState.set_baseline_sequence(svGameStatePacket.sim_sequence);
			
			if(CSimState.get_client_entity() != null) {
				Vector testpos = CSimState.get_client_entity().serverOutput.get_last_element().pos;
				while(!sentSeqQueue.is_empty()) {
					SentSequence p = sentSeqQueue.get_first_element();
					if(p.seq > svGameStatePacket.sequence) {
						break;
					}
					if(p.seq == svGameStatePacket.ack_sequence) {
						sentSeqQueue.remove_first_element();
						clientImage.set_udp_rtt((short)(System.currentTimeMillis()-p.sendTime));
						if(clientImage.get_con_id() == 1 && svGameStatePacket.ent_packets[0].size() > 0) {
							if(!p.pos.equals(svGameStatePacket.ent_packets[0].get(0).pos)) {
								System.out.println("ent pos mismatch: "+p.pos+"   "+svGameStatePacket.ent_packets[0].get(0).pos);
								
							}	
						}
						break;
					}
					sentSeqQueue.remove_first_element();

				}
			}
		}
		
		///////////////////////////////////////////////////////////////////////////////////////////
		
		for(int i = 1; i < RL_SIZE;i++) {
			servReqUpdates = servUpdList.get(i);
			clReqUpdate = clUpdList.get(i).get(0);
			while(!servReqUpdates.isEmpty()) {
				RequiredUpdate reqUpd = servReqUpdates.removeFirst();
				if(clReqUpdate.get_send_id() < reqUpd.get_send_id()) 
				{
					if(i==1) {
						GameFlowPacket gfpac =(GameFlowPacket)reqUpd.updObj;
						ClientGameFlowHandle.process_gameflow(gfpac);
					} 
//					else {
//						entityHandlers.get(i-RL_RESERVED_SIZE).client_read_state_change(reqUpd.updObj);
//					}
					clReqUpdate.send_id = reqUpd.get_send_id();
				}
			}
		}
		
//		System.out.println("net manage client ack:"+svGameStatePacket.ack_sequence+" "+ svGameStatePacket.command_id
//				+" pos: "+svGameStatePacket.ent_packets[0].get(0).pos);
		
		CSimState.unsentCommand.acknowledge_command(svGameStatePacket.command_id);
		CSimState.set_processed_command_id(svGameStatePacket.command_id);
		
//		for(Iterator<Command> it = CSimState.unsentCommand.get_iterator(false); it.hasNext();) {
//			Command command = it.next();
//			CSimState.newCommand.add_command(command);
//		}
//		CSimState.newCommand.read_all();
		
		if(svGameStatePacket.state == State.INIT) {
			CSimState.set_state(State.IDLE);
		} else {
			CSimState.set_state(svGameStatePacket.state);
		}
	}

	Vector vec = new Vector();
	protected void client_prepare_packet(ClientSimulationState CSimState)
	{
		ClientCommand clientCommand = CSimState.unsentCommand;
		Command commands[] =clientCommand.get_processed_commands();
		int start_id = clientCommand.get_start_id();
		int sequence = Net.get_sent_sequence();
		clCommandPacket.sequence = sequence;
		clCommandPacket.ack_sequence = CSimState.get_latest_recieved_sequence();
		clCommandPacket.ack_baseline_sequence = CSimState.get_baseline_sequence();
		clCommandPacket.start_command_id=start_id;
		clCommandPacket.commands = commands;
		clCommandPacket.array_size = commands.length;
		
//		System.out.println("client prepare packet: "+sequence+" "+start_id+" "+commands.length);
		
		SentSequence sentseq = sentSeqQueue.add_mset_element();
		sentseq.seq = sequence;
		sentseq.sendTime = System.currentTimeMillis();
		if(CSimState.get_client_entity() != null) {
			sentseq.pos.set(CSimState.get_client_entity().pos);
		}
		
		Net.inc_sent_sequence();
	}
	
	public void connect_client(ClientImage clientImage)
	{
		SimFlowHandle.add_new_client(clientImage);
	}
	
	public void attach_entity_to_client(ClientImage clientImage, SyncedEntity ent)
	{
		clientImage.CSimState.set_client_entity(ent);
		ent.CSimState = clientImage.CSimState;
		
		if(this.is_server()) {
			GameFlowPacket gpacket = new GameFlowPacket();
			gpacket.type = ClientGameFlowHandler.TYPE.ASSIGN_PLAYER;
			gpacket.message = ent.get_handler_id()+","+ent.get_sync_id();
			add_gameflow_packet(clientImage, gpacket);	
		}
	}
	
	public void disconnect_client(ClientImage clientImage)
	{
		SyncedEntity ent = clientImage.CSimState.get_client_entity();
		if(ent != null) {
			ent.CSimState = null;
			clientImage.CSimState.set_client_entity(null);
			SimFlowHandle.disconnect_client(clientImage.get_con_id(),ent);
		}
	}
	
	public void add_gameflow_packet(ClientImage clientImage, GameFlowPacket GFPacket)
	{
		RequiredUpdateList ServUpdate = clientImage.CSimState.reqUpdateList;
		ServUpdate.add_update(1,GFPacket,Net.get_sent_sequence());
	}
	

//	public boolean check_client_syncup(ClientImage clientImage,ClientGameStatePacket clPacket)
//	{
//		ClientSimulationState CSimState = clientImage.CSimState;
//
//		if(CSimState.state_equals(State.RUN)) {
//			if(CSimState.syncupCycleTimer.check()) {
//				CSimState.syncupCycleTimer.set(Info.MAX_SYNCUP_TIME);
//				CSimState.syncupCount = 0;
//			}
//		}
//		
//		if((Net.get_sim_sequence() - clPacket.ack_baseline_sequence) >= NetworkInfo.MAX_BUFFER_SIZE) {
//			System.out.println("setting to syncup: "+CSimState.syncupCount);
//			
//			if(CSimState.syncupCount >= Info.MAX_SYNCUPS) {
//				CSimState.set_state(State.CLOSE);
//				
//				coordinator.disconnect(clientImage, "ERROR: Server disconnected due to high latency");
//				return false;
//			}
//			CSimState.set_state(State.SYNCUP);
//			return false;
//		}
//		return true;
//	}
	
	
	public void run_client()
	{
//		System.out.println();
		if(CIHandle.is_empty()) {
			return;
		}
		ClientImage clientImage = CIHandle.get_client_by_localid(0);
		ClientSimulationState CSimState = clientImage.CSimState;
		
		ClientGameFlowHandle.loop();

		if(CSimState.state_equals(State.INIT)) {
			CSimState.reqUpdateList = new RequiredUpdateList(RL_SIZE);
			for(int i = 0; i < RL_SIZE; i++) {
				CSimState.reqUpdateList.add_update(i, null, 0);
				CSimState.reqUpdateList.reqUpdate[i].get(0).send_id=0;
			}
			CSimState.set_state(State.SYNCUP);
		}
		
		else if (CSimState.state_equals(State.IDLE)) {
			CSimState.retrive_recieved_packets(recvQueue);
			while (!recvQueue.isEmpty() ) {
				PlaceholderPacket plPacket = (PlaceholderPacket)recvQueue.removeFirst();
//				ServerGameStatePacket svPacket = (ServerGameStatePacket)recvQueue.removeFirst();
				CSimState.update_sequence(plPacket);
				this.client_read_packet(clientImage, plPacket);
			}
		}
		
		else if( CSimState.state_equals(State.SYNCUP)) {
			if(CSimState.is_state_changed()) {
				CSimState.off_state_change();
			}
			CSimState.retrive_recieved_packets(recvQueue);
			if (!recvQueue.isEmpty() ) {
				PlaceholderPacket plPacket = (PlaceholderPacket)recvQueue.removeLast();
				CSimState.update_sequence(plPacket);
				this.client_read_packet(clientImage, plPacket);
				
				client_prepare_packet(CSimState);
				NetworkHandle.send_packet(clientImage.get_con_id(), clCommandPacket, NetworkDataType.UNRELIABLE);
				sendTimer.set(50);
			}
		}
		
		else if(CSimState.state_equals(State.RUN)) {
			CSimState.retrive_recieved_packets(recvQueue);
			while (!recvQueue.isEmpty() ) {
				PlaceholderPacket plPacket = (PlaceholderPacket)recvQueue.removeFirst();
				CSimState.update_sequence(plPacket);
				this.client_read_packet(clientImage, plPacket);
			}
			
			if(CSimState.unsentCommand.push_cmd_from_inputs()) {
//				CSimState.newCommand.add_command(CSimState.unsentCommand.commandsNew.get_last_element());
//				Command command = CSimState.newCommand.commandsNew.get_last_element();
			}
			
			SimHandle.run_client();
			
			CSimState.set_processed_command_id(CSimState.unsentCommand.get_start_id()
					+
					CSimState.unsentCommand.commandsNew.size()-1);
			
//			CSimState.set_processed_command_id(CSimState.unsentCommand.get_start_id()
//					+
//					CSimState.unsentCommand.commandsNew.size()
//					- 1
//					);
			
//			CSimState.set_processed_command_id(CSimState.unsentCommand.commandsNew.get_last_element().get_command_id());
			
			
			if(sendTimer.check()) {
				client_prepare_packet(CSimState);
				NetworkHandle.send_packet(clientImage.get_con_id(), clCommandPacket, NetworkDataType.UNRELIABLE);
				sendTimer.set(50);
			}
		}
		
//		CSimState.newCommand.clear();
		recvQueue.clear();
	}

	public void run_server()
	{	
		
		SimFlowHandle.loop();
		for(Iterator<ClientImage> it = CIHandle.get_iterator(); it.hasNext();) {
			ClientImage clientImage = it.next();
//			if(clientImage.get_local_id() == 0) {
//				System.out.println();
//			}

			if(!clientImage.CSData.state_equals(SystemState.RUN)) {
				continue;
			}
			
			ClientSimulationState CSimState = clientImage.CSimState;
			
			CSimState.unsentCommand.acknowledge_command(CSimState.unsentCommand.get_start_id()
					+
					CSimState.unsentCommand.commandsNew.size()-1);
			CSimState.set_processed_command_id(CSimState.unsentCommand.get_start_id()
					+
					CSimState.unsentCommand.commandsNew.size()-1);

			CSimState.unsentCommand.clear();
			
//			System.out.println("state: "+State.INIT);
			
			if(CSimState.state_equals(State.INIT)) {
				clientImage.CSimState.reqUpdateList = new RequiredUpdateList(RL_SIZE);
				CSimState.syncupCycleTimer.set(Info.MAX_SYNCUP_TIME);
				CSimState.syncupCount = 0;
				CSimState.set_state(State.SYNCUP);
				connect_client(clientImage);
			}
			
			else if (CSimState.state_equals(State.SYNCUP)) {
				if(CSimState.is_state_changed()) {
					CSimState.off_state_change();
				}
				recvQueue.clear();
				CSimState.retrive_recieved_packets(recvQueue);
				if( !recvQueue.isEmpty() ) {
//					ClientGameStatePacket clPacket = (ClientGameStatePacket)recvQueue.removeLast();
					PlaceholderPacket plPacket = (PlaceholderPacket)recvQueue.removeLast();
					clientSerializer.read_command_stream(clCommandPacket, plPacket, NetworkHandle.get_kryo());
					CSimState.update_sequence(clCommandPacket);
					
					
					if(CSimState.get_baseline_sequence() == clCommandPacket.ack_sequence) {
						CSimState.set_state(State.RUN);
					}
				}
			}
			
			else if (CSimState.state_equals(State.RUN)) {
				recvQueue.clear();
				CSimState.retrive_recieved_packets(recvQueue);
				while( !recvQueue.isEmpty() ) {
//					ClientGameStatePacket clPacket = (ClientGameStatePacket)recvQueue.removeFirst();
					PlaceholderPacket plpacket = (PlaceholderPacket)recvQueue.removeFirst();
					
					CSimState.update_sequence(plpacket);
					
//					if(!check_client_syncup(clientImage, plpacket)) {
//						break;
//					}
					
					server_read_packet(clientImage, plpacket);

				}
			}
		}
		
		SimHandle.run_server();
		
		if(sendTimer.check()) {
			
			boolean should_prepare = false;
			for(Iterator<ClientImage> it = CIHandle.get_iterator(); it.hasNext();) {
				ClientImage clientImage = it.next();
				
				if(!clientImage.CSData.state_equals(SystemState.RUN)) {
					continue;
				}
				
				ClientSimulationState CSimState = clientImage.CSimState;
				if(CSimState.state_equals(State.CLOSE)) {
					continue;
				}
				
				if(CSimState.state_equals(State.RUN) || (CSimState.state_equals(State.SYNCUP) && CSimState.syncupSendTimer.check())) {
					should_prepare = true;
					break;
				}
			}
			
			if(should_prepare) {
				this.server_prepare_common_state();
			}
			
			for(Iterator<ClientImage> it = CIHandle.get_iterator(); it.hasNext();) {
				ClientImage clientImage = it.next();
				if(!clientImage.CSData.state_equals(SystemState.RUN)) {
					continue;
				}
				ClientSimulationState CSimState = clientImage.CSimState;
				
				if(CSimState.state_equals(State.SYNCUP)) {
					if(CSimState.syncupSendTimer.check()) {
						server_prepare_packet(clientImage,false);
						CSimState.set_baseline_sequence(svGameStatePacket.sequence);
						CSimState.syncupSendTimer.set(1000);
						CSimState.syncupCount+=1;
						NetworkHandle.send_packet(clientImage.get_con_id(),svGameStatePacket,NetworkDataType.UNRELIABLE);
					}
				}
				
				else if(CSimState.state_equals(State.RUN)) {
					server_prepare_packet(clientImage,true);
					NetworkHandle.send_packet(clientImage.get_con_id(),svGameStatePacket,NetworkDataType.UNRELIABLE);
				}

			}
			sendTimer.set(50);
			server_cleanup();
		}
		
	}
}


