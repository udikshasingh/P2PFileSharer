import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TimerTask;

public class UnchokedPeers extends TimerTask {

	@Override
	public void run() {
		try 
		{
			String string;
			BufferedReader br = new BufferedReader(new FileReader("PeerInfo.cfg"));
			while ((string = br.readLine()) != null)
			{
				String[]args = string.trim().split("\\s+");
				String peerId = args[0];
				int isCompleted = Integer.parseInt(args[3]);
				if(isCompleted == 1)
				{
					peerProcess.peerMap.get(peerId).isCompleted = 1;
					peerProcess.peerMap.get(peerId).isInterested = 0;
					peerProcess.peerMap.get(peerId).isChoked = 0;
				}
			}
			br.close();
		}
		catch (Exception e) {
			e.printStackTrace();
			//add logger
			//showLog(peerId + e.toString());
		}
		
		if(!peerProcess.unchokedPeer.isEmpty())
			peerProcess.unchokedPeer.clear();
		
		List<Peer> peers = new ArrayList<>();
		
		for (String pId : peerProcess.unchokedPeer.keySet()) {
			Peer peer = peerProcess.peerMap.get(pId);
			if (peer.isChoked == 1 && !pId.equals(peerProcess.peerId) && peer.isCompleted == 0 && peer.handshakeCompleted == 1) {
				peers.add(peer);
			}
		}
		
		if (peers.size() > 0) {
			Collections.shuffle(peers);
			Peer peer = peers.get(0);
			
			peerProcess.peerMap.get(peer.peerId).isOptUnchokedNeighbor = 1;
			peerProcess.unchokedPeer.put(peer.peerId, peerProcess.peerMap.get(peer.peerId));
			// LOG 4:
			//peerProcess.showLog(peerProcess.peerId + " has the optimistically unchoked neighbor " + peer.peerId);
			
			if (peerProcess.peerMap.get(peer.peerId).isChoked == 1)
			{
				peerProcess.peerMap.get(peer.peerId).isChoked = 0;
				unchoke(peerProcess.socketMap.get(peer.peerId), peer.peerId);
				have(peerProcess.socketMap.get(peer.peerId), peer.peerId);
				peerProcess.peerMap.get(peer.peerId).state = 3;
			}
		}
	}

	static void unchoke(Socket socket, String peerId) {
		
		//log(peerId + " is sending UNCHOKE message to remote Peer " + peerId);
		System.out.println(peerId + " is sending UNCHOKE message to remote Peer " + peerId);
		Message message = new Message(Message.UNCHOKE);
		byte[] byte_array = Message.encode(message);
		send(socket, byte_array);
		
	}
	
	static int send(Socket socket, byte[] byte_array) {
		
		try {
			
			OutputStream output = socket.getOutputStream();
			output.write(byte_array);
		
		} catch (IOException e) {
			
			e.printStackTrace();
			return 0;
			
		}
		return 1;
	}
	
	static void have(Socket socket, String peerId) {
		
		byte[] byte_array = peerProcess.bitOperation.getBytes();
		//log(peerProcess.peerId + " sending HAVE message to Peer " + peerId);
		System.out.println(peerProcess.peerId + " sending HAVE message to Peer " + peerId);
		Message message = new Message("4", byte_array);
		send(socket, Message.encode(message));
		byte_array = null;
		
	}
	
	
}
