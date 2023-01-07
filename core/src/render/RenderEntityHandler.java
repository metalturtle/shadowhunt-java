package render;

import java.util.Iterator;

import basic.Rectangle;
import basic.Vector;
import basic.resource.WorldResourceHandler;
import render.basic.RenderEntity;
import resource.TextureIDManager;
import util.ConfigManager;

public abstract class RenderEntityHandler<T extends RenderEntity> extends WorldResourceHandler <T>
{
	String name;
	
	protected RenderEntityMainHandler RendEntMainHandle;

	protected RenderEntityIterator<T> iterator;
	
	protected RenderEntityHandler(RenderEntityMainHandler RendEntityMainHandle) {
		this.RendEntMainHandle = RendEntityMainHandle;
//		this.TexIDManage = RendEntMainHandle.TexIDManage;
		iterator = new RenderEntityIterator<T>(this,this.worldResArray,null);
	}
	
	public abstract void renderer_init(ConfigManager ConfigManage);
	
	public T get_render_entity(int re_id) {
		return get_resource_object(re_id);
	}
	
	@Override
	public	Iterator<T> get_iterator(Rectangle rect) 
	{
		iterator.init(rect);
		return iterator;
	}
	
	@Override
	protected void reassign_resource_object_id(T obj, int new_id) {
		
	}
	
	@Override
	public void cleanup() {
		for(Iterator <T> it = get_iterator(null); it.hasNext();) {
			T ent = it.next();
			if(!ent.check_alive()) {
//				ent.mark_disable();
				disable_resource_object(ent);
		 	}
		}
		super.cleanup();
	}
	
	public abstract void dispose();
	
}
