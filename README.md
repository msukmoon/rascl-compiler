# RASCL (Realistically, A Small C Language) Compiler written in Java
This compiler includes lexical analyzer, symbol table, parser, and intermediate code generation with semantic analysis. It compiles a very minimal set of C language to its intermediate form, called "RIF" or "rascl intermediate form", that consists of quadruples (3-address code) that looks similar to the MIPS instructions set. This could be quickly translated to another register-based architecture later, however.

You could compile this minimal set of C code:
```c
int a;
float g[10];
int b, c;
int d[10];

{
  a = 5;
  d[a] = 5;
  b = d[5];
  b = 1;
  a = 0;
  c = 0;
  while (a < 5) {
     while (c < 10) {
        g[c] = 1.0 * a * c;
     };
  };
  if (a == b)
  {
    d[a] = 5;
    c = a + -b;
  }
  else
  {
    while (b < 5) {
        c = -a * b;
	b = b + 1;
    };
  };
  print c;
}
```

to this "RIF" code with this compiler:
```

.segment, 0, 0, .data
.int, 0, 1, a
.float, 0, 10, g
.int, 0, 1, b
.int, 0, 1, c
.int, 0, 10, d

.segment, 0, 0, .text
# Start ASSIGN statement ---
li, 5, 0, T0
sw, T0, 0, a
# Start ASSIGN statement ---
lw, a, 0, T1
sl, T1, 2, T1
la, d, 0, T2
add, T1, T2, T3
li, 5, 0, T4
sw, T4, 0, T3
# Start ASSIGN statement ---
li, 20, 0, T5
la, d, 0, T6
add, T5, T6, T7
lw, T7, 0, T9
sw, T9, 0, b
# Start ASSIGN statement ---
li, 1, 0, T10
sw, T10, 0, b
# Start ASSIGN statement ---
li, 0, 0, T11
sw, T11, 0, a
# Start ASSIGN statement ---
li, 0, 0, T12
sw, T12, 0, c
# Start WHILE statement ---
.label, 0, 0, L2
lw, a, 0, T14
li, 5, 0, T15
blt, T14, T15, L1
j, 0, 0, L0
.label, 0, 0, L1
# Start WHILE statement ---
.label, 0, 0, L5
lw, c, 0, T17
li, 10, 0, T18
blt, T17, T18, L4
j, 0, 0, L3
.label, 0, 0, L4
# Start ASSIGN statement ---
lw, c, 0, T19
sl, T19, 2, T19
la, g, 0, T20
add, T19, T20, T21
li, 1.0, 0, FT0
lw, a, 0, T23
toFloat, T23, 0, FT1
fmul, FT0, FT1, FT2
lw, c, 0, T25
toFloat, T25, 0, FT3
fmul, FT2, FT3, FT4
sw, FT4, 0, T21
j, 0, 0, L5
.label, 0, 0, L3
j, 0, 0, L2
.label, 0, 0, L0
# Start IF statement ---
lw, a, 0, T27
lw, b, 0, T29
beq, T27, T29, L7
j, 0, 0, L6
# Start IF statement THEN part ---
.label, 0, 0, L7
# Start ASSIGN statement ---
lw, a, 0, T30
sl, T30, 2, T30
la, d, 0, T31
add, T30, T31, T32
li, 5, 0, T33
sw, T33, 0, T32
# Start ASSIGN statement ---
lw, a, 0, T35
lw, b, 0, T37
li, 0, 0, T38
sub, T38, T37, T39
add, T35, T39, T40
sw, T40, 0, c
# Start IF statement ELSE part ---
.label, 0, 0, L6
# Start WHILE statement ---
.label, 0, 0, L10
lw, b, 0, T42
li, 5, 0, T43
blt, T42, T43, L9
j, 0, 0, L8
.label, 0, 0, L9
# Start ASSIGN statement ---
lw, a, 0, T45
li, 0, 0, T46
sub, T46, T45, T47
lw, b, 0, T49
mul, T47, T49, T50
sw, T50, 0, c
# Start ASSIGN statement ---
lw, b, 0, T52
li, 1, 0, T53
add, T52, T53, T54
sw, T54, 0, b
j, 0, 0, L10
.label, 0, 0, L8
# Start PRINT statement ---
lw, c, 0, T55
syscall, 2, T55, 0
```

## Getting Started
- Source codes are at rascl-compiler/src
- Test files are at rascl-compiler/test
- Sample production outputs are at rascl-compiler/output
- Executable JAR file is at rascl-compiler/rascl-compiler.jar
- Run this at command with the following method:
```
java -jar rascl-compiler.jar <your/path/to/input/file/from/your/current/directory> <your/path/to/output/file/from/your/current/directory>
```
- The example command would be:
```
java -jar rascl-compiler.jar test/T66_rascl_test_all_features7.rsc output/T66_rascl_test_all_features7.rso
```
- rascl-compiler is an eclipse project, so you could easily import it to eclipse too!
- To run this project on eclipse: 
	1. Go to "Run" -> "Run Configurations"
	2. Go to "Arguments" Tab
	3. Enter your arguments in "Program Arguments" 
	4. Click Apply and Run rasclCompiler.java
- Do not forget to add test/ in front of the file name if you are using provided test files.

### Requirements
I used Java SE 11, so java version higher than or equal to 11 will be required.

## Author
**Myungsuk Moon** - [msukmoon](https://github.com/msukmoon) - jaymoon9876@gmail.com

