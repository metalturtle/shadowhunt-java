package simulation;

import basic.Rectangle;
import basic.Vector;
import basic.resource.WorldResourceHandler;
import basic.resource.WorldResourceObject;
import handlers.basic.EntityMovementObject;
import handlers.basic.entity.SyncedGameEntity;
import handlers.basic.packets.EntityState;
import render.SpriteHandler;
import render.basic.Sprite;
import simulation.handlers.interaction.EntityCollisionHandler;

public abstract class SyncedGameEntityHandler<E extends EntityState,T extends SyncedGameEntity> extends SyncedEntityHandler<E,T> {

	final byte worldObjSize, spriteSize,intrSize;
	int timerSize;
	EntityCollisionHandler EntColHandle;
	public SyncedGameEntityHandler(SimulationCoordinator SimCoord, boolean history,byte worldObjSize, byte spriteSize, byte intrSize,byte timerSize) {
		super(SimCoord);
		this.worldObjSize = worldObjSize;
		this.spriteSize = spriteSize;
		this.intrSize = intrSize;
		this.timerSize = timerSize;
		this.EntColHandle = SimCoord.SimHandle.EntColHandle;
	}
	
	@Override
	public T add_resource_object(Vector pos, Rectangle bound, String params) {
		T ent = super.add_resource_object(pos, bound, params);
		this.init_children(ent,(byte)(worldObjSize+spriteSize+intrSize));

		this.set_child(ent,0,EntColHandle,ent.pos,ent.bound,null);
		
		ent.init_timer(timerSize);
		
//		EntityMovementObject moveobj = (EntityMovementObject)get_child(ent,0);
		EntityMovementObject moveobj = ent.get_move_object();
		
		ent.set_child_sizes(worldObjSize, spriteSize, intrSize);
		return ent;
	}
	
	//////////////////////////////////get child//////////////////////////////////////////
	public Sprite get_sprite(T ent,int id) {
		return (Sprite)get_child(ent,id+intrSize);
	}
	
	public EntityMovementObject get_move_object(T ent) {
		return (EntityMovementObject)get_child(ent,0);
	}
	
	public WorldResourceObject get_inventory_object(T ent, int id) {
		return get_child(ent,intrSize+spriteSize+id);
	}
	
	/////////////////////////////////set child//////////////////////////////////////
	
	public Sprite set_sprite(T ent,int id,SpriteHandler SpriteHandle,Vector pos, Rectangle bound,String params) {
		return (Sprite)set_child(ent,intrSize+id,SpriteHandle,pos,bound,params);
	}
	
	protected void set_inventory_object(T obj,int i, WorldResourceHandler WRH, Vector pos, Rectangle bound, String params) {
		System.out.println("setting invent obj: "+obj+" "+(intrSize+spriteSize+i));
		set_child(obj,intrSize+spriteSize+i,WRH,pos,bound,params);
	}
	
	////////////////////////////////////disable child//////////////////////////////////////////////////////
	
	public void disable_sprite_object(T ent, int id) {
		super.disable_child(ent, intrSize+id);
	}
	
	public void disable_inventory_object(T ent, int id) {
		System.out.println("get invent obj: "+(intrSize+spriteSize+id));
		super.disable_child(ent, intrSize+spriteSize+id);
	}
}
