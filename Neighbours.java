import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TimerTask;

public class Neighbours extends TimerTask {
	
	public void run() {
		
		String line = "";
		try {
			
			BufferedReader br = new BufferedReader(new FileReader("PeerInfo.cfg"));
			while ((line = br.readLine()) != null) {
				
				String[] configs = line.split("\\s+");
				String peerId = configs[0];
				int hasCompleteFile = Integer.parseInt(configs[3]);
				if (hasCompleteFile == 1) {
					
					peerProcess.remotePeerInfoHash.get(peerId).isCompleted = 1;
					peerProcess.remotePeerInfoHash.get(peerId).isInterested = 0;
					peerProcess.remotePeerInfoHash.get(peerId).isChoked = 0;
					
				}
				
			}
			br.close();

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int interestedNeighbours = 0;
		String preferredNeighbours = "";
		
		for (String peerId : peerProcess.remotePeerInfoHash.keySet()) {
			
			Peer peer = peerProcess.remotePeerInfoHash.get(peerId);
			if (peerId.equals(peerProcess.peerId)) {
				
				continue;
				
			}
			if (peer.isCompleted == 0 && peer.handshakeCompleted == 1) {
				
				interestedNeighbours++;
				
			}
			else if (peer.isCompleted == 1) {
	
					peerProcess.preferedNeighbors.remove(peerId);
					
			}
		}
		if (interestedNeighbours > Metadata.numberOfPreferredNeighbours) {
			
			if (!preferedNeighbours.isEmpty()) {
				
				preferedNeighbours.clear();
				
			}
			List<Peer> peerList = new ArrayList<>(remotePeerInfoHash.values());
			Collections.sort(peerList, new Comparator<Peer>() {
				
				public int compare(Peer peer1, Peer peer2) {
					
					if (peer1 == null && peer2 == null) 
						return 0;
					
					if (peer1 == null)
						return 1;
					
					if (peer2 == null)
						return -1;
					
					if (peer1 instanceof Comparable) {
					
						return peer2.compareTo(peer1);
						
					} 
					
				}
				
			});
			int count = 0;
			for (int i = 0; i < peerList.size(); i++) {
				
				if (count > Metadata.numberOfPreferredNeighbours - 1)
					break;
				if (peerList.get(i).handshakeCompleted == 1 && !peerList.get(i).peerId.equals(peerProcess.peerID) && peerProcess.remotePeerInfoHash.get(peerList.get(i).peerId).isCompleted == 0) {
					
					peerProcess.remotePeerInfoHash.get(peerList.get(i).peerId).isPreferredNeighbor = 1;
					peerProcess.preferedNeighbors.put(peerList.get(i).peerId, peerProcess.remotePeerInfoHash.get(peerList.get(i).peerId));
					
					count++;
					
				}
				
			}
			
		}
		
	}
}
