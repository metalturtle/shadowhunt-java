package handlers.basic.entity;

import basic.Rectangle;
import basic.Vector;
import handlers.basic.WeaponObject;

public abstract class SyncedLivingEntity extends SyncedGameEntity {

	float health,armour,damage;
	public WeaponObject[] weapons;
	public WeaponObject currentWeapon;
	
	public SyncedLivingEntity( ) {
		super();
	}
	
	public SyncedLivingEntity(Vector pos, Rectangle bound) {
		super(pos,bound);
	}
	
	public void set_health(float health){
		this.health = health;
	}
	
	public float get_health(){
		return health;
	}
	
	public void add_damage(float dam) {
		this.damage+=dam;
	}
	
	public void reset_damage() {
		this.damage = 0;
	}
	
	public float get_damage() {
		return this.damage;
	}
	
	public void set_weapon(WeaponObject weap, int i) {
		weapons[i] = weap;
	}
	
	public WeaponObject get_weapon(int i) {
		return weapons[i];
	}
	
	public void reset() {
		super.reset();
//		for(int i = 0; i < weapons.length; i++) {
//			weapons[i] = null;
//		}
		this.damage = 0;
	}
}
