// Myungsuk Moon
// myungsuk.moon@stonybrook.edu

package rasclCompiler.symbolTableManager;

public class TestSymbolTableManager {
	
	public static void main(String[] args) {
		// Initialize symbol table manager
		SymbolTableManager stm = new SymbolTableManager();
		stm.initSymbolTable();
		
		// Add three symbols
		stm.addSymbol("temperature");
		stm.addSymbol("velocity");
		stm.addSymbol("temp");
//		stm.addSymbol("thisisaverylonginputfoooooooooooooooooooooooooooooooooooooo");
//		stm.addSymbol("");
		
		// Test symbolInTable
		System.out.println("temperature in table? " + stm.symbolInTable("temperature"));
		System.out.println("bang in table? " + stm.symbolInTable("bang"));
//		System.out.println("long input in table? " + 
//							stm.symbolInTable("thisisaverylonginputfoooooooooooooooooooooooooooooooooooooo"));
//		System.out.println("empty string input in table? " + stm.symbolInTable(""));
		
		// Add attribute to symbols
		stm.addAttributeToSymbol("temperature", SymbolType.INT, 800000);
		stm.addAttributeToSymbol("velocity", SymbolType.FLOAT, 800020);
		stm.addAttributeToSymbol("temperature", SymbolType.ARRAY, 800040);
//		stm.addAttributeToSymbol("thisisaverylonginputfoooooooooooooooooooooooooooooooooooooo", 
//									SymbolType.ARRAY, 800200);
//		stm.addAttributeToSymbol("", SymbolType.CHAR, 800300);
		
		// Test getSymbol
		System.out.println(stm.getSymbol("temperature"));
		System.out.println(stm.getSymbol("velocity"));
		System.out.println(stm.getSymbol("bang"));
//		System.out.println(stm.getSymbol("thisisaverylonginputfoooooooooooooooooooooooooooooooooooooo"));
//		System.out.println(stm.getSymbol(""));
	}

}
