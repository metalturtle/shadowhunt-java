package Coordinator;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import com.esotericsoftware.kryo.Kryo;

import client.ClientImage;
import client.ClientImageHandler;
import handlers.basic.Timer;
import loader.ClientLoader;
import loader.Loader;
import network.ClientHandler;
import network.NetworkDataType;
import network.NetworkHandler;
import network.ServerHandler;
import network.packets.Packet;
import simulation.SimulationHandler;
import simulation.SimulationCoordinator;


public class Coordinator {

	public ClientImageHandler CIHandle;
	
	public Loader loader;
	public SimulationCoordinator SimCoord;

	public NetworkHandler NetworkHandle;
	public Kryo kryo;
	
	boolean SERVER;
	
	public Coordinator(Loader loader) {
		this.loader = loader;
	}
	
	public void server_init() {
		this.SERVER = true;
		this.CIHandle = new ClientImageHandler(true,this);
		NetworkHandle = new ServerHandler(CIHandle);
		CIHandle.NetworkHandle = NetworkHandle;
		this.kryo =((ServerHandler)NetworkHandle).server.getKryo();
	}
	
	public void client_init() {
		this.SERVER = false;
		this.CIHandle = new ClientImageHandler(false,this);
//		142.93.216.106
		NetworkHandle = new ClientHandler("157.245.107.75",2300,2301,CIHandle);
		this.CIHandle.NetworkHandle = NetworkHandle;
		this.kryo = ((ClientHandler)NetworkHandle).client.getKryo();
	}
	
	public Kryo get_kryo() {
		return this.kryo;
	}
	
	public boolean is_server() {
		return SERVER;
	}
	
	public void dispose() {
		NetworkHandle.close();
		loader.dispose();
	}
	
	/********** CLIENT SYSTEM MANAGEMENT **********/
	
	public static class SystemState {
		public final static byte IDLE,CONNECTING,CONNECTED,RESOURCE_VERIFY,START_LOAD,LOADING,RUN,STOP,DISCONNECT;
		
		static {
			byte val=0;
			IDLE=val++;
			CONNECTING=val++;
			CONNECTED=val++;
			RESOURCE_VERIFY=val++;
			START_LOAD=val++;
			LOADING=val++;
			RUN=val++;
			STOP=val++;
			DISCONNECT=val++;
		}
	}
	
	Queue <SystemPacket> systemQueue = new LinkedList<SystemPacket>();
	
	boolean check_sp_state(SystemPacket systemPacket,byte state) {
		if(systemPacket.code == state) {
			return true;
		}
		return false;
	}
	
	public void send_system_packet(ClientImage clientImage,byte type,byte code, String message) {
		ClientSystemData csdata = clientImage.CSData;
		SystemPacket syspac = csdata.create_system_packet(type, code, message);
		NetworkHandle.send_packet(clientImage.get_con_id(),syspac,NetworkDataType.RELIABLE);
	}
	
	public void disconnect(ClientImage clientImage,String message) {
		System.out.println("coordinator disconnect: "+message);
		ClientSystemData csdata = clientImage.CSData;
		csdata.set_state(SystemState.DISCONNECT);
		SystemPacket syspacket = csdata.create_system_packet(SystemPacket.STATE_CHANGE,SystemState.DISCONNECT,message);
		csdata.disconnectTimer.set(2000);
		NetworkHandle.send_packet(clientImage.get_con_id(),syspacket,NetworkDataType.RELIABLE);
	}
	
	public void server_handle_system_packet(ClientImage clientImage) {
		
		ClientSystemData csdata = clientImage.CSData;
		
		if(csdata.state_equals(SystemState.DISCONNECT)) {
			if(csdata.disconnectTimer.check()) {
				NetworkHandle.disconnect(clientImage.get_con_id());
			}
			return;
		}
		
		csdata.retrive_recieved_packets(systemQueue);
		while(!systemQueue.isEmpty()) {
			SystemPacket systemPacket = (SystemPacket)systemQueue.remove();
			csdata.update_sequence(systemPacket);
			if(systemPacket.type == SystemPacket.STATE_CHANGE) {
				byte state = (byte)systemPacket.code;

				if(systemPacket.ack_sequence != csdata.get_sequence()) {
					disconnect(clientImage,"ERROR: Disconnecting client due to unsynchronized system change");
					break;
				}
				
				if(csdata.state_equals(SystemState.IDLE)) {
					if(check_sp_state(systemPacket,SystemState.CONNECTED)) {
						if(verify_client_application(systemPacket.message)) {
							csdata.set_state(SystemState.RESOURCE_VERIFY);
							send_system_packet(clientImage,SystemPacket.STATE_CHANGE, SystemState.RESOURCE_VERIFY, "level:level.json");
						} else {
							this.disconnect(clientImage, "ERROR: Disconnecting client due to invalid application or device.");
							break;
						}
					} else {
						this.disconnect(clientImage, "ERROR: Disconnecting terclient due to invalid application state at app verification.");
						break;
					}
				}
				
				else if(csdata.state_equals(SystemState.RESOURCE_VERIFY)) {
					if(check_sp_state(systemPacket,SystemState.RESOURCE_VERIFY)) {
						if(systemPacket.message.equals("FAIL")) {
							this.disconnect(clientImage, "ERROR: Disconnecting client due to lack of level file.");
							break;
						}
						if(verify_client_level(systemPacket.message)) {
							csdata.set_state(SystemState.START_LOAD);
							send_system_packet(clientImage,SystemPacket.STATE_CHANGE,SystemState.START_LOAD,"");
						} else {
							this.disconnect(clientImage, "ERROR: Disconnecting client due to incorrect hash of level");
							break;
						}
					} else {
						this.disconnect(clientImage, "ERROR: Disconnecting client due to invalid application state at resource verification.");
						break;
					}
				}
				
				else if(csdata.state_equals(SystemState.START_LOAD)) {
					if(check_sp_state(systemPacket,SystemState.RUN)) {
						csdata.set_state(SystemState.RUN);
//						this.SimCoord.connect_client(clientImage);
					}
					if(check_sp_state(systemPacket,SystemState.LOADING)) {
						if(systemPacket.message.equals("FAIL")) {
							this.disconnect(clientImage,"ERROR: Disconnecting client due to failure of loading resources.");
							break;
						}
					}
				}
				
			}
		}	
	}
	
	public void client_load_system(ClientImage clientImage) throws Exception {
		
//		if(LocalClientImage == null) {
//			LocalClientImage = CIHandle.get_client_by_localid(0);
//		}

		
		ClientSystemData csdata = clientImage.CSData;
		
		if(csdata.state_equals(SystemState.DISCONNECT)) {
			return;
		}
		
		if(csdata.state_equals(SystemState.IDLE)) {
			csdata.set_state(SystemState.CONNECTED);
		}
		
		if(csdata.state_equals(SystemState.CONNECTED) && csdata.is_state_changed()) {
			send_system_packet(clientImage,SystemPacket.STATE_CHANGE, SystemState.CONNECTED, "A:SHADOW_HUNT,D:PC");
			csdata.off_state_change();
		}
		
		if(csdata.state_equals(SystemState.START_LOAD)) {
//			loader.start_load_thread(this,"level.json").start();
			
			csdata.set_state(SystemState.LOADING);
		}

		csdata.retrive_recieved_packets(systemQueue);
		
		while(!systemQueue.isEmpty()) {
			SystemPacket systemPacket = (SystemPacket)systemQueue.remove();
			csdata.update_sequence(systemPacket);
			
			if(systemPacket.type == SystemPacket.STATE_CHANGE) {
				
				if(check_sp_state(systemPacket,SystemState.DISCONNECT)) {
					csdata.set_state(SystemState.DISCONNECT);
					NetworkHandle.disconnect(clientImage.get_con_id());
					throw new Exception(systemPacket.message);
				}
				
				if(check_sp_state(systemPacket,SystemState.RESOURCE_VERIFY)) {
					csdata.set_state(SystemState.RESOURCE_VERIFY);
					send_system_packet(clientImage,SystemPacket.STATE_CHANGE, SystemState.RESOURCE_VERIFY, "s");
				}
				
				if(check_sp_state(systemPacket,SystemState.START_LOAD)) {
					csdata.set_state(SystemState.START_LOAD);
				}
			}
		}
		
		if(csdata.state_equals(SystemState.LOADING)) {
			((ClientLoader)loader).load_resources(this,"level.json");
			if(loader.is_ready()) {
				csdata.set_state(SystemState.RUN);
				send_system_packet(clientImage,SystemPacket.STATE_CHANGE,SystemState.RUN, "s");
				this.SimCoord = loader.SimCoord;
			}
			if(loader.is_error()) {
				send_system_packet(clientImage,SystemPacket.STATE_CHANGE,SystemState.LOADING, "F");
			}
		}
		
//		csdata.get_send_queue(systemQueue);
//		while(!systemQueue.isEmpty()) {
//			SystemPacket packet = (SystemPacket)systemQueue.remove();
////			send_packet(CSData,packet,NetworkDataType.RELIABLE);
//			NetworkHandle.send_packet(clientImage.get_con_id(),packet,NetworkDataType.RELIABLE);
//		}
	}
	
	
	/********** CLIENT SYSTEM VERIFICATION **********/

	boolean verify_client_application(String message) {
		return true;
	}
	
	boolean verify_client_level(String message) {
		return true;
	}
	
	/********** RUN METHODS **********/
	
	boolean FIRST_RUN_DONE;
	ClientSystemData CSData;
	Timer waitTimer = new Timer();
//	public ClientImage LocalClientImage;
	
	public void run_server() throws Exception {
		CIHandle.run();
		if(FIRST_RUN_DONE) {
			if(NetworkHandle.is_error()) {
				throw new Exception("ERROR: failed to init the server.");
			}
			ClientSystemData csdata = CSData;
			if(csdata.state_equals(SystemState.RUN)) {
				for(Iterator <ClientImage> it = CIHandle.get_iterator(); it.hasNext();) {
					ClientImage CImg = it.next();
					server_handle_system_packet(CImg);
					if(CImg.CSData.state_equals(SystemState.RUN)) {
					}
					this.SimCoord.run_server();
				}
			} else {
				if(csdata.state_equals(SystemState.CONNECTING)) {
					if(NetworkHandle.is_ready()) {
						csdata.set_state(SystemState.START_LOAD);
					}
				} else if (csdata.state_equals(SystemState.START_LOAD)) {
					loader.start_load_thread(this,"level.json").start();
					csdata.set_state(SystemState.LOADING);
				} else if(csdata.state_equals(SystemState.LOADING)) {
					if(loader.is_ready()) {
						this.SimCoord = loader.SimCoord;
						csdata.set_state(SystemState.RUN);
					}
					if(loader.is_error()) {
						throw new Exception(loader.get_load_message());
					}
				}
			}
		} else {
			FIRST_RUN_DONE=true;
//			this.LocalClientImage = this.CIHandle.add_client(0);
			CSData = new ClientSystemData();
			NetworkHandle.init();
			this.CSData.set_state(SystemState.CONNECTING);
		}
	}
	
	public void run_client() throws Exception {
		CIHandle.run();
		if(FIRST_RUN_DONE) {
			if(!NetworkHandle.is_ready()) {
				return;
			}
			if(NetworkHandle.is_error()) {
				throw new Exception("ERROR: failed to connect to server.");
			}
			if(CIHandle.is_empty()) {
				return;
			}
			if(!waitTimer.check()) {
				return;
			}
			
			ClientImage clientImage = CIHandle.get_client_by_localid(0);
			client_load_system(clientImage);
			
			if(clientImage.CSData.state_equals(SystemState.RUN)) {
				SimCoord.run_client();
			}
			
		} else {
			NetworkHandle.init();
			waitTimer.set(1000);
			FIRST_RUN_DONE=true;
		}
	}
	
	public void disconnect(ClientImage clientImage) {
		SimCoord.disconnect_client(clientImage);
	}
}
