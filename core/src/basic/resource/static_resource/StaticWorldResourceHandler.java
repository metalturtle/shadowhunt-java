package basic.resource.static_resource;

import java.util.ArrayList;
import java.util.Iterator;

import basic.Rectangle;
import basic.resource.WorldResourceInterface;


public abstract class StaticWorldResourceHandler<T extends StaticWorldResourceObject> {

	StaticWorldResIterator<T> worldResArrayIterator;
	protected short CELL_SIZE;
	int MAX_CELL_X,MAX_CELL_Y;
	protected StaticWorldResourceObject worldResArray[][][];
	protected int worldResLimit[][];
	protected Rectangle worldResBound[][];
	Rectangle worldBound;
	public StaticWorldResourceHandler()
	{
		CELL_SIZE = 120;
		worldBound = new Rectangle();
	}
	
	public void init(T arr[]) {
		float minx=0,miny=0,maxx=0,maxy=0;
		for(int i=0;i<arr.length;i++) {
			minx = Math.min(arr[i].x(),minx);
			miny = Math.min(arr[i].y(), miny);
			maxx = Math.max(arr[i].x()+arr[i].w(), maxx);
			maxy = Math.max(arr[i].y()+arr[i].h(), maxy);
		}
		worldBound.set(minx,miny,maxx-minx,maxy-miny);
		MAX_CELL_X = (int)maxx/CELL_SIZE+1;
		MAX_CELL_Y = (int)maxy/CELL_SIZE+1;
		worldResArray = new StaticWorldResourceObject[MAX_CELL_Y][MAX_CELL_X][];
		worldResBound = new Rectangle [MAX_CELL_Y][MAX_CELL_X];
		worldResLimit = new int[MAX_CELL_Y][MAX_CELL_X];
		for(int i=0;i<MAX_CELL_Y;i++) {
			for(int j = 0; j < MAX_CELL_X;j++) {
				worldResBound[i][j] = new Rectangle(j*CELL_SIZE,i*CELL_SIZE,CELL_SIZE,CELL_SIZE);
			}
		}
		
		for(int i = 0; i < arr.length; i++) {
			Rectangle rect = arr[i];
			short[] ids = get_cell_ids(rect);
			for(int p=ids[1];p<=ids[3];p++) {
				for(int q=ids[0];q<=ids[2];q++) {
					worldResLimit[p][q]+=1;
				}
			}
		}
		
		for(int i=0;i<MAX_CELL_Y;i++) {
			for(int j = 0; j < MAX_CELL_X;j++) {
				worldResArray[i][j] = new StaticWorldResourceObject[worldResLimit[i][j]];
				worldResLimit[i][j]=0;
			}
		}
		
		for(int i = 0; i < arr.length; i++) {
			T rect = arr[i];
			short[] ids = get_cell_ids(rect);
			StaticWorldResourceObject split_res_arr[][] = new StaticWorldResourceObject[ids[3]-ids[1]+1][ids[2]-ids[0]+1];
			Rectangle bound_arr[][] = new Rectangle[ids[3]-ids[1]+1][ids[2]-ids[0]+1];
			for(int p=ids[1];p<=ids[3];p++) {
				for(int q=ids[0];q<=ids[2];q++) {
					Rectangle bound = worldResBound[p][q];
					T split_res = create_new_element();
//					split_res.set(Rectangle.intersection(rect, bound));
					Rectangle.intersection(rect, bound, split_res);
					split_res_arr[p-ids[1]][q-ids[0]] = split_res;
					bound_arr[p-ids[1]][q-ids[0]] = bound;
//					split_static_object(rect,split_res,bound,ids[2]-ids[0]+1,ids[3]-ids[1]+1,q-ids[0],p-ids[1]);
					worldResArray[p][q][worldResLimit[p][q]] = split_res;
					worldResLimit[p][q]+=1;
				}
			}
			split_static_objects(rect,split_res_arr,bound_arr,ids[2]-ids[0]+1,ids[3]-ids[1]+1);
		}
		worldResArrayIterator = new StaticWorldResIterator<T>(worldResArray,this);
	}
	
	public int get_max_id(int x,int y) {
//		System.out.println("xy: "+x+" "+y);
		if(x < 0 || x >= MAX_CELL_X || y < 0 || y >= MAX_CELL_Y)
			return 0;
		return worldResLimit[y][x];
	}
	
	public boolean check_valid_cell(int x, int y) {
		if(x<0 || y < 0 || x>=MAX_CELL_X || y >= MAX_CELL_Y || get_max_id(x,y) == 0) {
			return false;
		}
		return true;
	}
	
	public abstract void split_static_objects(StaticWorldResourceObject res,StaticWorldResourceObject split_res[][],Rectangle cell_bound[][],int size_x,int size_y) ;
	
	short[] get_cell_id(int x,int y) {
		return new short[] {(short)(x/CELL_SIZE),(short)(y/CELL_SIZE)};
	}
	
	short[] get_cell_ids(Rectangle bound) {
		short first_id[] = get_cell_id((int)bound.x(),(int)bound.y());
		short last_id[] = get_cell_id((int)(bound.x()+bound.w()),(int)(bound.y()+bound.h()));
		return new short[] {first_id[0],first_id[1],last_id[0],last_id[1]};
	}
	
	public abstract T create_new_element();
	
	public	Iterator<T> get_iterator(Rectangle rect) 
	{
		if(rect == null)
			worldResArrayIterator.init(worldBound);
		else
			worldResArrayIterator.init(rect);
		return worldResArrayIterator;
	}
	
	protected void set_cell_size(short SIZE) {
//		this.CELL_SIZE = SIZE;
	}
}
