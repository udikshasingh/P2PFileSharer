
public class Peer implements Comparable<Peer>{
	
	String peerId;
	public String peerIP;
	String peerPort;
	int isCompleted;
	int handshakeCompleted;
	public double speed = 0;
	int peerIndex;
	int isFirst;
	int isInterested = 1;
	int isChoked = 1;
	int isPreferredNeighbor = 0;
	int state = -1;
	int isOptUnchokedNeighbor;
	
	public Peer(String peerId, String peerIP, String peerPort, int isFirst, int peerIndex) {
		this.peerId = peerId;
		this.peerIP = peerIP;
		this.peerPort = peerPort;
		this.isFirst = isFirst;
		this.peerIndex = peerIndex;
	}
	
	public int compareTo(Peer peer) {
		
		if (this.speed > peer.speed) 
			return 1;
		else if (this.speed == peer.speed) 
			return 0;
		else 
			return -1;
		
	}
	
	public int isFirst() {
		
		return isFirst;
		
	}
}
