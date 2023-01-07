package render.basic;

import basic.Rectangle;

import basic.Vector;
import basic.resource.WorldResourceObject;

public class Light extends RenderEntity
{
	short type;
	public float RED,BLUE,GREEN;
	
	public Light() {
		super(new Vector(0,0),new Rectangle(0,0,0,0),0,0);
		this.type = 0;
	}
	
	public Light(Vector pos, Rectangle bound, short type) {
		super(pos, bound,0,0);
		this.type = type;
	}
	public void setColor(float R,float G,float B)
	{
		RED = R;
		BLUE = B;
		GREEN = G;
	}
	
	public short get_type() {return this.type;}
	public void set_type(short type) {this.type = type;}
	
//	@Override
//	public void mark_disable() {
//		super.mark_disable();
//	}
}