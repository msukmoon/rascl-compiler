// Myungsuk Moon
// myungsuk.moon@stonybrook.edu

package rasclCompiler.parser;

public class ParseException extends RuntimeException {
	
	public ParseException(String functionName, String expectedToken, String receivedToken) {
		System.out.println("ParseException at " + functionName +
							": expected " + expectedToken + 
							" but received '" + receivedToken + "'");
	}
	
}
