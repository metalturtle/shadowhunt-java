package basic.resource.static_resource;

import basic.Rectangle;
import basic.Vector;
import basic.resource.ResourceObject;

public class StaticWorldResourceObject extends Rectangle implements ResourceObject {
	boolean enabled=true;
	StaticWorldResourceObject()
	{
		super(0,0,0,0);
	}
	
	public StaticWorldResourceObject(Rectangle bound)
	{
		super(bound);
	}
	
	
	public StaticWorldResourceObject(float x, float y, float w, float h)
	{
		super(x,y,w,h);
	}
	
	public boolean check_enabled()
	{
		return enabled;
	}
	
	public void enable()
	{
		enabled = true;
	}
	
	public void disable()
	{
		enabled = false;
	}
	
	public Rectangle get_view_rect() {
		return this;
	}

	@Override
	public void set_local_id(int local_id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void set_pos_bound(Vector pos, Rectangle bound) {
		// TODO Auto-generated method stub
		
	}
	
}
