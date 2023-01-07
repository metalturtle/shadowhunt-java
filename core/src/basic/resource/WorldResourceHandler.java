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
//	private LinkedList<T> markedDisabledList;
//	private LinkedList <Integer>disabledList;
	
	
	WorldResArrayIterator<T> worldResArrayIterator;
//	MarkedDisabledIterator<T> disabledIterator;
	int ID_LOCAL_ASSIGN,active_size,minimum_size;
	protected int WRH_ID;
	static int LAST_WRH_ID;
	static boolean MARK_DISABLE_FLAG;
	
	int current_cleanup_tick,max_count_current_cycle;
	final int max_cleanup_tick;
//	protected int DISABLED_COUNT;
	
	public WorldResourceHandler() {
		this.WRH_ID = LAST_WRH_ID;
		LAST_WRH_ID+=1;
//		disabledList = new LinkedList<Integer>();
		worldResArray = new ArrayList <T>();
//		markedDisabledList = new LinkedList<T>();
		worldResArrayIterator = new WorldResArrayIterator<T>(worldResArray,new Rectangle(0,0,0,0));
//		disabledIterator = new MarkedDisabledIterator<T>(worldResArray);
		max_cleanup_tick = RenderInfo.TARGET_FPS*30;
		LAST_WRH_ID+=1;
	}
	
	private boolean check_free_resource() {
//		return DISABLED_COUNT>0;
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
			elem.set_enable_flag(true);
			elem.set_pos_bound(pos, bound);
			active_size+=1;
//			DISABLED_COUNT-=1;
			return elem;
		}
		T elem = create_new_element();
		
		elem.set_enable_flag(true);
		elem.set_wrh(this);
		elem.set_local_id(worldResArray.size());
		elem.set_pos_bound(pos, bound);
		this.worldResArray.add(elem);
		active_size+=1;
//		DISABLED_COUNT-=1;
		return elem;
	}
	
	
	@Override
	protected void disable_resource_object(int local_id) {
		T obj = this.worldResArray.get(local_id);
		disable_resource_object(obj);
	}
	
	@Override
	protected void disable_resource_object(T obj) {
		disable_children(obj);
		if(obj.parent != null) {
			obj.parent.children[obj.childId] = null;
			obj.parent = null;
			obj.childId = -1;
		}
		obj.set_enable_flag(false);
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
//		DISABLED_COUNT+=1;
		active_size-=1;
	}
	
	////////////////// children///////////////////////////
	
	protected void init_children(T obj, byte size) {
		obj.init_children(size);
	}
	
	protected WorldResourceObject get_child(T obj,int i) {
		return obj.get_child(i);
	}
	
	protected WorldResourceObject set_child(T obj,int i, WorldResourceHandler WRH, Vector pos, Rectangle bound, String params) {
		WorldResourceObject child = WRH.add_resource_object(pos, bound, params);
//		obj.set_child(i,child);
		obj.children[i] = child;
		child.parent = obj;
		child.childId = i;
		return child;
	}
	
	protected void disable_child(T obj, int i) {
//		obj.disable_child(i);
//		System.out.println("disabling child: "+obj+" "+obj.get_child(i));
		obj.get_child(i).WRH.disable_resource_object(obj.get_child(i));
	}
	
	protected void disable_children(T obj) {
		for(int i = 0; i < obj.children.length; i++) {
			if(obj.children[i] != null) {
				disable_child(obj,i);
			}
		}
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
//				&& ((worldResArray.size() - secondWRA.size()) > (minimum_size-secondWRA.size()))
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
