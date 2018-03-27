package net.intelie.challenges;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class EventMap implements EventStore {

	//Chosen Data Structure based on Java Doc: https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/ConcurrentHashMap.html
	// The Concurrent Hash Map is a Thread Safe HashMap implementation
	private ConcurrentHashMap<Long, Event> concurrentEventMap = new ConcurrentHashMap<Long, Event>();
	private final AtomicLong eventKeyGenerator = new AtomicLong();

	@Override
	public void insert(Event event) {
		concurrentEventMap.putIfAbsent(eventKeyGenerator.incrementAndGet(), event);
	}

	@Override
	public void removeAll(String type) {

		//First, query the EventMap, searching for the Events with different types
        Map<Long, Event> queryResult = concurrentEventMap.entrySet().stream()
                .filter(event -> !type.equals(event.getValue().getType()))
                .collect(Collectors.toConcurrentMap(x -> x.getKey(), x -> x.getValue()));

        //Clearing the map for safety;
		concurrentEventMap.clear();

		//Setting the memory map to the filtered map: Works for empty result too.
        concurrentEventMap = new ConcurrentHashMap<Long, Event>(queryResult);
	}

	@Override
	public EventIterator query(String type, long startTime, long endTime) {
		Map<Long, Event> queryResult = concurrentEventMap.entrySet().stream()
				.filter(event -> type.equals(event.getValue().getType()) && startTime <= event.getValue().getTimestamp()
					 && event.getValue().getTimestamp() < endTime)
				.collect(Collectors.toConcurrentMap(x -> x.getKey(), x -> x.getValue()));
		
		//Returns the new EventIterator implementation based on the filter operation.
		return new EventQueryResults(new ConcurrentHashMap<Long, Event>(queryResult));
	}

	public ConcurrentHashMap<Long, Event> getConcurrentEventMap() {
		return concurrentEventMap;
	}

}
