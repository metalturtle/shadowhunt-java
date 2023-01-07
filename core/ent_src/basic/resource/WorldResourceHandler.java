package basic.resource;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import basic.Rectangle;
import basic.Vector;
import render.LightHandler;
import render.RenderInfo;


public abstract class WorldResourceHandler<T extends WorldResourceObject> extends WorldResourceInterface<ArrayList<WorldResourceObject>,T> {

	protected ArrayList <T> worldResArray;
	private ArrayList <T> secondWRA;
	
	WorldResArrayIterator<T> worldResArrayIterator;
	int ID_LOCAL_ASSIGN,active_size,minimum_size;
	protected int WRH_ID;
	static int LAST_WRH_ID;
	static boolean MARK_DISABLE_FLAG;
	
	int current_cleanup_tick,max_count_current_cycle;
	final int max_cleanup_tick;
	
	public WorldResourceHandler() {
		this.WRH_ID = LAST_WRH_ID;
		LAST_WRH_ID+=1;
		worldResArray = new ArrayList <T>();
		worldResArrayIterator = new WorldResArrayIterator<T>(worldResArray,new Rectangle(0,0,0,0));
		max_cleanup_tick = RenderInfo.TARGET_FPS*30;
		LAST_WRH_ID+=1;
	}
	
	private boolean check_free_resource() {
		for(int i = 0; i <worldResArray.size(); i++) {
			if(!worldResArray.get(i).check_enabled())
				return true;
		}
		return false;
	}
	
	private int get_free_id() {
		if(check_free_resource()) {
			for(int i = 0; i <worldResArray.size(); i++) {
				if(!worldResArray.get(i).check_enabled())
					return i;
			}
		}
		return -1;	
	}
	
	@Override
	public T get_resource_object(int local_id) {
		if(local_id<0 || local_id>worldResArray.size())
			return null;
		T obj = worldResArray.get(local_id);
		if(!obj.check_enabled())
			return null;
		return worldResArray.get(local_id);
	}

	@Override
	public T add_resource_object(Vector pos,Rectangle bound, String params) {
		if(check_free_resource()) {
			T elem = worldResArray.get(get_free_id());
			elem.init(pos,bound,params);
			active_size+=1;
			return elem;
		}
		T elem = create_new_element();
		
		
		elem.set_wrh(this);
		elem.set_local_id(worldResArray.size());
		this.worldResArray.add(elem);
		elem.init(pos,bound,params);
		active_size+=1;
		return elem;
	}
	
	
	@Override
	protected void disable_resource_object(int local_id) {
		T obj = this.worldResArray.get(local_id);
		disable_resource_object(obj);
	}
	
	@Override
	protected void disable_resource_object(T obj) {
		obj.disable();
		compact_array(obj);
	}
	
	
	@Override
	public	Iterator<T> get_iterator(Rectangle rect) {
		worldResArrayIterator.init(rect);
		return worldResArrayIterator;
	}
	
	public void disable_all_objects() {
		for(Iterator<T> it = get_iterator(null); it.hasNext();) {
			T ent = it.next();
			disable_resource_object(ent);
		}
	}
	
	public int get_size() {return worldResArray.size();}
	public int get_active_count() {
		return active_size;
	}
	
	protected void compact_array(T del_obj) {
		active_size-=1;
	}
	

	////////////////////////cleanup//////////////////////////////
	
	public void set_minimum_size(int minsize) {
		this.minimum_size = minsize;
	}
	
	abstract protected void reassign_resource_object_id(T obj, int new_id);
	
	private void resize_world_resource_array() {
		secondWRA = new ArrayList<T>();
		
		for(Iterator <T>it = get_iterator(null);it.hasNext();) {
			T obj = it.next();
			secondWRA.add(obj);
		}
		
		if(worldResArray.size() >= minimum_size
				&& secondWRA.size() < minimum_size
				) {
			for(int i = 0; i < worldResArray.size();i++) {
				T obj = worldResArray.get(i);
				if(!obj.check_enabled()) {
					secondWRA.add(obj);
				}
			}
		}

		for(int i = 0; i < secondWRA.size(); i++ ) {
			secondWRA.get(i).set_local_id(i);
		}
		worldResArray = secondWRA;
		
		secondWRA = null;
	}
	
	public void cleanup() {
//		disable_marked_entities();
		if(current_cleanup_tick > max_cleanup_tick) {
			current_cleanup_tick=0;
			if(max_count_current_cycle< worldResArray.size()) {
//				resize_world_resource_array();
			}
			max_count_current_cycle=0;
		}
		current_cleanup_tick+=1;
		max_count_current_cycle = Math.max(max_count_current_cycle,get_active_count());
	}
}
