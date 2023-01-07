package loader;

import java.io.FileReader;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import Coordinator.Coordinator;
import Coordinator.HandlerInitRunner;
import render.MainRenderer;
import simulation.SimulationHandler;
import simulation.SimulationCoordinator;
import util.ConfigManager;

public class ServerLoader extends Loader {

	public ServerLoader(ConfigManager ConfigManage, MainRenderer MRenderer,HandlerInitRunner HandleInitRunner) {
		super(ConfigManage, MRenderer,HandleInitRunner);
	}
	
	@Override
	public void load_resources(Coordinator coordinator,String filename) throws Exception {
		Object obj;
		
		this.set_load_info("LOADING LEVEL FILE", false, false);
		this.wait_thread();
		obj = new JSONParser().parse(new FileReader(filename));
		JSONObject levelFile = (JSONObject) obj;
		this.set_load_info("LEVEL FILE IS LOADED", false, false);
		this.wait_thread();
		
		this.set_load_info("LOADING TEXTURE RESOURCES", false, false);
		this.wait_thread();
		MRenderer.load(ConfigManage, levelFile);
		this.set_load_info("TEXTURE RESOURCES ARE LOADED", false, false);
		this.wait_thread();
		
		this.set_load_info("LOADING GAME", false, false);
		this.wait_thread();
		this.SimCoord = new SimulationCoordinator(coordinator,HandleInitRunner,true);
		load_collision(levelFile,SimCoord.SimHandle.GWorld);
//		this.SimCoord.load_from_level_file();
		this.load_misc_object(levelFile);
		this.set_load_info("GAME IS LOADED", false, false);
		this.wait_thread();
		
		MRenderer.set_ready(true);
		this.set_load_info("STARTING GAME", true, false);
		this.wait_thread();
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean verify_level(String filename) {
		
		return false;
	}
}
