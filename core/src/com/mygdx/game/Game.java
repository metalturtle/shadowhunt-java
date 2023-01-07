package com.mygdx.game;

import Coordinator.Coordinator;
import Coordinator.HandlerInitRunner;
import basic.InputState;
import game.render.HidingSpriteHandler;
import game.synced.ShadowActorHandler;
import gdxgraphics.GdxMainRenderer;
import gdxgraphics.GdxHidingSpriteHandler;
import loader.ClientLoader;
import loader.ServerLoader;
import render.MainRenderer;
import render.RenderEntityMainHandler;
import render.RenderInfo;
import simulation.SimulationHandler;
import util.ConfigManager;
import util.GameInfo;

public class Game {

	boolean keys[];
	String ip_string="localhost";
	int tcp_port=2300,udp_port=2301;
	public ConfigManager ConfigManage;
	
	long frame_end_time, frame_start_time;
	static short last_fps;
	boolean first_frame;
	public Coordinator Coordinate;
	MainRenderer MRenderer;
	HandlerInitRunner initRunner;
	String ERROR_MESSAGE="";
	
    public Game(ConfigManager ConfigManage,MainRenderer MRenderer,HandlerInitRunner initRunner,String bind_name[],char bind_keys[]) {
    	this.ConfigManage = ConfigManage;
    	this.MRenderer = MRenderer;
    	this.initRunner = initRunner;
    	InputState.init(bind_name, bind_keys);
		
		keys = new boolean[256];
		frame_start_time = System.currentTimeMillis();
		frame_end_time = System.currentTimeMillis() + 1000/RenderInfo.TARGET_FPS;
    }
    
    public void set_ip_string(String ip_string, int tcp_port, int udp_port)
    {
    	this.ip_string = ip_string;
    	this.tcp_port = tcp_port;
    	this.udp_port = udp_port;
    }
    
    public void quit()
    {
    	System.exit(0);
    }
    
    /********** LIFECYCLE METHODS **********/
    
	public void create() {

	}
	
	public void reset_keys() {
		for(int i = 0; i <256;i++) {
			keys[i] = false;
		}
		InputState.map_keyboard(keys);
	}
	
	public void set_mouse(float x,float y) {
		InputState.map_mouse(x, y);
	}
	

	
	public void dispose() {
		if(Coordinate != null)
			Coordinate.dispose();
	}

	public void pause() {
		System.out.println("paused");
	}
	
	public void resize(int x, int y) {
//		currentAppRunner.resize(x,y);
	}
	
	public void resume() {
	}
	
	public void keyUp(char c) {
		keys[c] = false;
		keys[Character.toLowerCase(c)] = false;
		InputState.map_keyboard(keys);
	}
	
	public void keyDown(char c) {
		keys[c] = true;
		keys[Character.toLowerCase(c)] = true;
		InputState.map_keyboard(keys);
	}
	
	/********** COORDINATOR MANAGEMENT **********/
	
	byte COORDINATOR_STATE;
	
	static class AppCoordExecState {
		static final byte NULL,COORDINATOR_CREATION,COORDINATOR_RUN,COORDINATOR_DISPOSE;
		static {
			byte val=0;
			NULL=val++;
			COORDINATOR_CREATION=val++;
			COORDINATOR_RUN=val++;
			COORDINATOR_DISPOSE=val++;
		}
	}
	/********** COORDINATOR MANAGEMENT **********/
	
	public void start_coordinator() {
		this.COORDINATOR_STATE = AppCoordExecState.COORDINATOR_CREATION;
		this.ERROR_MESSAGE = "";
	}
 	
	public void dispose_coordinator(Exception e) {
		Coordinate.dispose();
		Coordinate = null;
		COORDINATOR_STATE = AppCoordExecState.COORDINATOR_DISPOSE;
	}
	
	/********** COORDINATOR MANAGEMENT FOR SERVER **********/
	
	public void server_create_coordinator() {
		ServerLoader loader = new ServerLoader(ConfigManage, MRenderer,initRunner);
		Coordinate = new Coordinator(loader);
		Coordinate.server_init();
	}
	
	public void server_run_coordinator() {
		try {
			Coordinate.run_server();
		} catch(Exception e) {
			e.printStackTrace();
			dispose_coordinator(e);
		}
	}
	
	/********** COORDINATOR MANAGEMENT FOR CLIENT **********/
	
	public void client_create_coordinator() {
		ClientLoader loader = new ClientLoader(ConfigManage, MRenderer,initRunner);
		Coordinate = new Coordinator(loader);
		Coordinate.client_init();
	}
	
	public void client_run_coordinator() {
		try {
			Coordinate.run_client();
		} catch(Exception e) {
			System.out.println("client run coordinator throw error");
			ERROR_MESSAGE = e.getMessage();
			e.printStackTrace();
			dispose_coordinator(e);
		}
	}
	
	public String get_error_message() {
		return ERROR_MESSAGE;
	}
	
	/********** UTILITY METHODS **********/
	
	public void calc_time() {
		frame_start_time = frame_end_time;
		frame_end_time = System.currentTimeMillis();
		if(first_frame) {
			last_fps = (short)RenderInfo.TARGET_FPS;
			first_frame = false;
		} else {
			last_fps = (short)(frame_end_time - frame_start_time);
		}
//		GameInfo.set_frame_time(frame_end_time);
//		GameInfo.set_delta_time(((float)last_fps)/1000f);
		GameInfo.set_time(last_fps);
	}
	
	/********** RUN METHODS **********/
	
	public boolean run_server() {
		calc_time();
		if(COORDINATOR_STATE == AppCoordExecState.COORDINATOR_RUN) {
			server_run_coordinator();
		} else if(COORDINATOR_STATE == AppCoordExecState.COORDINATOR_CREATION) {
			server_create_coordinator();
			COORDINATOR_STATE = AppCoordExecState.COORDINATOR_RUN;
		} else if (COORDINATOR_STATE == AppCoordExecState.COORDINATOR_DISPOSE ) {
			return false;
		}
		return true;
	}
	
	public boolean run_client() {
		calc_time();
		if(COORDINATOR_STATE == AppCoordExecState.COORDINATOR_RUN) {
			client_run_coordinator();
		} else if(COORDINATOR_STATE == AppCoordExecState.COORDINATOR_CREATION) {
			client_create_coordinator();
			COORDINATOR_STATE = AppCoordExecState.COORDINATOR_RUN;
		} else if (COORDINATOR_STATE == AppCoordExecState.COORDINATOR_DISPOSE ) {
			return false;
		}
		return true;
	}
	
}
