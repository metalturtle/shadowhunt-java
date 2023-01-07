package basic.resource;

import java.util.ArrayList;
import java.util.Iterator;

import basic.Rectangle;


public class WorldResArrayIterator<T extends WorldResourceObject> implements Iterator<T> {

	protected ArrayList <T> worldResourceObjects;
	protected Rectangle viewRectangle;
	protected int i=0;
	public boolean logs = false;
	
	protected WorldResArrayIterator(ArrayList<T> worldResArray,Rectangle viewRect)
	{
		this.worldResourceObjects = worldResArray;
		this.viewRectangle = viewRect;
	}
	
	public void init(Rectangle viewRect)
	{
		this.viewRectangle=viewRect;
		this.i=-1;
		find_next();
	}
	
	public void write_logs(String msg)
	{
		if(logs == true)
			System.out.println(msg);
	}
	
	@Override
	public boolean hasNext()
	{
		if(i >= worldResourceObjects.size()) {
			
			return false;
		}
			
		return true;
	}

	public void find_next()
	{
		i+=1;
		while(i<worldResourceObjects.size()
				&&
					(!worldResourceObjects.get(i).check_enabled()
					||
					!(viewRectangle==null||Rectangle.check_rect_intersection(viewRectangle,worldResourceObjects.get(i).get_view_rect()))
					)
				) 
		{
			i+=1;
		}
	}
	
	public T next() {
		T res = worldResourceObjects.get(i);
		find_next();
		return res;
	}
}


class WorldResourceArray extends ArrayList <WorldResourceObject>
{
	private static final long serialVersionUID = 1L;
}