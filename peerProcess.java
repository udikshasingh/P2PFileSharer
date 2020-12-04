import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.Vector;

/**
 * 
 */

/**
 * @author shrey
 *
 */

//import java.io.*;
//import java.net.*;

public class peerProcess {
	public ServerSocket socket = null;
	public static String peerId;
	public int pIndx;
	public int portNum;
	public static Map<String, Peer> peerMap = new HashMap<String, Peer>();
	
	public static  Map<String, Peer> preferedNeighboursMap = new HashMap<String, Peer>();
	public static Map<String, Peer> unchokedPeer = new HashMap<>();
	
	public static Map<String, Socket> socketMap = new HashMap<String, Socket>();
	
	public static List<Thread> fromList = new ArrayList<Thread>();
	public Thread listener;
	public static Vector<Thread> tSender = new Vector<Thread>();
	public static BitOperation bitOperation = null;
	public static volatile Timer timer;
	
	
	static FileOutputStream logFile;
	static OutputStreamWriter out;

	
	public static void main (String[] args) throws IOException {
		try {
			
			peerProcess pProc = new peerProcess();
			
			peerId=args[0];
			//Logger.startLog("log_peer_" + peerId +".log");
			logFile = new FileOutputStream("log_peer_" + peerId +".log");
			out = new OutputStreamWriter(logFile);
			printLog(peerId + " has started");
			
			//reading Common.cfg
			String commLine = "";
			try {
				//Metadata meta = new Metadata();
				BufferedReader br = new BufferedReader(new FileReader("Common.cfg"));
				while ((commLine = br.readLine()) != null) {
					String[] comm = commLine.split("\\s+");
					if (comm[0].toLowerCase().equals("NumberOfPreferredNeighbors")){
						Metadata.numberOfPreferredNeighbours = Integer.parseInt(comm[1]);
					}  
					else if (comm[0].toLowerCase().equals("UnchokingInterval")) {
						Metadata.unchokeInterval = Integer.parseInt(comm[1]);
					} 
					else if (comm[0].toLowerCase().equals("OptimisticUnchokingInterval")) {
						Metadata.optimisticUnchokeInterval = Integer.parseInt(comm[1]);
					} 
					else if (comm[0].toLowerCase().equals("FileName")) {
						Metadata.fname = comm[1];
					} 
					else if (comm[0].toLowerCase().equals("FileSize")) {
						Metadata.fsize = Integer.parseInt(comm[1]);
					} 
					else if (comm[0].toLowerCase().equals("PieceSize")) {
						Metadata.pieceLength = Integer.parseInt(comm[1]);
					} 
				}

				br.close();
			}
			catch(FileNotFoundException e) {
				System.out.println(peerId + ": " + "encountered FileNotFoundException while reading CommonConfiguration file");
			}
			catch(IOException e) {
				System.out.println(peerId + ": " + "encountered IOException while reading CommonConfiguration file");
			}
				
			
			
			//read PeerInfo File
			String peerLine;
			try {
				BufferedReader br = new BufferedReader(new FileReader("PeerInfo.cfg"));
				int i = 0;
				while ((peerLine = br.readLine()) != null) {
					String[] peer = peerLine.split("\\s+");
					peerMap.put(peer[0], new Peer(peer[0], peer[1], peer[2], Integer.parseInt(peer[3]), i) );
					i++;
				}
				br.close();
			} 
			catch(FileNotFoundException e) {
				System.out.println(peerId + ": " + "encountered FileNotFoundException while reading CommonConfiguration file");
			}
			catch(IOException e) {
				System.out.println(peerId + ": " + "encountered IOException while reading CommonConfiguration file");
			}
			
			//initializeNeighbours();
			for (String pId : peerMap.keySet()) {
				if (!pId.equals(peerId)) {
					preferedNeighboursMap.put(pId, peerMap.get(pId));
				}
			}
			
			int flag = 0;
			Iterator<String> itr = peerMap.keySet().iterator();
			for(int i=0;i<peerMap.size();i++) {
				Peer peer = peerMap.get(itr.next());
				if(peerId.equalsIgnoreCase(peer.peerId)) {
					pProc.portNum = Integer.parseInt(peerMap.get(itr.next()).peerPort);
					pProc.pIndx = peerMap.get(itr.next()).peerIndex;
					//Peer p = new Peer();
					if(peer.isFirst()==1) {
						flag = 1;
						break;
					}
					
				}
			}
			
			
		    // Initialize the BitOperation class 
			
			bitOperation = new BitOperation();
			if(flag==1) {
				bitOperation.initializeBitOp(peerId, 1);
			}
			else if (flag== 0) {
				bitOperation.initializeBitOp(peerId, 0);
			}
			
			
			if(flag == 1) {
				try
				{
					pProc.socket = new ServerSocket(pProc.portNum);
					
					//instantiates and starts Listening Thread
					pProc.listener = new Thread(new Server(pProc.socket, peerId));
					pProc.listener.start();
				}
				catch(SocketTimeoutException e)
				{
					printLog(peerId + " timed out : " + e.toString());
					out.close();
					logFile.close();
					e.printStackTrace();
					System.exit(0);
				}
				catch(IOException e)
				{
					printLog(peerId + " error in IO: " + pProc.portNum + e.toString());
					out.close();
					logFile.close();
					e.printStackTrace();
					System.exit(0);
				}
				catch (Exception e) {
					printLog(peerId + " encountered an exception: " + pProc.portNum + e.toString());
					out.close();
					logFile.close();
					e.printStackTrace();
					System.exit(0);
				}
			}
			else {
				try {
					File peerFile = new File(peerId);
					peerFile.mkdir();
					byte b = 0;
					File file = new File(peerId, Metadata.fname);
					OutputStream op = new FileOutputStream(file, true);
					for (int i = 0; i < Metadata.fsize; i++)
						op.write(b);
					op.close();
				} 
				catch (Exception e) {
					printLog(peerId + ": ERROR while creating peer file : " + e.getMessage());
				}
				
				for (String pId : peerMap.keySet()) {
					Peer peer = peerMap.get((pId));
					if (pProc.pIndx > peer.peerIndex) {
						Thread thread = new Thread(new ConnectionManager(peer.peerIP, Integer.parseInt(peer.peerPort), 1, peerId));
						fromList.add(thread);
						thread.start();
					}
				}
				
				try {
					pProc.socket = new ServerSocket(pProc.portNum);
					pProc.listener = new Thread(new Server(pProc.socket, peerId));
					pProc.listener.start();
				} catch (SocketTimeoutException e) {
					e.printStackTrace();
					System.exit(0);
					//add logger here
				}
				
				catch (IOException e) {
					e.printStackTrace();
					System.exit(0);
					//add logger here
				}
			}
			timer = new Timer();
			timer.schedule(new Neighbours(), Metadata.unchokeInterval * 1000 * 0, Metadata.unchokeInterval * 1000);
			
			
		}
		catch(Exception e)
		{
			printLog(peerId + " encountered exception : " + e.getMessage() );
		}
		finally
		{
			printLog(peerId + " Exiting Peer process...");
			out.close();
			logFile.close();
//	        e.printStackTrace();
			System.exit(0);
		}
	}
	

    public static void printLog(String msg)
	{
    	String time = Calendar.getInstance().getTime().toString();
    	try {
			out.write(time +": Peer " + msg + '\n');
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println(time + ": Peer " + msg);
	}
	
}
