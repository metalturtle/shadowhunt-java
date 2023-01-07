package simulation;

import basic.RoundQueue;

public class ChatHandler {

	RoundQueue<char[]> chatMessages;
	RoundQueue<Integer> chatLength;
	
	int bufferSize = 2;
	
	public ChatHandler(SimulationCoordinator SimCoord) {
		char [][] buffer = new char[bufferSize][];
		for(int i =0; i < bufferSize; i++) {
			buffer[i] = new char[128+32];
		}
		chatMessages = new RoundQueue<char[]>(buffer,true);
		
		Integer clen[] = new Integer[bufferSize];
		chatLength = new RoundQueue<Integer>(clen,true);
	}
	
	public void add_message(String message) {
		char[] arr = chatMessages.add_mset_element();
		for(int i = 0; i < message.length(); i++) {
			arr[i] = message.charAt(i);
		}
		chatLength.add_element(message.length());
		
	}
	
}
