package simulation;
import java.util.ArrayList;
import java.util.HashMap;

import Coordinator.Coordinator;
import Coordinator.HandlerInitRunner;
import basic.InputState;
import basic.ObjectPoolingSystem;
import basic.Rectangle;
import client.ClientImageHandler;
import handlers.basic.GameFlow;
import loader.Loader;
import network.NetworkHandler;
import simulation.handlers.EntityHandler;
import simulation.handlers.interaction.EntityCollisionHandler;
import util.GameInfo;
import world.World;
import handlers.GameEventHandler;
import render.RenderEntityMainHandler;

public class SimulationHandler{

	public World GWorld;
	public ObjectPoolingSystem objectPoolingSystem;
	public EntityCollisionHandler EntColHandle;
	
	public RenderEntityMainHandler RendEntMainHandle;

	ArrayList <EntityHandler> EntHandlers;
	ArrayList <SyncedEntityHandler> SyncEntHandlers;
	
	HashMap <String,Integer> EntHandleNameToHandleID;
	int ENT_HANDLERS_SIZE;
	int SYNCED_ENT_HANDLERS_SIZE;
	boolean RUN;
	
	public SimulationHandler(RenderEntityMainHandler RendEntMainHandle)
	{
		this.objectPoolingSystem = new ObjectPoolingSystem();
		 
		GWorld=new World();	
		this.EntColHandle = new EntityCollisionHandler(GWorld);
		this.RendEntMainHandle = RendEntMainHandle;

		EntHandlers = new ArrayList <EntityHandler>();
		SyncEntHandlers = new ArrayList<SyncedEntityHandler>();
		EntHandleNameToHandleID = new HashMap<String,Integer>();
	}
	
	public void start()
	{
		RUN = true;
	}
	
	public void add_entity_handler(EntityHandler EntHandle,String name)
	{
		name = name.toLowerCase();
		int handler_id = EntHandlers.size();
		EntHandle.set_handler_id(handler_id);
		EntHandle.set_name(name);
		EntHandleNameToHandleID.put(name,handler_id);
		EntHandlers.add(EntHandle);
		
		ENT_HANDLERS_SIZE+=1;
		if(EntHandle instanceof SyncedEntityHandler){
			SyncedEntityHandler SyncedHandler = (SyncedEntityHandler)EntHandle;
			SyncedHandler.set_synced_handler_id(SYNCED_ENT_HANDLERS_SIZE);
			SyncEntHandlers.add(SyncedHandler);
			SYNCED_ENT_HANDLERS_SIZE+=1;
		}
	}
	
	public boolean containsHandler(String handler)
	{
		return EntHandleNameToHandleID.containsKey(handler);
	}
	
	public EntityHandler get_entity_handler(int HANDLER_ID)
	{
		return EntHandlers.get(HANDLER_ID);
	}
	
	public EntityHandler get_entity_handler(String name)
	{
		name = name.toLowerCase();
		int HANDLER_ID = EntHandleNameToHandleID.get(name);
		return EntHandlers.get(HANDLER_ID);
	}
	
	public ArrayList<SyncedEntityHandler> get_synced_handlers()
	{
		return SyncEntHandlers;
	}
	
	public ArrayList <EntityHandler> get_ent_handlers()
	{
		return EntHandlers;
	}
	
	public int get_ent_handlers_size()
	{
		return ENT_HANDLERS_SIZE;
	}
	
	public int get_synced_ent_handlers_size()
	{
		return SYNCED_ENT_HANDLERS_SIZE;
	}
	
	public void run_server()
	{
		for(int i = 0; i < SyncEntHandlers.size(); i++) {
			EntityHandler EntHandle = SyncEntHandlers.get(i);
			EntHandle.run_server();
		}
		cleanup();
	}
	
	public void run_client()
	{
		for(int i = 0; i < SyncEntHandlers.size(); i++) {
			EntityHandler EntHandle = SyncEntHandlers.get(i);
			EntHandle.run_client();
		}	
		cleanup();
	}
	
	public void cleanup()
	{
		for(int i=0; i < EntHandlers.size(); i++) {
			EntityHandler EntHandle = EntHandlers.get(i);
			EntHandle.cleanup();
		}
		EntColHandle.cleanup();
		RendEntMainHandle.cleanup();
		this.objectPoolingSystem.loop();
	}
	
}