package com.mygdx.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import main.client.GdxGame;
import network.NetworkInfo;
import render.RenderInfo;
import util.ConfigManager;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument


public class DesktopLauncher {
	public static void main (String[] arg) {
		ConfigManager ConfigManage;
    	ConfigManage = new ConfigManager();
    	ConfigManage.read_files();
    	
    	RenderInfo.init(ConfigManage.get_object("app", "render"));
    	
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setTitle("graphicsdemo");
		config.setWindowSizeLimits(RenderInfo.SCREEN_WIDTH, RenderInfo.SCREEN_HEIGHT, RenderInfo.SCREEN_WIDTH, RenderInfo.SCREEN_HEIGHT);
		NetworkInfo.init(false,ConfigManage.get_object("app", "network"));
		new Lwjgl3Application(new GdxGame(ConfigManage,arg), config);
	}
}
