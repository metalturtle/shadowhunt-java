package network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import client.ClientImageHandler;
import network.packets.*;


public class ServerHandler extends NetworkHandler {
		public Server server;
		Thread server_thread;
		boolean IS_READY_FLAG,IS_ERROR;
		Object lock =  new Object();

		public ServerHandler(ClientImageHandler clientimghandle){
			super(clientimghandle);
			server = new Server();
			Kryo kryo = server.getKryo();
			NetworkInfo.set_kryo(kryo);
			
			server.addListener(new Listener(){
				@Override
				public void received (Connection con, Object object)	{
					if(object instanceof Packet) {
						Packet packet = (Packet)object;
						ClImgHandle.recieve_packet(con.getID(), packet);
					}
				}
				@Override
				public void connected(Connection con) {
					ClImgHandle.connect_client(con.getID());
				}
				@Override
				public void disconnected(Connection con) {
					ClImgHandle.disconnect_client(con.getID());
				}
			});		
//(ip.DstAddr == 127.0.0.1 and udp.PayloadLength > 0 and udp.DstPort == 2301) or (ip.DstAddr == 127.0.0.1 and tcp.PayloadLength > 0 and tcp.DstPort == 2300)
		}
		
		public void init () {
			try {
				server.start();
				server.bind(2300 , 2301);
				Thread.sleep(1000);
				IS_READY_FLAG = true;
			} catch (Exception e) {
				IS_ERROR = true;
				e.printStackTrace();
			}
		}
		
		public void send_packet(int conid, Packet packet, byte type) {
			if(type == NetworkDataType.RELIABLE) {
				server.sendToTCP(conid, packet);
			} 
			if(type == NetworkDataType.UNRELIABLE) {
				server.sendToUDP(conid, packet);
			}
		}
		
		public void disconnect(int conid) {
			Connection[] connections = server.getConnections();
			for(int i = 0; i < connections.length; i++) {
				if(connections[i].getID() == conid) {
					connections[i].close();
				}
			}
		}
		
		public Kryo get_kryo() {
			return server.getKryo();
		}
		
		public boolean is_ready() {
			synchronized(lock) {
				return IS_READY_FLAG;
			}
		}
		
		public boolean is_error() {
			return IS_ERROR;
		}
		
		public void close() {
			server.close();
		}
}
