// Myungsuk Moon
// myungsuk.moon@stonybrook.edu

package rasclCompiler.lexicalAnalyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Scanner;

public class LexicalAnalyzer {
	
	HashMap<Character, Integer> eventIndex;
	HashMap<Integer, TokenType> stateToToken;
	int[][] stateTable;
	char[] charArray;
	int cursor;
	
	/* Accepts filename with the source code to be analyzed.
	 * Opens file and creates a buffer for text it will read and analyze.
	 * Returns 1 if the file open succeeded or 0 otherwise.
	 */ 
	public int initLexer(String fileName) {	
		try {	
		// Open file with the given fileName
		Scanner s = new Scanner(new File(fileName));
		
		// Read the whole text from a file
		s.useDelimiter("\\Z");
		charArray = s.next().toCharArray();
		
		// The cursor starts at zero
		cursor = 0;
		
		// Define eventIndex
		eventIndex = new HashMap<Character, Integer>();
		eventIndex.put(' ', 0);
		eventIndex.put('\n', 0);
		eventIndex.put('\t', 0);
		eventIndex.put('.', 2);
		eventIndex.put(';', 4);
		eventIndex.put('(', 5);
		eventIndex.put(')', 6);
		eventIndex.put(',', 7);
		eventIndex.put('{', 8);
		eventIndex.put('}', 9);
		eventIndex.put('[', 10);
		eventIndex.put(']', 11);
		eventIndex.put('=', 12);
		eventIndex.put('<', 13);
		eventIndex.put('>', 14);
		eventIndex.put('+', 15);
		eventIndex.put('-', 16);
		eventIndex.put('*', 17);
		eventIndex.put('/', 18);
		eventIndex.put('!', 20);
		eventIndex.put('&', 21);
		eventIndex.put('|', 22);
		
		// Define stateToToken
		stateToToken = new HashMap<Integer, TokenType>();
		stateToToken.put(1, TokenType.ICONST);
		stateToToken.put(2, TokenType.FCONST);
		stateToToken.put(3, TokenType.ID);
		stateToToken.put(4, TokenType.SEMICOLON);
		stateToToken.put(5, TokenType.LPAREN);
		stateToToken.put(6, TokenType.RPAREN);
		stateToToken.put(7, TokenType.COMMA);
		stateToToken.put(8, TokenType.LBRACE);
		stateToToken.put(9, TokenType.RBRACE);
		stateToToken.put(10, TokenType.LBRACKET);
		stateToToken.put(11, TokenType.RBRACKET);
		stateToToken.put(12, TokenType.ASSIGN);
		stateToToken.put(13, TokenType.EQ);
		stateToToken.put(14, TokenType.LT);
		stateToToken.put(15, TokenType.GT);
		stateToToken.put(16, TokenType.PLUS);
		stateToToken.put(17, TokenType.MINUS);
		stateToToken.put(18, TokenType.MULT);
		stateToToken.put(19, TokenType.DIV);
		stateToToken.put(20, TokenType.COMMENT);
		stateToToken.put(21, TokenType.COMMENT);
		stateToToken.put(22, TokenType.COMMENT);
		stateToToken.put(23, TokenType.NOT);
		stateToToken.put(24, TokenType.BITAND);
		stateToToken.put(25, TokenType.AND);
		stateToToken.put(26, TokenType.BITOR);
		stateToToken.put(27, TokenType.OR);
		stateToToken.put(28, TokenType.IF);
		stateToToken.put(29, TokenType.ELSE);
		stateToToken.put(30, TokenType.WHILE);
		stateToToken.put(31, TokenType.INT);
		stateToToken.put(32, TokenType.FLOAT);
		stateToToken.put(33, TokenType.PRINT);
		stateToToken.put(34, TokenType.READ);
		
		// Create dfa transition table
		stateTable = new int[][] {
			{	0,	1,	0,	3,	4,	5,	6,	7,	8,	9,	10,	11,	12,	14,	15,	16,	17,	18,	19,	0,	23,	24,	26	},
			{	-1,	1,	2,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1	},
			{	-1,	2,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1	},
			{	-1,	-1,	-1,	3,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1	},
			{	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1	},
			{	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1	},
			{	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1	},
			{	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1	},
			{	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1	},
			{	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1	},
			{	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1	},
			{	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1	},
			{	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	13,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1	},
			{	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1	},
			{	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1	},
			{	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1	},
			{	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1	},
			{	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1	},
			{	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1	},
			{	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	20,	-1,	-1,	-1,	-1,	-1	},
			{	20,	20,	20,	20,	20,	20,	20,	20,	20,	20,	20,	20,	20,	20,	20,	20,	20,	21,	20,	20,	20,	20,	20	},
			{	20,	20,	20,	20,	20,	20,	20,	20,	20,	20,	20,	20,	20,	20,	20,	20,	20,	21,	22,	20,	20,	20,	20	},
			{	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1	},
			{	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1	},
			{	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	25,	-1	},
			{	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1	},
			{	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	27	},
			{	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1,	-1	}
		};
		
		// Return 1 if file open was successful
		return 1;
		}
		catch(FileNotFoundException e) { return 0; }
	}
	
	/* Scans the text and returns TokenInfo with the
	 * information for the next token it will recognize.
	 * This could return Token with null values at the end.
	 */
	public Token getNextToken() {
		// Begin from the starting state
		// and initialize tokenBuilder to build token
		int currStateIndex = 0;
		int nextStateIndex = 0;
		StringBuilder tokenBuilder = new StringBuilder();
		
		// Run state machine until reaching
		// the accept state and the end of the charArray.
		// Continues when nextStateIndex is zero.
		// Breaks when nextStateIndex is a negative integer.
		while(cursor < charArray.length && nextStateIndex >= 0) {
			// Get nextStateIndex from stateTable
			int currEventIndex = getEventIndex(nextStateIndex, charArray[cursor]);
			currStateIndex = nextStateIndex;
			
			// Done early when table returns a negative value
			if(stateTable[currStateIndex][currEventIndex] < 0) {
				break;
			}
			nextStateIndex = stateTable[currStateIndex][currEventIndex];
			
			// Append current character to the tokenBuilder
			if(nextStateIndex > 0) {
				tokenBuilder.append(charArray[cursor]);
			}
			cursor++;
		}
		// Special case at the end of the charArray
		if(cursor == charArray.length) {
			currStateIndex = nextStateIndex;
		}
		// Create and return TokenInfo. Return EOF if the tokenType is null.
		TokenType tokenType = stateToToken.get(
								checkForKeyword(currStateIndex, tokenBuilder.toString())); 
		if(tokenType == null) {
			return new Token(TokenType.ENDFILE, "EOF");
		}
		else {
			return new Token(stateToToken.get(
								checkForKeyword(currStateIndex, tokenBuilder.toString())), 
								tokenBuilder.toString());
		}
	}
	
	private int getEventIndex(int state, char c) {
		if((c >= 'a' && c <='z') || (c >= 'A' && c <='Z') || (c == '_')) {
			return 3;
		}
		else if(c >= '0' && c <= '9') {
			return 1;
		}
		else {
			return eventIndex.get(c);
		}
	}
	
	private int checkForKeyword(int state, String tokenText) {
		if(state == 3) {
			if(tokenText.equals("if")) { return 28; }
			if(tokenText.equals("else")) { return 29; }
			if(tokenText.equals("while")) { return 30; }
			if(tokenText.equals("int")) { return 31; }
			if(tokenText.equals("float")) { return 32; }
			if(tokenText.equals("print")) { return 33; }
			if(tokenText.equals("read")) { return 34; }
			else { return state; }
		}
		else {
			return state;
		}
	}
	
}
