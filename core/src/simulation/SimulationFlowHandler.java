package simulation;

import java.util.HashMap;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import Coordinator.Coordinator;
import basic.Rectangle;
import basic.Vector;
import client.ClientImage;
import client.ClientImageHandler;
import handlers.basic.ClientCommand;
import handlers.basic.entity.Entity;
import handlers.basic.entity.SyncedEntity;
import network.ServerHandler;
import simulation.handlers.EntityHandler;
import util.GameInfo;

abstract public class SimulationFlowHandler
{
	public SimulationHandler SimHandle;
	public SimulationCoordinator SimCoord;
	protected ClientImageHandler CIHandle;

	protected JSONObject jsonFile, resourceFile;

	public SimulationFlowHandler(SimulationCoordinator SimCoord, ClientImageHandler CIHandle)
	{
		this.SimCoord = SimCoord;
		this.SimHandle = SimCoord.SimHandle;
		this.CIHandle = CIHandle;
	}
	
	protected void send_simflow_to_clients(GameFlowPacket gfpacket)
	{
		for (Iterator <ClientImage> iterator = CIHandle.get_iterator(); iterator.hasNext();) {
			ClientImage clientImage = iterator.next();
			if(clientImage.is_disconnected()) {
				continue;
			}
			if(!clientImage.CSData.state_equals(Coordinator.SystemState.RUN)) {
				continue;
			}
			if(!(clientImage.CSimState.state_equals(SimulationCoordinator.State.SYNCUP)
					|| clientImage.CSimState.state_equals(SimulationCoordinator.State.RUN))) {
				continue;
			}
			SimCoord.add_gameflow_packet(clientImage,gfpacket);
		}
	}
	
	
	abstract public SyncedEntity add_new_client(ClientImage clientImage);
	abstract public void disconnect_client(int con_id,SyncedEntity entity);
	abstract public void loop();
	abstract public void level_load_entities(JSONArray jsonObj);
	abstract public void create_misc_object(EntityHandler EntHandle, Vector pos, Rectangle bound, String params);
}
