package game.synced;

import basic.InputPacketProcessor;
import basic.InputState;
import basic.OutputPacketProcessor;
import basic.Rectangle;
import basic.Vector;
import basic.resource.WorldResourceObject;
import game.entity.Pickup;
import game.entity.ShadowLivingEntity;
import game.render.HidingSpriteHandler;
import handlers.EntityStateField;
import handlers.GameEventHandler;
import handlers.basic.ClientCommand;
import handlers.basic.Command;
import handlers.basic.EntityMovementObject;
import handlers.basic.PacketFieldInfo;
import handlers.basic.TeamObject;
import handlers.basic.Timer;
import handlers.basic.WeaponObject;
import handlers.basic.PacketFieldInfo.Interpolation;
import handlers.basic.entity.Entity;
import handlers.basic.entity.SyncedEntity;
import handlers.basic.packets.EntityState;
import network.NetworkInfo;
import render.LightHandler;
import render.RenderEntityMainHandler;
import render.RenderInfo;
import render.SpriteHandler;
//import render.basic.GUIElement;
import render.basic.Light;
import render.basic.Sprite;
import resource.TextureIDManager;
import simulation.SimulationCoordinator;
import simulation.SyncedEntityHandler;
import simulation.SyncedGameEntityHandler;
import simulation.handlers.interaction.EntityCollisionHandler;
import simulation.handlers.interaction.WeaponHandler;
import util.GameInfo;


public class ShadowActorHandler extends SyncedGameEntityHandler<ActorStatePacket,ShadowLivingEntity> {
	int MAX_ACTORS;
	public EntityCollisionHandler ColHandler;
	public WeaponHandler WeapHandler;
	public RenderEntityMainHandler RendEntMainHandle;
//	public GameEventHandler GEventHandler;
	SpriteHandler SH;
	HidingSpriteHandler HASH;
	LightHandler LH;
	TextureIDManager TexIDManage;
	
	Vector player_start_pos;
	
	static class GLOBAL_PLAYER_STATE {static byte state; static boolean hard = false;static boolean updated = false; static boolean active = false; static byte health = 100;}
	static class ENT_ACTION {static int UP=-1,DOWN=-1,LEFT=-1,RIGHT=-1,GUN_CHANGE_UP=-1,GUN_CHANGE_DOWN=-1,GUN_SHOOT=-1;};
	
//	GUIElement playerHealthGUI, durationGUI;
	public TeamObject teamObjectArray[];
	
	public static class EntState {
		public static final byte DEAD=0,PLAYABLE=1,PLAYABLE_LOBBY=2,FROZEN_ROTATE=3,FROZEN=4,RED_PELLET=5;
	}
	
	final byte SEEKER_TEAM_ID, HIDER_TEAM_ID;
	ShadowLivingEntity main_entity;
	boolean MAIN_ENT_CHANGE;
//	GUIElement killedGUI;
	int GUN_LIMIT=1;
	int GLOBAL_STATE_VAR;
	final float hider_speed=12f,seeker_speed=18f;
	
	public ShadowActorHandler( SimulationCoordinator SimCoord) {
		super(SimCoord, true,(byte)2,(byte)2,(byte)1,(byte)1);
		SEEKER_TEAM_ID = 0;
		HIDER_TEAM_ID = 1;
		System.out.println("acthandle: "+this.WRH_ID);
		
		this.RendEntMainHandle = SimCoord.SimHandle.RendEntMainHandle;
		this.ColHandler = SimCoord.SimHandle.EntColHandle;
		this.WeapHandler = new WeaponHandler(SimCoord.SimHandle);
		this.TexIDManage = RendEntMainHandle.TexIDManage;
		this.MAX_ACTORS = 16;
		player_start_pos = new Vector(500,300);
		GLOBAL_PLAYER_STATE.state = EntState.PLAYABLE;
		TeamObject teams[] = new TeamObject[2];
		teams[0] = new TeamObject( 0, "SEEK", new Rectangle(12,12,0,0));
		teams[1] = new TeamObject( 0, "HIDE", new Rectangle(12, 12,0,0));
		this.SH = SimCoord.SimHandle.RendEntMainHandle.SpriteHandle;

//		try {
//			Registry.setup_renderer("HidingActorSpriteHandler");
//			Registry.setup_renderer("SparkRenderer");
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		this.HASH = (HidingSpriteHandler)SimCoord.SimHandle.RendEntMainHandle.get_handler("HidingSpriteHandler");
		this.LH = SimCoord.SimHandle.RendEntMainHandle.LightHandle;
		add_teams(teams);
		ENT_ACTION.UP = InputState.get_cmd_id_by_name("UP");
		ENT_ACTION.DOWN = InputState.get_cmd_id_by_name("DOWN");
		ENT_ACTION.LEFT = InputState.get_cmd_id_by_name("LEFT");
		ENT_ACTION.RIGHT = InputState.get_cmd_id_by_name("RIGHT");
		ENT_ACTION.GUN_CHANGE_UP = InputState.get_cmd_id_by_name("CHANGE_UP");
		ENT_ACTION.GUN_CHANGE_DOWN = InputState.get_cmd_id_by_name("CHANGE_DOWN");
		ENT_ACTION.GUN_SHOOT = InputState.get_cmd_id_by_name("SHOOT");
	}
	
	void add_teams(TeamObject team_obj_array[]) {
		this.teamObjectArray = team_obj_array;
	}
	
	public ShadowLivingEntity get_main_entity() {
		return main_entity;
	}
	
	public void set_main_entity(ShadowLivingEntity ent) {
		MAIN_ENT_CHANGE = true;
		main_entity = ent;
	}
	
	@Override
	protected String get_params_from_packet(ActorStatePacket act_packet) {
		System.out.println("sahandle creating a new entity: "+act_packet.team_id);
		return (byte)act_packet.team_id+"";
	}
	
	@Override
	public ShadowLivingEntity add_resource_object(Vector pos, Rectangle bound, String params) {
		ShadowLivingEntity act = super.add_resource_object(pos,bound,params);
		byte team_id = Byte.parseByte(params);
		act.pos.set(180,343);
		act.bound.set(new Rectangle(-3,-3,6,6));
		act.set_health((byte)100);

		act.currentState = new ActorStatePacket();
		
		EntityMovementObject moveObj = act.get_move_object();
		moveObj.bound.set(-4,-4,8,8);
		moveObj.set_z_height((byte)10);
		act.set_team_id(team_id);
		teamObjectArray[team_id].add_team_player();
		act.weapons = new WeaponObject[1];
		act.get_move_object().pos.set(act.pos);
		float color = 0.1f;
		
		if(team_id == 0) {
			System.out.println("Added seeker");
			act.set_speed(28f);
//			act.set_speed(0.7f);
			Sprite leg_sprite = set_sprite(act,0,SH,act.pos,new Rectangle(-4,-4,8,8),"actor_legs");
			Sprite body_sprite = set_sprite(act,1,SH,act.pos,new Rectangle(-4,-4,8,8),"actor_torso_walk_machgun");
			
//			leg_sprite.set_color(color, color, color, color);
//			body_sprite.set_color(color, color, color, color);
			
			give_light_to_actor(act,5000*1000);
			WeaponObject weap = this.WeapHandler.add_weapon(2, new Vector(0,0),new Vector(0,0), 1000,act);
			act.weapons[0] = weap;
			act.currentWeapon = act.weapons[0];
		} else {
			act.set_speed(24f); 
//			act.set_speed(1f);
			Sprite leg_sprite = set_sprite(act,0,HASH,act.pos,new Rectangle(-4,-4,8,8),"actor_legs");
			Sprite body_sprite = set_sprite(act,1,HASH,act.pos,new Rectangle(-4,-4,8,8),"actor_stealth_standing");
		}
		
		act.set_state(GLOBAL_PLAYER_STATE.state);
		System.out.println("entity created");
		duration=GameInfo.get_time_millis()+3000;
		return act;
	}
	
	@Override
	public void disable_resource_object(ShadowLivingEntity ent) {
		teamObjectArray[ent.get_team_id()].remove_team_player();
		if(get_main_entity() == ent)
			set_main_entity(null);
		System.out.println("entity disconnected");
		super.disable_resource_object(ent);
	}
	
	boolean shoot_gun(ShadowLivingEntity act,long lagcomptime) {
		return WeapHandler.handle_weapon(act.currentWeapon,act.get_move_object(),act.pos, act.get_angle(),lagcomptime);
	}
	
	void handle_weapon(ShadowLivingEntity act)
	{
		ShadowLivingEntity hit;
		WeaponObject weap = act.currentWeapon;
		WorldResourceObject wres = weap.get_hit_entity();
		
		if(wres == null) {
			return;
		}
		if(!(wres instanceof ShadowLivingEntity)) {
			return;
		}
		hit = (ShadowLivingEntity)wres;
		if(act.get_team_id() == hit.get_team_id())
			return;
		float damage = weap.get_damage();
		hit.set_health((byte) (hit.get_health()-damage));
		weap.set_hit_entity(null);
	}
	
	void set_global_actor_state(byte state, boolean hard) {
		GLOBAL_PLAYER_STATE.state = state;
		GLOBAL_PLAYER_STATE.hard = hard;
		GLOBAL_PLAYER_STATE.active = true;
	}
	
	Vector toPoint = new Vector();
	Vector move_player(ShadowLivingEntity ent,Command command) {
		ent.set_angle(command.get_mouse_angle());
		boolean keys[] = command.get_keys();
		boolean up = keys[ENT_ACTION.UP],
				down= keys[ENT_ACTION.DOWN],
				left=keys[ENT_ACTION.LEFT],
				right=keys[ENT_ACTION.RIGHT];

		if(up==false && down == false && left == false && right == false)
		{
			return null;
		}
		if((up == true&& down==true)||(left == true&& right==true) )
		{
			up=down=left=right = false;
			return null;
		}
		float side=0,vert=0;
		if(right == true) {
			side+=50;
		}
		if(left == true) {
			side -=50;
		}
		if(down == true) {
			vert-=50;
		}
		if(up == true) {
			vert +=50;
		}
		toPoint.set(ent.pos.x()+side,ent.pos.y()+vert);
		
		toPoint.substract(ent.pos);
		toPoint.unitVector();
		ent.vdir.set(toPoint);
		return toPoint;
	}
	
//	boolean moved;
	
	Vector rendVec = new Vector();
	void render_actor(ShadowLivingEntity act) {
		
		rendVec.set(act.pos);
		rendVec.substract(act.get_sprite(0).pos);
		
		if(act.get_state() == EntState.DEAD) {
			if(act.get_team_id() == 1) {
				act.get_sprite(0).set_animation(0);
				act.get_sprite(0).set_visible(false);
				act.get_sprite(1).set_sprite_type(RendEntMainHandle.TexIDManage.get_sprite_id("stealth_dead"));
				act.get_sprite(1).bound.set(-3,-3,14,7);
				
			} else {
				act.get_sprite(0).set_animation(0);
				act.get_sprite(1).set_sprite_type(RendEntMainHandle.TexIDManage.get_sprite_id("rect2"));
			}
		} else {
			float angle=Vector.GetAngle(act.vdir);
			boolean flag=false;
			
			if(Math.abs(rendVec.x()) > Vector.EPSILON)	{
				flag = true;
			}
			if(Math.abs(rendVec.y()) > Vector.EPSILON ) {
				flag = true;
			}
			
//			if(flag && act.get_local_id() == 0) {
//				System.out.println("rend: "+rendVec.length() );
//			}
			
			if(!flag) {
				act.get_sprite(0).set_animation(0);
			} else {
				act.get_sprite(0).run_animation(GameInfo.get_delta_time());
			}
			
			act.get_sprite(0).set_angle(angle);
			act.get_sprite(0).pos.set(act.pos);
			act.get_sprite(1).pos.set(act.pos);
			act.get_sprite(1).set_angle(act.get_angle());
		}
}
	
	@Override 
	protected void client_handle_entity(ShadowLivingEntity act)
	{
		
		if(get_main_entity() != act) {
			interpolate_client_entity(act);
		} else {
			run_entity_commands(act);	
		}
		
//		act.get_sprite(0).pos.set(act.pos);
//		act.get_sprite(1).pos.set(act.pos);
		render_actor(act);
		
		
		if (act.get_inventory_object(0) != null) {
			Light light = (Light)act.get_inventory_object(0);
			set_light_pos(act,light);
		}

		float ang_rounded = Math.round(act.get_angle()*100)/100f;
		act.set_angle(ang_rounded);
		act.vdir.set(0,0);
	}
	
	@Override
	protected void server_handle_entity(ShadowLivingEntity act) {
		if (GLOBAL_PLAYER_STATE.active == true ) {
			act.set_state(GLOBAL_PLAYER_STATE.state);
			act.set_health(GLOBAL_PLAYER_STATE.health);
		}
		else {
			if(act.get_health()<=0) {
				act.set_state(EntState.DEAD);
			}
		}
		
		run_entity_commands(act);

		act.get_move_object().pos.set(act.pos);
		
		if(act.get_state() != EntState.DEAD) {
			if (act.get_inventory_object(0) != null) {
				Light light = (Light)act.get_inventory_object(0);
				if(act.get_timer(0).check()) {				
					this.disable_inventory_object(act,0);
					ActorStatePacket pac = (ActorStatePacket)act.currentState;
					pac.has_light+=1;
				} else {
					set_light_pos(act,light);
				}
			}
			
			act.vdir.set(0,0);
			act.get_move_object().reset();
		}
	}
	
	Rectangle testrect;
	long duration;
	boolean test;
	Vector movement_vec = new Vector();
	long dur_check;
	@Override
	protected void handle_entity_command(ShadowLivingEntity act, Command ent_command) {
		Vector toPoint,distvec = new Vector(act.pos);
		boolean move=false;
		
		if(act.get_team_id() == 0) {
			HASH.set_mode(false);
		} else {
			HASH.set_mode(true);
		}
		switch(act.get_state())
		{
			case EntState.DEAD:
				if(act.is_first_state()) {
					teamObjectArray[act.get_team_id()].remove_team_player();
					act.read_first_state();
				}
				
				break;
			case EntState.PLAYABLE:
			case EntState.PLAYABLE_LOBBY:
			case EntState.RED_PELLET:
				toPoint = move_player(act,ent_command);
				if(toPoint != null){
					this.ColHandler.move_collision(act.get_move_object(), act, ent_command.get_delta_time());
					move = true;
					distvec.substract(act.pos);
				}
				
				boolean keys[] = ent_command.get_keys();
				
				if(act.currentWeapon != null) {
					if(keys[ENT_ACTION.GUN_SHOOT]){
						if(!ent_command.is_read_once()) {
							boolean gun_shot = shoot_gun(act,GameInfo.get_time_millis());
						}
					}
					if(NetworkInfo.is_server() == true)
						handle_weapon(act);
				}

				RendEntMainHandle.GCamera.set_follow_entity(act);
			break;
			case EntState.FROZEN_ROTATE:
				toPoint = move_player(act,ent_command);
			break;
		}
		
//		if(SimCoord.is_server() && move) {
//			System.out.println("movevec: "+distvec.length());
//		}
	}
	
	@Override
	public void run_server() {
//		WeapHandler.reset();
		super.run_server();
		if(GLOBAL_PLAYER_STATE.active == true ) {
			GLOBAL_PLAYER_STATE.active = false;
		}
	}

	@Override
	public void run_client() {
		super.run_client();
	}

	@Override
	public ShadowLivingEntity create_new_element() {
		return new ShadowLivingEntity();
	}

	@Override
	public Class[] init_packets() {
		Class [] cl = new Class[2];
		cl[0] = ActorStatePacket.class;
		cl[1] = ActorStatePacket[].class;
		return cl;
	}
	
	@Override
	public PacketFieldInfo[] get_packet_field_info() {
		return new PacketFieldInfo[] {
				new PacketFieldInfo("pos",'v',PacketFieldInfo.BASELINE,Interpolation.INTERPOLATE),
				new PacketFieldInfo("angle",'f',PacketFieldInfo.ON_CHANGE,Interpolation.ANGLE),
				new PacketFieldInfo("state",'b',PacketFieldInfo.ON_CHANGE,Interpolation.PREVIOUS),
				new PacketFieldInfo("health",'b',PacketFieldInfo.ON_CHANGE,Interpolation.PREVIOUS),
				new PacketFieldInfo("team_id",'b',PacketFieldInfo.ON_CHANGE,Interpolation.PREVIOUS),
				new PacketFieldInfo("shoot_count",'b',PacketFieldInfo.NOT_EMPTY,Interpolation.PREVIOUS),
				new PacketFieldInfo("has_light",'b',PacketFieldInfo.ON_CHANGE_ONE_READ,Interpolation.PREVIOUS),
				new PacketFieldInfo("duration",'l',PacketFieldInfo.NOTHING,Interpolation.PREVIOUS),
		};
	}
	
	@Override
	public void write_entity_state(ActorStatePacket act_packet,ShadowLivingEntity ent) {
		act_packet.pos.set(ent.pos);
		act_packet.angle = ent.get_angle();
		act_packet.state = ent.get_state();
		act_packet.team_id = ent.get_team_id();
//		System.out.println("writing team id:"+act_packet.team_id);
		act_packet.has_light = (byte)(ent.get_inventory_object(0) == null ? 0:1);
		
		if(ent.currentWeapon != null && is_network_sending()) {
			act_packet.shoot_count = (byte)ent.currentWeapon.get_shoot_count();
			ent.currentWeapon.set_shoot_count(0);
		}
	}

	
	@Override
	public void read_entity_state(ActorStatePacket act_packet, ShadowLivingEntity ent) {
		ent.pos.set(act_packet.pos);
//		System.out.println("act packet: "+act_packet.pos);
		ent.set_angle(act_packet.angle);
		ent.set_state(act_packet.state);
		ent.set_team_id(act_packet.team_id);
		if(ent != this.get_main_entity()) {
			ent.get_move_object().pos.set(ent.pos);
		}
		
		if(act_packet.required_fields[5]) {
			if(ent != this.get_main_entity()) {
				if(act_packet.shoot_count>0) {
					shoot_gun(ent,0);
				}
			}
		}
		
		if(act_packet.required_fields[6]) {
			act_packet.required_fields[7]=false;
			act_packet.has_light = act_packet.has_light;
			if(act_packet.has_light==1 && ent.get_inventory_object(0) == null) {
				give_light_to_actor(ent,1000*10);
			}
			if(act_packet.has_light == 0 && ent.get_inventory_object(0) != null) {
//				this.disable_child(ent, 0);
				disable_inventory_object(ent,0);
			}
		}
	}
	
	public void modify_state_send_fields(ActorStatePacket act_packet) {
	}
	
	void set_light_pos(ShadowLivingEntity act,Light light) {
		light.pos.set(act.pos.x()-light.bound.w()/2,act.pos.y()-light.bound.w()/2);
	}
	
	public boolean give_light_to_actor(ShadowLivingEntity act,long duration) {
		if(act.get_team_id() == 0) {
			if(act.get_inventory_object(0) != null) {
//				act.set_duration(act.get_duration()+duration);
				act.get_timer(0).extend(duration);
			} else {
//				act.set_worldObj(LH.add_resource_object(act.pos,new Rectangle(0,0,64,64),null),0);
				this.set_inventory_object(act, 0, LH, new Vector(act.pos), new Rectangle(0,0,64,64), null);
				act.get_timer(0).set(duration);
				System.out.println("adding light");
			}
			ActorStatePacket pac = (ActorStatePacket)act.currentState;
			pac.has_light+=1;
//			act.stateUpdates.add_game_update(act.get_timer(0).get_duration());
			return true;
		}
		return false;
	}
	
	
	@Override
	public void set_packet_field(ActorStatePacket act_packet,InputPacketProcessor input, int id) {
		if(id==0) input.read_vector(act_packet.pos);
		if(id==1) act_packet.angle = input.read_float();
		if(id==2) act_packet.state = input.read_byte();
		if(id==3) act_packet.health = input.read_byte();
		if(id==4) act_packet.team_id = input.read_byte();
		if(id==5) act_packet.shoot_count = input.read_byte();
		if(id==6) act_packet.has_light = input.read_byte();
		if(id==7) act_packet.duration = input.read_long();
	}
	
	@Override
	public void get_packet_field(ActorStatePacket act_packet,OutputPacketProcessor output, int id) {		
		if(id==0)output.write_vector(act_packet.pos);
		if(id==1)output.write_float(act_packet.angle);
		if(id==2)output.write_byte(act_packet.state);
		if(id==3)output.write_byte(act_packet.health);
		if(id==4)output.write_byte(act_packet.team_id);
		if(id==5) output.write_byte(act_packet.shoot_count);
		if(id==6)output.write_byte(act_packet.has_light);
		if(id==7)output.write_long(act_packet.duration);
	}

	@Override
	public ActorStatePacket create_entity_state_raw() {
		return new ActorStatePacket();
	}
}
