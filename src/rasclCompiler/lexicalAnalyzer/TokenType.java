// Myungsuk Moon
// myungsuk.moon@stonybrook.edu

package rasclCompiler.lexicalAnalyzer;

public enum TokenType {
	
	SEMICOLON,
	LPAREN,
	RPAREN,
	COMMA,
	LBRACE,
	RBRACE,
	LBRACKET,
	RBRACKET,
	
	ASSIGN,
	LT,
	GT,
	EQ,
	PLUS,
	MINUS,
	MULT,
	DIV,
	NOT,
	AND,
	OR,
	BITAND,
	BITOR,
	
	IF,
	ELSE,
	WHILE,
	INT,
	FLOAT,
	PRINT,
	READ,
	
	ID,
	
	ICONST,
	FCONST,
	
	COMMENT,
	
	INVALID,
	ENDFILE,
	BACKUPCHECK,
	
}
