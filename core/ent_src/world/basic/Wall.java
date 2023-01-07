package world.basic;

import basic.Rectangle;
import basic.Vector;
import basic.resource.WorldResourceObject;
import basic.resource.static_resource.StaticWorldResourceObject;

public class Wall extends StaticWorldResourceObject
{
	boolean rayBlock = true;
	public Wall() {
		super(0,0,0,0);
	}
	
	public Wall(Rectangle bound) {
		super(bound);
	}
	
	public Wall(float x,float y, float w, float h) {
		super(x,y,w,h);
	}
	
	public void set_ray_block(boolean ray_block) {
		this.rayBlock = ray_block;
	}
	
	public boolean get_ray_block() {
		return this.rayBlock;
	}
	
}