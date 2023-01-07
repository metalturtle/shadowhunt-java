package scanserver;

import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import client.ClientImageHandler;
import network.NetworkDataType;
import network.NetworkInfo;
import network.packets.Packet;

public class ScanClient {
	public Client client;
	String ip_string = "localhost";
	public CopyOnWriteArrayList <ScanPacket> serverList = new CopyOnWriteArrayList<ScanPacket>();
	
	public ScanClient(String ip_string){
		this.ip_string = ip_string;
	    client = new Client();
	    
	    Kryo kryo = client.getKryo();
	    NetworkInfo.set_kryo(kryo);
	    
		client.start();
		client.setKeepAliveTCP(10000);
		try {
			client.connect(10000,ip_string , 3000 , 3001);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	    client.addListener(new Listener() {
	        public void received (Connection connection, Object object) {
	        	if(object instanceof Packet) {
	        		Packet packet = (Packet)object;
	        	}
	        }
		    @Override
		    public void connected(Connection con) {
		    }
		    @Override
		    public void disconnected(Connection con) {
		    }
	     }
	    );
	}

}
