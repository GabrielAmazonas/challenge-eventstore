package net.intelie.challenges;

import java.util.concurrent.ConcurrentHashMap;

public class EventQueryResults implements EventIterator {
	
	private ConcurrentHashMap<Long, Event> queriedEventMap;
	private long currentEventKey;
	

	public EventQueryResults(ConcurrentHashMap<Long, Event> queriedEventMap) {
		this.queriedEventMap = queriedEventMap;
	}

	@Override
	public boolean moveNext() {
		if((currentEventKey + 1) > queriedEventMap.size()) {
			return false;
		} else {
			currentEventKey = queriedEventMap.keys().nextElement();
			return true;
		}
			
	}

	@Override
	public Event current() {
		if(currentEventKey == 0) {
			throw new IllegalStateException("moveNext() method not executed yet.");
		}
		
		//We need to check the content to see wich is the first key
		
		return queriedEventMap.get(currentEventKey);
	}

	@Override
	public void remove() {
		if(currentEventKey == 0) {
			throw new IllegalStateException("moveNext() method not executed yet.");
		}
		queriedEventMap.remove(currentEventKey);
	}




}
