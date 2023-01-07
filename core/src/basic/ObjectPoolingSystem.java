package basic;

import java.util.ArrayList;

public class ObjectPoolingSystem {

	ArrayList<ObjectPooling> pools = new ArrayList<ObjectPooling>();
	
	public void add_object_pooling(ObjectPooling pooling) {
		pools.add(pooling);
	}
	
	public void loop() {
		for(int i = 0; i < pools.size(); i++) {
			pools.get(i).reduce_size();
			pools.get(i).release();
		}
	}
}
