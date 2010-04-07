package api.wireless;

public class TestUtils {
	
	public int findDifference(String actual, String expected) {
		int index = -1;
		for(int i = 0; i < actual.length(); i++){
			char ac = actual.charAt(i);
			char ex = expected.charAt(i);
			if(ac != ex){
				index = i;
				break;
			}
		}
		return index;
	}

	public String clearWhiteSpaces(String string) {
		StringBuffer buffer = new StringBuffer();
		for(int i = 0; i < string.length(); i++){
			char ch = string.charAt(i);
			if(ch != '\n' && ch != ' '){
				buffer.append(ch);
			}
		}
		return buffer.toString();
	}
}
