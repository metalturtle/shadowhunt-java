package simulation;

import java.util.LinkedList;

import simulation.RequiredUpdateList.RequiredUpdate;

public class RequiredUpdateList {
	
	public int LAST_SEND_ID[];
	
	public LinkedList<RequiredUpdate> reqUpdate[];
	
	public class RequiredUpdate {
		int send_id;
		int sequence;
		Object updObj;
		public int get_send_id() {return send_id;}
		public int get_sequence() {return this.sequence;}
		public Object get_update_object() {return this.updObj;}
	}
	
	public RequiredUpdateList(int size) {
		LAST_SEND_ID = new int[size];
		reqUpdate = new LinkedList[size];
		for(int i = 0; i < size; i++) {
			reqUpdate[i] = new LinkedList<>();
		}
	}
	
	public LinkedList<RequiredUpdate> get(int id) {
		return reqUpdate[id];
	}
	
	public void clear() {
		for(int i = 0; i < reqUpdate.length; i++ ) {
			reqUpdate[i].clear();
		}
	}
	
	public void add_update(int id,Object obj,int sequence) {
		LAST_SEND_ID[id]+=1;
		RequiredUpdate requpd = new RequiredUpdate();
		requpd.send_id = LAST_SEND_ID[id];
		requpd.sequence = sequence;
		requpd.updObj = obj;
		reqUpdate[id].add(requpd);
	}
	
	public void remove_update(int id, int ack_sequence) {
		int k = 0; 
		for(int i = 0; i < reqUpdate[id].size(); i++)
		{
			RequiredUpdate upd = reqUpdate[id].get(i);
			if(upd.sequence <= ack_sequence) {
				k+=1;
			}
		}
		for(int i = 0; i < k; i++) {
			reqUpdate[id].removeFirst();
		}
	}
}