//package render;
//
//import java.util.ArrayList;
//
//import basic.Rectangle;
//import basic.Vector;
//import render.basic.GUIElement;
//import util.ConfigManager;
//
//public class GUIHandler extends RenderEntityHandler<GUIElement> {
//
//	ArrayList <GUIElement> GUIElements;
//	public final int TEXT_TYPE = 0;
//	GUIHandler(RenderEntityMainHandler RendEntMainHandle) {
//		super(RendEntMainHandle);
//		GUIElements = new ArrayList <GUIElement>();
//	}
//	
//	public void renderer_init() {
//	}
//	
//	public GUIElement add_resource_object(Vector pos, Rectangle bound, String params) {
//		GUIElement gui = super.add_resource_object(pos,bound,params);
//		gui.set_text(params);
//		gui.set_forever(true);
//		return gui;
//	}
//
//	@Override
//	public void renderer_init(ConfigManager ConfigManage) {
//	}
//
//	@Override
//	public void dispose() {
//	}
//
//	@Override
//	public GUIElement create_new_element() {
//		// TODO Auto-generated method stub
//		return new GUIElement();
//	}
//
//	@Override
//	protected void before_disable_object(GUIElement k) {
//	}
//	
////	void render_loop (SpriteBatch spriteBatch) {
////		for (int i = 0; i < GUIElements.size(); i++) {
////			GUIElement gui_element = GUIElements.get(i);
////			if (gui_element.check_enabled() == false || gui_element.check_visible() == false)
////				continue;
////			font.draw(spriteBatch,gui_element.get_text(),gui_element.pos.x() , gui_element.pos.y());
////			if(!gui_element.check_alive()){
////				gui_element.disable();
////			}
////		}
////	}
////	
////	void dispose() {
////		font.dispose();
////		generator.dispose();
////	}
//}
