//package basic.resource;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//
//import basic.Rectangle;
//
//public class MarkedDisabledIterator<T extends WorldResourceObject> implements Iterator<T> {
//	protected ArrayList <T> worldResourceObjects;
//	protected int i=0;
//	public boolean logs = false;
//	
//	protected MarkedDisabledIterator(ArrayList<T> worldResArray)
//	{
//		this.worldResourceObjects = worldResArray;
//	}
//	
//	public void init()
//	{
//		this.i=-1;
//		find_next();
//	}
//	
//	public void write_logs(String msg)
//	{
//		if(logs == true)
//			System.out.println(msg);
//	}
//	
//	@Override
//	public boolean hasNext()
//	{
//		if(i >= worldResourceObjects.size())
//			return false;
//		return true;
//	}
//
//	public void find_next() {
//		i+=1;
//		while(i<worldResourceObjects.size() && !(worldResourceObjects.get(i).is_marked_disable())) {
//			i+=1;
//		}
//	}
//	
//	public T next() {
//		T res = worldResourceObjects.get(i);
//		find_next();
//		return res;
//	}
//}
