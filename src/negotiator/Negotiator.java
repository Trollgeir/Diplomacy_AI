package negotiator;

import java.util.concurrent.LinkedBlockingQueue;

public class Negotiator {
	protected LinkedBlockingQueue<String[]> queue = new LinkedBlockingQueue<String[]>();
	
	public Negotiator(){
		
	}
	public void addProposal(String[] proposal) {
		synchronized(queue) {
			queue.add(proposal);
		}
	}
	public void clear() {
		synchronized(queue) {
			queue.clear();
		}
	}

	public void handleProposal() {
		synchronized(queue) {
			for (String[] s : queue) {
				if (s[0].equals("FRM")){
					//handle proposal
				} else if (s[0].equals("YES")) {
					// handle confirmed proposal
				} else if (s[0].equals("REJ")) {
					// handle rejected proposal
				} else {
					// HUH
				}
			}
		}
	}
}
