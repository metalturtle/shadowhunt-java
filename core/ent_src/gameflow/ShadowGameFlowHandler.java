package gameflow;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;

import org.json.simple.JSONArray;

import Coordinator.Coordinator;
import basic.Rectangle;
import basic.Vector;
import basic.resource.WorldResourceObject;
import client.ClientImage;
import client.ClientImageHandler;
import game.entity.ShadowLivingEntity;
//import game.synced.PickupHandler;
import game.synced.ShadowActorHandler;
import handlers.basic.ClientCommand;
import handlers.basic.Timer;
import handlers.basic.entity.SyncedEntity;
import simulation.ClientGameFlowHandler;
import simulation.ClientSimulationState;
import simulation.GameFlowPacket;
import simulation.SimulationCoordinator;
import simulation.SimulationFlowHandler;
import simulation.SimulationHandler;
import simulation.SimulationCoordinator;
import simulation.handlers.EntityHandler;
import util.GameInfo;

public class ShadowGameFlowHandler extends SimulationFlowHandler {

	class PickupLocation {
		int type;
		 Vector pos;
		 PickupLocation(int type,Vector pos) {
			 this.type = type;
			 this.pos = pos;
		 }
	}
	boolean NEW_STATE = false, LOBBY_COUNTDOWN = false, FREEZE_ACTOR_FRAME = false;
	
	Vector seekerpos=new Vector(),hiderpos=new Vector(),centerpos = new Vector();
	
	byte GAMEFLOW_STATE;
	static class STATE {
		static final byte WAIT_FOR_PLAYERS,READY_TO_START,COUNTDOWN,PLAY,HIDER_WIN,SEEKER_WIN,DRAW;
		static {
			byte val=0;
			WAIT_FOR_PLAYERS=val++;
			READY_TO_START=val++;
			COUNTDOWN=val++;
			PLAY=val++;
			HIDER_WIN=val++;
			SEEKER_WIN=val++;
			DRAW=val++;
		}
	}
	
	
	GameFlowPacket gfpacket;
	ShadowActorHandler ShadowActorHandle;
	ArrayList <PickupLocation> pickupLocations;
	long timePickupGenerate;
	Random rand = new Random();
	Timer timer = new Timer();
	
	final int MINUTE = 1000*60;
	
	public ShadowGameFlowHandler(SimulationCoordinator SimCoord, ClientImageHandler CIHandle ) {
		super(SimCoord,CIHandle);
		gfpacket = new GameFlowPacket();
		pickupLocations = new ArrayList <PickupLocation>();
		ShadowActorHandle = (ShadowActorHandler)SimHandle.get_entity_handler("ShadowActorHandler");
	}
	
	@Override
	public void create_misc_object(EntityHandler EntHandle, Vector pos, Rectangle bound, String params) {		
	}
	
	@Override
	public void level_load_entities(JSONArray jsonArr) {
		for(int i = 0; i < jsonArr.size(); i++) {
			JSONArray miscObjArray = (JSONArray)jsonArr.get(i);
			float x = (float)(double)((Double)(miscObjArray.get(0)));
			float y = (float)(double)(Double)miscObjArray.get(1);
			String name = (String)miscObjArray.get(4);
			String params = (String)miscObjArray.get(5);
			if(name.equals("Spawn")) {
				if(params.equals("team1")) {
					seekerpos.set(x,y);
				}
				if(params.equals("team2")) {
					hiderpos.set(x,y);
				}
				if(params.equals("center")) {
					centerpos.set(x,y);
				}
			}
		}
	}
	
	@Override
	public SyncedEntity add_new_client(ClientImage clientImage) {
		SimCoord.add_gameflow_packet(clientImage, create_banner_packet("WELCOME TO SERVER"));
		return add_actor(centerpos,clientImage,"1");
		
	}
	
	public SyncedEntity add_actor(Vector vec,ClientImage clientImage,String param) {
		ShadowLivingEntity ent= ShadowActorHandle.add_resource_object(null,null,param);
		ent.pos.set(vec);
		SimCoord.attach_entity_to_client(clientImage,ent);
		SimCoord.add_gameflow_packet(clientImage,create_timer_packet(timer));
		return ent;
	}
	
	@Override
	public void disconnect_client(int con_id,SyncedEntity ent) {
		if(ent != null) {
			ShadowActorHandle.disable_resource_object((ShadowLivingEntity)ent);
		}
	}
	
	void init_game() {
		System.out.println("init game");
		int k = 0;
		for (Iterator <ClientImage> iterator = CIHandle.get_iterator(); iterator.hasNext();) {
			ClientImage clientImage = iterator.next();

			if(clientImage.CSimState.get_client_entity() != null) {
				SimCoord.disconnect_client(clientImage);
			}
			k++;
		}
//		ShadowActorHandle.disable_all_objects();
		int seeker = rand.nextInt(k);
		System.out.println("seeker: "+seeker);
		ClientImage seekercimg = CIHandle.get_client_by_localid(seeker);
		this.add_actor(seekerpos,seekercimg,"0");
		
		for (Iterator <ClientImage> iterator = CIHandle.get_iterator(); iterator.hasNext();) {
			ClientImage clientImage = iterator.next();
			if(clientImage.is_disconnected()) {
				continue;
			}
			if(!clientImage.CSData.state_equals(Coordinator.SystemState.RUN)) {
				continue;
			}
			if(!(clientImage.CSimState.state_equals(SimulationCoordinator.State.SYNCUP)
					|| clientImage.CSimState.state_equals(SimulationCoordinator.State.RUN))) {
				continue;
			}
			if(clientImage.get_local_id() != seeker)
			{
				this.add_actor(hiderpos,clientImage,"1");
			}
		}
		WorldResourceObject.starttest=true;
	}
	
	void reset_game() {
		for (Iterator <ClientImage> iterator = CIHandle.get_iterator(); iterator.hasNext();) {
			ClientImage clientImage = iterator.next();

			if(clientImage.CSimState.get_client_entity() != null) {
				SimCoord.disconnect_client(clientImage);
			}
		}
		
		for (Iterator <ClientImage> iterator = CIHandle.get_iterator(); iterator.hasNext();) {
			ClientImage clientImage = iterator.next();
			if(clientImage.is_disconnected()) {
				continue;
			}
			if(!clientImage.CSData.state_equals(Coordinator.SystemState.RUN)) {
				continue;
			}
			if(!(clientImage.CSimState.state_equals(SimulationCoordinator.State.SYNCUP)
					|| clientImage.CSimState.state_equals(SimulationCoordinator.State.RUN))) {
				continue;
			}
			this.add_actor(centerpos,clientImage,"1");
		}
	}
	
	boolean check_hider_exists() {
		for(Iterator <ShadowLivingEntity> it = ShadowActorHandle.get_iterator(null); it.hasNext();) {
			ShadowLivingEntity ent = it.next();
			if(ent.get_team_id() == 1 && ent.get_state() != ShadowActorHandler.EntState.DEAD) {
				
				return true;
			}
		}
		return false;
	}
	
	boolean check_seeker_exists() {
		for(Iterator <ShadowLivingEntity> it = ShadowActorHandle.get_iterator(null); it.hasNext();) {
			ShadowLivingEntity ent = it.next();
			if(ent.get_team_id() == 0) {
				return true;
			}
		}
		return false;
	}
	
	
	
	GameFlowPacket create_timer_packet(Timer timer) {
		GameFlowPacket gtimer = new GameFlowPacket();
		gtimer.type = ClientGameFlowHandler.TYPE.TIMER;
		gtimer.message = "t,"+timer.get_remaining_duration();
		return gtimer;
	}
	
	GameFlowPacket create_banner_packet(String message) {
		GameFlowPacket gmessage = new GameFlowPacket();
		gmessage.type = ClientGameFlowHandler.TYPE.BANNER;
		gmessage.message = message;
		return gmessage;
	}
	
	void set_timer(long timeval) {
		timer.set(timeval);
		send_simflow_to_clients(create_timer_packet(timer));
	}
	
//	@Override
//	public void loop() {}
	
	boolean check_state(byte state) {
		return GAMEFLOW_STATE == state;
	}
	
	void set_state(byte state) {
		GAMEFLOW_STATE=state;
		NEW_STATE=true;
	}
	
	boolean is_new_state() {
		return NEW_STATE;
	}
	
	void reset_new_state() {
		this.NEW_STATE=false;
	}
	
//	public void loop() {
//		if(timer.check()) {
//			System.out.println("done");
//		}
//	}
	
	@Override
	public void loop() {
		if(check_state(STATE.WAIT_FOR_PLAYERS)) {
			if(ShadowActorHandle.get_active_count() >=2) {
				set_state(STATE.READY_TO_START);
				set_timer(5000);
			}
		}
		if(check_state(STATE.READY_TO_START)) {
			if(is_new_state()) {
				this.send_simflow_to_clients(create_banner_packet("GAME WILL START"));
				reset_new_state();
			}
			
			if(ShadowActorHandle.get_active_count() < 2) {
				set_state(STATE.WAIT_FOR_PLAYERS);
			} else {
				if(timer.check()) {
					System.out.println("countdown");
					set_state(STATE.COUNTDOWN);
				}	
			}
		}
		if(check_state(STATE.COUNTDOWN)) {
			if(is_new_state()) {
				init_game();
				set_timer(5000);
				reset_new_state();
			}
			if(timer.check()) {
				set_state(STATE.PLAY);
			}
		}
		if(check_state(STATE.PLAY)) {
			if(is_new_state()) {
				set_timer(2*MINUTE);
				reset_new_state();
			}
			if(timer.check()) {
				set_state(STATE.HIDER_WIN);
			}
			else {
				boolean hidecheck = check_hider_exists();
				boolean seekcheck = check_seeker_exists();
				if(!(hidecheck&seekcheck)) {
					set_state(STATE.DRAW);
				}
				if(!hidecheck) {
					set_state(STATE.SEEKER_WIN);
					
				}
				if(!seekcheck) {
					set_state(STATE.HIDER_WIN);
				}
			}
		}
		if(check_state(STATE.HIDER_WIN) || check_state(STATE.SEEKER_WIN) || check_state(STATE.DRAW)) {
			if(is_new_state()) {
				set_timer(5000);
				reset_new_state();
				if(check_state(STATE.HIDER_WIN)) {
					this.send_simflow_to_clients(create_banner_packet("HIDER WINS"));
				}
				if(check_state(STATE.SEEKER_WIN)) {
					this.send_simflow_to_clients(create_banner_packet("SEEKER WINS"));
				}
				if(check_state(STATE.DRAW)) {
					this.send_simflow_to_clients(create_banner_packet("DRAW"));
				}
				
			}
			if(timer.check()) {
				set_state(STATE.READY_TO_START);
				timer.set(5000);
				reset_game();
			}
		}
	}

}
