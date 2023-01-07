//package handlers.basic.entity;
//
//import basic.Rectangle;
//import basic.Vector;
//import handlers.basic.EntityMovementObject;
//import handlers.basic.WeaponObject;
//
//public class Projectile extends Entity {
//	float distance_covered;
//	public WeaponObject weapon;
//	short type;
////	int collision_id;
//	int sprite_id;
//	int owner_collision_id;
//	public EntityMovementObject moveObj;
//	
//	public Projectile() {
//		super();
//		this.distance_covered = 0;
//		this.disable();
//	}
//	
//	public Projectile(Vector p,Rectangle bound) {
//		super(p,bound);
//		this.distance_covered = 0;
//		this.disable();
//	}
//	
//	public void init(Vector p, Rectangle bound, short type, Vector dir, float speed) {
//		this.pos.set(p);
//		this.bound.set(bound);
//		this.type = type;
//		this.vdir.set(dir);
//		this.speed = speed;
//		this.enable();
//	}
//	
////	public void set_collision_id(int collision_id) {this.collision_id = collision_id;}
////	public int get_collision_id() {return this.collision_id;}
//	public void set_sprite_id(int sprite_id) {this.sprite_id = sprite_id;}
//	public int get_sprite_id() {return this.sprite_id;}
//	public void set_owner_collision_id(int owner_collision_id) {this.owner_collision_id = owner_collision_id; }
//	public int get_owner_collision_id () {return this.owner_collision_id;}
//	
////	@Override
////	public void set(Entity obj) {
////		Projectile proj = (Projectile)obj;
////		this.type = proj.type;
//////		this.collision_id = proj.collision_id;
////		this.sprite_id = proj.sprite_id;
////		this.owner_collision_id = proj.owner_collision_id;
////	}
//}
