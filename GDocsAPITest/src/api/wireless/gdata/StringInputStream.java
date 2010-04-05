package api.wireless.gdata;

import java.io.IOException;
import java.io.InputStream;

public class StringInputStream extends InputStream {

	byte[] buffer;
	int index;
	
	
	public StringInputStream(String string){
		buffer = string.getBytes();
		index = 0;
	}
	
	@Override
	public int read() throws IOException {
		if(index < buffer.length){
			return (int) buffer[index++];
		}
		return -1;
	}

}
