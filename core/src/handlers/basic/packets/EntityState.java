package handlers.basic.packets;

import basic.Vector;
import handlers.basic.entity.Entity;

public class EntityState {
	public int actid;
//	public int sequence;
//	public float x,y,angle;
	public Vector pos = new Vector();
	public float angle;
	public byte state;
	public boolean delta;
	public boolean required_fields[];
//	public byte info[];
	boolean active;
	int search_sequence;
	long timestamp;
//	public Object obj[][];
	public int state_send_ids[];

	public long get_timestamp() {return timestamp;}
//	public long get_lagcomp() {return lagcomp;}
//	
	public void set_timestamp(long timestamp) {this.timestamp = timestamp;}
//	public void set_lagcomp(long lagcomp) {this.lagcomp = lagcomp;}
	
	public void set_search_sequence(int sequence) {this.search_sequence = sequence;}
	public int get_search_sequence() {return this.search_sequence;}
	
	public void reset_required_fields() {
		for(int i = 0; i < required_fields.length;i++) {
			required_fields[i]=false;
		}
	}
}
