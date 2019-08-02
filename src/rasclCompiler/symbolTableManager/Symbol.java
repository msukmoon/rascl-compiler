// Myungsuk Moon
// myungsuk.moon@stonybrook.edu

package rasclCompiler.symbolTableManager;

public class Symbol {
		
	String id;
	SymbolType type;
	int address;
	
	public Symbol(String initId, SymbolType initType, int initAddress) {
		id = initId;
		type = initType;
		address = initAddress;
	}
	
	public String getSymbolId() {
		return id;
	}
	
	public SymbolType getSymbolType() {
		return type;
	}
	
	public void setSymbolId(String initId) {
		id = initId;
	}
	
	public void setSymbolType(SymbolType initType) {
		type = initType;
	}
	
	public String toString() {
		return "(ID: " + id + ", Type: " + type + ", Address: 0x" + address + ")";
	}
	
}
