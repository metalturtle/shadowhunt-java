package handlers.basic;

import basic.Rectangle;
import basic.Vector;

public class TeamObject {
	String name;
	int team_id;
	public Rectangle spawn_bound;
	int count;
	
	public TeamObject() {
		name = "";
		team_id = -1;
		spawn_bound = new Rectangle(0,0,0,0);
	}
	
	public TeamObject(int id, String name, Rectangle spawn_bound) {
		this.team_id = id;
		this.name = name;
		this.spawn_bound = new Rectangle(spawn_bound);
	}
	
	public void set_name(String name) {
		this.name = name;
	}
	
	public void set_team_id (int team_id) {
		this.team_id = team_id;
	}
	
	public Vector get_spawn_point () {
		return new Vector (spawn_bound.x(),spawn_bound.y());
	}
	
	public void add_team_player() {this.count+=1;}
	public void remove_team_player() {this.count = this.count-1;}
	public int get_team_count() {return this.count;}
}
