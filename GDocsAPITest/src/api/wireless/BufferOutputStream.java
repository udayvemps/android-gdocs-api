package api.wireless;

import java.io.IOException;
import java.io.OutputStream;

public class BufferOutputStream extends OutputStream {
	
	byte[] arr;
	
	public BufferOutputStream(){
		arr = new byte[0];
	}

	@Override
	public void write(int oneByte) throws IOException {
		byte[] tmp = new byte[arr.length + 1];
		System.arraycopy(arr, 0, tmp, 0, arr.length);
		
		int lastPos = tmp.length -1;
		tmp[lastPos] = (byte) oneByte;
		
		arr = tmp;
	}

	public byte[] getData() {
		return arr.clone();
	}
	
	

}
