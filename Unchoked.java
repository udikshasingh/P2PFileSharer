/**
 * 
 */

/**
 * @author shrey
 *
 */

import java.util.*;

private static void sendUnChoke(Socket socket, String remotePeerID) 
{
	showLog(peerID + " is sending UNCHOKE message to remote Peer " + remotePeerID);
	DataMessage d = new DataMessage(DATA_MSG_UNCHOKE);
	byte[] msgByte = DataMessage.encodeMessage(d);
	SendData(socket, msgByte);
}


private static void sendHave(Socket socket, String remotePeerID) 
{
	byte[] encodedBitField = peerProcess.ownBitField.encode();
	showLog(peerID + " sending HAVE message to Peer " + remotePeerID);
	DataMessage d = new DataMessage(DATA_MSG_HAVE, encodedBitField);
	SendData(socket,DataMessage.encodeMessage(d));
	encodedBitField = null;
}



public class Unchoked extends TimerTask
{
	
	public void run() 
		{
			//updates remotePeerInfoHash
			readPeerInfoAgain();
			if(!Unchoked.isEmpty())
				Unchoked.clear();
			Enumeration<String> keys = remotePeerInfoHash.keys();
			Vector<RemotePeerInfo> peers = new Vector<RemotePeerInfo>();
			while(keys.hasMoreElements())
			{
				String key = (String)keys.nextElement();
				RemotePeerInfo pref = remotePeerInfoHash.get(key);
				if (pref.isChoked == 1 
						&& !key.equals(peerID) 
						&& pref.isCompleted == 0 
						&& pref.isHandShaked == 1)
					peers.add(pref);
			}
			
			// Randomize the vector elements 	
			if (peers.size() > 0)
			{
				Collections.shuffle(peers);
				RemotePeerInfo p = peers.firstElement();
				
				remotePeerInfoHash.get(p.peerId).isOptUnchokedNeighbor = 1;
				Unchoked.put(p.peerId, remotePeerInfoHash.get(p.peerId));
				// LOG 4:
				peerProcess.showLog(peerProcess.peerID + " has the optimistically unchoked neighbor " + p.peerId);
				
				if (remotePeerInfoHash.get(p.peerId).isChoked == 1)
				{
					peerProcess.remotePeerInfoHash.get(p.peerId).isChoked = 0;
					sendUnChoke(peerProcess.peerIDToSocketMap.get(p.peerId), p.peerId);
					sendHave(peerProcess.peerIDToSocketMap.get(p.peerId), p.peerId);
					peerProcess.remotePeerInfoHash.get(p.peerId).state = 3;
				}
			}
			
		}		

}

public static void readPeerInfoAgain()
	{
		try 
		{
			String st;
			BufferedReader in = new BufferedReader(new FileReader("PeerInfo.cfg"));
			while ((st = in.readLine()) != null)
			{
				String[]args = st.trim().split("\\s+");
				String peerID = args[0];
				int isCompleted = Integer.parseInt(args[3]);
				if(isCompleted == 1)
				{
					remotePeerInfoHash.get(peerID).isCompleted = 1;
					remotePeerInfoHash.get(peerID).isInterested = 0;
					remotePeerInfoHash.get(peerID).isChoked = 0;
				}
			}
			in.close();
		}
		catch (Exception e) {
			showLog(peerID + e.toString());
		}
	}
