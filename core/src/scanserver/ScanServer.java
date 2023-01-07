package scanserver;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import client.ClientImageHandler;
import network.NetworkDataType;
import network.NetworkHandler;
import network.NetworkInfo;
import network.packets.Packet;

public class ScanServer {
	public Server server;
	Thread server_thread;
	boolean IS_READY_FLAG,IS_ERROR;
	Object lock =  new Object();
	
//	CopyOnWriteArrayList<ScanPacket> serverList = new CopyOnWriteArrayList<ScanPacket> ();
//	CopyOnWriteArrayList<ScanPacket> clientList = new CopyOnWriteArrayList<ScanPacket> ();
	ConcurrentHashMap <Integer,ScanPacket> serverList = new ConcurrentHashMap<Integer,ScanPacket>();
	
	
	public ScanServer(){
		server = new Server();
		Kryo kryo = server.getKryo();
		NetworkInfo.set_kryo(kryo);
		
		try {
			server.start();
			server.bind(3000 , 3001);
			IS_READY_FLAG = true;
		} catch (Exception e) {
			IS_ERROR = true;
			e.printStackTrace();
		}
		
		server.addListener(new Listener(){
			@Override
			public void received (Connection con, Object object)	{
				if(object instanceof ScanPacket) {
					ScanPacket packet = (ScanPacket)object;
					if(packet.server) {
						serverList.put(con.getID(),packet);
					} else {
						Set <Integer>set = serverList.keySet();
						for(Integer val : set) {
							ScanPacket scanpac = serverList.get(val);
							con.sendTCP(scanpac);
						}
					}
				}
			}
			@Override
			public void connected(Connection con) {
				
			}
			@Override
			public void disconnected(Connection con) {
				if(serverList.containsKey(con.getID())) {
					serverList.remove(con.getID());
				}
				
			}
		});		
//(ip.DstAddr == 127.0.0.1 and udp.PayloadLength > 0 and udp.DstPort == 2301) or (ip.DstAddr == 127.0.0.1 and tcp.PayloadLength > 0 and tcp.DstPort == 2300)
	}
	
}

