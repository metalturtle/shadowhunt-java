package game.entity;

import basic.*;
import handlers.basic.EntityMovementObject;
import handlers.basic.entity.Entity;
import handlers.basic.entity.SyncedEntity;
import render.basic.Sprite;
import util.GameInfo;

public class Pickup extends SyncedEntity
{
	public String type;
	long delay,start;
//	int collision_id;
	public Sprite sprite;
	public EntityMovementObject moveObj;
	
	public Pickup(Vector pos,Rectangle bound)
	{
		super(pos,bound);
//		this.type = type;
		this.type = "";
//		this.delay = delay;
		start = GameInfo.get_time_millis();
//		this.collision_id = -1;
	}
	
	public Pickup()
	{
		super(new Vector(0,0),new Rectangle(0,0,0,0));
		this.type = "";
		this.delay = 0;
		start = GameInfo.get_time_millis();
//		this.collision_id = -1;
	}
	public long get_start() {return start;}
	public long get_delay() {return delay;}
//	public boolean check_active() {return active;}
//	public int get_type() {return type;}
//	public int get_collision_id() {return this.collision_id;}
//	public int get_sprite_id() {return this.sprite_id;}
	
	public void set_start(long start) {this.start = start;}
	public void set_delay(long delay) {this.delay = delay;}
//	public void set_active(boolean active) {this.active = active;}
//	public void set_type(int type) {this.type = type;}
//	public void set_collision_id(int collision_id) {this.collision_id = collision_id;}
//	public void set_sprite_id(int sprite_id) {this.sprite_id = sprite_id;}
	
//	@Override
//	public	EntityStatePacket create_packet()
//	{
//		PickupStatePacket pickup_packet = new PickupStatePacket();
//		pickup_packet.set(this.get_local_id(), this.pos.getX(),this.pos.getY(),this.get_angle(), this.get_state(),this.get_type());
//		return pickup_packet;
//	}
	
//	@Override
//	public void set(Entity ent)
//	{
//		super.set(ent);
//		Pickup pickup = (Pickup)ent;
//		this.type = pickup.type;
//	}
	
//	@Override
//	public Entity clone()
//	{
//		Pickup pickup = new Pickup();
//		pickup.set(this);
//		return pickup;
//	}
	
	@Override
	public void reset() {
		super.reset();
//		this.type = 0;
	}
	
//	@Override
//	public void mark_disable() {
//		super.mark_disable();
//	}

}