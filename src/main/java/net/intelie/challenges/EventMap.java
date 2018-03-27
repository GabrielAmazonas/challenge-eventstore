package net.intelie.challenges;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class EventMap implements EventStore {

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

		//Setting the memory map to the filtered map:
        concurrentEventMap = new ConcurrentHashMap<Long, Event>(queryResult);
	}

	@Override
	public EventIterator query(String type, long startTime, long endTime) {
		Map<Long, Event> queryResult = concurrentEventMap.entrySet().stream()
				.filter(event -> type.equals(event.getValue().getType()) && startTime >= event.getValue().getTimestamp()
						&& endTime < event.getValue().getTimestamp())
				.collect(Collectors.toConcurrentMap(x -> x.getKey(), x -> x.getValue()));

		return new EventQueryResults(new ConcurrentHashMap<Long, Event>(queryResult));
	}

	public ConcurrentHashMap<Long, Event> getConcurrentEventMap() {
		return concurrentEventMap;
	}

}
