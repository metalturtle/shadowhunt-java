package client;

import java.util.Iterator;
import java.util.LinkedList;


public class ClientImageIterator implements Iterator<ClientImage> {

	LinkedList <ClientImage> clientImageList = new LinkedList<ClientImage>();
	int i = -1;
	ClientImageIterator(LinkedList <ClientImage> clientImageList) {
		this.clientImageList = clientImageList;
		goto_valid_next();
		
	}
	@Override
	public boolean hasNext() {
		return i < clientImageList.size();
	}

	void goto_valid_next() {
		i+=1;
		while(hasNext() && (clientImageList.get(i).is_disconnected() ))
			i++;
	}
	
	@Override
	public ClientImage next() {
		ClientImage clientObject = clientImageList.get(i);
		goto_valid_next();
		return clientObject;
	}

}
