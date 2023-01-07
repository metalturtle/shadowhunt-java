package basic.resource;

import java.util.Iterator;

import basic.Rectangle;
import basic.Vector;

public abstract class WorldResourceInterface<T,K extends ResourceObject> {
	T worldResourceObjects;
	public abstract K create_new_element();
	public abstract K get_resource_object(int id);
	protected abstract K add_resource_object(Vector pos,Rectangle bound,String params);
	protected abstract void disable_resource_object(int id);
	protected abstract void disable_resource_object(K k);
	public abstract Iterator<K> get_iterator(Rectangle view_rect);
}
