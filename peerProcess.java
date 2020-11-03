import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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
	public ServerSocket sock = null;
	public static String peerId;
	public int pIndx;
	public int portNum;
	public static Map<String, Peer> peerMap = new HashMap<String, Peer>();
	
	public static  Map<String, Peer> preferedNeighboursMap = new HashMap<String, Peer>();
	
	public static Map<String, Socket> socketMap = new HashMap<String, Socket>();
	public Thread thread;
	public static Vector<Thread> tSender = new Vector<Thread>();
	public static BitOperation bitOperation = null;
	
	
	static FileOutputStream logFile;
	static OutputStreamWriter out;

	
	public static void main (String[] args) throws IOException {
		try {
			
			peerProcess pProc = new peerProcess();
			
			peerId=args[0];
			//Logger.startLog("log_peer_" + peerId +".log");
			logFile = new FileOutputStream("log_peer_" + peerId +".log");
			out = new OutputStreamWriter(logFile);
			printLog(peerId + " is started");
			
			//reading Common.cfg
			String commLine = "";
			try {
				//Metadata meta = new Metadata();
				BufferedReader br = new BufferedReader(new FileReader("Common.cfg"));
				while ((commLine = br.readLine()) != null) {
					String[] comm = commLine.split("\\s+");
					if (comm[0].toLowerCase().equals("numberofpreferredneighbors")){
						Metadata.numberOfPreferredNeighbours = Integer.parseInt(comm[1]);
					}  
					else if (comm[0].toLowerCase().equals("unchokinginterval")) {
						Metadata.unchokeInterval = Integer.parseInt(comm[1]);
					} 
					else if (comm[0].toLowerCase().equals("optimisticunchokinginterval")) {
						Metadata.optimisticUnchokeInterval = Integer.parseInt(comm[1]);
					} 
					else if (comm[0].toLowerCase().equals("filename")) {
						Metadata.fname = comm[1];
					} 
					else if (comm[0].toLowerCase().equals("filesize")) {
						Metadata.fsize = Integer.parseInt(comm[1]);
					} 
					else if (comm[0].toLowerCase().equals("piecesize")) {
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
				/*	peerMap.put(peer[0], new Peer(peer[0],
							peer[1], peer[2], Integer.parseInt(peer[3]), i) );*/
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
			
			int flag = 0;
			Iterator<String> itr = peerMap.keySet().iterator();
			for(int i=0;i<peerMap.size();i++) {
				if(peerId.equalsIgnoreCase(peerMap.get(itr.next()).peerId)) {
					pProc.portNum = Integer.parseInt(peerMap.get(itr.next()).peerPort);
					pProc.pIndx = peerMap.get(itr.next()).peerIndex;
					Peer p = new Peer();
					if(p.isFirst()==1) {
						flag = 1;
						break;
					}
					
				}
			}
			
			/*while(i.hasNext()) {
				Peer rmp = peerMap.get(i.next());
				if(rmp.peerId==peerId)
				{
					// checks if the peer is the first peer or not
					pProc.portNum = Integer.parseInt(rmp.peerPort);
					pProc.pIndx = rmp.peerIndex;
					if(rmp.isFirst() == 1)
					{
						firstPeerFlag = true;
						break;
						
					}
				}
			}*/
			
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
					pProc.sock = new ServerSocket(pProc.portNum);
					
					//instantiates and starts Listening Thread
					pProc.thread = new Thread(new listeningThread(pProc.sock, peerId));
					pProc.thread.start();
				}
				catch(SocketTimeoutException e)
				{
					printLog(peerId + " encountered time-out expetion: " + e.toString());
					out.close();
					logFile.close();
					e.printStackTrace();
					System.exit(0);
				}
				catch(IOException e)
				{
					printLog(peerId + " encountered exception while starting listening thread: " + pProc.portNum + e.toString());
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
					printLog(peerId + ": ERROR in creating the file : " + e.getMessage());
				}
//	------------------------------------------------------------------			
				
//				for(int i=0;i<peerMap.size();i++) {
//					if(pProc.pIndx>peerMap.get(itr.next()).peerIndex) {
//						
//					}
//				}
//				-----------------------------------------------------------------------------
			}
			
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
