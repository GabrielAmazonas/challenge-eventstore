package net.intelie.challenges;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

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

        //THIS IS A WARNING:
        //Some of us (not everyone) are coverage freaks.
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
    public void testingConcurrentInsertion() throws Exception {
        Event event = new Event("some_type", 123L, "go");
        Event event2 = new Event("some_type", 123L, "go");
        
        Thread thread1 = new Thread(new InsertEventRunnable(event));
        Thread thread2 = new Thread(new InsertEvent2Runnable(event2));
        thread1.start();
        thread2.start();
        
        assertEquals(event, eventMap.getConcurrentEventMap().get(1L));
        assertEquals(null, eventMap.getConcurrentEventMap().get(2L));

    }
    public void testAfterConcurrentInsertion() throws Exception {
    	 Event event = new Event("some_type", 123L, "go");
         Event event2 = new Event("some_type", 123L, "go");
        
        assertEquals(event, eventMap.getConcurrentEventMap().get(1L));
        assertEquals(event2, eventMap.getConcurrentEventMap().get(2L));

    }
    
    public class InsertEventRunnable implements Runnable{
    	
    	Event event;
    	
    	public InsertEventRunnable(Event e) {
    		this.event = e;
    	}

		@Override
		public void run() {
			eventMap.insert(this.event);
			
		}
    	
    }
    
 public class InsertEvent2Runnable implements Runnable{
    	
    	Event event;
    	
    	public InsertEvent2Runnable(Event e) {
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