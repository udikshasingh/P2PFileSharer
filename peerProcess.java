/**
 * 
 */

/**
 * @author shrey
 *
 */

import java.io.*;
import java.net.*;
import java.util.*;
public class peerProcess 
{
	public ServerSocket sock = null;
	public static String pID;
	public int pIndx;
	public int portNum;
	public static HashMap<String, remotePeer> remotePeerDets = new HashMap<String, remotePeer>();
	
	public Thread thread;
	public static Vector<Thread> tSender = new Vector<Thread>();
	
	public static void main (String[] args) 
	{
		try 
		{
			peerProcess pProc = new peerProcess();
			
			pID=args[0];
			Logger.startLog("log_peer_" + pID +".log");
			printLog(pID + " is started");
			
			readCommonConfig();
			readPeerConfig();
			//initializeNeighbours();
			
			//Iterator rmpIter = remotePeerDets.entrySet().iterator();
			
			boolean firstPeerFlag = false;
			Iterator<String> i = remotePeerDets.keySet().iterator();
			while(i.hasNext()) {
				remotePeer rmp = remotePeerDets.get(i.next());
				if(rmp.pId==pID)
				{
					// checks if the peer is the first peer or not
					pProc.portNum = Integer.parseInt(rmp.pPort);
					pProc.pIndx = rmp.pIndex;
					if(rmp.getIsFirst() == 1)
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
			
			if(firstPeerFlag==true) 
			{
				try
				{
					pProc.sock = new ServerSocket(pProc.portNum);
					
					//instantiates and starts Listening Thread
					pProc.thread = new Thread(new listeningThread(pProc.sock, pID));
					pProc.thread.start();
				}
				catch(SocketTimeoutException e)
				{
					printLog(pID + " encountered time-out expetion: " + e.toString());
					Logger.stopLog();
					System.exit(0);
				}
				catch(IOException e)
				{
					printLog(pID + " encountered exception while starting listening thread: " + pProc.portNum + e.toString());
					Logger.stopLog();
					System.exit(0);
				}
				catch (Exception e) {
					printLog(pID + " encountered an exception: " + pProc.portNum + e.toString());
					Logger.stopLog();
					System.exit(0);
				}
			}
			
			
			
		}
		catch(Exception e)
		{
			printLog(pID + " encountered exception : " + e.getMessage() );
		}
		finally
		{
			printLog(pID + " Exiting Peer process...");
			Logger.stopLog();
			System.exit(0);
		}
	}
	
    public static void readCommonConfig() 
    {
		
	}
    public static void readPeerConfig() 
    {
		
    }
    public static void printLog(String msg)
	{
		Logger.writeLog(Calendar.getInstance().getTime() + ": Peer " + msg);
		System.out.println(Calendar.getInstance().getTime() + ": Peer " + msg);
	}
	
}
