package world;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import basic.Vector;
import basic.resource.WorldResourceHandler;
import basic.resource.WorldResourceObject;
import basic.resource.static_resource.StaticWorldResourceHandler;
import basic.resource.static_resource.StaticWorldResourceObject;
import world.basic.Wall;
import basic.Rectangle;


public class World extends StaticWorldResourceHandler<Wall>
{
	
	public World()
	{
		super();
	}

	@Override
	public void split_static_objects(StaticWorldResourceObject res, StaticWorldResourceObject[][] split_res, Rectangle[][] cell_bound, int size_x, int size_y) {
		
		Wall reswall = (Wall)res;
		for(int i = 0; i < size_y; i++) {
			for(int j = 0; j < size_x;j++) {
				Wall wall = (Wall)split_res[i][j];
				wall.set_ray_block(reswall.get_ray_block());
			}
		}
	}

	int count=0;
	@Override
	public Wall create_new_element() {
		count+=1;
//		System.out.println("creating wall: "+count);
		return new Wall();
	}
	
}
