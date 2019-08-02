// Myungsuk Moon
// myungsuk.moon@stonybrook.edu

package rasclCompiler;

import java.io.FileNotFoundException;

import rasclCompiler.parser.Parser;

public class rasclCompiler {
	
	public static void main(String[] args) throws FileNotFoundException {
		// Initialize parser and it will drive the compiler
		Parser p = new Parser();
		p.initParser(args[0], args[1]);
	}
	
}
