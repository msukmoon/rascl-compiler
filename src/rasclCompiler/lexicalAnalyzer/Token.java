// Myungsuk Moon
// myungsuk.moon@stonybrook.edu

package rasclCompiler.lexicalAnalyzer;

public class Token {
	
	TokenType tokenType;
	String tokenText;
	
	public Token(TokenType initTokenType, String initTokenText) {	
		tokenType = initTokenType;
		tokenText = initTokenText;	
	}
	
	public TokenType getTokenType() {
		return tokenType;
	}
	
	public String getTokenText() {
		return tokenText;
	}
	
	public String toString() {
		if(tokenType == TokenType.ID || tokenType == TokenType.ICONST || 
				tokenType == TokenType.FCONST || tokenType == TokenType.COMMENT) {
			return tokenType + ": " + tokenText;
		}
		else {
			return "" + tokenType;
		}
	}

}
