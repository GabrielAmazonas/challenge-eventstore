package net.intelie.challenges;


/**
 * This is just an event stub, feel free to expand it if needed.
 * 
 * 
 */
public class Event {
    private final String type;
    private final long timestamp;
    private final String description;
    
    /**
     * This is just an event stub, feel free to expand it if needed.
     * 
     * 
     */
    public Event(String type, long timestamp, String description) {
        this.type = type;
        this.timestamp = timestamp;
        this.description = description;
        
    }

    public String getType() {
		return type;
	}

	public long getTimestamp() {
		return timestamp;
	}


	public String getDescription() {
		return description;
	}


}
