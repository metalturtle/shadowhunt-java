package basic.resource.static_resource;

import java.util.ArrayList;
import java.util.Iterator;

import basic.Rectangle;


public class StaticWorldResIterator<T extends StaticWorldResourceObject> implements Iterator<T> {

	private StaticWorldResourceObject worldResArray[][][];
	Rectangle viewRectangle;
	int x,y,startx,starty,endx,endy;
	int i;
	StaticWorldResourceHandler WorldResHandle;
	public boolean logs = false;
	
	StaticWorldResIterator(StaticWorldResourceObject worldResArray[][][], StaticWorldResourceHandler<T> WorldResHandle)
	{
		this.worldResArray = worldResArray;
		this.viewRectangle = new Rectangle();
		this.WorldResHandle = WorldResHandle;
		
	}
	
	public void init(Rectangle viewRect)
	{
		this.viewRectangle=viewRect;
		short ids[] = WorldResHandle.get_cell_ids(viewRect);
		startx = ids[0];
		starty = ids[1];
		endx = ids[2];
		endy = ids[3];
		x = startx;
		y = starty;
		i=0;
		while( y <= endy && !WorldResHandle.check_valid_cell(x, y)) {
			if(x == endx) {
				x=startx;
				y+=1;
			} else {
				x +=1;
			}
		}
	}
	
	public void write_logs(String msg)
	{
		if(logs == true)
			System.out.println(msg);
	}
	
	@Override
	public boolean hasNext()
	{
		return !(y >= endy+1 && x == startx && i == 0);
	}
	

	void find_next() {
		do {
			if(i == WorldResHandle.get_max_id(x, y)-1) {
				do {
					if(x == endx) {
						x=startx;
						y+=1;
					} else {
						x +=1;
					}
				} while(y<= endy && !WorldResHandle.check_valid_cell(x, y));
				i=0;
			} else {
				i+=1;
			}
		} while(hasNext() && !Rectangle.check_rect_intersection(worldResArray[y][x][i], viewRectangle));
	}
	
	public T next() {
		T elem = (T)worldResArray[y][x][i];
		find_next();
		return elem;
	}
}
