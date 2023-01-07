package basic;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Iterator;


public class RoundQueue<T> 
{
	public T data[];
	int id_start,id_end,size,active_size;
	boolean overwrite;
	RoundQueueIterator<T> iterator;
	public RoundQueue(T[] data, boolean overwrite)
	{
//		this.size = size;
		this.id_start=-1;
		this.id_end = -1;
		this.overwrite = overwrite;
//		data = new ArrayList <T>(size);
		set_data(data);
	
		iterator = new RoundQueueIterator<T>(this,true);
	}
	
	public int get_start_id() {return id_start;}
	public int get_end_id() {return id_end;}
	
	public void set_data(T[] data)
	{
		this.data = data;
//		iterator.data = data;
		this.size = data.length;
//		iterator.size = size;
	}
	
	public T get_first_element() 
	{
		if(id_start==id_end && id_start == -1)
			return null;
		return data[id_start];
	}
	
	public T get_last_element() 
	{
		if(id_start==id_end && id_start == -1)
			return null;
		return data[id_end];
	}
	
	public int get_next_id(int id)
	{
		return (id+1)%size;
	}
	
	public int get_prev_id(int id)
	{
		return (id+size-1)%size;
	}
	
	boolean check_last_id(int id)
	{
		if(id == get_end_id())
			return true;
		return false;
	}
	
	public T get_element(int id)
	{
		if(id_start == id_end && id_end ==-1)
			return null;
		return data[id];
	}
	
	public boolean is_empty() {
		if(id_start==id_end && id_start == -1)
			return true;
		else return false;
	}
	
	public boolean add_element(T obj)
	{
		if(id_start == id_end && id_end == -1)
		{
			id_start = id_end = 0;
			data[id_start]=obj;
			active_size+=1;
			return true;
		}
		
		int id_next = get_next_id(id_end);
		if(id_next==id_start)
		{
			if(overwrite==false)
				return false;
			id_start=get_next_id(id_start);
		} else {
			active_size+=1;
		}
		data[id_next]=obj;
		id_end = id_next;
		return true;
	}
	
	public T add_mset_element()
	{
		if(id_start == id_end && id_end == -1)
		{
			id_start = id_end = 0;
			active_size+=1;
//			data.get(id_start).set(obj);
			return data[id_start];
		}
		
		int id_next = get_next_id(id_end);
		if(id_next==id_start)
		{
			if(overwrite==false)
			{
				return null;
			}
			id_start=get_next_id(id_start);
		} else {
			active_size+=1;
		}
//		data.get(id_next).set(obj);
		id_end = id_next;
		return data[id_next];
	}
	
	public T remove_first_element()
	{
		int cur_id=0;
		if(id_start == id_end)
		{
			if(id_end == -1)
				return null;
			cur_id = id_start;
			id_start = id_end = -1;
			active_size=0;
			return data[cur_id];
		}
		cur_id = id_start;
		id_start = get_next_id(id_start);
		active_size-=1;
		return data[cur_id];
	}
	
	public T remove_last_element()
	{
		int cur_id=0;
		if(id_start == id_end)
		{
			if(id_end == -1)
				return null;
			cur_id = id_start;
			id_start = id_end = -1;
			active_size=0;
			return data[cur_id];
		}
		cur_id = id_end;
		id_end = get_prev_id(id_end);
		active_size-=1;
		return data[cur_id];
	}
	
	
	public void clear() {
		this.id_start = this.id_end = -1;
		this.active_size=0;
	}
	public int size() {
		return active_size;
	}
	
	public Iterator<T> get_iterator(boolean reverse)
	{
		iterator.init(reverse);
		return iterator;
	}
}
