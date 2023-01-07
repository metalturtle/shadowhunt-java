package simulation.handlers;

import basic.resource.WorldResourceHandler;
import handlers.basic.entity.Entity;
import simulation.SimulationCoordinator;
import simulation.SimulationHandler;

public abstract class EntityHandler<T extends Entity> extends WorldResourceHandler<T> {
	
	public SimulationCoordinator SimCoord;
	protected int HANDLER_ID;
	String name;
	
	protected EntityHandler(SimulationCoordinator SimCoord) {
		this.SimCoord = SimCoord;
	}
	
	
	public void set_name(String name) {
		this.name = name;
	}
	
	public String get_name() {
		return name;
	}
	
	public void set_handler_id(int handler_id) {
		this.HANDLER_ID = handler_id;
	}
	
	public int get_handler_id() {
		return this.HANDLER_ID;
	}

	abstract public void run_server();
	abstract public void run_client();

	@Override
	protected void reassign_resource_object_id(T obj, int new_id) {
		
	}
}
