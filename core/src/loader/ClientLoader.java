package loader;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import Coordinator.Coordinator;
import Coordinator.HandlerInitRunner;
import basic.Rectangle;
import handlers.basic.Timer;
import render.MainRenderer;
import resource.TextureIDManager;
import resource.TextureResourceManager;
import simulation.SimulationHandler;
import simulation.SimulationCoordinator;
import util.ConfigManager;
import world.World;
import world.basic.Wall;

public class ClientLoader extends Loader {
	
	int loader_stage;
	Timer timer = new Timer();
	JSONObject levelFile;
	
	static class STAGE {
		static final int FILE, RESOURCES,READY, SIM,DONE;
		static {
			int val=0;
			FILE = val++;
			RESOURCES = val++;
			SIM = val++;
			READY = val++;
			DONE = val++;
		}
	}
	
	public ClientLoader(ConfigManager ConfigManage, MainRenderer MRenderer,HandlerInitRunner HandleInitRunner) {
		super(ConfigManage, MRenderer,HandleInitRunner);
	}

	@Override
	public boolean verify_level(String filename) {
		return true;
	}
	
	boolean check_stage(int stage) {
		return timer.check() && loader_stage == stage;
	}
	
	void set_stage(int stage) {
		loader_stage = stage;
		timer.set(500);
	}
	
	@Override
	public void load_resources(Coordinator coordinator,String filename) throws Exception {
		if(check_stage(STAGE.FILE)) {
			this.SimCoord=null;
			Object obj;
			System.out.println("file");
			this.set_load_info("LOADING LEVEL FILE", false, false);
			obj = new JSONParser().parse(new FileReader(filename));
			levelFile = (JSONObject) obj;
			set_stage(STAGE.RESOURCES);
		}
		if(check_stage(STAGE.RESOURCES)) {
			System.out.println("resources");
			this.set_load_info("LOADING TEXTURE RESOURCES", false, false);
			MRenderer.load(ConfigManage, levelFile);
			set_stage(STAGE.SIM);
		}
		if(check_stage(STAGE.SIM)) {
			System.out.println("sim");
			this.set_load_info("LOADING GAME", false, false);
			this.SimCoord = new SimulationCoordinator(coordinator,HandleInitRunner,false);
			load_collision(levelFile,SimCoord.SimHandle.GWorld);
			this.load_misc_object(levelFile);
			HandleInitRunner.add_renderers(MRenderer);
			set_stage(STAGE.READY);
		}
		if(check_stage(STAGE.READY)) {
			MRenderer.set_ready(true);
			levelFile=null;
			this.set_load_info("STARTING GAME", true, false);
			set_stage(STAGE.DONE);
		}
	}

	@Override
	public void dispose() {
		
	}

}
