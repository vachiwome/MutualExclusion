package core;

import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Map;

import network.ResourceManager;
import network.SocketManager;

public class BasicProcess {

	/**
	 * This is a basic network process.
	 * id is the process identifier
	 * timeStamp is the local lamport time
	 * outGoingResources store the resourceManagers for resources that other processes requested
	 * inComingResources stores the resourceManagers for resource that this process requested
	 * wantsResource determines whether this process is interested in a resource or not
	 *	usingResource hold a true value when this process is already using that resource
	 */
	
	protected int id;
	protected int timeStamp;
	protected SocketManager sockManager;
	protected Map<Integer, SocketManager> sockets = new HashMap<Integer, SocketManager>();
	protected Map<Integer, ResourceManager> outGoingResources = new HashMap<Integer, ResourceManager>();
	protected Map<Integer, ResourceManager> inComingResources = new HashMap<Integer, ResourceManager>();
	protected Map<Integer, Boolean> wantsResource = new HashMap<Integer, Boolean>();
	protected Map<Integer, Boolean> usingResource = new HashMap<Integer, Boolean>();
	
	
	public BasicProcess(int id, int port) {
		this.id = id;
		this.sockManager = new SocketManager(port);
		try {
			this.sockManager.setSocket(new DatagramSocket(port));
		}
		catch(SocketException ex) {
			ex.printStackTrace();
		}
	}

	/**
	 *tell this process about some other process together with
	 *the port that the process is listening on
	 */
	
	public void addProcess(int id, int port) {
		sockets.put(id, new SocketManager(port));
	}
	
	/**
	 * Add a new resource. Make sure you synchronize the listening and 
	 * the talking thread
	 */
	public void addResource(int resourceId) {
		synchronized (inComingResources) {
			inComingResources.put(resourceId, new ResourceManager(resourceId));
		}
		synchronized (outGoingResources) {
			outGoingResources.put(resourceId, new ResourceManager(resourceId));
		}
		synchronized (wantsResource) {
			wantsResource.put(resourceId, false);
		}
		synchronized (usingResource) {
			usingResource.put(resourceId, false);						
		}
	}

	public SocketManager getSockManager() {
		return sockManager;
	}

	public void setSockManager(SocketManager sockManager) {
		this.sockManager = sockManager;
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
}
