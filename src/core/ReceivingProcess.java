package core;


import network.Message;

public class ReceivingProcess extends SendingProcess {
	/**
	 * This process can do both sending and receiving messages
	 */
	public ReceivingProcess(int id, int port) {
		super(id, port);
	}

	public void receiveMessage(Message m) {
		switch(m.getType()) {
			case Message.REQUEST:
				receiveRequestMessage(m);
				break;
			case Message.OK:
				receiveOkMessage(m);
				break;
			default:
				throw new RuntimeException("Unknown type" + m.getType());		
		}
	}
	
	/**
	 * Receive a request and process it accordingly
	 */
	public void receiveRequestMessage(Message m) {
		int resourceId = (Integer) m.getData().get(0);
		int timeStamp = (Integer) m.getData().get(1);
		//System.out.println(id + " received request for " + resourceId);
		synchronized (usingResource) {
				synchronized (wantsResource) {
					
					if (!usingResource.get(resourceId) && !wantsResource.get(resourceId)) {
						sendOkMessage(m.getSourceId(), resourceId);
					}
					else if (usingResource.get(resourceId)) {
						synchronized (outGoingResources) {
							outGoingResources.get(resourceId).addToQueue(m.getSourceId());
						}
					}
					else if (wantsResource.get(resourceId)) {
						if (this.timeStamp < timeStamp) {
							synchronized (outGoingResources) {
								outGoingResources.get(resourceId).addToQueue(m.getSourceId());	
							}
						}
						else {
							sendOkMessage(m.getSourceId(), resourceId);
						}
					}
					else {
						// what???
					}
					
				}
			}
		//System.out.println("finished processing request message");
	}
	
	public void receiveOkMessage(Message m) {
		int resourceId = (Integer) m.getData().get(0);	
		synchronized (inComingResources) {
			inComingResources.get(resourceId).removeFromQueue(m.getSourceId());			
		}
		System.out.println("P" + m.getSourceId() + " said OK to P" + id + " for R" + resourceId);
	}
}
