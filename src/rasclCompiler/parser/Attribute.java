package rasclCompiler.parser;

import rasclCompiler.symbolTableManager.SymbolType;

public class Attribute {
	String id;
	SymbolType type;
	String size;
	String label;
	
	public Attribute() {
		id = null;
		type = null;
		size = "1";
		label = null;
	}
	
	public Attribute(String initId, SymbolType initType) {
		id = initId;
		type = initType;
		size = "1";
		label = null;
	}
	
	public String getId() {
		return id;
	}
	
	public SymbolType getType() {
		return type;
	}
	
	public String getSize() {
		return size;
	}
	
	public String getLabel() {
		return label;
	}
	
	public void setId(String initId) {
		id = initId;
	}
	
	public void setType(SymbolType initType) {
		type = initType;
	}
	
	public void setSize(String initSize) {
		size = initSize;
	}
	
	public void setLabel(String initLabel) {
		label = initLabel;
	}
	
	public String toString() {
		return "(ID: " + id + ", Type: " + type + 
					", Size: " + size + ", Label: " + label + ")";
	}
}
