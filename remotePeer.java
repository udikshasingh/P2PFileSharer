/**
 * 
 */

/**
 * @author shrey
 *
 */

import java.util.Date;

public class remotePeer //implements Comparable<RemotePeerInfo>
{
	public String pId;
	public String pAddr;
	public String pPort;
	public int isFirst;
	public double dataRate = 0;
	public int isInterested = 1;
	public int isPrefNeighbor = 0;
	public int istUnchokedNeighbor = 0;
	public int isChoked = 1;
	//public BitField bitField;
	public int state = -1;
	public int pIndex;
	public int isCompleted = 0;
	public int isHandShaked = 0;
	public Date start;
	public Date fin;
	
	public remotePeer(String pId, String pAddr, String pPort, int pIndex)
	{
		this.pId = pId;
		this.pAddr = pAddr;
		this.pPort = pPort;
		//bitField = new BitField();
		this.pIndex = pIndex;
	}
	/*public remotePeer(String pId, String pAddress, String pPort, int pIsFirstPeer, int pIndex)
	{
		peerId = pId;
		peerAddress = pAddress;
		peerPort = pPort;
		isFirstPeer = pIsFirstPeer;
		bitField = new BitField();
		peerIndex = pIndex;
	}*/
	public String getPeerId() {
		return pId;
	}
	public void setPeerId(String pId) {
		this.pId = pId;
	}
	public String getPeerAddress() {
		return pAddr;
	}
	public void setPeerAddress(String pAddr) {
		this.pAddr = pAddr;
	}
	public String getPeerPort() {
		return pPort;
	}
	public void setPeerPort(String pPort) {
		this.pPort = pPort;
	}
	public int getIsFirst() {
		return isFirst;
	}
	public void setIsFirst(int isFirst) {
		this.isFirst = isFirst;
	}
	public int compareTo(remotePeer peer1) {
		
		if (this.dataRate > peer1.dataRate) 
			return 1;
		else if (this.dataRate == peer1.dataRate) 
			return 0;
		else 
			return -1;
	}

}