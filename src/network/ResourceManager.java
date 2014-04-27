package network;

import java.util.HashMap;
import java.util.Map;


public class ResourceManager {
	
	/**
	 * This is responsible for managing a resource.
	 * It takes care of how many processes would like
	 * to access that resource
	 */
	private int id;
	private Map<Integer, Boolean> queue = new HashMap<Integer, Boolean>();
	
	public ResourceManager(int id) {
		this.id = id;
	}

	public boolean isResourceReady() {
		return queue.isEmpty();
	}
	
	public void addToQueue(int peerId) {
		queue.put(peerId, true);
	}
	
	public void removeFromQueue(int peerId) {
		if (queue.containsKey(peerId)) {
			queue.remove(peerId);
		}
	}

	public Map<Integer, Boolean> getQueue() {
		return queue;
	}
	
	public void clearQueue() {
		queue.clear();
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
