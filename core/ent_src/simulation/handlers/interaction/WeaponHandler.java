package simulation.handlers.interaction;

import java.util.ArrayList;
import java.util.Random;

import basic.Rectangle;
import basic.Vector;
import basic.resource.WorldResourceObject;
import handlers.basic.EntityMovementObject;
import handlers.basic.GameRay;
import handlers.basic.WeaponObject;
import handlers.basic.entity.Entity;
import render.EffectHandler;
import render.RenderEntityMainHandler;
import render.basic.RenderEntity;
import render.basic.Sprite;
import simulation.SimulationHandler;
import util.GameInfo;

class WeaponType {
	float power,accuracy;
	long shoot_delay,reload_delay;
	int state,type,onhand_capacity,total_capacity;
	Vector offset;
	String renderers[], rendererParams[];
	WeaponType(){
		power = 10;
		accuracy = 20f;
		state = 0;
		type = 0;
		shoot_delay = 100;
		reload_delay = 1000;
		onhand_capacity = 17;
		total_capacity = 680;
		offset = new Vector(32,-20);
	}
	WeaponType(float power,float accuracy,long shoot_delay,long reload_delay,int total_capacity,Vector offset,String renderers[],String rendererParams[])
	{
		this.power = power;
		this.accuracy = accuracy;
		this.state = 0;
		this.type = 0;
		this.shoot_delay = shoot_delay;
		this.reload_delay = reload_delay;
		this.onhand_capacity = 17;
		this.total_capacity = total_capacity;
		this.offset = offset;
		this.renderers = renderers;
		this.rendererParams = rendererParams;
	}
}

public class WeaponHandler{
	
//	ArrayList <WeaponObject> weapons;
	ArrayList <Integer> weap_colids = new ArrayList<Integer>();
//	RayHandler RayHandle;
	static final int PISTOL = 0;
	static final int ARIFLE = 1;
	int GUN_COUNT = 0;
	WeaponType weapontypes[];
	RenderEntityMainHandler MREHandle;
	EffectHandler EffectHandle;
	EntityCollisionHandler ColHandle;
	SimulationHandler SimHandle;
	
	public WeaponHandler(SimulationHandler SimHandle)
	{
		this.SimHandle = SimHandle;
//		this.RayHandle = SimHandle.RayHandle;
		this.ColHandle = SimHandle.EntColHandle;
		this.MREHandle = SimHandle.RendEntMainHandle;
//		this.ProjectileHandle = SimHandle.ProjectileHandle;
//		weapons = new ArrayList<WeaponObject>();
		weapontypes = new WeaponType[3];
		weapontypes[0] = new WeaponType(10,14f,1000,1000,0,new Vector(32,-20),new String[] {"EffectHandler","EffectHandler","SparkHandler"}, new String[] {"muzzle_flash","bullet_tracer",""});
		weapontypes[1] = new WeaponType(10,14f,500,1000,0,new Vector(32,-20),new String[] {"EffectHandler","EffectHandler","SparkHandler"}, new String[] {"muzzle_flash","bullet_tracer",""});
		weapontypes[2] = new WeaponType(50,3f,200,1000,0,new Vector(32,-20),new String[] {"EffectHandler","EffectHandler","SparkHandler"}, new String[] {"muzzle_flash","bullet_tracer",""});
		EffectHandle = MREHandle.EffectHandle;
		System.out.println("creating weapons handler");
//		weapon_tracer = new Sprite(new Vector(0,0),new Rectangle(0,0,0,0),0,EffectHandle.get_sprite_id("bullet_tracer"));
//		rect_sprite = new Sprite(new Vector(0,0),new Rectangle(0,0,0,0),0,EffectHandle.get_sprite_id("rect2"));
//		weapon_tracer.set_forever(false);
//		weapon_tracer.set_lifespan(500);
	}
	
	public void init() {
	}
	
	public WeaponObject add_weapon(int type,Vector pos,Vector dir,int bullets,Entity owner_ent)
	{
//		int rayid = RayHandle.add_ray();
		WeaponObject weap = new WeaponObject(owner_ent,1000,-1,type);
		GUN_COUNT +=1;
		return weap;
	}
	
	Vector pos = new Vector();
	Vector dir = new Vector();
	GameRay ray = new GameRay();
	Vector hit_pos = new Vector();
	Rectangle temp_rect = new Rectangle();
	public boolean handle_weapon(WeaponObject weap,EntityMovementObject moveObj,Vector shoot_pos,float angle,long lagcomptime) {
		WeaponType weaptype = weapontypes[weap.get_type()];
		
		weap.set_hit_entity(null);
		
		if (weap.get_type() == 0) {
		}
		else {
			dir.SetAngle(angle);
			dir.multiply(300);
		}
		
		weap.set_shoot('t');
		
		if(weap.get_active_delay()<=GameInfo.get_time_millis())
		{
			
			weap.set_shoot('t');
			weap.set_shoot_count(weap.get_shoot_count()+1);
			if(weap.get_shoot() == 't' && weap.get_bullets() > 0 )
			{
				if(weap.get_type() == 1) {
					dir.SetAngle(angle);
					
					weap.set_active_delay( GameInfo.get_time_millis()+weaptype.shoot_delay);
					weap.set_shoot('s');
				}
				else if (weap.get_type() != 0) {
					
					ray.pos.set(shoot_pos);
					ray.dir.set(dir);

					ray.handle_ray(SimHandle.EntColHandle,SimHandle.GWorld);
					weap.set_active_delay( GameInfo.get_time_millis()+weaptype.shoot_delay);
					weap.add_bullets(-1);
					weap.set_shoot('s');


					hit_pos.set(ray.hit_pos);
					hit_pos.substract(shoot_pos);
					temp_rect.set(0,0,hit_pos.length(),0.6f);
					
					Sprite tracer_sprite = EffectHandle.add_resource_object(shoot_pos,temp_rect,"bullet_tracer");
					tracer_sprite.set_angle(Vector.GetAngle(hit_pos));
					tracer_sprite.set_lifespan(80);
					tracer_sprite.set_a(0.1f);
					
					WorldResourceObject hit_entity = ray.hitEntity;

					weap.set_damage(0);
					weap.set_hit_entity(hit_entity);
					if((hit_entity != null && hit_entity != weap.get_owner_entity())) {
						weap.set_damage(weapontypes[weap.get_type()].power);
					}
					temp_rect.set(0,0,8,8);
					RenderEntity sparkEnt = (RenderEntity)MREHandle.get_handler(weaptype.renderers[2]).add_resource_object(ray.hit_pos,temp_rect,null);
					sparkEnt.set_angle(ray.normalAngle);
				} else {
//					float offset_x = 30*1.41f*(float)Math.cos(Math.toRadians(angle-45));
//					float offset_y = 30*1.41f*(float)Math.sin(Math.toRadians(angle-45));
//					temp_rect.set(shoot_pos.x()+offset_x,shoot_pos.y()+offset_y,30,60);
//					weap.set_hit_entity(ColHandle.check_coll_rect_move_obj(temp_rect, weap.get_collision_id() ));
//					weap.set_active_delay( GameInfo.get_time_millis()+weaptype.shoot_delay);
//					weap.set_shoot('s');
//					EffectHandler EH = RendHandle.EffectHandle;
//					temp_rect.set(0,0,30,60);
//					pos.set(shoot_pos.x()+offset_x, shoot_pos.y()+offset_y);
//					Sprite tracer_sprite = EH.add_resource_object(pos,temp_rect,null);
//					tracer_sprite.set_sprite_type(weapon_tracer.get_sprite_type());
//					tracer_sprite.set_lifespan(weapon_tracer.get_lifespan());
//					tracer_sprite.set_forever(false);
//					if(weap.get_hit_entity() != null) {
//						weap.set_damage(weapontypes[weap.get_type()].power);
//					}
				}
			}
			return true;
		}
		return false;
	}
	
//	public void reset()
//	{
//		RayHandle.reset_hitid();
//	}
}