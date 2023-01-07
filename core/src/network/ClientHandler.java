package network;
import java.io.IOException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import Coordinator.Coordinator;
import client.ClientImageHandler;
import network.packets.*;

public class ClientHandler extends NetworkHandler{
	public Client client;
	String ip_string = "localhost";
	int tcp_port = 2300,udp_port = 2301;
	boolean IS_READY_FLAG,IS_ERROR;
	Object lock = new Object();
	
	public ClientHandler(String ip_string,int tcp_port, int udp_port, ClientImageHandler clientsystem){
		super(clientsystem);
		this.ip_string = ip_string;
		this.tcp_port = tcp_port;
		this.udp_port = udp_port;
	    client = new Client();
	    
	    Kryo kryo = client.getKryo();
	    NetworkInfo.set_kryo(kryo);
	    client.addListener(new Listener() {
	        public void received (Connection connection, Object object) {
	        	if(object instanceof Packet) {
	        		Packet packet = (Packet)object;
	        		ClImgHandle.recieve_packet(0, packet);
	        	}
	        }
		    @Override
		    public void connected(Connection con) {
		    	synchronized(lock) {
		    		IS_READY_FLAG = true;
		    	}
		    	ClImgHandle.connect_client(0);
		    }
		    @Override
		    public void disconnected(Connection con) {
		    	synchronized(lock) {
		    		IS_READY_FLAG = false;
		    	}
		    	ClImgHandle.disconnect_client(0);
		    }
	     }
	    );
	}
	
	public void init () throws Exception {
		client.start();
		client.setKeepAliveTCP(10000);
		System.out.println("before connect");
		client.connect(10000,ip_string , tcp_port , udp_port);
		System.out.println("after connect");
	}
	
	public void disconnect(int conid) {
		client.close();
	}

	@Override
	public void send_packet(int conid, Packet packet, byte type) {
		if(type == NetworkDataType.RELIABLE) {
			client.sendTCP(packet);
		}
		if(type == NetworkDataType.UNRELIABLE) {
			client.sendUDP(packet);
		}
	}
	
	public boolean is_ready() {
		return IS_READY_FLAG;
	}
	
	public boolean is_error() {
		return IS_ERROR;
	}
	
	@Override
	public void close() {
		client.close();
	}

	@Override
	public Kryo get_kryo() {
		return client.getKryo();
	}

}
 