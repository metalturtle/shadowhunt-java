package handlers.basic;

import basic.Vector;
import basic.resource.WorldResourceObject;
import handlers.basic.entity.Entity;

public class WeaponObject{
//	int ownerid;
	char shoot;
	int bullets;
	int rayid;
	int type;
	long active_delay;
	float damage;
	int colid;
//	float angle;
	WorldResourceObject hit_entity, owner_entity;
//	public Vector shoot_pos, owner_pos;
//	int collision_id;
	boolean enabled;
	int shoot_count;
	String renderers[],rendererParams[];
	
	public WeaponObject()
	{
		enabled = true;
	}
	public WeaponObject(Entity owner_entity,int bullets,int rayid,int type)
	{
		set(owner_entity, bullets, rayid, type);
		this.renderers = renderers;
		this.rendererParams = rendererParams;
	}
	
	public void set(Entity owner_entity,int bullets,int rayid,int type) {
		this.owner_entity = owner_entity;
		this.shoot = 'f';
		this.bullets = bullets;
		this.rayid = rayid;
		this.type = type;
		damage = 0;
		active_delay = 0;
//		colid = -1;
//		angle = 0;
//		shoot_pos = new Vector(0,0);
//		owner_pos = new Vector(0,0);
//		this.collision_id = collision_id;
		enabled = true;
	}

	public WorldResourceObject get_owner_entity() {return owner_entity;}
	public void set_shoot_count(int c) {this.shoot_count=c;}
	public int get_shoot_count() {return this.shoot_count;}
	public char get_shoot() {return shoot;}
	public void set_shoot(char shoot) {this.shoot = shoot;}

	public int get_rayid() {return rayid;}
	public int get_type() {return type;}
	public long get_active_delay() {return active_delay;}
	public void set_active_delay(long delay) {this.active_delay = delay;}
	public int get_collision_id() {return colid;}
//	public float get_angle() {return angle;}
//	public void set_angle(float angle) {this.angle = angle;}
	public void set_bullets(int bullets) {this.bullets = bullets;}
	public int get_bullets() {return bullets;}
	public void add_bullets(int bullets) {this.bullets += bullets;}
	public void set_damage(float damage) {this.damage = damage;}
	public float get_damage() {return this.damage;}
	public void set_hit_entity(WorldResourceObject hit_entity2) {this.hit_entity = hit_entity2;}
	public WorldResourceObject get_hit_entity() {return this.hit_entity;}
	public void enable() {this.enabled = true;}
	public void disable() {this.enabled = false;}
	public boolean check_enable() {return this.enabled;}
}