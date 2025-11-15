# Imperative Language Compiler

This project implements a **compiler** for an imperative programming language.

## Compilation Process

The compiler follows these phases:
1. **Lexical Analysis** - Tokenizes source code using JFlex
2. **Syntactic Analysis** - Builds AST using Java CUP parser
3. **Semantic Analysis** - Validates types, scopes, and declarations
4. **Optimization** (optional) - Performs AST optimizations
5. **Code Generation** - Generates Jasmin assembly code
6. **Assembly** - Compiles Jasmin code to JVM bytecode (.class files)
7. **Executable Creation** - Packages .class files into executable JAR file

The output is a **standalone executable JAR file** that can be run independently:
- **JAR file**: `output/program.jar` - A complete executable that can be distributed and run on any system with JVM installed
- **Execution command**: `java -jar output/program.jar`

## Structure

```
compiler/
├── src/main/java/com/compiler/
│   ├── Main.java                  # entry point
│   ├── Lexer.java                 # lexical analysis (JFlex)
│   ├── ImperativeLangParser.java  # syntactic analysis (CUP)
│   ├── semantic/                  # semantic analysis
│   │   ├── SemanticAnalyzer.java
│   │   ├── SymbolTable.java
│   │   └── SemanticError.java
│   ├── JasminCodeGenerator.java   # code generation (Jasmin)
│   ├── optimizer/                 # AST optimizations
│   │   └── Optimizer.java
│   └── [AST nodes...]
├── tests/                         # test files (.txt)
└── lib/                           # dependencies (jasmin.jar, java-cup)
```

## Quick Start

### Build with Maven

```bash
cd compiler
mvn clean package -DskipTests
```

### Running the Compiler

```bash
# single file
java -jar target/IL-compiler.jar tests/test01_simple_variables.txt

# with debug output
java -jar target/IL-compiler.jar tests/test01_simple_variables.txt --debug

# with optimizations
java -jar target/IL-compiler.jar tests/test01_simple_variables.txt --optimize

# all tests
java -jar target/IL-compiler.jar --test-all
```

### Running Compiled Programs

After compilation, the compiler creates an executable JAR file in the `output/` directory. To run the compiled program:

```bash
# Run the compiled executable JAR file
java -jar output/program.jar
```
## Features

### Lexical Analysis
Tokenization: keywords, identifiers, literals, operators.

### Syntactic Analysis
AST construction from tokens with grammar validation.

### Semantic Analysis
- variable declaration before use
- type checking for assignments and operations
- function parameter validation
- array index validation
- record field access control
- scope management

### Optimizations
- Constant folding
- Dead code elimination
- Unused variable removal

### Code Generation (Compilation)
- **AST traversal** - The compiler traverses the AST and generates low-level code
- **Jasmin assembly code generation** - Translates AST to Jasmin assembly format (JVM IR)
- **JVM bytecode compilation** - Assembles Jasmin code into executable `.class` files using Jasmin assembler
- **Executable file creation** - Packages compiled bytecode into standalone JAR files
- **Target platform: JVM** - Generates code that runs on Java Virtual Machine
- **Standalone executables** - Creates JAR files that can be run independently without the compiler
- Support for all language constructs (variables, arrays, records, routines, control flow)

**How it works:**
1. The compiler goes through the AST and generates Jasmin assembly code
2. Instead of immediate execution, the compiler writes commands in the form of bytecode
3. Using Jasmin assembler, the final executable JAR file is created
4. The source code is transformed into JVM bytecode and saved as an executable file


## Technologies

- **JFlex** — lexical analyzer generator
- **Java CUP** — parser generator
- **Jasmin** — JVM bytecode assembler
- **Maven** — build automation
- **Java 8+** — implementation language

---
