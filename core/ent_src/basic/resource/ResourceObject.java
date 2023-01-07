package basic.resource;

import basic.Rectangle;
import basic.Vector;

public interface ResourceObject {
	public boolean check_enabled();
	public void set_local_id(int local_id);
	public Rectangle get_view_rect();
	public void set_pos_bound(Vector pos, Rectangle bound);
}
