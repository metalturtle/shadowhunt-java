package handlers.interaction;

import basic.Rectangle;
import basic.Vector;
import basic.resource.WorldResourceHandler;
import handlers.basic.entity.Entity;
import world.World;

public class CollisionInteraction extends EntityInteraction {

	public Entity my_entity,hit_entity;
//	Rectangle box;
	public Vector toPoint;
	boolean solid_flag;
	float speed;
	public Vector totalMoveVecArray;
	public boolean[] entMoveAxisCheck;
	byte z_pos, z_height, type;
	
	public CollisionInteraction(Vector pos, Rectangle bound) {
		super(pos, bound);
	}

	@Override
	public void test(World world, WorldResourceHandler<EntityInteraction> list) {
		// TODO Auto-generated method stub
		
	}

}
