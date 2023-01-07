package basic.resource;

import basic.*;

public class WorldResourceObject implements ResourceObject
{
	protected int local_id;
	public Vector pos;
	public Rectangle bound;
	protected boolean enabled;
	protected Rectangle viewRectangle;
	protected WorldResourceHandler<WorldResourceObject> WRH;
	protected WorldResourceObject children[]= new WorldResourceObject[0],parent;
	int childId=-1;
	Rectangle test;
	public static boolean starttest=false;
	
	WorldResourceObject() {
		this.pos = new Vector(0,0);
		this.bound = new Rectangle(0,0,0,0);
		this.enabled = true;
		this.viewRectangle = new Rectangle(0,0,0,0);
	}
	
	public WorldResourceObject(Vector pos,Rectangle bound) {
		this.pos = new Vector(pos);
		this.bound = new Rectangle(bound);
		this.viewRectangle = new Rectangle(pos.x()+bound.x(),pos.y()+bound.y(),bound.w(),bound.h());
		this.enabled = true;
	}
	
	protected void set_wrh(WorldResourceHandler WRH) {
		this.WRH = WRH;
	}
	
	public WorldResourceHandler get_wrh() {
		return this.WRH;
	}
	
	public void set_pos_bound(Vector pos, Rectangle bound) {
		if(pos !=null) this.pos.set(pos);
		if(bound!=null) this.bound.set(bound);
	}
	
	public boolean check_enabled() {
		return enabled;
	}
	
	protected void set_enable_flag(boolean flag) {
		enabled = flag;
	}
	
	public Rectangle get_view_rect() {
		this.viewRectangle.set(pos.x()+bound.x(),pos.y()+bound.y(),bound.w(),bound.h());
		return viewRectangle;
	}
	
	public int get_local_id() {return local_id;}
	public Rectangle get_bound() {return bound;}
	
	public void set_local_id(int local_id) {
		this.local_id = local_id;
	}
	
	/////////////children////////////////
	
	protected void init_children(byte size) {
		this.children = new WorldResourceObject[size];
	}
	
	protected WorldResourceObject get_child(int i) {
		return this.children[i];
	}
	
	public boolean is_child() {
		return this.parent != null;
	}
	
	public boolean is_parent() {
		for(int i = 0; i < children.length; i++) {
			if(this.children[i] != null) return true;
		}
		return false;
	}
	
	public WorldResourceObject get_parent() {
		return parent;
	}
}
