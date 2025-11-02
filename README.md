# Imperative Language Compiler - Frontend

## Structure

```
compiler/
├── src/main/java/com/compiler/
│   ├── Main.java                  # entry point
│   ├── Lexer.java                 # lexical analysis (JFlex)
│   ├── ImperativeLangParser.java  # syntactic analysis (CUP)
│   ├── semantic/                  # semantic analysis (from Base)
│   │   ├── SemanticAnalyzer.java
│   │   ├── SymbolTable.java
│   │   └── SemanticError.java
│   └── [AST nodes...]
├── tests/                         # test files (.txt)
└── lib/                           # dependencies
```

## Quick Start

### Compilation

```bash
cd compiler
javac -cp "lib/*" -d target/classes -sourcepath src/main/java \
    src/main/java/com/compiler/Main.java
```

### Running

```bash
# single file
java -cp "lib/*:target/classes" com.compiler.Main tests/test01_simple_variables.txt

# with debug output
java -cp "lib/*:target/classes" com.compiler.Main tests/test01_simple_variables.txt --debug

# all tests
java -cp "lib/*:target/classes" com.compiler.Main --test-all

# all tests with error details
java -cp "lib/*:target/classes" com.compiler.Main --test-all --verbose
```

## Features

### Lexical Analysis
Tokenization: keywords, identifiers, literals, operators.

### Syntactic Analysis
AST construction from tokens with grammar validation.

### Semantic Analysis
- ✅ variable declaration before use
- ✅ type checking for assignments and operations
- ✅ function parameter validation
- ✅ array index validation
- ✅ record field access control
- ✅ scope management

## Technologies

- **JFlex** — lexical analyzer generator
- **Java CUP** — parser generator
- **Java 8+** — implementation language

## Documentation

Detailed documentation for defense preparation: [DEFENSE.md](DEFENSE.md)

---

**All 13 tests pass successfully ✓**
