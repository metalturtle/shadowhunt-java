package menu;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import basic.Rectangle;

public class GUI extends Rectangle {

	String name;
//	public TextureRegion texreg;
	Rectangle grid_rect;
	boolean convert_done;
	
	boolean clickable=true,visible=true;
	String text="untitled";
	byte type=0;
	
	public static class GUI_TYPE {
		public static final byte TEXT,IMAGE;
		
		static {
			byte val = 0;
			TEXT = val++;
			IMAGE = val++;
		}
	}
	
	public GUI(String name,Rectangle rect,byte type) {
		super(rect);
		grid_rect = new Rectangle(rect);
		this.name = name;
		this.type = type;
	}
	
	public void set_visible(boolean flag) {
		this.visible = flag;
	}
	public void set_type(byte type) {
		this.type = type;
	}
	
	public String get_name() {
		return name;
	}
	
	public void set_clickable_flag(boolean flag) {
		this.clickable = flag;
	}
	
	public void set_text(String text) {
		this.text = text;
	}
	
	public String get_text() {
		return text;
	}
	
	public boolean is_visible() {
		return visible;
	}
	
//	public void set_texture(TextureRegion texreg) {
//		this.texreg = texreg;
//	}
}
