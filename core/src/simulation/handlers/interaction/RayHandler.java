package simulation.handlers.interaction;
//package handlers;
//
//import java.util.ArrayList;
//import java.util.Iterator;
//
//import basic.Rectangle;
//import basic.Vector;
//import game.entity.GameActor;
//import handlers.basic.EntityUpdate;
//import handlers.basic.entity.Entity;
//import world.World;
//import world.basic.Wall;
//import handlers.basic.EntityMovementObject;
//
//public class RayHandler
//{
//
////	ArrayList <EntityMovementObject> moveObjects;
//	EntityCollisionHandler ColHandle;
//	ArrayList <RayObject> rays;
////	GameActorHandler ActHandler;
//	World GWorld;
//	
//	RayHandler(World world,EntityCollisionHandler colhandler)
//	{
//		rays = new ArrayList <RayObject>();
//		this.GWorld=world;
//		this.ColHandle = colhandler;
//	}
//	
//	int add_ray() 
//	{
//		rays.add(new RayObject());
//		return rays.size()-1;
//	}
//	
//	void set_ray(Vector pos,Vector dir,int rayid,int boxid,long lagcomptime) 
//	{	
//		RayObject ray = rays.get(rayid);
//		ray.pos.set(pos);
//		ray.dir.set(dir);
//		ray.boxid = boxid;
//		ray.lagcomptime = lagcomptime;
//	}
//	void shoot_ray(int rayid)
//	{
//		rays.get(rayid).flag = true;
//	}
//	
//	Vector get_ray_pos(int rayid)
//	{
//		//return ray_pos.get(rayid);
//		return rays.get(rayid).pos;
//	}
//	
//	Vector get_ray_dir(int rayid)
//	{
//		return rays.get(rayid).dir;
//	}
//	Vector get_ray_hit_pos(int rayid)
//	{
//		RayObject ray = rays.get(rayid);
//		float u = ray.hit_u;
//		Vector hit_pos =new Vector(0,0);
//		hit_pos.set(ray.pos);
//		Vector dir = new Vector(0,0);
//		dir.set(ray.dir);
//		dir.multiply(Math.min(u,1));
//		hit_pos.add(dir);
//		return hit_pos;
//	}
//	
//	int get_hit_id (int rayid)
//	{
//		int rid = rays.get(rayid).hitid;
//		return rid;
//	}
//	
////	int get_hit_type(int rayid)
////	{
////		return rays.get(rayid).hittype;
////	}
//	
//	Entity get_hit_entity(int rayid)
//	{
//		return rays.get(rayid).hit_entity;
//	}
//	
//	void reset_hitid()
//	{
//		for(int i=0;i<rays.size();i++)
//		{
//			rays.get(i).hitid = -1;
//			rays.get(i).hit_entity = null;
//			
//		}
//	}
//	
//	
//	
//	static float ray_intersect(Vector pos,Vector dir,Vector p1,Vector p2) {
//		
//		float x = 0,y = 0,u=999,t = 999;
//		Vector p[]  = new Vector [2];
//		p[0] = p1;
//		p[1] = p2;
//		float r_cr_x =p[1].x() * dir.y() - p[1].y() * dir.x();
//		if(r_cr_x != 0)
//		{
//			u = ( pos.x() - p[0].x() )*p[1].y() - ( pos.y() - p[0].y() )*p[1].x();
//			t = ( pos.x() - p[0].x() )*dir.y() - ( pos.y() - p[0].y() )*dir.x();
//			u = u/r_cr_x;
//			t = t/r_cr_x;
//			if ( (0 <= u && u <= 1) && (0 <= t && t <= 1) )
//			{
//			}
//			else {
//				u = 999;
//			}
//		
//		}
//		return u;
//	}
//	
//	public static float check_intersection(Vector pos,Vector dir,Rectangle wall) 
//	{ 
//		Vector p[] = new Vector [4];
//		float u = 999;
//        if(pos.x() < wall.x())
//		{
//			p[0] = new Vector(wall.x(),wall.y());
//			p[1] = new Vector(0,wall.h());
//			u = Math.min(ray_intersect(pos,dir,p[0],p[1]),u);
//		}
//		else if(pos.x() > wall.x() + wall.w())
//		{
//			p[0] = new Vector(wall.x()+wall.w(),wall.y());
//			p[1] = new Vector(0,wall.h());
//			u = Math.min(ray_intersect(pos,dir,p[0],p[1]),u);
//		}
//		if(pos.y() < wall.y())
//		{
//			p[0] = new Vector(wall.x(),wall.y());
//			p[1] = new Vector(wall.w(),0);
//			u = Math.min(ray_intersect(pos,dir,p[0],p[1]),u);
//		}
//		else if (pos.y() > wall.y()+wall.h())
//		{
//			p[0] = new Vector(wall.x(),wall.y()+wall.h());
//			p[1] = new Vector(wall.w(),0);
//			u = Math.min(ray_intersect(pos,dir,p[0],p[1]),u);
//		}
//		return u;	
//	}
//	
////	public static float check_room_intersection(Vector pos,Vector dir,Rectangle room) {
////		Vector p[] = new Vector[4];
////		for(int i=0;i<4;i++)
////			p[i] = new Vector(0,0);
////		int k=0;
////		boolean right = dir.x()>0?true:false;
////		boolean up = dir.y()>0?true:false;
////		boolean left = !right;
////		boolean down = !up;
////		if(right) 
////		{
////			p[k].set(room.x()+room.w(),room.y());
////			p[k+1].set(0,room.h());
////			k+=2;
////		}
////		if(up)
////		{
////			p[k].set(room.x(),room.y()+room.h());
////			p[k+1].set(room.w(), 0);
////			k+=2;
////		}
////		if(left)
////		{
////			p[k].set(room.x(),room.y());
////			p[k+1].set(0,room.h());
////			k+=2;
////		}
////		if(down)
////		{
////			p[k].set(room.x(),room.y());
////			p[k+1].set(room.w(), 0);
////			k+=2;
////		}
////		float u = ray_intersect(pos,dir,p[0],p[1]);
////		if(u == 999 && k>2)
////			u = ray_intersect(pos,dir,p[2],p[3]);
////		return u;
////	}
//	
//	void handle_ray(RayObject ray)
//	{
////		RayObject ray = rays.get(ray_id);
//		Vector rpos = ray.pos;
//		Vector rdir = ray.dir;
//		boolean rflag = ray.flag;
//		float u = 999, temp;
//		int hitid,hittype; 
////		Entity act = new GameActor();
////		Entity old_act = new GameActor();
//		Entity hitEnt=null;
//		Rectangle hit_rect = new Rectangle(0,0,0,0);
////		if(rflag == true) 
//		{
//
//			for(Iterator <Wall> iterator = GWorld.get_iterator(null);iterator.hasNext();)
//			{
//				Wall wall = iterator.next();
//				temp = check_intersection(rpos,rdir,wall.get_view_rect());
//				if(u>temp)
//				{
//					u = temp;
//				}					
//			}
//
//			hitid = -1;
//			hittype = -1;
//
////			for(Iterator <GameActor>iterator = ActHandler.masterGameState.get_iterator(null);iterator.hasNext();)
//			for(Iterator<EntityMovementObject> iterator = ColHandle.get_iterator(null);iterator.hasNext();)
//			{
//				EntityMovementObject moveObj = iterator.next();
////				act = move_obj.my_entity;
////				if(act.get_collision_id() == ray.entid || act.get_collision_id() == -1)
////					continue;
//				
////				EntityUpdate entupdate = act.predict_entity(ray.lagcomptime)[2];
////				if(entupdate == null)	
////				{
////					continue;
////				}
////				old_act = act;
//				Rectangle box= moveObj.bound;
//				hit_rect.set(moveObj.pos.x()+box.x(), moveObj.pos.y()+box.y(), box.w(), box.h());
//				float utest = check_intersection(rpos,rdir,hit_rect);
//				if(utest != 999 && utest <u)
//				{
//					
//					u = utest;
//					hitEnt = moveObj.my_entity;
////					hitid = act.get_local_id();
//					//hittype = act.get_type();
//				}					
//			}
//			ray.hit_u = u;
//			ray.hitid = hitid;
//			ray.hit_entity = hitEnt;
//		}
//		ray.flag = false;
//	}
//}