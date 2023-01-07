package render;

import basic.InputState;
import basic.Rectangle;
import basic.Vector;
import handlers.basic.entity.Entity;

public class GameCamera extends Rectangle
{
//	GameActorHandler actorhandler;
//	float offsetx,offsety;
	Entity follow_entity;
	byte type;
	float speed, acceleration, lastDistance,maxSpeed;
	Vector jerkVector;
	boolean jerk;
	float ZOOM;
	GameCamera(float x,float y, float w,float h)
	{
		super(x,y, w, h);
		this.maxSpeed = 5;
		this.acceleration = 0.5f;
		jerkVector = new Vector(0,0);
		this.ZOOM = 1;
	}
	
	public GameCamera(GameCamera cam) {
		this.set(cam);
		this.maxSpeed = 5;
		this.acceleration = 0.5f;
		jerkVector = new Vector(0,0);
		this.ZOOM = 1;
	}
	
	public float get_screen_val(float world_val) {
		return RenderInfo.screen_to_world(world_val/ZOOM);
	}
	
	public void set_zoom (float zoom) {
		this.ZOOM = zoom;
		this.w = ZOOM*w;
		this.h = ZOOM*h;
	}
	
	public float get_zoom() {
		return this.ZOOM;
	}
	
	public void set_max_speed(float speed) {
		this.maxSpeed = speed;
	}
	
	public float get_max_speed() {
		return this.maxSpeed;
	}
	
	public void set_acceleration(float acceleration) {
		this.acceleration = acceleration;
	}
	
	public float get_acceleration() {
		return this.acceleration;
	}
	
	public void set_type (byte type) {
		this.type = type;
	}
	
	public void set_follow_entity(Entity ent)
	{
		this.follow_entity = ent;
	}
	
	public void set_jerk(Vector jerkVec) {
		this.jerk = true;
		jerkVector.set(jerkVec);
	}
	
	public void drag_behind_entity() {
		Vector pos = new Vector(this.x,this.y);
		Vector mouse_dist = new Vector(InputState.mouse);
		mouse_dist.unitVector();
		mouse_dist.multiply(7);
//		System.out.println("mouse: "+mouse_dist.getX()+" "+mouse_dist.getY());
		Vector dir = new Vector(follow_entity.pos.x() + mouse_dist.x(), follow_entity.pos.y() + mouse_dist.y());
		if (jerk == true) {
			dir.add(jerkVector);
			jerk = false;
		}
		dir.substract(pos);
		float distance = dir.length(), currentSpeed=0;
		if (distance < 1f) {
			this.speed = 0;
			return;
		}
		else if(distance < 10) {
			currentSpeed = distance/10f;
		}
		else if(distance < 100)
			{
			currentSpeed = follow_entity.get_speed();
		}
		else {
			this.x = follow_entity.pos.x();
			this.y = follow_entity.pos.y();
			return;
		}
		dir.multiply(currentSpeed/distance);
		pos.add(dir);
		this.x = pos.x();
		this.y = pos.x();
	}
	
	public void set_to_entity() {
		this.x = follow_entity.pos.x()-w/2;
		this.y = follow_entity.pos.y()-h/2;
	}
	
	public void update() {
		if(type == 0) {
			if(follow_entity != null) {
				set_to_entity();
//				drag_behind_entity();
			}
		}
	}
	
}
