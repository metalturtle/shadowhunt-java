package handlers;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

import handlers.basic.GameEvent;

public class GameEventHandler 
{
	LinkedList <GameEvent> gameEvents;
	HashMap <String,Short> eventType;
	static short EVENT_ID_ASSIGN;
	public GameEventHandler()
	{
		gameEvents = new LinkedList<GameEvent>();
		eventType = new HashMap<String,Short>();
		EVENT_ID_ASSIGN = 1;
	}
	
	public Short create_event_type(String name)
	{
		eventType.put(name, EVENT_ID_ASSIGN);
		return EVENT_ID_ASSIGN++;
	}
	
	public Short get_event_type(String name)
	{
		return eventType.get(name);
	}
	
	public GameEvent create_event(short type,short life,String args[])
	{
		GameEvent ge = new GameEvent(type,life,args);
		gameEvents.add(ge);
		return ge;
	}
	
	public GameEvent create_event(GameEvent ge)
	{
		gameEvents.add(ge);
		return ge;
	}
	
	
	public GameEvent check_event(short type)
	{
//		for(GameEvent ge:gameEvents)
//		{
//			if(ge.get_type()== type)
//				return ge;
//		}
		return null;
	}
	
	public GameEvent[] get_event_array()
	{
		int size = gameEvents.size();
		if(size<=0)
			return null;
		GameEvent arr[] = new GameEvent[size];
		int k=0;
		for(GameEvent ge:gameEvents)
		{
			arr[k++] = ge;
		}
		return arr;
	}
	
	public void loop()
	{
//		Iterator<GameEvent> iterator = gameEvents.iterator();
//		while(iterator.hasNext())
//		{
//			GameEvent ge = iterator.next();
//			ge.set_life((short) (ge.get_life()-1));
//			if(ge.get_life()<0)
//				iterator.remove();
//		}
	}
}
