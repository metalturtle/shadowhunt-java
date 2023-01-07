package client;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.ConcurrentHashMap;

import Coordinator.Coordinator;
import Coordinator.SystemPacket;
import network.NetworkHandler;
import network.packets.Packet;
import simulation.ClientGameStatePacket;
import simulation.PlaceholderPacket;
import simulation.ServerGameStatePacket;

public class ClientImageHandler {

	private LinkedList <ClientImage> clientImageList = new LinkedList<ClientImage>();
	private ConcurrentHashMap <Integer,ClientImage> clientImageMap = new ConcurrentHashMap<Integer,ClientImage>();
	private LinkedList <Integer> connectRequest = new LinkedList<Integer>();
	private LinkedList <Integer> disconnectRequest = new LinkedList<Integer>();
	Object connect_status_lock = new Object(),client_image_lock = new Object();
	public NetworkHandler NetworkHandle;
	
	boolean SERVER;
	Coordinator coordinator;
	public ClientImageHandler(boolean server,Coordinator coordinator) {
		this.SERVER = server;
		this.coordinator = coordinator;
	}

	/********** GET CLIENT IMAGE **********/
	
	public ClientImage get_client_by_conid(int conid) {
		return clientImageMap.get(conid);
	}
	
	public ClientImage get_client_by_localid(int localid) {
		return clientImageList.get(localid);
	}
	
	public Iterator<ClientImage> get_iterator() {
		return clientImageList.iterator();
	}
	
	public int size() {
		return clientImageList.size();
	}
	
	public boolean is_empty() {
		return clientImageList.isEmpty();
	}
	
	/********** NETWORKING CALLBACKS **********/
	
	public boolean connect_client(int conid) {
		synchronized(connect_status_lock) {
			connectRequest.add(conid);
			return true;
		}
	}
	
	public void disconnect_client(int conid) {
		synchronized(connect_status_lock) {
			disconnectRequest.add(conid);
		}
	}
	
	public void recieve_packet(int conid,Packet packet) {
		synchronized(client_image_lock) {
			ClientImage clientobj = clientImageMap.get(conid);
			if(clientobj == null) {
				this.NetworkHandle.disconnect(conid);
			}
			if(packet instanceof SystemPacket) {
				clientobj.CSData.recieve_packet((SystemPacket)packet);
			}
			if(!SERVER && packet instanceof ServerGameStatePacket) {
				clientobj.CSimState.recieve_packet(packet);
			}
//			if(SERVER && packet instanceof ClientGameStatePacket) {
//				clientobj.CSimState.recieve_packet(packet);
//			}
			if(packet instanceof PlaceholderPacket) {
				clientobj.CSimState.recieve_packet(packet);
			}
		}
	}
	
	/********** CONNECT/DISCONNECT **********/
		
	protected void handle_new_connections(LinkedList<Integer> connectList) {
		while(!connectList.isEmpty()) {
			int conid = connectList.removeFirst();
			ClientImage clientImage = add_client(conid);
			clientImage.set_connect();
		}
	}
	
	protected void remove_disconnected_clients(LinkedList<Integer> disconnectList) {
		while(!disconnectList.isEmpty()) {
			int conid = disconnectList.removeFirst();
			System.out.println("client image handler removing client image: "+conid);
			if(clientImageMap.containsKey(conid)) {
				coordinator.disconnect(clientImageMap.get(conid));
				remove_client(conid);
			}
		}
	}

	public ClientImage add_client(int conid) {
		int lid = clientImageList.size();
		ClientImage clientImage = new ClientImage(conid,lid);
		clientImageMap.put(conid,clientImage);
		clientImageList.add(clientImage);
		return clientImage;
	}

	public void remove_client(int conid) {
		ClientImage clientImage = clientImageMap.remove(conid);
		clientImageList.remove(clientImage);
	}
	

	/********** RUN **********/
	
	LinkedList <Integer> connectList = new LinkedList<Integer>();
	LinkedList <Integer> disconnectList = new LinkedList<Integer>();
	
	public void run() {
		synchronized(connect_status_lock) {
			if(!connectRequest.isEmpty()) {
				connectList.addAll(connectRequest);
				connectRequest.clear();
			}
			if(!disconnectRequest.isEmpty()) {
				disconnectList.addAll(disconnectRequest);
				disconnectRequest.clear();
			}
		}
		synchronized(client_image_lock) {
			handle_new_connections(connectList);
			remove_disconnected_clients(disconnectList);
		}
	}
	
}
