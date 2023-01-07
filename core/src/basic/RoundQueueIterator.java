package basic;

import java.util.ArrayList;
import java.util.Iterator;

public class RoundQueueIterator<T> implements Iterator<T>
{

	int id_curr,i;
	RoundQueue<T> rQueue;
	ArrayList<T> data;
	boolean reverse = false;
	int size;
	RoundQueueIterator(RoundQueue<T> rQueue,boolean reverse)
	{
		this.rQueue = rQueue;
//		data = this.rQueue.data;
		size = this.rQueue.size;
		init(reverse);
	}
	
	public void init(boolean reverse)
	{
		this.reverse = reverse;
		i=0;
		if(reverse == false)
		{
			this.id_curr =rQueue.get_start_id();
		}
		else
		{
			this.id_curr = rQueue.get_end_id();
		}
	}
	
	
	@Override
	public boolean hasNext() {
		return this.id_curr != -1 && i < rQueue.active_size;
	}

	int get_next_id(int id)
	{
		return (id+1)%size;
	}
	
	int get_prev_id(int id)
	{
		return (id+size-1)%size;
	}
	
	T get_data(int id) {
		return data.get(id);
	}
	
//	@Override
//	public T next()  {
//		if(!hasNext())
//			return null;
//		T elem = get_data(id_curr);
//		if(reverse)
//			id_curr = get_prev_id(id_curr);
//		else
//			id_curr = get_next_id(id_curr);
//		i+=1;
//		return elem;
//	}
	
	
	@Override
	public T next()  {
		if(!hasNext())
			return null;
		T elem = rQueue.get_element(id_curr);
		if(reverse)
			id_curr = rQueue.get_prev_id(id_curr);
		else
			id_curr = rQueue.get_next_id(id_curr);
		i+=1;
		return elem;
	}
}
