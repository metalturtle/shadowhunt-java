package menu;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import basic.Rectangle;
import basic.Vector;
import render.RenderInfo;

public final class Screen {

	GUI hoverGUI;
	ScreenHandler ScreenHandle;
	String name;
	float rw,rh;
	
	public GUICollection guiItems;
	
	public Screen(ScreenHandler ScreenHandle,String name) {
		guiItems = new GUICollection();
		this.ScreenHandle = ScreenHandle;
		this.name = name;
		rw = RenderInfo.get_screen_width();
		rh = RenderInfo.get_screen_height();
	}
	
	public Screen(ScreenHandler ScreenHandle, String name, GUICollection guiCol) {
		guiItems = guiCol;
		this.ScreenHandle = ScreenHandle;
		this.name = name;
		rw = RenderInfo.get_screen_width();
		rh = RenderInfo.get_screen_height();
	}
	
	public GUI get_hover_gui() {
		return hoverGUI;
	}

	public String get_name() {
		return name;
	}
	
	public void add_gui(GUI gui) {
		guiItems.add(gui);
	}
	
	Vector mouse = new Vector();
	GUI check_gui(int x,int y) {
		mouse.set(x,y);
		for(int i =0; i < guiItems.size(); i++) {
			GUI gui = guiItems.get(i);
			if(gui.visible && Rectangle.check_point_intersection(gui, mouse)) {
				return gui;
			}
		}
		return null;
	}
	
	public void hover(int x, int y) {
		hoverGUI = check_gui(x,y);
	}
	
	public void pressed(int x, int y) {
		hoverGUI = check_gui(x,y);
	}
	
	public void released(int x, int y)
	{
		hoverGUI = check_gui(x,y);
	}
	
	
	public void render(SpriteBatch spriteBatch) 
	{	
		spriteBatch.begin();
		for(int i =0; i < guiItems.size(); i++) {
			GUI gui = guiItems.get(i);
			if(!gui.visible)
				continue;
			if(hoverGUI != null && hoverGUI.clickable && hoverGUI == gui) {
				spriteBatch.draw(ScreenHandle.bgtex,gui.x(),gui.y(),gui.w(),gui.h());
			}
			if(gui.type == GUI.GUI_TYPE.IMAGE) {
				spriteBatch.draw(ScreenHandle.imageNameMap.get(gui.get_text()),gui.x(),gui.y(),gui.w(),gui.h());
			}
			if(gui.type == GUI.GUI_TYPE.TEXT)
				ScreenHandle.guiText.render(gui.text,spriteBatch,gui.x(),gui.y(),gui.w());
		}
		spriteBatch.end();
	}
	
//	abstract void gui_actions();
}
