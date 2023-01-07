package render;
import java.util.Iterator;

import basic.Vector;
import basic.resource.WorldResourceHandler;
import render.basic.Light;
import util.ConfigManager;
import basic.Rectangle;

public class LightHandler extends WorldResourceHandler<Light> {

//	AbstractRenderHandler RendHandle;
//	GameCamera GCam;
    float THRESHOLD = 0.8f;
    
	LightHandler() {
		super();
//		this.RendHandle = RendHandle;
//		this.GCam = RendHandle.GCamera;
	}
	
	
	public Light create_new_element() {
		return new Light();
	}
	
	@Override
	public Light add_resource_object(Vector pos, Rectangle bound, String params) {
		Light l = super.add_resource_object(pos, bound, params);
		bound.h(bound.w());
		pos.set(pos.x()-bound.w()/2,pos.y()-bound.w()/2);
		l.setColor(1, 1, 1);
		if(params != null && params.length() > 0) {
			String tokens[] = params.split(",");
			int length = tokens.length;
			if(length>0) {
				l.set_type(Short.parseShort(tokens[0]));
			}
			if(length>1) {
				l.RED = Float.parseFloat(tokens[1]);
			}
			if(length>2) {
				l.BLUE = Float.parseFloat(tokens[2]);
			}
			if(length>3) {
				l.GREEN = Float.parseFloat(tokens[3]);
			}
		}
		return l;
	}

	public void set_pos(int light_id, Vector pos) {
		Light light = get_resource_object(light_id);
		light.pos.set(pos.x()-light.bound.w()/2,pos.y()-light.bound.w()/2);
	}
	
	void resize(int resizex,int resizey) {
	}
	
	@Override
	protected void reassign_resource_object_id(Light obj, int new_id) {
		
	}
	
}
