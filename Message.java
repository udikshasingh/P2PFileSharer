import java.io.UnsupportedEncodingException;

public class Message {

	static final String UNCHOKE = "1";
	byte[] payload = null;
	static final int TYPE = 1;
	int length = TYPE;
	String messageLength;
	byte[] size = null;
	String messageType;
	byte[] type_array = null;
	
	
	public Message(String message) {
        
        try {
            
            if (message == UNCHOKE)
            {
                this.setLength(1);
                this.setType(message);
                this.payload = null;
            }
            else
                throw new Exception("DataMessage:: Constructor - Wrong constructor selection.");
            
            
        } catch (Exception e) {
        	
            //peerProcess.log(e.toString());
        	e.printStackTrace();
            
        }
        
    }
	
	 Message(String message, byte[] payload) {

		try 
		{
			if (payload != null) {
				
                this.length = payload.length + 1;
                this.messageLength = String.valueOf((Integer)payload.length + 1); 
                this.size = intArrayToByteArray(payload.length + 1);
                if (this.size.length > 4)
                    throw new Exception("DataMessage:: Constructor - message length is too large.");
                
                this.payload = payload;
                
			} 
			else {
				
                if (message == "0" || message == "1" || message == "2" || message == "3") {
                    
                    this.length = 1;
                    this.messageLength = ((Integer)1).toString();
                    this.size = intArrayToByteArray(1);
                    this.payload = null;
                    
                }
                else
                    throw new Exception("Payload is Empty");

			}

			//this.setMessageType(message);
			try {
				
				this.messageType = message.trim();
				this.type_array = this.messageType.getBytes("UTF8");
				
			} catch (UnsupportedEncodingException e) {
				
				//peerProcess.log(e.toString());
				e.printStackTrace();
				
			}
			if (this.type_array.length > 1)
				throw new Exception("Payload size exceeds limit.");

		} catch (Exception e) {
			
			//peerProcess.log(e.toString());
			e.printStackTrace();
			
		}

	}

	
	public static byte[] encode(Message message) {
        
		byte[] messageStream = null;
        int type;
        
        try
        {
            
        	type =Integer.parseInt(message.messageType);
            if (message.size.length > 4)
                throw new Exception("Invalid message length.");
            
            else if (type < 0 || type > 7)
                throw new Exception("Invalid message type.");
            
            else if (message.messageType == null)
                throw new Exception("Invalid message type.");
            
            else if (message.size == null)
                throw new Exception("Invalid message length.");
            
            if (message.payload != null) {
            	
            	messageStream = new byte[4 + 1 + message.payload.length];
                
                System.arraycopy(message.size, 0, messageStream, 0, message.size.length);
                System.arraycopy(message.type_array, 0, messageStream, 4, 1);
                System.arraycopy(message.payload, 0, messageStream, 4 + 1, message.payload.length);
                
                
            } else {
            	messageStream = new byte[4 + 1];
                
                System.arraycopy(message.size, 0, messageStream, 0, message.size.length);
                System.arraycopy(message.type_array, 0, messageStream, 4, 1);
                
            }
            
        }
        catch (Exception e)
        {
            //peerProcess.log(e.toString());
        	e.printStackTrace();
            messageStream = null;
        }
        
        return messageStream;
    }
	
	public void setLength(int length) {
		
        this.length = length;
        this.messageLength = ((Integer)length).toString();
        this.size = intArrayToByteArray(length);
        
    }
	
	public void setType(String type) {
		
		try {
			
			this.messageType = type.trim();
			this.type_array = this.messageType.getBytes("UTF8");
			
		} 
		catch (UnsupportedEncodingException e) {
			
			//peerProcess.log(e.toString());
			e.printStackTrace();
			
		}
		
	}
	
	public static byte[] intArrayToByteArray(int value)
	{
        byte[] arr = new byte[4];
        for (int i = 0; i < 4; i++) 
        {
            int diff = (arr.length - 1 - i) * 8;
            arr[i] = (byte) ((value >>> diff) & 0xFF);
        }
        return arr;
    }

}
