package core;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import network.Message;
import network.ResourceManager;
import network.SocketManager;
import util.Util;

public class NetworkingProcess extends ReceivingProcess {

	/**
	 * A NetworkingProcess is a general process that can perform
	 * all the tasks that are required by the task
	 */
	public NetworkingProcess(int id, int port) {
		super(id, port);
	}
	
	public void start() {
		listener.start();
		talker.start();
	}
	
	/**
	 * Ask your peers for a resource
	 */
	public boolean askForResource(int resourceId) {
		for (Map.Entry<Integer, SocketManager> entry : sockets.entrySet()) {
			sendRequestMessage(entry.getKey(), resourceId);
		}

		long t0 = System.currentTimeMillis();
		ResourceManager resMan = null;
		synchronized (inComingResources) {
			resMan = inComingResources.get(resourceId);			
		}
		while (!resMan.isResourceReady()) {
			synchronized (inComingResources) {
				resMan = inComingResources.get(resourceId);			
			}
			// if you do not receive responses in 15s, time out
			if (System.currentTimeMillis() - t0 > 15000) {
				System.out.println("P" + id + " timed out after waiting 15s for the request made for R" + resourceId);
				return false;
			}
			
		}
		long delta = System.currentTimeMillis() - t0;
		System.out.println("P" + id + " waited for R" + resourceId + " for " + delta + "ms");
		return true;
	}
	
	/**
	 * Using a resource in our case is just sleeping
	 */
	public void useResource(int resourceId) {
		System.out.println("P" + id + " is using R" + resourceId);
		synchronized (usingResource) {
			usingResource.put(resourceId, true);
		}
		synchronized (wantsResource) {
				wantsResource.put(resourceId, false);				
		}
		Util.sleep(5000);
	}
	
	public void releaseResource(int resourceId) {
		Map<Integer, Boolean> queue = null;
		synchronized (outGoingResources) {
			queue = outGoingResources.get(resourceId).getQueue();			
		}
		
		for (Map.Entry<Integer, Boolean> entry : queue.entrySet()) {
			sendOkMessage(entry.getKey(), resourceId);
		}
		synchronized (usingResource) {
			usingResource.put(resourceId, false);
		}
		synchronized (outGoingResources) {
			outGoingResources.get(resourceId).clearQueue();				
		}		
		System.out.println("P" + id + " released R" + resourceId);
	}
	
	/**
	 * Choose a resource at random from the available resources
	 */
	public int randomResource() {
		List<Integer> resources = new ArrayList<Integer>();
		synchronized (outGoingResources) {
			for (Map.Entry<Integer, ResourceManager> entry : outGoingResources.entrySet()) {
				resources.add(entry.getKey());
			}			
		}
		
		Random rand = new Random();
		return rand.nextInt(resources.size());
	}
	
	/**
	 * Method for listening for messages that come from peers
	 * This is an independent thread
	 */
	private Thread listener = new Thread () {
		
		public void run() {
			System.out.println("Listener running for " + id);
			while (true) {
				Message m = sockManager.processPacket(sockManager.receivePacket());
				receiveMessage(m);
			}
			
		}
	};
	
	/**
	 * Thread that does the talking. It is independent of the thread
	 * that listens for peer messages
	 */
	private Thread talker = new Thread () {			
		public void run() {	
			System.out.println("Talker running for " + id);
			while (true) {
				int resource = randomResource();
				synchronized (wantsResource) {
					wantsResource.put(resource, true);					
				}
				if (askForResource(resource)) {
					useResource(resource);
					releaseResource(resource);
				}
				else {
					synchronized (wantsResource) {
						wantsResource.put(resource, false);				
					}
				}
				//Util.sleep(2000);
			}
			
		}
		
	};
}
