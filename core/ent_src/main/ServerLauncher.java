package main;

import com.mygdx.game.ServerGame;

import network.NetworkInfo;
import render.RenderInfo;
import util.ConfigManager;

public class ServerLauncher {

	public static void main(String args[]) {
		ConfigManager ConfigManage;
    	ConfigManage = new ConfigManager();
    	ConfigManage.read_files();
    	
    	RenderInfo.init(ConfigManage.get_object("app", "render"));
		NetworkInfo.init(true,ConfigManage.get_object("app", "network"));
		new ServerGame(ConfigManage,args);
	}
}
