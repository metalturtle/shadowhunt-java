package loader;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import Coordinator.Coordinator;
import Coordinator.HandlerInitRunner;
import basic.Rectangle;
import render.MainRenderer;
import simulation.SimulationHandler;
import simulation.SimulationCoordinator;
import util.ConfigManager;
import world.World;
import world.basic.Wall;

public abstract class Loader {
	public ConfigManager ConfigManage;
	HandlerInitRunner HandleInitRunner;
//	public SimulationHandler SimHandle;
	public SimulationCoordinator SimCoord;
	public MainRenderer MRenderer;
	
	private Object lock = new Object();
	protected String loadMessage="STARTING";
	private boolean IS_READY_FLAG,IS_ERROR_FLAG;
	final long WAIT_MILLIS=200;
	
	
	Loader(ConfigManager ConfigManage, MainRenderer MRenderer,HandlerInitRunner HandleInitRunner) {
		this.ConfigManage = ConfigManage;
		this.MRenderer = MRenderer;
		this.HandleInitRunner = HandleInitRunner;
	}
	
	void load_renderer(JSONObject levelFile) throws Exception {
		MRenderer.load(ConfigManage,levelFile);
	}

	void load_collision(JSONObject levelFile, World GWorld) {
		JSONObject module = (JSONObject)levelFile.get("collision");
		
		if (module != null) {
			int size = ((Number)module.get("size")).intValue();
			JSONArray array = (JSONArray)module.get("object");
			Wall walls[] = new Wall[size];
			for(int i=0;i<size;i++) {
				JSONArray texjson = (JSONArray)array.get(i);
				walls[i] = new Wall(((Number)texjson.get(0)).floatValue()
						,((Number)texjson.get(1)).floatValue()
						,((Number)texjson.get(2)).floatValue()
						,((Number)texjson.get(3)).floatValue()) ;
				walls[i].set_ray_block((Boolean)texjson.get(4));
			}
			GWorld.init(walls);
		}
	}
	
	void load_misc_object(JSONObject levelFile) {
		JSONObject miscObject = (JSONObject)levelFile.get("misc");
		JSONArray miscArray = (JSONArray)miscObject.get("object");
		SimCoord.load_from_level_file(miscArray);
	}
	
	abstract public boolean verify_level(String filename);
	abstract protected void load_resources(Coordinator coordinator,String filename) throws Exception;
	
	protected void set_load_info(String message,boolean load_done,boolean error) {
		System.out.println("loading stage: "+message);
		synchronized(lock) {
			this.loadMessage = message;
			this.IS_ERROR_FLAG = error;
			this.IS_READY_FLAG = load_done;
		}
	}
	
	protected void wait_thread() {
		try {
			Thread.sleep(WAIT_MILLIS);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public String get_load_message() {
		synchronized(lock) {
			return new String(this.loadMessage);
		}
	}
	
	public boolean is_ready() {
		synchronized(lock) {
			return this.IS_READY_FLAG;
		}
	}
	
	public boolean is_error() {
		synchronized(lock) {
			return this.IS_ERROR_FLAG;
		}
	}
	
	public Thread start_load_thread(final Coordinator coordinator,final String filename) {
		return new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					load_resources(coordinator,filename);
				} catch(Exception e) {
					e.printStackTrace();
					set_load_info("ERROR: loading exception at stage '"+loadMessage+"' failed. errmsg - "+e.getMessage(),false,true);
				}
			}
		});
	}
	
	public void dispose() {
		
	}
}