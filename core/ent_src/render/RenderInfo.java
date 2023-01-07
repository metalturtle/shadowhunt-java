package render;

import java.util.HashMap;

import basic.Vector;

public class RenderInfo {
	public static int TARGET_FPS = 60;
	public static float ASPECT_RATIO = 21/9;
	public static int SCREEN_WIDTH = 800;
	public static int SCREEN_HEIGHT = (int)(((float)SCREEN_WIDTH)/ASPECT_RATIO);
	private static float UNIT_SIZE = 10;
	public static float WORLD_X = SCREEN_WIDTH/UNIT_SIZE,	WORLD_Y = SCREEN_HEIGHT/UNIT_SIZE;
	public static float AMBIENT_LIGHT;


	public static void init(HashMap<Object,Object> rendconfig) {
    	int sw = (int)((long)rendconfig.get("screen_width"));
    	int sh = (int)(long)rendconfig.get("screen_height");
		SCREEN_WIDTH = sw;
		SCREEN_HEIGHT = sh;
		WORLD_X = SCREEN_WIDTH/UNIT_SIZE;
		WORLD_Y = SCREEN_HEIGHT/UNIT_SIZE;
		ASPECT_RATIO=SCREEN_WIDTH/SCREEN_HEIGHT;
		
		AMBIENT_LIGHT = (float)(double)rendconfig.get("ambient_light");
	}
	
	static public Vector get_percent(float percent_x, float percent_y) {
		return new Vector((percent_x/100)*RenderInfo.SCREEN_WIDTH,(percent_y/100)*RenderInfo.SCREEN_HEIGHT);
	}
	
	public static int get_screen_width() {
		return SCREEN_WIDTH;
	}
	
	public static int get_screen_height() {
		return SCREEN_HEIGHT;
	}
	
	public static float get_world_width() {
		return WORLD_X;
	}
	
	public static float get_world_height() {
		return WORLD_Y;
	}
	
	public static float get_unit_size() {
		return UNIT_SIZE;
	}
	
	public static float world_to_screen(float val) {
		return val*UNIT_SIZE;
	}
	
	public static float screen_to_world(float val) {
		return val/UNIT_SIZE;
	}
	
}
