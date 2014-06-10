package game;

import java.util.concurrent.LinkedBlockingQueue;

public abstract class Receiver {

	protected LinkedBlockingQueue<String[]> queue = new LinkedBlockingQueue<String[]>();

	public abstract void onMessage(String[] message);

	public void addMessage(String[] message) {
		synchronized(queue) {
			queue.add(message);
		}
	}
	public void clearMessages() {
		synchronized(queue) {
			queue.clear();
		}
	}

	public void handleMessages() {
		synchronized(queue) {
			for (String[] s : queue) {
				/*Do something?*/
			}
		}
	}

}