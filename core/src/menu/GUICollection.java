package menu;

import java.util.ArrayList;

import render.RenderInfo;

public class GUICollection {

	ArrayList <GUI> guiItems = new ArrayList<GUI>();

	public final int CELL_SIZE,Y_CELL_COUNT;
	
	public GUICollection() {
		CELL_SIZE = RenderInfo.get_screen_width()/100;
		Y_CELL_COUNT = RenderInfo.get_screen_height()/CELL_SIZE;
	}
	
	public void add(GUI gui) {
		int c = CELL_SIZE;
		System.out.println("c: "+c);
		if(!gui.convert_done) {
			gui.set(gui.x()*c,gui.y()*c,gui.w()*c,gui.h()*c);
			gui.convert_done = true;
		}
			
//		gui.set(gui.x()*rw,gui.y()*rh,gui.w()*rw,gui.h()*rh);
		guiItems.add(gui);
	}
	
	public GUI get(int i) {
		return guiItems.get(i);
	}
	
	public int size() {
		return guiItems.size();
	}
}
