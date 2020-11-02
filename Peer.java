
public class Peer implements Comparable<Peer>
{
	
	String peerId;
	String peerMachine;
	String peerPort;
	int isCompleted;
	int handshakeCompleted;
	public double speed = 0;
	
	public int compareTo(Peer peer) 
	{
		
		if (this.speed > peer.speed) 
			return 1;
		else if (this.speed == peer.speed) 
			return 0;
		else 
			return -1;
		
	}
}
