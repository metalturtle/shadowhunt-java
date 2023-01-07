package handlers.basic;

import java.util.Iterator;

import basic.Rectangle;
import basic.Vector;
import basic.resource.WorldResourceObject;
import handlers.basic.entity.Entity;
import simulation.handlers.interaction.EntityCollisionHandler;
import world.World;
import world.basic.Wall;

public class GameRay
{
	public Vector pos = new Vector(0,0);
	public Vector dir= new Vector(0,0);
	public Vector hit_pos= new Vector(0,0);
	float hit_u;
	long lagcomptime;
	boolean hitFlag;
	public WorldResourceObject hitEntity;
//	static Vector normal = new Vector(0,0);
	static float staticNormalAngle;
	public float normalAngle;
	static Vector stat_p[];
	static {
		stat_p = new Vector[] {new Vector(),new Vector(),new Vector(),new Vector()};
	}
	
	public GameRay()
	{
		set(new Vector(0,0),new Vector(0,0));
		
	}
	GameRay(Vector pos,Vector dir) {
		set(pos,dir);
	}
	void set(Vector pos,Vector dir)
	{
		this.pos.set(pos);
		this.dir.set(dir);
		this.hit_u = 999f;
	}
	
static float ray_intersect(Vector pos,Vector dir,Vector p1,Vector p2) {
		
		float x = 0,y = 0,u=999,t = 999;
		Vector p[]  = new Vector [2];
		p[0] = p1;
		p[1] = p2;
		float r_cr_x =p[1].x() * dir.y() - p[1].y() * dir.x();
		if(r_cr_x != 0)
		{
			u = ( pos.x() - p[0].x() )*p[1].y() - ( pos.y() - p[0].y() )*p[1].x();
			t = ( pos.x() - p[0].x() )*dir.y() - ( pos.y() - p[0].y() )*dir.x();
			u = u/r_cr_x;
			t = t/r_cr_x;
			if ( (0 <= u && u <= 1) && (0 <= t && t <= 1) )
			{
			}
			else {
				u = 999;
			}
		
		}
		return u;
	}

	public static float check_intersection(Vector pos,Vector dir,Rectangle wall) 
	{ 
		float u = 999;
        if(pos.x() < wall.x())
		{
        	stat_p[0].set(wall.x(),wall.y());
        	stat_p[1].set(0,wall.h());
			u = Math.min(ray_intersect(pos,dir,stat_p[0],stat_p[1]),u);
//			normal.set(-1,0);
			staticNormalAngle=180;
		}
		else if(pos.x() > wall.x() + wall.w())
		{
			stat_p[0].set(wall.x()+wall.w(),wall.y());
			stat_p[1].set(0,wall.h());
			u = Math.min(ray_intersect(pos,dir,stat_p[0],stat_p[1]),u);
//			normal.set(1,0);.
			staticNormalAngle=0;
		}
		if(pos.y() < wall.y())
		{
			stat_p[0].set(wall.x(),wall.y());
			stat_p[1].set(wall.w(),0);
			u = Math.min(ray_intersect(pos,dir,stat_p[0],stat_p[1]),u);
//			normal.set(0,-1);.
			staticNormalAngle=-90;
		}
		else if (pos.y() > wall.y()+wall.h())
		{
			stat_p[0].set(wall.x(),wall.y()+wall.h());
			stat_p[1].set(wall.w(),0);
			u = Math.min(ray_intersect(pos,dir,stat_p[0],stat_p[1]),u);
//			normal.set(0,1);.
			staticNormalAngle=90;
		}
		return u;	
	}
	
	Vector final_hit=new Vector();
	Rectangle bound = new Rectangle();
	public void handle_ray(EntityCollisionHandler EntColHandle,World GWorld)
	{
		Vector rpos = pos;
		Vector rdir = dir;
		float u = 999, temp;
		Rectangle hit_rect = new Rectangle(0,0,0,0);
		
		hitFlag = false;
		hitEntity=null;
		
		final_hit.set(dir);
//		final_hit.add(rpos);
		bound.set(rpos.x(),rpos.y(),rdir.x(),rdir.y());
		
		for(Iterator <Wall> iterator = GWorld.get_iterator(bound);iterator.hasNext();)
		{
			Wall wall = iterator.next();
			if(!wall.get_ray_block())
				continue;
			temp = check_intersection(rpos,rdir,wall.get_view_rect());
			hitFlag = true;
			if(u>temp)
			{
				u = temp;
				normalAngle = staticNormalAngle;
			}
		}

		
		for(Iterator<EntityMovementObject> iterator = EntColHandle.get_iterator(bound);iterator.hasNext();) {
			EntityMovementObject moveObj = iterator.next();
			Rectangle box= moveObj.bound;
			hit_rect.set(moveObj.pos.x()+box.x(), moveObj.pos.y()+box.y(), box.w(), box.h());
			float utest = check_intersection(rpos,rdir,hit_rect);
			if(utest != 999 && utest <u) {
				u = utest;
				hitEntity = moveObj.get_parent();
				hitFlag = true;
				normalAngle = staticNormalAngle;
			}					
		}
		hit_u = u;
		hit_pos.set(dir);
		hit_pos.multiply(u);
		hit_pos.add(pos);
	}
}