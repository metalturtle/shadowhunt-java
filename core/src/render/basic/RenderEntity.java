package render.basic;

import basic.Rectangle;
import basic.Vector;
import handlers.basic.entity.Entity;
import util.GameInfo;

//import java.lang.Math.min ;
import java.lang.Math;

public class RenderEntity extends Entity
{
	boolean forever,visible=true;
	int sprite_type;
	long lifespan,start;
	float timestamp;
	float r,g,b,a;
	short blend_option;
	public RenderEntity()
	{
		super();
		this.lifespan = 0;
		set_color(1f,1f,1f,1f);
		this.forever=true;
	}
	public RenderEntity(Vector pos,Rectangle bound,float angle,int sprite_type)	{
		super();
		this.set(pos,bound,angle,sprite_type);
		this.forever=true;
	}
	public void set(Vector pos,Rectangle bound,float angle,int sprite_type)
	{
		this.pos.set(pos);
		this.bound.set(bound);
		this.set_angle(angle);
		this.set_state(state);
		this.sprite_type = sprite_type;
		this.start = GameInfo.get_time_millis();
		this.set_color(1f,1f,1f,1f);
	}
	
	public void set(Entity ent) {
		RenderEntity rendEnt = (RenderEntity)ent;
		this.set(rendEnt.pos,rendEnt.bound,rendEnt.get_angle(),rendEnt.sprite_type);	
	}
	public float get_timestamp()	{return timestamp;}
	public long get_lifespan() {return lifespan;}
	public long get_start() {return start;}
	public boolean check_forever() {return forever;}
	public float get_r() {return r;}
	public float get_g() {return g;}
	public float get_b() {return b;}
	public float get_a() {return a;}
	public int get_sprite_type() {return sprite_type;};
	public boolean check_visible() {return visible;}
	public void set_color(float r,float g,float b,float a) {this.r = r;this.b=b;this.g=g;this.a=a;	}
	public void set_forever(boolean forever) {this.forever = forever;}
	public void set_lifespan(long lifespan) {
		set_start();
		this.forever=false;
		this.lifespan = lifespan;
	}
	public void set_start() {this.start = GameInfo.get_time_millis();}
	public void set_visible(boolean visible) {this.visible = visible;}
	
	public void set_r(float r) {this.r = r;}
	public void set_g(float g) {this.g = g;}
	public void set_b(float b) {this.b = b;}
	public void set_a(float a) {this.a = a;}
	
	public void set_sprite_type(int sprite_type)
	{
		this.sprite_type = sprite_type;
	}
	
	public boolean check_alive()
	{
		if(forever)
			return true;
		if(GameInfo.get_time_millis()>this.start+this.lifespan)
		{
			return false;
		}
		return true;
	}
	
//	@Override
//	public void mark_disable() {
//		super.mark_disable();
//	}
	
	@Override
	public Rectangle get_view_rect() {
		Rectangle viewrect = super.get_view_rect();
		
		float x0 = pos.x();
		float y0 = pos.y();
		
		float x,y;
		float x1,y1,x2,y2,x3,y3,x4,y4;
		float rad = (float)Math.toRadians(get_angle());
		
		x=viewrect.x();y=viewrect.y();
		x1 = (float)(x0+(x-x0)*Math.cos(rad)+(y-y0)*Math.sin(rad));
		y1 = (float)(y0-(x-x0)*Math.sin(rad)+(y-y0)*Math.cos(rad));
		
		x=viewrect.x()+viewrect.w();y=viewrect.y();
		x2 = (float)(x0+(x-x0)*Math.cos(rad)+(y-y0)*Math.sin(rad));
		y2 = (float)(y0-(x-x0)*Math.sin(rad)+(y-y0)*Math.cos(rad));
		
		x=viewrect.x()+viewrect.w();y=viewrect.y()+viewrect.h();
		x3 = (float)(x0+(x-x0)*Math.cos(rad)+(y-y0)*Math.sin(rad));
		y3 = (float)(y0-(x-x0)*Math.sin(rad)+(y-y0)*Math.cos(rad));
		
		x=viewrect.x();y=viewrect.y()+viewrect.h();
		x4 = (float)(x0+(x-x0)*Math.cos(rad)+(y-y0)*Math.sin(rad));
		y4 = (float)(y0-(x-x0)*Math.sin(rad)+(y-y0)*Math.cos(rad));
		
		float minx=Math.min(Math.min(Math.min(x1, x2),x3),x4);
		float miny=Math.min(Math.min(Math.min(y1, y2),y3),y4);
		
		float maxx=Math.max(Math.max(Math.max(x1, x2),x3),x4);
		float maxy=Math.max(Math.max(Math.max(y1, y2),y3),y4);
		float w = maxx-minx,h = maxy-miny;
		
		viewRectangle.set(minx,miny,w,h);
		
		return viewRectangle;
	}
	
}
