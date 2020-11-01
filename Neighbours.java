import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
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
					
					peerProcess.peerMap.get(peerId).isCompleted = 1;
					peerProcess.peerMap.get(peerId).isInterested = 0;
					peerProcess.peerMap.get(peerId).isChoked = 0;
					
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
		
		for (String peerId : peerProcess.peerMap.keySet()) {
			
			Peer peer = peerProcess.peerMap.get(peerId);
			if (peerId.equals(peerProcess.peerId)) {
				
				continue;
				
			}
			if (peer.isCompleted == 0 && peer.handshakeCompleted == 1) {
				
				interestedNeighbours++;
				
			}
			else if (peer.isCompleted == 1) {
	
					peerProcess.preferedNeighboursMap.remove(peerId);
					
			}
		}
		if (interestedNeighbours > Metadata.numberOfPreferredNeighbours) {
			
			if (!peerProcess.preferedNeighboursMap.isEmpty()) {
				
				peerProcess.preferedNeighboursMap.clear();
				
			}
			List<Peer> peerList = new ArrayList<>(peerProcess.peerMap.values());
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
					return peer2.toString().compareTo(peer1.toString());
					
				}
				
			});
			int count = 0;
			for (int i = 0; i < peerList.size(); i++) {
				
				if (count > Metadata.numberOfPreferredNeighbours - 1)
					break;
				if (peerList.get(i).handshakeCompleted == 1 && !peerList.get(i).peerId.equals(peerProcess.peerId) && peerProcess.peerMap.get(peerList.get(i).peerId).isCompleted == 0) {
					
					peerProcess.peerMap.get(peerList.get(i).peerId).isPreferredNeighbor = 1;
					peerProcess.peerMap.put(peerList.get(i).peerId, peerProcess.peerMap.get(peerList.get(i).peerId));
					
					count++;
					
					preferredNeighbours = preferredNeighbours + peerList.get(i).peerId + ", ";
					
					if (peerProcess.peerMap.get(peerList.get(i).peerId).isChoked == 1) {
						
						unchoke(peerProcess.socketMap.get(peerList.get(i).peerId), peerList.get(i).peerId);
						peerProcess.peerMap.get(peerList.get(i).peerId).isChoked = 0;
						have(peerProcess.socketMap.get(peerList.get(i).peerId), peerList.get(i).peerId);
						peerProcess.peerMap.get(peerList.get(i).peerId).state = 3;
						
					}
					
				}
				
			}
			
		}
		else {
			
			for (String peerId : peerProcess.peerMap.keySet()) {
				
				Peer peer = peerProcess.peerMap.get(peerId);
				if(peerId.equals(peerProcess.peerId)) continue;
				
				if (peer.isCompleted == 0 && peer.handshakeCompleted == 1) {
					
					if (!peerProcess.preferedNeighboursMap.containsKey(peerId)) {
						
						preferredNeighbours = preferredNeighbours + peerId + ", ";
						peerProcess.preferedNeighboursMap.put(peerId, peerProcess.peerMap.get(peerId));
						peerProcess.peerMap.get(peerId).isPreferredNeighbor = 1;
						
					}
					if (peer.isChoked == 1) {
						
						unchoke(peerProcess.socketMap.get(peerId), peerId);
						peerProcess.peerMap.get(peerId).isChoked = 0;
						have(peerProcess.socketMap.get(peerId), peerId);
						peerProcess.peerMap.get(peerId).state = 3;
						
					}
					
				}
				
			}
			
		}
		if (preferredNeighbours.length() != 0) {
			
			//log(peerProcess.peerId + " has selected the preferred neighbours - " + preferredNeighbours);
			System.out.println(peerProcess.peerId + " has selected the preferred neighbours - " + preferredNeighbours);
			
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
