package handlers.basic;

import basic.Rectangle;
import basic.Vector;
import basic.resource.WorldResourceObject;
import handlers.basic.entity.Entity;

public class EntityMovementObject extends WorldResourceObject
{
	public WorldResourceObject hitEntity;
//	Rectangle box;
	public Vector toPoint;
	boolean solid_flag;
	float speed;
	public Vector totalMoveVecArray;
	public boolean[] entMoveAxisCheck;
	byte z_pos, z_height, type;
	
	public EntityMovementObject () {
		super(new Vector(0,0), new Rectangle(0,0,0,0));
		this.toPoint = new Vector(0,0);
		this.solid_flag = false;
		this.speed = 0;
//		this.myEntity = null;
		totalMoveVecArray = new Vector(0,0);
		entMoveAxisCheck = new boolean[2];
		this.z_pos = 0;
		this.z_height = 0;
	}
	
	EntityMovementObject(Entity ent,Rectangle box, byte z_pos, byte z_height, boolean solid_flag)
	{
		super(ent.pos,box);
		this.toPoint = new Vector(0,0);
		this.solid_flag = solid_flag;
		this.speed = 0;
//		this.myEntity = ent;
		totalMoveVecArray = new Vector(0,0);
		entMoveAxisCheck = new boolean[2];
		this.z_pos = z_pos;
		this.z_height = z_height;
	}
	
	public void set (Entity ent,Rectangle box, byte z_pos, byte z_height, boolean solid_flag) {
		this.pos.set(ent.pos);
		this.bound.set(box);
		this.solid_flag = solid_flag;
//		this.myEntity = ent;
		this.z_pos = z_pos;
		this.z_height = z_height;
	}
	
	public void reset() {
		this.hitEntity = null;
		this.totalMoveVecArray.set(0,0);
		entMoveAxisCheck[0] =entMoveAxisCheck[1]=false;
	}
	
	public void set_speed(float speed) {this.speed = speed;}
	public void set_z_pos(byte z_pos) {this.z_pos = z_pos;}
	public void set_z_height(byte z_height) {this.z_height = z_height;}
	public void set_solid_flag(boolean solid_flag) {this.solid_flag = solid_flag;}
	public void set_type(byte type) {this.type = type;}
	
	public float get_speed() {return this.speed;}
	public byte get_z_pos() {return this.z_pos;}
	public byte get_z_height() {return z_height;}
	public boolean get_solid_flag() {return this.solid_flag;}
	public byte get_type() {return this.type;}
	
//	@Override
//	public void mark_disable() {
//		super.mark_disable();
//	}
	
}