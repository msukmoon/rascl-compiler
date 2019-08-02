package rasclCompiler.parser;

import java.io.FileNotFoundException;
import java.io.PrintWriter;

import rasclCompiler.lexicalAnalyzer.LexicalAnalyzer;
import rasclCompiler.lexicalAnalyzer.Token;
import rasclCompiler.lexicalAnalyzer.TokenType;
import rasclCompiler.symbolTableManager.Symbol;
import rasclCompiler.symbolTableManager.SymbolTableManager;
import rasclCompiler.symbolTableManager.SymbolType;

public class Parser {
	
	LexicalAnalyzer la;
	SymbolTableManager stm;
	PrintWriter writer;
	Token currentToken;
	int tempRegCount;
	int fltTempRegCount;
	int labelCount;
	
	public void initParser(String in, String out) throws FileNotFoundException {
		// Initialize writer
		writer = new PrintWriter(out);
		
		// Initialize lexical analyzer with arg
		la = new LexicalAnalyzer();
		la.initLexer(in);
				
		// Initialize symbol table manager
		stm = new SymbolTableManager();
		stm.initSymbolTable();
		
		// Temporary register and label count starts from zero
		tempRegCount = 0;
		fltTempRegCount = 0;
		labelCount = 0;
		
		// Start with the first procedure
		currentToken = la.getNextToken();
		program();
		
		// Close writer
		writer.close();
		
		// Debug
		// System.out.println("Done");
		
		// Debug
		// System.out.println(stm);
	}
	
	// Returns true if currentToken's type
	// and any of the argument are equal.
	private boolean match(TokenType... tokenTypes) {
		TokenType currentTokenType = currentToken.getTokenType();
		for(TokenType tokenType : tokenTypes) {
			if(tokenType == currentTokenType) {
				return true;
			}
		}
		return false;
	}
	
	// Returns true if currentToken's type
	// and the token argument are equal.
	// Throws runtime exception otherwise.
	private boolean match(TokenType tokenType, String functionName, String expectedToken) {
		if(tokenType == currentToken.getTokenType()) {
			return true;
		}
		else {
			throw new ParseException(functionName, expectedToken, currentToken.getTokenText());
		}
	}
	
	private void genMessage(String message) {
		writer.println(message);
	}
	
	private void genInst(String op, String arg1, String arg2, String res) {
		writer.println(op + ", " + arg1 + ", " + arg2 + ", " + res);
	}
	
	private String getNewTempReg() {
		return "T" + tempRegCount++;
	}
	
	private String getNewFltTempReg() {
		return "FT" + fltTempRegCount++;
	}
	
	private String getNewLabel() {
		return "L" + labelCount++;
	}
	
	// program -> decllist bstatementlist EOF
	private void program() {
		if(match(TokenType.INT, TokenType.FLOAT, TokenType.LBRACE)) {
			// Debug
			// System.out.println("program -> decllist bstatementlist EOF");
			// decllist
			genInst("\n.segment", "0", "0", ".data");
			decllist();
			// bstatementlist
			genInst("\n.segment", "0", "0", ".text");
			bstatementlist();
			// ENDFILE
			if(match(TokenType.ENDFILE, "program", "ENDFILE")) {
				currentToken = la.getNextToken();
			}
		}
		// ERROR
		else {
			throw new ParseException("program", "INT, FLOAT or '{'", currentToken.getTokenText());
		}
	}
	
	// decllist -> decl decllist
	// decllist -> e
	private void decllist() {
		if(match(TokenType.INT, TokenType.FLOAT)) {
			// Debug
			// System.out.println("decllist -> decl decllist");
			// decl
			decl();
			// decllist
			decllist();
		}
		// EPSILON
		// Debug
		// else { System.out.println("decllist -> e"); }
	}
	
	// bstatementlist -> LBRACE statementlist RBRACE
	private void bstatementlist() {
		// LBRACE
		if(match(TokenType.LBRACE)) {
			// Debug
			// System.out.println("bstatementlist -> LBRACE statementlist RBRACE");
			currentToken = la.getNextToken();
			// statementlist
			statementlist();
			// RBRACE
			if(match(TokenType.RBRACE, "bstatementlist", "'}'")) {
				currentToken = la.getNextToken();
			}
		}
		// ERROR
		else {
			throw new ParseException("bstatementlist", "'{'", currentToken.getTokenText());
		}
	}
	
	// statementlist -> statement SEMICOLON statementlist
	// statementlist -> e
	private void statementlist() {
		if(match(TokenType.WHILE, TokenType.IF, TokenType.ID, TokenType.PRINT, TokenType.READ)) {
			// Debug
			// System.out.println("statementlist -> statement SEMICOLON statementlist");
			// statement
			statement();
			// SEMICOLON
			if(match(TokenType.SEMICOLON, "statementlist", "';'")) {
				currentToken = la.getNextToken();
				// statementlist
				statementlist();
			}
		}
		// EPSILON
		// Debug
		// else { System.out.println("statementlist -> e"); }
	}
	
	// decl -> typespec variablelist
	private void decl() {
		if(match(TokenType.INT, TokenType.FLOAT)) {
			// Debug
			// System.out.println("decl -> typespec variablelist");
			// typespec
			Attribute attr = typespec();	// Receive attr from typespec.
			// variablelist
			variablelist(attr);				// Pass attr to variablelist
		}
		else {
			throw new ParseException("decl", "INT or FLOAT", currentToken.getTokenText());
		}
	}
	
	// variablelist -> variable variablelisttail
	private void variablelist(Attribute iattr) {
		if(match(TokenType.ID)) {
			// Debug
			// System.out.println("variablelist -> variable variablelisttail");
			// variable
			variable(iattr);
			// variablelisttail
			variablelisttail(iattr);
		}
		else {
			throw new ParseException("variablelist", "ID", currentToken.getTokenText());
		}
	}
	
	// variablelisttail -> COMMA variable variablelisttail
	// variablelisttail -> SEMICOLON
	private void variablelisttail(Attribute iattr) {
		// COMMA
		if(match(TokenType.COMMA)) {
			// Debug
			// System.out.println("variablelisttail -> COMMA variable variablelisttail");
			currentToken = la.getNextToken();
			// variable
			variable(iattr);
			// variablelisttail
			variablelisttail(iattr);
		}
		// SEMICOLON
		else if(match(TokenType.SEMICOLON)) {
			// Debug
			// System.out.println("variablelisttail -> SEMICOLON");
			currentToken = la.getNextToken();
		}
		// ERROR
		else {
			throw new ParseException("variablelisttail", "',' or ';'", currentToken.getTokenText());
		}
	}
	
	// typespec -> INT
	// typespec -> FLOAT
	private Attribute typespec() {
		// INT
		if(match(TokenType.INT)) {
			// Debug
			// System.out.println("typespec -> INT");
			currentToken = la.getNextToken();
			// Return attr INT
			return new Attribute(null, SymbolType.INT);
		}
		// FLOAT
		else if(match(TokenType.FLOAT)) {
			// Debug
			// System.out.println("typespec -> FLOAT");
			currentToken = la.getNextToken();
			// Return attr FLOAT
			return new Attribute(null, SymbolType.FLOAT);
		}
		// ERROR
		else {
			throw new ParseException("typespec", "INT or FLOAT", currentToken.getTokenText());
		}
	}
	
	// variable -> ID variabletail
	private Attribute variable(Attribute iattr) {
		if(iattr == null) {
			// Debug
			// System.out.println("variable -> ID variabletail");
			// Get the identifier from the symbol table
			Symbol symbol = stm.getSymbol(currentToken.getTokenText());
			String id = symbol.getSymbolId();
			SymbolType type = symbol.getSymbolType();
			currentToken = la.getNextToken();
			// variabletail
			// Set attr type later because arraydimtail
			// will think it is in the declaration phase
			Attribute attr = variabletail(new Attribute(id, null));
			attr.setType(type);
			return attr;
		}
		// ID
		else if(match(TokenType.ID)) {
			// Debug
			// System.out.println("variable -> ID variabletail");
			// Keep the token here
			String previousTokenText = currentToken.getTokenText();
			currentToken = la.getNextToken();
			// variabletail
			SymbolType iattrType = iattr.getType();
			Attribute attr = variabletail(new Attribute(previousTokenText, iattrType));
			// Plug in variable to the symbol table			
			stm.addSymbol(previousTokenText);
			stm.addAttributeToSymbol(previousTokenText, iattrType, -1);
			if(iattrType == SymbolType.INT) {
				genInst(".int", "0", attr.getSize(), previousTokenText);
			}
			else if(iattrType == SymbolType.FLOAT) {
				genInst(".float", "0", attr.getSize(), previousTokenText);
			}
			// Omit generating instructions otherwise
			return null;
		}
		// ERROR
		else {
			throw new ParseException("variable", "ID", currentToken.getTokenText());
		}
	}
	
	// variabletail -> arraydim
	// variabletail -> e
	private Attribute variabletail(Attribute iattr) {
		// arraydim
		if(match(TokenType.LBRACKET)) {
			// Debug
			// System.out.println("variabletail -> arraydim");
			return arraydim(iattr);
		}
		// EPSILON
		else {
			// Debug
			// System.out.println("variabletail -> e");
			return iattr;
		}
	}
	
	// arraydim -> LBRACKET arraydimtail
	private Attribute arraydim(Attribute iattr) {
		// LBRACKET
		if(match(TokenType.LBRACKET)) {
			// Debug
			// System.out.println("arraydim -> LBRACKET arraydimtail");
			currentToken = la.getNextToken();
			// arraydimtail
			return arraydimtail(iattr);
		}
		// ERROR
		else {
			throw new ParseException("arraydim", "'['", currentToken.getTokenText());
		}
	}
	
	// arraydimtail -> ID RBRACKET
	// arraydimtail -> ICONST RBRACKET
	private Attribute arraydimtail(Attribute iattr) {
		// ID
		if(match(TokenType.ID)) {
			// Debug
			// System.out.println("arraydimtail -> ID RBRACKET");
			String previousTokenText = currentToken.getTokenText();
			currentToken = la.getNextToken();
			// RBRACKET
			if(match(TokenType.RBRACKET, "arraydimtail", "']'")) {
				currentToken = la.getNextToken();
				String newTempReg1 = getNewTempReg();
				genInst("lw", previousTokenText, "0", newTempReg1);
				genInst("sl", newTempReg1, "2", newTempReg1);
				String newTempReg2 = getNewTempReg();
				genInst("la", iattr.getId(), "0", newTempReg2);
				String newTempReg3 = getNewTempReg();
				genInst("add", newTempReg1, newTempReg2, newTempReg3);
				return new Attribute(newTempReg3, null);
			}
			return null;
		}
		// ICONST
		else if(match(TokenType.ICONST)) {
			// Debug
			// System.out.println("arraydimtail -> ICONST RBRACKET");
			// Array declaration is done here
			if(iattr.getType() != null) {
				String previousTokenText = currentToken.getTokenText();
				currentToken = la.getNextToken();
				// RBRACKET
				if(match(TokenType.RBRACKET, "arraydimtail", "']'")) {
					currentToken = la.getNextToken();
					Attribute attr = new Attribute();
					attr.setSize(previousTokenText);
					return attr;
				}
			}
			else {
				String previousTokenText = currentToken.getTokenText();
				currentToken = la.getNextToken();
				String newTempReg1 = getNewTempReg();
				int size = Integer.parseInt(previousTokenText) * 4;
				genInst("li", Integer.toString(size), "0", newTempReg1);
				// RBRACKET
				if(match(TokenType.RBRACKET, "arraydimtail", "']'")) {
					currentToken = la.getNextToken();
					String newTempReg2 = getNewTempReg();
					genInst("la", iattr.getId(), "0", newTempReg2);
					String newTempReg3 = getNewTempReg();
					genInst("add", newTempReg1, newTempReg2, newTempReg3);
					return new Attribute(newTempReg3, null);
				}
			}
			return null;
		}
		// ERROR
		else {
			throw new ParseException("arraydimtail", "ID or ICONST", currentToken.getTokenText());
		}
	}
	
	// statement -> whilestatement
	// statement -> ifstatement
	// statement -> assignmentexpression
	// statement -> printexpression
	// statement -> readstatement
	private void statement() {
		// whilestatement
		if(match(TokenType.WHILE)) {
			// Debug
			// System.out.println("statement -> whilestatement");
			genMessage("# Start WHILE statement ---");
			whilestatement();
		}
		// ifstatement
		else if(match(TokenType.IF)) {
			// Debug
			// System.out.println("statement -> ifstatement");
			genMessage("# Start IF statement ---");
			ifstatement();
		}
		// assignmentexpression
		else if(match(TokenType.ID)) {
			// Debug
			// System.out.println("statement -> assignmentexpression");
			genMessage("# Start ASSIGN statement ---");
			assignmentexpression();
		}
		// printexpression
		else if(match(TokenType.PRINT)) {
			// Debug
			// System.out.println("statement -> printexpression");
			genMessage("# Start PRINT statement ---");
			printexpression();
		}
		// readstatement
		else if(match(TokenType.READ)) {
			// Debug
			// System.out.println("statement -> readstatement");
			genMessage("# Start READ statement ---");
			readstatement();
		}
		// ERROR
		else {
			throw new ParseException("statement", "WHILE, IF, ID, PRINT or READ", currentToken.getTokenText());
		}
	}
	
	// assignmentexpression -> variable ASSIGN otherexpression
	private void assignmentexpression() {
		// variable
		if(match(TokenType.ID)) {
			// Debug
			// System.out.println("assignmentexpression -> variable ASSIGN otherexpression");
			Attribute attr1 = variable(null);
			// ASSIGN
			if(match(TokenType.ASSIGN, "assignmentexpression", "'='")) {
				currentToken = la.getNextToken();
				// otherexpression
				Attribute attr2 = otherexpression();
				// Check for type coercion
				if(attr1.getType() == SymbolType.FLOAT && attr2.getType() == SymbolType.INT) {
					String newFltTempReg = getNewFltTempReg();
					genInst("toFloat", attr2.getId(), "0", newFltTempReg);
					genInst("sw", newFltTempReg, "0", attr1.getId());
				}
				else {
					genInst("sw", attr2.getId(), "0", attr1.getId());
				}
			}
		}
		// ERROR
		else {
			throw new ParseException("assignmentexpression", "ID", currentToken.getTokenText());
		}
	}
	
	// printexpression -> PRINT variable
	private void printexpression() {
		// PRINT
		if(match(TokenType.PRINT)) {
			// Debug
			// System.out.println("printexpression -> PRINT variable");
			currentToken = la.getNextToken();
			// variable
			Attribute attr = variable(null);
			String newTempReg = getNewTempReg();
			genInst("lw", attr.getId(), "0", newTempReg);
			genInst("syscall", "2", newTempReg, "0");
		}
		// ERROR
		else {
			throw new ParseException("printexpression", "PRINT", currentToken.getTokenText());
		}
	}
	
	// otherexpression -> term otherexpressiontail
	private Attribute otherexpression() {
		// term
		if(match(TokenType.ID, TokenType.ICONST, TokenType.FCONST, TokenType.LPAREN, TokenType.MINUS)) {
			// Debug
			// System.out.println("otherexpression -> term otherexpressiontail");
			// term
			Attribute attr = term();
			// otherexpressiontail
			return otherexpressiontail(attr);
		}
		// ERROR
		else {
			throw new ParseException("otherexpression", "ID, ICONST, FCONST, '(' or '-'", currentToken.getTokenText());
		}
	}
	
	// otherexpressiontail -> PLUS term otherexpressiontail
	// otherexpressiontail -> MINUS term otherexpressiontail
	// otherexpressiontail -> e
	private Attribute otherexpressiontail(Attribute iattr) {
		if(match(TokenType.PLUS)) {
			// Debug
			// System.out.println("otherexpressiontail -> PLUS term otherexpressiontail");
			currentToken = la.getNextToken();
			// term
			Attribute attr = term();
			// Check for type coercion
			if(attr.getType() == SymbolType.FLOAT && iattr.getType() == SymbolType.FLOAT) {
				String newFltTempReg = getNewFltTempReg();
				genInst("fadd", iattr.getId(), attr.getId(), newFltTempReg);
				attr.setId(newFltTempReg);
				attr.setType(SymbolType.FLOAT);
			}
			else if(attr.getType() == SymbolType.FLOAT && iattr.getType() != SymbolType.FLOAT) {
				String newFltTempReg1 = getNewFltTempReg();
				genInst("toFloat", iattr.getId(), "0", newFltTempReg1);
				String newFltTempReg2 = getNewFltTempReg();
				genInst("fadd", newFltTempReg1, attr.getId(), newFltTempReg2);
				attr.setId(newFltTempReg2);
				attr.setType(SymbolType.FLOAT);
			}
			else if(attr.getType() != SymbolType.FLOAT && iattr.getType() == SymbolType.FLOAT) {
				String newFltTempReg1 = getNewFltTempReg();
				genInst("toFloat", attr.getId(), "0", newFltTempReg1);
				String newFltTempReg2 = getNewFltTempReg();
				genInst("fadd", iattr.getId(), newFltTempReg1, newFltTempReg2);
				attr.setId(newFltTempReg2);
				attr.setType(SymbolType.FLOAT);
			}
			else {
				String newTempReg = getNewTempReg();
				genInst("add", iattr.getId(), attr.getId(), newTempReg);
				attr.setId(newTempReg);
			}
			// otherexpressiontail
			return otherexpressiontail(attr);
		}
		else if(match(TokenType.MINUS)) {
			// Debug
			// System.out.println("otherexpressiontail -> MINUS term otherexpressiontail");
			currentToken = la.getNextToken();
			// term
			Attribute attr = term();
			// Check for type coercion
			if(attr.getType() == SymbolType.FLOAT && iattr.getType() == SymbolType.FLOAT) {
				String newFltTempReg = getNewFltTempReg();
				genInst("fsub", iattr.getId(), attr.getId(), newFltTempReg);
				attr.setId(newFltTempReg);
				attr.setType(SymbolType.FLOAT);
			}
			else if(attr.getType() == SymbolType.FLOAT && iattr.getType() != SymbolType.FLOAT) {
				String newFltTempReg1 = getNewFltTempReg();
				genInst("toFloat", iattr.getId(), "0", newFltTempReg1);
				String newFltTempReg2 = getNewFltTempReg();
				genInst("fsub", newFltTempReg1, attr.getId(), newFltTempReg2);
				attr.setId(newFltTempReg2);
				attr.setType(SymbolType.FLOAT);
			}
			else if(attr.getType() != SymbolType.FLOAT && iattr.getType() == SymbolType.FLOAT) {
				String newFltTempReg1 = getNewFltTempReg();
				genInst("toFloat", attr.getId(), "0", newFltTempReg1);
				String newFltTempReg2 = getNewFltTempReg();
				genInst("fsub", iattr.getId(), newFltTempReg1, newFltTempReg2);
				attr.setId(newFltTempReg2);
				attr.setType(SymbolType.FLOAT);
			}
			else {
				String newTempReg = getNewTempReg();
				genInst("sub", iattr.getId(), attr.getId(), newTempReg);
				attr.setId(newTempReg);
			}
			// otherexpressiontail
			return otherexpressiontail(attr);
		}
		// EPSILON
		else {
			// Debug
			// System.out.println("otherexpressiontail -> e");
			return iattr;
		}
	}
	
	// term -> factor termtail
	private Attribute term() {
		// factor
		if(match(TokenType.ID, TokenType.ICONST, TokenType.FCONST, TokenType.LPAREN, TokenType.MINUS)) {
			// Debug
			// System.out.println("term -> factor termtail");
			Attribute attr = factor();
			// termtail
			return termtail(attr);
		}
		else {
			throw new ParseException("term", "ID, ICONST, FCONST, '(' or '-'", currentToken.getTokenText());
		}
	}
	
	// termtail -> MULT factor termtail
	// termtail -> DIV factor termtail
	// termtail -> e
	private Attribute termtail(Attribute iattr) {
		// MULT
		if(match(TokenType.MULT)) {
			// Debug
			// System.out.println("termtail -> MULT factor termtail");
			currentToken = la.getNextToken();
			// factor
			Attribute attr = factor();
			// Check for type coercion
			if(attr.getType() == SymbolType.FLOAT && iattr.getType() == SymbolType.FLOAT) {
				String newFltTempReg = getNewFltTempReg();
				genInst("fmul", iattr.getId(), attr.getId(), newFltTempReg);
				attr.setId(newFltTempReg);
				attr.setType(SymbolType.FLOAT);
			}
			else if(attr.getType() == SymbolType.FLOAT && iattr.getType() != SymbolType.FLOAT) {
				String newFltTempReg1 = getNewFltTempReg();
				genInst("toFloat", iattr.getId(), "0", newFltTempReg1);
				String newFltTempReg2 = getNewFltTempReg();
				genInst("fmul", newFltTempReg1, attr.getId(), newFltTempReg2);
				attr.setId(newFltTempReg2);
				attr.setType(SymbolType.FLOAT);
			}
			else if(attr.getType() != SymbolType.FLOAT && iattr.getType() == SymbolType.FLOAT) {
				String newFltTempReg1 = getNewFltTempReg();
				genInst("toFloat", attr.getId(), "0", newFltTempReg1);
				String newFltTempReg2 = getNewFltTempReg();
				genInst("fmul", iattr.getId(), newFltTempReg1, newFltTempReg2);
				attr.setId(newFltTempReg2);
				attr.setType(SymbolType.FLOAT);
			}
			else {
				String newTempReg = getNewTempReg();
				genInst("mul", iattr.getId(), attr.getId(), newTempReg);
				attr.setId(newTempReg);
			}
			// termtail
			return termtail(attr);
		}
		// DIV
		else if(match(TokenType.DIV)) {
			// Debug
			// System.out.println("termtail -> DIV factor termtail");
			currentToken = la.getNextToken();
			// factor
			Attribute attr = factor();
			// Check for type coercion
			if(attr.getType() == SymbolType.FLOAT && iattr.getType() == SymbolType.FLOAT) {
				String newFltTempReg = getNewFltTempReg();
				genInst("fdiv", iattr.getId(), attr.getId(), newFltTempReg);
				attr.setId(newFltTempReg);
				attr.setType(SymbolType.FLOAT);
			}
			else if(attr.getType() == SymbolType.FLOAT && iattr.getType() != SymbolType.FLOAT) {
				String newFltTempReg1 = getNewFltTempReg();
				genInst("toFloat", iattr.getId(), "0", newFltTempReg1);
				String newFltTempReg2 = getNewFltTempReg();
				genInst("fdiv", newFltTempReg1, attr.getId(), newFltTempReg2);
				attr.setId(newFltTempReg2);
				attr.setType(SymbolType.FLOAT);
			}
			else if(attr.getType() != SymbolType.FLOAT && iattr.getType() == SymbolType.FLOAT) {
				String newFltTempReg1 = getNewFltTempReg();
				genInst("toFloat", attr.getId(), "0", newFltTempReg1);
				String newFltTempReg2 = getNewFltTempReg();
				genInst("fdiv", iattr.getId(), newFltTempReg1, newFltTempReg2);
				attr.setId(newFltTempReg2);
				attr.setType(SymbolType.FLOAT);
			}
			else {
				String newTempReg = getNewTempReg();
				genInst("div", iattr.getId(), attr.getId(), newTempReg);
				attr.setId(newTempReg);
			}
			// termtail
			return termtail(attr);
		}
		// EPSILON
		else {
			// Debug
			// System.out.println("termtail -> e");
			return iattr;
		}
	}
	
	// factor -> variable
	// factor -> ICONST
	// factor -> FCONST
	// factor -> LPAREN otherexpression RPAREN
	// factor -> MINUS factor
	private Attribute factor() {
		// variable
		if(match(TokenType.ID)) {
			// Debug
			// System.out.println("factor -> variable");
			Attribute attr = variable(null);
			if(attr.getId() != null && attr.getType() == SymbolType.FLOAT) {
				String newFltTempReg = getNewFltTempReg();
				genInst("lw", attr.getId(), "0", newFltTempReg);
				return new Attribute(newFltTempReg, attr.getType());
			}
			String newTempReg = getNewTempReg();
			if(attr.getId() != null) {
				newTempReg = getNewTempReg();
				genInst("lw", attr.getId(), "0", newTempReg);
			}
			return new Attribute(newTempReg, attr.getType());
		}
		// ICONST
		else if(match(TokenType.ICONST)) {
			// Debug
			// System.out.println("factor -> ICONST");
			String newTempReg = getNewTempReg();
			genInst("li", currentToken.getTokenText(), "0", newTempReg);
			currentToken = la.getNextToken();
			return new Attribute(newTempReg, SymbolType.INT);
		}
		// FCONST
		else if(match(TokenType.FCONST)) {
			// Debug
			// System.out.println("factor -> FCONST");
			String newFltTempReg = getNewFltTempReg();
			genInst("li", currentToken.getTokenText(), "0", newFltTempReg);
			currentToken = la.getNextToken();
			return new Attribute(newFltTempReg, SymbolType.FLOAT);
		}
		// LPAREN
		else if(match(TokenType.LPAREN)) {
			// Debug
			// System.out.println("factor -> LPAREN otherexpression RPAREN");
			currentToken = la.getNextToken();
			// otherexpression
			Attribute attr = otherexpression();
			// RPAREN
			if(match(TokenType.RPAREN, "factor", "')'")) {
				currentToken = la.getNextToken();
			}
			return attr;
		}
		// MINUS
		else if(match(TokenType.MINUS)) {
			// Debug
			// System.out.println("factor -> MINUS factor");
			currentToken = la.getNextToken();
			// factor
			Attribute attr = factor();
			if(attr.getType() == SymbolType.FLOAT) {
				String newFltTempReg1 = getNewFltTempReg();
				genInst("li", "0", "0", newFltTempReg1);
				String newFltTempReg2 = getNewFltTempReg();
				genInst("fsub", newFltTempReg1, attr.getId(), newFltTempReg2);
				return new Attribute(newFltTempReg2, attr.getType());
			}
			else {
				String newTempReg1 = getNewTempReg();
				genInst("li", "0", "0", newTempReg1);
				String newTempReg2 = getNewTempReg();
				genInst("sub", newTempReg1, attr.getId(), newTempReg2);
				return new Attribute(newTempReg2, attr.getType());
			}
		}
		// ERROR
		else {
			throw new ParseException("factor", "ID, ICONST, FCONST, '(' or '-'", currentToken.getTokenText());
		}
	}
	
	// whilestatement -> WHILE condexpr whiletail
	private void whilestatement() {
		// WHILE
		if(match(TokenType.WHILE)) {
			// Debug
			// System.out.println("whilestatement -> WHILE condexpr whiletail");
			currentToken = la.getNextToken();
			String label1 = getNewLabel();
			String label2 = getNewLabel();
			Attribute attr = new Attribute();
			attr.setLabel(label2);
			String label3 = getNewLabel();
			genInst(".label", "0", "0", label3);
			// condexpr
			condexpr(attr);
			genInst("j", "0", "0", label1);
			genInst(".label", "0", "0", label2);
			// whiletail
			whiletail();
			genInst("j", "0", "0", label3);
			genInst(".label", "0", "0", label1);
		}
		// ERROR
		else {
			throw new ParseException("whilestatement", "WHILE", currentToken.getTokenText());
		}
	}
	
	// whiletail -> compoundstatement
	private void whiletail() {
		// compoundstatement
		if(match(TokenType.LBRACE)) {
			// Debug
			// System.out.println("whiletail -> compoundstatement");
			compoundstatement();
		}
		// ERROR
		else {
			throw new ParseException("compoundstatement", "'{'", currentToken.getTokenText());
		}
	}
	
	// compoundstatement -> LBRACE statementlist RBRACE
	private void compoundstatement() {
		// LBRACE
		if(match(TokenType.LBRACE)) {
			// Debug
			// System.out.println("compoundstatement -> LBRACE statementlist RBRACE");
			currentToken = la.getNextToken();
			// statementlist
			statementlist();
			// RBRACE
			if(match(TokenType.RBRACE, "compoundstatement", "'}'")) {
				currentToken = la.getNextToken();
			}
		}
		// ERROR
		else {
			throw new ParseException("compoundstatement", "'{'", currentToken.getTokenText());
		}
	}
	
	// ifstatement -> IF condexpr compoundstatement istail
	private void ifstatement() {
		// IF
		if(match(TokenType.IF)) {
			// Debug
			// System.out.println("ifstatement -> IF condexpr compoundstatement istail");
			currentToken = la.getNextToken();
			String label1 = getNewLabel();
			String label2 = getNewLabel();
			Attribute attr = new Attribute();
			attr.setLabel(label2);
			// condexpr
			condexpr(attr);
			genInst("j", "0", "0", label1);
			// compoundstatement
			genMessage("# Start IF statement THEN part ---");
			genInst(".label", "0", "0", label2);
			compoundstatement();
			// istail
			genMessage("# Start IF statement ELSE part ---");
			genInst(".label", "0", "0", label1);
			istail();
		}
		// ERROR
		else {
			throw new ParseException("ifstatement", "IF", currentToken.getTokenText());
		}
	}
	
	// istail -> ELSE compoundstatement
	// istail -> e
	private void istail() {
		// ELSE
		if(match(TokenType.ELSE)) {
			// Debug
			// System.out.println("istail -> ELSE compoundstatement");
			currentToken = la.getNextToken();
			// compoundstatement
			compoundstatement();
		}
		// EPSILON
		// Debug
		// else { System.out.println("istail -> e"); }
	}
	
	// condexpr -> LPAREN vorc condexprtail RPAREN
	// condexpr -> vorc condexprtail
	// condexpr -> NOT condexpr
	private void condexpr(Attribute iattr) {
		// LPAREN
		if(match(TokenType.LPAREN)) {
			// Debug
			// System.out.println("condexpr -> LPAREN vorc condexprtail RPAREN");
			currentToken = la.getNextToken();
			// vorc
			Attribute attr = vorc();
			attr.setLabel(iattr.getLabel());
			// condexprtail
			condexprtail(attr);
			// RPAREN
			if(match(TokenType.RPAREN, "condexpr", "')'")) {
				currentToken = la.getNextToken();
			}
		}
		// vorc
		else if(match(TokenType.ID, TokenType.ICONST, TokenType.FCONST)) {
			// Debug
			// System.out.println("condexpr -> vorc condexprtail");
			Attribute attr = vorc();
			attr.setLabel(iattr.getLabel());
			// condexprtail
			condexprtail(attr);
		}
		// NOT
		else if(match(TokenType.NOT)) {
			// Debug
			// System.out.println("condexpr -> NOT condexpr");
			currentToken = la.getNextToken();
			// condexpr
			condexpr(iattr);
		}
		// ERROR
		else {
			throw new ParseException("condexpr", "'(', '!', ID, ICONST or FCONST", currentToken.getTokenText());
		}
	}
	
	// condexprtail -> LT vorc
	// condexprtail -> GT vorc
	// condexprtail -> EQUAL vorc
	private void condexprtail(Attribute iattr) {
		// LT
		if(match(TokenType.LT)) {
			// Debug
			// System.out.println("condexprtail -> LT vorc");
			currentToken = la.getNextToken();
			// vorc
			Attribute attr = vorc();
			genInst("blt", iattr.getId(), attr.getId(), iattr.getLabel());
		}
		// GT
		else if(match(TokenType.GT)) {
			// Debug
			// System.out.println("condexprtail -> GT vorc");
			currentToken = la.getNextToken();
			// vorc
			Attribute attr = vorc();
			genInst("bgt", iattr.getId(), attr.getId(), iattr.getLabel());
		}
		// EQUAL
		else if(match(TokenType.EQ)) {
			// Debug
			// System.out.println("condexprtail -> EQ vorc");
			currentToken = la.getNextToken();
			// vorc
			Attribute attr = vorc();
			genInst("beq", iattr.getId(), attr.getId(), iattr.getLabel());
		}
		// ERROR
		else {
			throw new ParseException("condexprtail", "'<', '>' or EQUAL", currentToken.getTokenText());
		}
	}
	
	// vorc -> variable
	// vorc -> ICONST
	// vorc -> FCONST
	private Attribute vorc() {
		// variable
		if(match(TokenType.ID)) {
			// Debug
			// System.out.println("vorc -> variable");
			Attribute attr = variable(null);
			if(attr.getId() != null && attr.getType() == SymbolType.FLOAT) {
				String newFltTempReg = getNewFltTempReg();
				genInst("lw", attr.getId(), "0", newFltTempReg);
				return new Attribute(newFltTempReg, attr.getType());
			}
			String newTempReg = getNewTempReg();
			if(attr.getId() != null) {
				newTempReg = getNewTempReg();
				genInst("lw", attr.getId(), "0", newTempReg);
			}
			return new Attribute(newTempReg, attr.getType());
		}
		// ICONST
		else if(match(TokenType.ICONST)) {
			// Debug
			// System.out.println("vorc -> ICONST");
			String newTempReg = getNewTempReg();
			genInst("li", currentToken.getTokenText(), "0", newTempReg);
			currentToken = la.getNextToken();
			return new Attribute(newTempReg, SymbolType.INT);
		}
		// FCONST
		else if(match(TokenType.FCONST)) {
			// Debug
			// System.out.println("vorc -> FCONST");
			String newFltTempReg = getNewFltTempReg();
			genInst("li", currentToken.getTokenText(), "0", newFltTempReg);
			currentToken = la.getNextToken();
			return new Attribute(newFltTempReg, SymbolType.FLOAT);
		}
		// ERROR
		else {
			throw new ParseException("vorc", "ID, ICONST or FCONST", currentToken.getTokenText());
		}
	}
	
	// printstatement -> PRINT variable
	private void printstatement() {
		// PRINT
		if(match(TokenType.PRINT)) {
			// Debug
			// System.out.println("printstatement -> PRINT variable");
			currentToken = la.getNextToken();
			// variable
			variable(null);
		}
		// ERROR
		else {
			throw new ParseException("printstatement", "PRINT", currentToken.getTokenText());
		}
	}
	
	// readstatement -> READ variable
	private void readstatement() {
		// READ
		if(match(TokenType.READ)) {
			// Debug
			// System.out.println("readstatement -> READ variable");
			currentToken = la.getNextToken();
			// variable
			Attribute attr = variable(null);
			String newTempReg = getNewTempReg();
			genInst("syscall", "3", newTempReg, "0");		
			genInst("sw", newTempReg, "0", attr.getId());
		}
		// ERROR
		else {
			throw new ParseException("readstatement", "READ", currentToken.getTokenText());
		}
	}

}
