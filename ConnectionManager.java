import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * 
 */

/**
 * @author shrey
 *
 */
public class ConnectionManager implements Runnable {
	
	int connectionType;
	String peerId;
	Socket socket = null;
	private InputStream inputstream;
	private OutputStream outputstream;
	public ConnectionManager(String IPAddress, int portNo, int connectionType, String peerId) 
	{	
		try 
		{
			this.connectionType = connectionType;
			this.peerId = peerId;
			this.socket = new Socket(IPAddress, portNo);			
		} 
		catch (UnknownHostException e) 
		{
			e.printStackTrace();
			//peerProcess.showLog(peerId + " RemotePeerHandler : " + e.getMessage());
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			//peerProcess.showLog(peerId + " RemotePeerHandler : " + e.getMessage());
		}
		this.connectionType = connectionType;
		
		try 
		{
			inputstream = socket.getInputStream();
			outputstream = socket.getOutputStream();
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			//peerProcess.showLog(peerId + " RemotePeerHandler : " + ex.getMessage());
		}
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}

}
