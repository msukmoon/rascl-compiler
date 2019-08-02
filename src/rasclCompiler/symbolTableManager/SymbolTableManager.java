// Myungsuk Moon
// myungsuk.moon@stonybrook.edu

package rasclCompiler.symbolTableManager;

import java.util.HashMap;

public class SymbolTableManager {
	
	HashMap<String, Symbol> symbolTable;
	
	/*
	 * Initializes the symbol table manager
	 */
	public void initSymbolTable() {	
		// Define SymbolTable
		symbolTable = new HashMap<String, Symbol>();
	}
	
	/*
	 * Add Symbol record with only identifier to the hashmap.
	 * Returns true if the Symbol is successfully added to the hashmap.
	 */
	public boolean addSymbol(String identifier) {
		// Put symbol to the hashmap only with the identifier
		symbolTable.put(identifier, new Symbol(identifier, null, -1));
		return true;
	}
	
//	/*
//	 * Add Symbol record to the hashmap.
//	 * Returns true if the Symbol is successfully added to the hashmap.
//	 */
//	public boolean addSymbol(String identifier, Symbol symbol) {
//		// Put symbol to the hashmap only with the identifier
//		symbolTable.put(identifier, symbol);
//		return true;
//	}
	
	/*
	 * Locates Symbol in the hashmap and add new attributes to the found symbol.
	 * Returns true if the Symbol is found and is successfully updated.
	 * Returns false if the Symbol is not found.
	 */
	public boolean addAttributeToSymbol(String identifier, SymbolType attribute, int address) {
		// Hashmap does not contain identifier
		if(!symbolTable.containsKey(identifier)) {
			return false;
		}
		// Update the symbol if found
		else {
			symbolTable.put(identifier, new Symbol(identifier, attribute, address));
			return true;
		}
	}
	
	/*
	 * Returns true if the Symbol with the given identifier is found.
	 * Returns false otherwise.
	 */
	public boolean symbolInTable(String identifier) {
		return symbolTable.containsKey(identifier);
	}
	
	/*
	 * Finds and returns Symbol with the matching identifier.
	 * Returns null if the Symbol is not found.
	 */
	public Symbol getSymbol(String identifier) {
		return symbolTable.get(identifier);
	}
	
	/*
	 * Returns the String representation of symbolTable 
	 */
	public String toString() {
		return symbolTable.toString();
	}
	
}
