package simulation.handlers.interaction;

import java.util.Iterator;

import basic.Rectangle;
import basic.Vector;
import basic.resource.WorldResourceHandler;
import basic.resource.WorldResourceObject;
import handlers.basic.entity.Entity;
import handlers.basic.Command;
import handlers.basic.EntityMovementObject;
import world.World;
import world.basic.Wall;


public class EntityCollisionHandler extends WorldResourceHandler<EntityMovementObject> {
	World GWorld;
	
	float MINSTEP = 1;
	static float EPSILON = 0.0001f;
	
	public EntityCollisionHandler(World world) {
		super();
		this.GWorld=world;
	}

	public EntityMovementObject create_new_element() {
		return new EntityMovementObject();
	}
	
	public void set_box (int col_id, Vector pos, Rectangle box, byte z_pos, byte z_height, boolean solid_flag) {
		EntityMovementObject moveobj = get_resource_object(col_id);
		moveobj.pos.set(pos);
		moveobj.bound.set(box);
		moveobj.set_z_pos( z_pos );
		moveobj.set_z_height(z_height);
		moveobj.set_solid_flag(solid_flag);
	}
	
	public void set_pos (int col_id, Vector pos) {
		EntityMovementObject moveobj = get_resource_object(col_id);
		moveobj.pos.set(pos);
	}
	
	public void set_type(int col_id, byte type) {
		get_resource_object(col_id).set_type(type);
	}
	
	public byte get_type (int col_id) {
		return get_resource_object(col_id).get_type();
	}
	
	void set_solid(int colid,boolean solid) {
		get_resource_object(colid).set_solid_flag(solid);
	}
	
	public WorldResourceObject check_collision_move_objects(EntityMovementObject moveobj) {
		Rectangle rect1 = moveobj.get_view_rect();
		moveobj.hitEntity=null;
		for(Iterator<EntityMovementObject> it = get_iterator(rect1); it.hasNext();) {
			EntityMovementObject hit_moveobj = it.next();
			if(moveobj == hit_moveobj)
				continue;
//			if(hit_moveobj.is_marked_disable()  || hit_moveobj.get_parent() == null || hit_moveobj.get_parent().is_marked_disable()) {
//				continue;
//			}
			if(hit_moveobj.get_parent() == null)
				continue;
			Rectangle rect2 = hit_moveobj.get_view_rect();
			if(Rectangle.check_rect_intersection(rect1,rect2)) {
				moveobj.hitEntity = hit_moveobj.get_parent();
				hit_moveobj.hitEntity = moveobj.get_parent();
				return hit_moveobj.get_parent();
			}
		}
		return null;
	}
	
//	Entity check_coll_rect_move_obj(Rectangle rect, int ignore_col_id)
//	{
//		for(Iterator<EntityMovementObject> it = get_iterator(rect); it.hasNext();)
//		{
//			EntityMovementObject hit_moveobj = it.next();
//			if(hit_moveobj.get_local_id() == ignore_col_id)
//				continue;
//			Rectangle rect2 = hit_moveobj.get_view_rect();
//			if(Rectangle.check_rect_intersection(rect,rect2))
//			{
//				return hit_moveobj.myEntity;
//			}
//		}
//		return null;
//	}
	
	Rectangle ent_rect = new Rectangle(),move_box = new Rectangle(), union_box = new Rectangle();
	Vector tempvec = new Vector(),final_pos = new Vector();
	public Vector move_collision(EntityMovementObject moveobj,Entity ent,float delta_time) {
		moveobj.toPoint.set(ent.vdir);
		moveobj.set_speed(ent.get_speed());
		
		float minstepX=0,minstepY=0;
		moveobj.entMoveAxisCheck[0] = moveobj.entMoveAxisCheck[1] = false;
		tempvec.set(moveobj.toPoint);
		tempvec.multiply(moveobj.get_speed()*(delta_time));
//		tempvec.multiply(moveobj.get_speed());
		
		moveobj.totalMoveVecArray.set(tempvec);
		
		if(tempvec.x()!=0)
			moveobj.entMoveAxisCheck[0] = true;
		if(tempvec.y()!=0)
			moveobj.entMoveAxisCheck[1] = true;
		
		float minstep,epsilon = 1f;
		Rectangle box = moveobj.bound;
		ent_rect.set(ent.pos.x() + box.x() ,ent.pos.y() + box.y(),box.w(),box.h());
		move_box.set(ent_rect);
		move_box.x(move_box.x()+moveobj.totalMoveVecArray.x());
		move_box.y(move_box.y()+moveobj.totalMoveVecArray.y());
		Rectangle.union(ent_rect,  move_box, union_box);
		move_box.set(union_box);

		while(moveobj.entMoveAxisCheck[1]==true) {
			minstep = Math.signum(moveobj.totalMoveVecArray.y())*Math.min(Math.abs(moveobj.totalMoveVecArray.y()),MINSTEP);
			ent_rect.y(ent_rect.y()+minstep);
			for(Iterator <Wall> iterator =GWorld.get_iterator(move_box);iterator.hasNext();) {
				Wall wall = iterator.next();
				Rectangle wall_rect = wall.get_view_rect();
				if(Rectangle.check_rect_intersection(ent_rect,wall_rect)) {
					if(minstep>0) {
						minstep = Math.max(minstep-((ent_rect.y()+ent_rect.h())-(wall_rect.y()))-epsilon,0);
					}
					if(minstep<0) {
						minstep = Math.min(minstep + ((wall_rect.y()+wall_rect.h())-ent_rect.y())+epsilon,0);
					}
					moveobj.entMoveAxisCheck[1] = false;
				}
			}
			tempvec = moveobj.totalMoveVecArray;
			tempvec.set(tempvec.x(),tempvec.y()-minstep);
			moveobj.totalMoveVecArray.set(tempvec);
			if(Math.abs(tempvec.y()) < epsilon)
				moveobj.entMoveAxisCheck[1] = false;
			minstepY += minstep;
		}

		ent_rect.set(ent.pos.x()+box.x(),ent.pos.y()+box.y(),box.w(),box.h());
		while(moveobj.entMoveAxisCheck[0]==true) {
			minstep = Math.signum(moveobj.totalMoveVecArray.x())*Math.min(Math.abs(moveobj.totalMoveVecArray.x()),MINSTEP);
			ent_rect.x(ent_rect.x()+minstep);
			for(Iterator <Wall> iterator =GWorld.get_iterator(move_box);iterator.hasNext();) {
				Wall wall = iterator.next();
				Rectangle wall_rect = wall.get_view_rect();
				if(Rectangle.check_rect_intersection(ent_rect,wall_rect))
				{
					if(minstep>0)
					{
						minstep = Math.max(minstep-((ent_rect.x()+ent_rect.w())-(wall_rect.x()))-epsilon,0);
					}
					if(minstep<0)
					{
						minstep = Math.min(minstep + ((wall_rect.x()+wall_rect.w())-ent_rect.x())+epsilon,0);
					}
					moveobj.entMoveAxisCheck[0] = false;
				}
			}
			
			tempvec = moveobj.totalMoveVecArray;
			tempvec.set(tempvec.x()-minstep,tempvec.y());				
			moveobj.totalMoveVecArray.set(tempvec);
			
			if(Math.abs(tempvec.x()) < epsilon)
				moveobj.entMoveAxisCheck[0] = false;
			minstepX += minstep;
		}
		check_collision_move_objects(moveobj);
		final_pos.set(ent.pos);
		final_pos.set(final_pos.x()+minstepX,final_pos.y()+minstepY);
		moveobj.pos.set(final_pos);
		ent.pos.set(final_pos);
		return final_pos;
	}
	
	@Override
	protected void disable_resource_object(EntityMovementObject moveObj) {
		moveObj.hitEntity = null;
		super.disable_resource_object(moveObj);
//		moveObj.myEntity = null;
	}
	
	@Override
	protected void reassign_resource_object_id(EntityMovementObject obj, int new_id) {
		
	}
	
//	@Override
//	public void cleanup() {
////		if(DISABLED_COUNT > 0) {
////			System.out.println("checking: "+DISABLED_COUNT+" "+get_active_count());
////		}
//		System.out.println("cleanup before: "+worldResArray.size());
//		super.cleanup();
//		System.out.println("cleanup: "+worldResArray.size());
//	}
}
