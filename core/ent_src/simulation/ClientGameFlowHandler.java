package simulation;

import org.json.simple.JSONArray;

import basic.Rectangle;
import basic.Vector;
import client.ClientImage;
import game.entity.ShadowLivingEntity;
import game.synced.ShadowActorHandler;
import handlers.basic.GameEvent;
import handlers.basic.Timer;
import handlers.basic.entity.SyncedEntity;
import menu.GUI;
import render.RenderInfo;
import simulation.handlers.EntityHandler;

public class ClientGameFlowHandler extends SimulationFlowHandler {
	
//	SimulationHandler SimHandle;
	SimulationCoordinator SimCoord;
	GUI posGUI,rttGUI,bannerGUI,timerGUI;
	
	Timer bannerTimer = new Timer(),gameTimer = new Timer();
	long MINUTE = 1000*60;
	
	public static class TYPE {
		public static final byte NULL,BANNER,ASSIGN_PLAYER,TIMER;
		static {
			byte val=0;
			NULL = val++;
			BANNER=val++;
			ASSIGN_PLAYER=val++;
			TIMER=val++;
		}
	}
	
	public ClientGameFlowHandler(SimulationCoordinator SimCoord) {
		super(SimCoord,SimCoord.CIHandle);
//		this.SimHandle = SimHandle;
		this.SimCoord = SimCoord;
		Rectangle rdesc = new Rectangle(50,SimCoord.SimHandle.RendEntMainHandle.GGUICollection.Y_CELL_COUNT/2,1,5);
		Rectangle top = new Rectangle(50,SimCoord.SimHandle.RendEntMainHandle.GGUICollection.Y_CELL_COUNT-1,1,5);
		posGUI = new GUI("position",rdesc,GUI.GUI_TYPE.TEXT);
		posGUI.set_text("checking text value");
		
		bannerGUI = new GUI("banner",rdesc,GUI.GUI_TYPE.TEXT);
		
		rttGUI = new GUI("RTT",top,GUI.GUI_TYPE.TEXT);
		
		timerGUI = new GUI("TIMER",top,GUI.GUI_TYPE.TEXT);
		timerGUI.set_visible(false);
		
		
//		this.SimCoord.SimHandle.RendEntMainHandle.GGUICollection.add(rttGUI);
		this.SimCoord.SimHandle.RendEntMainHandle.GGUICollection.add(bannerGUI);
		this.SimCoord.SimHandle.RendEntMainHandle.GGUICollection.add(timerGUI);
		
		bannerGUI.set_visible(false);
	}

	public short get_minute(long timer) {
		if(timer < MINUTE  ) {
			return 0;
		}
		return (short)(timer/(MINUTE));
	}
	
	public short get_seconds(long timer) {
		return (short)((timer/1000)%60);
	}
	
	
	
	public void process_gameflow(GameFlowPacket game_flow_packet) {
		ClientImage clientImage = SimCoord.CIHandle.get_client_by_localid(0);
		ClientSimulationState CSimState = SimCoord.CIHandle.get_client_by_localid(0).CSimState;
		if (game_flow_packet.type == TYPE.BANNER) {
			bannerGUI.set_text(game_flow_packet.message);
			bannerTimer.set(5000);
			bannerGUI.set_visible(true);
		}
		if(game_flow_packet.type==TYPE.ASSIGN_PLAYER) {
			String token[] = game_flow_packet.message.split(",");
			int handler_id = Integer.parseInt(token[0]);
			int new_main_ent = Integer.parseInt(token[1]);
			SyncedEntityHandler EntHandle = (SyncedEntityHandler) SimCoord.SimHandle.get_entity_handler(handler_id);
			
			if(EntHandle.check_translate_id_exists(new_main_ent)) {
				
				SyncedEntity ent = EntHandle.get_entity_from_translate_id(new_main_ent);
//				ent.CSimState = CSimState;
//				CSimState.set_client_entity(ent);
//				ent.set_flag(SyncedEntity.ServerType.CLIENT_USING);
				SimCoord.attach_entity_to_client(clientImage,ent);
				SimCoord.SimHandle.RendEntMainHandle.GCamera.set_follow_entity(ent);
				
				ShadowActorHandler SH = (ShadowActorHandler)EntHandle;
				SH.set_main_entity((ShadowLivingEntity)ent);
			}
			
			if(CSimState.get_client_entity() != null) {
				posGUI.set_text(CSimState.get_client_entity().pos+"");
			}
		}
		if(game_flow_packet.type == TYPE.TIMER) {
			String token[] = game_flow_packet.message.split(",");
			if(token[0].charAt(0) == 't') {
				long timerval = Long.parseLong(token[1]);
				gameTimer.set(timerval);
				timerGUI.set_visible(true);
			} else {
				timerGUI.set_visible(false);
			}
		}
	}

	@Override
	public SyncedEntity add_new_client(ClientImage clientImage) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void disconnect_client(int con_id, SyncedEntity entity) {
		((SyncedEntityHandler)entity.get_wrh()).disable_resource_object(entity);
	}

	@Override
	public void loop() {
		ClientImage clientImage = SimCoord.CIHandle.get_client_by_localid(0);
//		rttGUI.set_text("RTT: "+clientImage.get_udp_rtt());
		if(bannerTimer.check()) {
			bannerGUI.set_visible(false);
		}
		if(timerGUI.is_visible()) {
			timerGUI.set_text(""+get_minute(gameTimer.get_remaining_duration())
			+":"
			+get_seconds(gameTimer.get_remaining_duration()));
		}
	}

	@Override
	public void level_load_entities(JSONArray jsonObj) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void create_misc_object(EntityHandler EntHandle, Vector pos, Rectangle bound, String params) {
		// TODO Auto-generated method stub
		
	}
}
