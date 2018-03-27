package net.intelie.challenges;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.BeforeClass;

public class EventTest {
	private static EventMap eventMap = new EventMap();

	@BeforeClass
	public static void clearMap() throws Exception {
		eventMap.getConcurrentEventMap().clear();
	}

	@Test
	public void thisIsAWarning() throws Exception {
		Event event = new Event("some_type", 123L, "go");

		// THIS IS A WARNING:
		// Some of us (not everyone) are coverage freaks.
		// ANOTHER WARNING: Hey freaks, thanks for the opportunity!
		assertEquals(123L, event.getTimestamp());
		assertEquals("some_type", event.getType());
	}

	@Test
	public void testingInsertion() throws Exception {
		Event event = new Event("some_type", 123L, "go");
		EventMap eventMap = new EventMap();

		eventMap.insert(event);

		assertEquals(event, eventMap.getConcurrentEventMap().get(1L));
	}
	
	@Test
	public void testingRemove() throws Exception {
		Event event = new Event("some_type", 123L, "go");
		EventMap eventMap = new EventMap();

		eventMap.insert(event);

		assertEquals(event, eventMap.getConcurrentEventMap().get(1L));
		
		eventMap.removeAll("some_type");
		assertEquals(null, eventMap.getConcurrentEventMap().get(1L));
	}
	
	@Test
	public void testingRemovingSpecifiedType() throws Exception {
		Event event = new Event("some_type", 123L, "go");
		Event event2 = new Event("other_type", 123L, "go");
		EventMap eventMap = new EventMap();

		eventMap.insert(event);
		eventMap.insert(event2);

		assertEquals(event, eventMap.getConcurrentEventMap().get(1L));
		
		eventMap.removeAll("some_type");
		
		//Only the "some_type" should be removed.
		assertEquals(null, eventMap.getConcurrentEventMap().get(1L));
		assertEquals(event2, eventMap.getConcurrentEventMap().get(2L));
	}

	@Test
	public void testingConcurrentInsertion() throws Exception {
		Event event = new Event("some_type", 123L, "go");
		Event event2 = new Event("some_type", 123L, "go");
		EventMap eventMap = new EventMap();
		

		Thread thread1 = new Thread(new InsertEventRunnable(event));
		Thread thread2 = new Thread(new InsertEventRunnable(event2));
		thread1.start();
		thread2.start();

		// Both should be null, as both threads are sleeping at the assert.
		assertEquals(null, eventMap.getConcurrentEventMap().get(1L));
		assertEquals(null, eventMap.getConcurrentEventMap().get(2L));

	}
	
	
	@Test
	public void testingQuery() throws Exception {
		//Instantiating the events
		Event event = new Event("some_type", 100L, "go");
		Event event2 = new Event("some_type", 200L, "go");
		Event event3 = new Event("some_type", 400L, "go");
		Event event4 = new Event("other_type", 500L, "go");
		Event event5 = new Event("other_type", 700L, "go");
		
		//Instantiate the eventMap and insert all the events.
		EventMap eventMap = new EventMap();
		
		eventMap.insert(event);
		eventMap.insert(event2);
		eventMap.insert(event3);
		eventMap.insert(event4);
		eventMap.insert(event5);
		
		//Query must return an Iterator with only the event5
		EventIterator eventIterator = eventMap.query("other_type", 550L, 750L);
		
		eventIterator.moveNext();
		
		// Both should be null, as both threads are sleeping at the assert.
		assertEquals(event5, eventIterator.current());

	}

	
	private class InsertEventRunnable implements Runnable {

		Event event;

		public InsertEventRunnable(Event e) {
			this.event = e;
		}

		@Override
		public void run() {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			eventMap.insert(this.event);

		}

	}

}