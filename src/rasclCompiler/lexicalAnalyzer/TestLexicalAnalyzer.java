// Myungsuk Moon
// myungsuk.moon@stonybrook.edu

package rasclCompiler.lexicalAnalyzer;

import java.util.Scanner;

public class TestLexicalAnalyzer {

	public static void main(String[] args) {
		// Read file name from user
		Scanner s = new Scanner(System.in);	
		System.out.print("Enter file name: ");
		String fileName = s.nextLine();
		s.close();
		
		// Call initLexer with fileName
		LexicalAnalyzer la = new LexicalAnalyzer();
		la.initLexer(fileName);
		
		// Call getNextToken repeatedly
		// until it returns Token with null values.
		Token t;
		while((t = la.getNextToken()).getTokenType() != TokenType.ENDFILE) {
			System.out.println(t);
		}
		System.out.println(t);
	}

}
