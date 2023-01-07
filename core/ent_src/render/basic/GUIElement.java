//package render.basic;
//
//import handlers.basic.entity.Entity;
//import util.GameInfo;
//
//public class GUIElement extends RenderEntity {
//	
//	String text="";
////	boolean enabled;
//	
//	public void set_text(String text) {this.text = text;}
//	public String get_text() {return this.text;}
//
//	public void set_local_id (int local_id) {this.local_id = local_id;}
//	
//	@Override
//	public void enable() {
//		this.enabled=true;
//	}
//	
//	@Override
//	public void disable() {
//		this.enabled=false;
//	}
//	@Override
//	public boolean check_alive()
//	{
//		if(forever)
//			return true;
//		if(GameInfo.get_time_millis()>this.start+this.lifespan)
//		{
//			return false;
//		}
//		return true;
//	}
//}
