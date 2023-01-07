package handlers.interaction;

import basic.Rectangle;
import basic.Vector;
import basic.resource.WorldResourceHandler;
import basic.resource.WorldResourceObject;
import handlers.basic.entity.Entity;
import world.World;

public abstract class EntityInteraction extends WorldResourceObject {
	public EntityInteraction(Vector pos, Rectangle bound) {
		super(pos,bound);
		// TODO Auto-generated constructor stub
	}
	public Entity myEntity, hitEntity;
	boolean hitFlag;
	
	public abstract void test(World world, WorldResourceHandler<EntityInteraction> list);
	
	public boolean check_hit() {return hitFlag;}
}
