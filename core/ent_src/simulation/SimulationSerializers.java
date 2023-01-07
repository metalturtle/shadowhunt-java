package simulation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import basic.InputState;
import basic.ObjectPoolCreator;
import basic.ObjectPooling;
import handlers.basic.Command;
import handlers.basic.PacketFieldInfo;
import handlers.basic.Timer;
import handlers.basic.packets.EntityState;
import network.packets.Packet;
import simulation.RequiredUpdateList.RequiredUpdate;
import util.GameInfo;

class ClientSerializer extends Serializer<Packet> {

	byte COMMAND_SIZE;
	float COMMAND_BYTE_COUNT;
	ArrayList <ClientGameStatePacket> clientPackets = new ArrayList<ClientGameStatePacket>();
	
	ClientSerializer() {
		this.COMMAND_SIZE = (byte)InputState.get_cmd_key_length();
		this.COMMAND_BYTE_COUNT = ((float)this.COMMAND_SIZE)/8f;
	}

	@Override
	public Packet read(Kryo kryo, Input input, Class<Packet> arg2) {
//		ClientGameStatePacket cl_packet = new ClientGameStatePacket();
		PlaceholderPacket ppacket = new PlaceholderPacket();
		ppacket.sequence = input.readInt();
		ppacket.ack_sequence = input.readInt();
		
//		cl_packet.ack_baseline_sequence = input.readInt();
//		cl_packet.start_command_id = input.readInt();
		
//		cl_packet.ack_baseline_sequence = input.readInt();
//		int com_len = input.readByte();
//		ppacket.array_size = com_len;

		try {
			ppacket.data = GameInfo.readAllBytes(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ppacket;
	}

	@Override
	public void write(Kryo kryo, Output output, Packet packet) {
		ClientGameStatePacket cl_packet = (ClientGameStatePacket)packet;
		output.writeInt(cl_packet.sequence);
		output.writeInt(cl_packet.ack_sequence);
		output.writeInt(cl_packet.ack_baseline_sequence);
		output.writeInt(cl_packet.start_command_id);
//		output.writeInt(cl_packet.ack_baseline_sequence);
		output.writeByte(cl_packet.commands.length);
		
		int size =(int) Math.ceil(COMMAND_BYTE_COUNT*cl_packet.commands.length);
		byte command_keys[] = new byte[size];
		int k=0;
		int bitsize = COMMAND_SIZE*cl_packet.commands.length;
		while(k<bitsize) {
			boolean keys[] = cl_packet.commands[k/COMMAND_SIZE].get_keys();
			command_keys[k/8] |= (byte) (((keys[k%COMMAND_SIZE])? 1:0)<<(k%8));
			k+=1;
		}
		output.writeBytes(command_keys);
		
		for(int i = 0; i < cl_packet.commands.length; i++) {
			output.writeByte(cl_packet.commands[i].get_mouse_byte());
		}

		for(int i = 0; i < cl_packet.commands.length;i++) {
			output.writeInt(cl_packet.commands[i].get_last_fps(),true);
		}
	}

	Input input = new Input();
	public void read_command_stream(ClientGameStatePacket clPacket,PlaceholderPacket plpacket,Kryo kryo) {
		
		clPacket.sequence = plpacket.sequence;
		clPacket.ack_sequence = plpacket.ack_sequence;
		
		input.setBuffer(plpacket.data);
		
		clPacket.ack_baseline_sequence = input.readInt();
		clPacket.start_command_id = input.readInt();
		
		int com_len = input.readByte();
		clPacket.array_size = com_len;
		
		Command commands[] = clPacket.commands;
		if(com_len > 0) {
			int bitsize = COMMAND_SIZE*com_len;
			int k = 0;
			byte keyval=0;
			while(k<bitsize) {
				if(k==0 || k%8==0) {
					keyval = input.readByte();
				}
				boolean keys[] = commands[k/COMMAND_SIZE].get_keys();
				keys[k%COMMAND_SIZE] = (keyval&(1<<(k%8)))>0;
				
				k+=1;
			}
			for(int i = 0; i < com_len; i++) {	
				commands[i].set_mouse_byte(input.readByte());
			}
			
			for(int i = 0; i< com_len;i++) {
				commands[i].set_last_fps((short)input.readInt(true));
			}
		}
	}
}


class EntitySerializer extends Serializer<EntityState> {
	
	PacketFieldInfo []variables;
	final SyncedEntityHandler EntHandle;
	
	ObjectPooling <EntityState> statePool;
	KryoInputPacketProcessor inputFieldProcessor;
	KryoOutputPacketProcessor outputFieldProcessor;
	
	public EntitySerializer(PacketFieldInfo [] variables,final SyncedEntityHandler EntHandle,SimulationHandler SimHandle) {
		this.variables = variables;
		this.EntHandle = EntHandle;
		ObjectPoolCreator<EntityState> creator = new ObjectPoolCreator<EntityState>() {
			public EntityState create_object() {
				return EntHandle.create_entity_state();
			}
		};
		statePool = new ObjectPooling<EntityState>(creator,10);
		SimHandle.objectPoolingSystem.add_object_pooling(statePool);
		inputFieldProcessor = new KryoInputPacketProcessor();
		outputFieldProcessor = new KryoOutputPacketProcessor();
	}
	
	public void reset_used() {
		statePool.release();
	}

	@Override
	public EntityState read(Kryo kryo, Input input, Class<EntityState> arg2) {
		EntityState ent_packet = statePool.get_resource();
		ent_packet.actid = input.readInt(true);
		ent_packet.required_fields = new boolean[variables.length];
		byte param=0;
		for(int i = 0; i <= variables.length;i++) {
			if(i==0 || i%8 ==0) {
				param = input.readByte();
			}
			if(i==0) {
				if((param&1) > 0)
					ent_packet.delta = true;
				else
					ent_packet.delta = false;
			} else {
				ent_packet.required_fields[i-1] = (param&(1<<(i%8)))>0;
			}
		}
		inputFieldProcessor.input = input;
		EntHandle.deserialize_entity_state(ent_packet, inputFieldProcessor);
		return ent_packet;
	}

	@Override
	public void write(Kryo kryo, Output output, EntityState ent_packet) {
//		output.writeByte(ent_packet.delta==true?1:0);
		output.writeInt(ent_packet.actid,true);
		byte params[] = new byte[(int)Math.ceil(((float)variables.length+1f)/8f)];
		params[0] = (byte)(ent_packet.delta==true?1:0);
		for(int i = 1; i <= variables.length;i++) {
			params[i/8] |= (byte)(ent_packet.required_fields[i-1]?(1<<(i%8)):0);
		}
		for(int i = 0; i < params.length;i++) {
			output.writeByte(params[i]);
		}
		outputFieldProcessor.output = output;
		EntHandle.serialize_entity_state(ent_packet, outputFieldProcessor);
	}
	
	public void write_int(int val) {
		
	}
	
}


class ServerSerializer extends Serializer<Packet> {
	
	SimulationCoordinator SimCoord;
	GameFlowSerializer GFlowSerializer;
	
	ServerSerializer(SimulationCoordinator SimCoord) {
		this.SimCoord = SimCoord;
		GFlowSerializer = new GameFlowSerializer();
	}
	
	@Override
	public Packet read(Kryo kryo, Input input, Class<Packet> arg2) {
//		ServerGameStatePacket serv = new ServerGameStatePacket();
		
		PlaceholderPacket plPacket = new PlaceholderPacket();
		
		plPacket.sequence = input.readInt();//2
		plPacket.ack_sequence = input.readInt();
		
		try {
			plPacket.data = GameInfo.readAllBytes(input);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return plPacket;
	}

	@Override
	public void write(Kryo kryo, Output output, Packet packet) {
		ServerGameStatePacket sv_gamestate_packet = (ServerGameStatePacket)packet;
		output.writeInt(sv_gamestate_packet.sequence);
		output.writeInt(sv_gamestate_packet.ack_sequence);
		output.writeInt(sv_gamestate_packet.sim_sequence);
		output.writeInt(sv_gamestate_packet.baseline_sim_sequence);
		output.writeByte(sv_gamestate_packet.state);
//		output.writeInt(sv_gamestate_packet.baseline_sequence);
		output.writeInt(sv_gamestate_packet.command_id);
		
		
		for(int i = 0; i < sv_gamestate_packet.ent_packets.length;i++) {
			output.writeInt(sv_gamestate_packet.ent_packets[i].size(),true);
			for(int j = 0; j < sv_gamestate_packet.ent_packets[i].size();j++) {
				SimCoord.EntSerializers.get(i).write(kryo, output, sv_gamestate_packet.ent_packets[i].get(j));
			}
		}
		
		RequiredUpdateList rulist = sv_gamestate_packet.reqUpdateList;
		
		byte params[] = new byte[(int)Math.ceil(((float)SimCoord.RL_SIZE)/8f)];
//		for(int i = 0; i < SimCoord.RL_SIZE;i++) {
//			if(!rulist.get(i).isEmpty()) {
//				params[i/8] |= 1<<(i%8);
//			}
//		}
//		for(int i = 0; i < params.length;i++) {
//			output.writeByte(params[i]);
//		}
		
		for(int i = 0; i < SimCoord.RL_SIZE;i++) {
			output.writeByte(!rulist.get(i).isEmpty()?1:0);
		}
		
		for(int i = 0; i < SimCoord.RL_SIZE; i++) {
			LinkedList<RequiredUpdate> requpdates = rulist.get(i);
			if(!requpdates.isEmpty()) {
				output.writeInt(requpdates.size());
				
				while(!requpdates.isEmpty()) {
					RequiredUpdate requpd = requpdates.removeFirst();
					output.writeInt(requpd.send_id);
					
					if(i==0) {
						write_disabled_entities((LinkedList<Integer>[])requpd.updObj,output);
					}
					else if (i==1) {
						GFlowSerializer.write(kryo, output, (GameFlowPacket)requpd.updObj);
					}
					else {
						kryo.writeClassAndObject(output,requpd.updObj);
					}
				}
			}
		}
	}
	
	Input input = new Input();
	
	void read_entity_stream(ServerGameStatePacket serv,PlaceholderPacket plPacket, Kryo kryo) {
		
		input.setBuffer(plPacket.data);
		
		serv.sequence = plPacket.sequence;
		serv.ack_sequence = plPacket.ack_sequence;
		
		serv.sim_sequence = input.readInt();
		serv.baseline_sim_sequence = input.readInt();
		serv.state = input.readByte();
//		serv.baseline_sequence = input.readInt();
		serv.command_id = input.readInt();
		
		int ent_pac_length =  (byte)SimCoord.entityHandlers.size();
//		serv.ent_packets = new ArrayList [ent_pac_length];
		for(int i = 0; i <ent_pac_length; i++) {
//			serv.ent_packets[i] = new ArrayList<EntityState>();
			int size = input.readInt(true);
			for(int j = 0; j <size;j++) {
				EntityState ent_pac=SimCoord.EntSerializers.get(i).read(kryo, input, null);
				serv.ent_packets[i].add(ent_pac);
			}
		}
		
		for(int i = 0; i < SimCoord.EntSerializers.size(); i++) {
			SimCoord.EntSerializers.get(i).reset_used();
		}
		
//		serv.reqUpdateList = new RequiredUpdateList(SimCoord.RL_SIZE);
		
		byte param=0;
		boolean params[] = new boolean[SimCoord.RL_SIZE];
//		for(int i = 0; i < SimCoord.RL_SIZE;i++) {
//			if(i==0 || i%8==0) {
//				param = input.readByte();
//			}
//			params[i] = (param&(1<<i))>0;
//		}
		for(int i = 0; i < SimCoord.RL_SIZE; i++) {
//			if(i == 0 || i%8)
			params[i] = input.readByte()>0;
		}
		
		for(int i = 0; i < params.length;i++) {
			if(params[i]) {
				int obj_arr_size = input.readInt();
				
				while(obj_arr_size> 0) {
					int send_id = input.readInt();
					if(i==0) {
						LinkedList <Integer>[] disabledList = read_disabled_entities(input);
						serv.reqUpdateList.add_update(i,disabledList , 0);
					}
					else if (i==1) {
						serv.reqUpdateList.add_update(i, (GameFlowPacket)GFlowSerializer.read(kryo, input,null), 0);
					}
					else {
						serv.reqUpdateList.add_update(i,kryo.readClassAndObject(input),0);
					}
					serv.reqUpdateList.get(i).getLast().send_id = send_id;
					obj_arr_size--;
				}
			}
		}
		
	}
	

	LinkedList <Integer> list = new LinkedList<Integer>();
	void write_disabled_entities(LinkedList<Integer> disabledEntList[],Output output) 
	{
		byte size = 0;
		for(int i = 0; i < disabledEntList.length; i++) {
			if(disabledEntList[i].size() > 0) {
				size++;
			}
		}
		output.writeByte(size);
		for(int i = 0; i < disabledEntList.length; i++) {
			
			if(!disabledEntList[i].isEmpty()) {
				output.writeByte(i);
				output.writeInt(disabledEntList[i].size(),true);
				list.addAll(disabledEntList[i]);
//				System.out.println("checking disabld ent list size:"+disabledEntList[i].size());
				while(!list.isEmpty()) {
					output.writeInt(list.removeFirst(),true);
				}
			}
		}
	}
	
	LinkedList <Integer>[] read_disabled_entities(Input input)
	{
		int hno = input.readByte();
		LinkedList <Integer> disabledList[] = new LinkedList[SimCoord.entityHandlers.size()];
		for(int i = 0; i <SimCoord.entityHandlers.size(); i++) {
			disabledList[i] = new LinkedList<Integer>();
		}
		while(hno > 0) {
			int hid = input.readByte();
			int count = input.readInt(true);
//			System.out.println("serializer checking disabled "+count);
			while(count > 0) {
				disabledList[hid].add(input.readInt(true));
				count--;
			}
			hno--;
		}
		return disabledList;
	}
}

class GameFlowSerializer extends Serializer<GameFlowPacket> {

	@Override
	public GameFlowPacket read(Kryo kryo, Input input, Class<GameFlowPacket> clazz) {
		GameFlowPacket gfpacket = new GameFlowPacket();
		byte params = input.readByte();
		gfpacket.type = input.readByte();
		if((params&1) > 0) {
			String string = input.readString();
			gfpacket.message = string;
		}
//		if((params&2)>0) {
//			long duration = input.readLong(true);
//			gfpacket.timer = new Timer();
//			gfpacket.timer.set(duration);
//		}
		return gfpacket;
	}

	@Override
	public void write(Kryo kryo, Output output, GameFlowPacket gameflow_packet) {
		byte params=0;
		params = (byte)((gameflow_packet.message == null || gameflow_packet.message.length() == 0)?0:1);
//		params += (byte)((gameflow_packet.timer == null)?0:2);
		output.writeByte(params);
		output.writeByte(gameflow_packet.type);
		if((params&1) > 0) {
			output.writeString(gameflow_packet.message);
		}
//		if((params&2)>0) {
//			output.writeLong(GameInfo.get_time_millis() - gameflow_packet.timer.get_finish_time(),true);
//		}
	}

}