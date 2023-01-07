package basic.resource;

import basic.Rectangle;
import basic.Vector;

public interface ResourceObject {
	public boolean check_enabled();
//	protected abstract void enable();
//	protected abstract void disable();
//	public int get_local_id();
	public void set_local_id(int local_id);
	public Rectangle get_view_rect();
	public void set_pos_bound(Vector pos, Rectangle bound);
}
