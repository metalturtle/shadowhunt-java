package basic;

import java.util.ArrayList;

import handlers.basic.packets.EntityState;
import render.RenderInfo;

class ObjectPoolItem<T> {
	T item;
//	int next_free;
}

public class ObjectPooling<T> {

	ArrayList<ObjectPoolItem<T>> data = new ArrayList<ObjectPoolItem<T>>();
	ObjectPoolCreator<T> creator;
	int used=0;
	int maxused;
	int max_tick,current_tick;
	
	public ObjectPooling(ObjectPoolCreator creator,int seconds_tick) {
		this.creator = creator;
		max_tick = RenderInfo.TARGET_FPS*seconds_tick;
	}
	
	
	public T get_resource() {
		if(data.size() <= used)  {
//			System.out.println("Creating resource");
			T obj = creator.create_object();
			ObjectPoolItem poolItem = new ObjectPoolItem();
			poolItem.item = obj;
			data.add(poolItem);
			used+=1;
			return obj;
		}
		used+=1;
		return data.get(used-1).item;
	}
	
	public void release() {
		maxused = Math.max(used, maxused);
		used=0;
		current_tick+=1;
	}
	
	public void reduce_size() {
		if(current_tick >= max_tick) {
			current_tick =0;
			int max_items = maxused;
			int i = data.size();
			while(i > max_items) {
				data.remove(data.size()-1);
				i-=1;
			}
			maxused=0;
		}
	}
}
