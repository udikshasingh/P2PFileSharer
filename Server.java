/**
 * 
 */

/**
 * @author shrey
 *
 */
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Runnable 
{
	private ServerSocket serverSock;
	private String pNum;
	Socket clientSock;
	Thread senderThrd;
	
	public Server(ServerSocket sock, String peer) 
	{
		this.serverSock = sock;
		this.pNum = peer;
	}
	
	public void run() 
	{
		while(true)
		{
			try
			{
				clientSock = serverSock.accept();
				// instantiates thread for handling individual remote peer
				//-->> need to be implemented(-> RemotePeerHandler class) :: senderThrd = new Thread(new RemotePeerHandler(remoteSocket,0,peerID));
				peerProcess.printLog(pNum + " Connection is established");
				peerProcess.tSender.add(senderThrd);
				senderThrd.start(); 
			}
			catch(Exception e)
			{
				peerProcess.printLog(this.pNum + " Exception in connection: " + e.toString());
			}
		}
	}
	
	public void releaseSocket()
	{
		try 
		{
			if(!clientSock.isClosed())
				clientSock.close();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
}

