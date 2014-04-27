package core;

import java.util.ArrayList;
import java.util.List;

import network.Message;
import network.SocketManager;

public class SendingProcess extends BasicProcess {

	/**
	 * This is a process that is capable of sending messages
	 * to its peers.
	 */
	public SendingProcess(int id, int port) {
		super(id, port);
	}
	
	// methods for talking with only one peer
	public void sendMessageToPeer(int peerId, int type, List<Object> data) {
		SocketManager sock = sockets.get(peerId);
		Message m = new Message(id, type);
		m.setData(data);	
		//System.out.println(id +" is sending message to " + peerId);
		sock.sendMessage(m, sock.getAddr(),sock.getPort());
	}
	
	public void sendRequestMessage(int peerId, int resourceId) {
		List<Object> data = new ArrayList<Object>();
		data.add(resourceId);
		data.add(timeStamp);
		
		sendMessageToPeer(peerId, Message.REQUEST, data);
		synchronized (inComingResources) {
			inComingResources.get(resourceId).addToQueue(peerId);			
		}
	}
	
	public void sendOkMessage(int peerId, int resourceId) {
		List<Object> data = new ArrayList<Object>();
		data.add(resourceId);
		sendMessageToPeer(peerId, Message.OK, data);
	}	
}
