# Imperative Language Compiler

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

### Running

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

### Code Generation
- Jasmin assembly code generation
- JVM bytecode compilation
- Support for all language constructs (variables, arrays, records, routines, control flow)


## Technologies

- **JFlex** — lexical analyzer generator
- **Java CUP** — parser generator
- **Jasmin** — JVM bytecode assembler
- **Maven** — build automation
- **Java 8+** — implementation language

---

**All 10 tests pass successfully**
