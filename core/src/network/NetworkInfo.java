package network;

import java.util.HashMap;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;

import Coordinator.SystemPacket;
import handlers.basic.Command;
import handlers.basic.GameEvent;
import handlers.basic.entity.Entity;
import handlers.basic.packets.EntityState;
import util.GameInfo;
import network.packets.Packet;
import simulation.ClientGameStatePacket;
import simulation.PlaceholderPacket;
import simulation.ServerGameStatePacket;

public class NetworkInfo {
	public static int CLIENT_INTERPOLATION_DELAY=100;
	private static boolean SERVER;
	public static int SEQUENCE = 1;
	public static int NETWORK_RATE = 20;
	public static int MAX_BUFFER_SIZE = 20;
	public static int COMMAND_BUFFER_SIZE=60;
	public static long MAXIMUM_ACCEPTABLE_LATENCY = 500;
	
	public class Type {
		public static final byte SYS=0,TCP=1,UDP=2;
	}
	private static int registrationLowerBound=0;
	static HashMap <Class,Serializer> serializerClassMap = new HashMap<Class,Serializer>();
	static HashMap <Integer,Serializer> serializerIntegerMap = new HashMap<Integer,Serializer>();
	static HashMap <Class,Integer> serializerIDMap = new HashMap<Class,Integer>();
	
	
	private static long last_send_timestamp;
	private static short server_rate = (short) (1000/NetworkInfo.NETWORK_RATE);
	private static int REGISTER_ID;
	
	public static void init(boolean server,HashMap<Object,Object> config) {
		SERVER = server;
		NETWORK_RATE=(int)(long)config.get("network_rate");
//		LAST_ACK_LIMIT=(int)(long)config.get("max_network_send");
//		HISTORY_BUFFER_SIZE=(int)(long)config.get("history_buffer_size");
	}
	
	public static boolean is_server() {
		return SERVER;
	}
	
	public static void set_kryo(Kryo kryo)
	{
	    kryo.register(Packet.class);
	    kryo.register(int[].class);
	    kryo.register(int[][].class);
	    kryo.register(short[].class);
	    kryo.register(byte[].class);
	   
	    kryo.register(boolean[].class);
	    kryo.register(Command.class);
	    kryo.register(Command[].class);
	    kryo.register(Command[][].class);
	    kryo.register(String.class);
	    
	    kryo.register(GameEvent.class);
	    kryo.register(GameEvent[].class);
	    kryo.register(String.class);
	    kryo.register(String[].class);
	    
	    kryo.register(SystemPacket.class,new SystemPacketSerializer());
	    kryo.register(EntityState.class);
	    kryo.register(EntityState[].class);
	    kryo.register(EntityState[][].class);
	    kryo.register(PlaceholderPacket.class);
	    registrationLowerBound = kryo.getNextRegistrationId();
	    REGISTER_ID=registrationLowerBound;
	}
	
	static public void register_kryo(Kryo kryo, Class clazz,Serializer serializer) {
		if(serializer == null) {
			kryo.register(clazz,REGISTER_ID);
		} else {
			kryo.register(clazz,serializer,REGISTER_ID);
		}
		REGISTER_ID+=1;
	}
	
//	static public void register_kryo(Kryo kryo, NetworkClass[] netObjArr) {
////		int channelId = registrationLowerBound+((int)channel - (int)'A')*16;
//		for(int i = 0; i < Math.min(netObjArr.length,16); i++) {
//			NetworkClass netObj = netObjArr[i];
//			if(netObj.serializer != null) {
//				kryo.register(netObj.clazz,netObj.serializer,i+channelId);
//				serializerClassMap.put(netObj.clazz,netObj.serializer);
//				serializerIntegerMap.put(i+channelId,netObj.serializer);
//			} else {
//				kryo.register(netObj.clazz,i+channelId);
//			}
//		}
//	}
	
	public static Serializer get_serializer(Class clazz) {
		return serializerClassMap.get(clazz);
	}
	
	public static Serializer get_serializer(int id) {
		return serializerIntegerMap.get(id);
	}
	
	public static int get_serializer_id(Class clazz) {
		return serializerIDMap.get(clazz);
	}
	
	public static boolean is_network_sending() {
		return GameInfo.get_time_millis()-last_send_timestamp>NetworkInfo.server_rate;
	}
	
	public static void set_last_send_timestamp() {
		NetworkInfo.last_send_timestamp = GameInfo.get_time_millis();
	}
}



