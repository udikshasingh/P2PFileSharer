import java.io.IOException;
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
	
	public static void main (String[] args) {
		try {
			peerProcess pProc = new peerProcess();
			
			peerId=args[0];
			Logger.startLog("log_peer_" + peerId +".log");
			printLog(peerId + " is started");
			
			readCommonConfig();
			readPeerConfig();
			//initializeNeighbours();
			
			//Iterator rmpIter = remotePeerDets.entrySet().iterator();
			
			boolean firstPeerFlag = false;
			Iterator<String> i = peerMap.keySet().iterator();
			while(i.hasNext()) {
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
			}
			
			/*	
		    // Initialize the Bit field class 
						ownBitField = new BitField();
						ownBitField.initOwnBitfield(peerID, isFirstPeer?1:0);
						
						messageProcessor = new Thread(new MessageProcessor(peerID));
						messageProcessor.start();
			*/
			
			if(firstPeerFlag==true) {
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
					Logger.stopLog();
					System.exit(0);
				}
				catch(IOException e)
				{
					printLog(peerId + " encountered exception while starting listening thread: " + pProc.portNum + e.toString());
					Logger.stopLog();
					System.exit(0);
				}
				catch (Exception e) {
					printLog(peerId + " encountered an exception: " + pProc.portNum + e.toString());
					Logger.stopLog();
					System.exit(0);
				}
			}
			
			
			
		}
		catch(Exception e)
		{
			printLog(peerId + " encountered exception : " + e.getMessage() );
		}
		finally
		{
			printLog(peerId + " Exiting Peer process...");
			Logger.stopLog();
			System.exit(0);
		}
	}
	
    public static void readCommonConfig() {
		
	}
    public static void readPeerConfig() {
		
    }
    public static void printLog(String msg)
	{
		Logger.writeLog(Calendar.getInstance().getTime() + ": Peer " + msg);
		System.out.println(Calendar.getInstance().getTime() + ": Peer " + msg);
	}
	

}
