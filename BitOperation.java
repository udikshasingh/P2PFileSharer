
public class BitOperation {
	
	int length;
	Piece[] pieces;

	public byte[] getBytes()
	{
		
		int size = this.length / 8;
		
		if (length % 8 != 0)
			length = length + 1;
		
		byte[] byte_array = new byte[length];
		int n = 0;
		int count = 0;
		int i;
		
		for (i = 1; i <= this.length; i++) {
			
			int present = this.pieces[i-1].isPresent;
			n = n << 1;
			if (present == 1) 
			{
				n = n + 1;
				
			} else
				n = n + 0;

			if (i % 8 == 0 && i!=0) {
				
				byte_array[count] = (byte) n;
				count++;
				n = 0;
				
			}
			
		}
		if ((i-1) % 8 != 0) {
			
			int ctr = ((size) - (size / 8) * 8);
			n = n << (8 - ctr);
			byte_array[count] = (byte) n;
			
		}
		return byte_array;
		
	}
	
}
