# Imperative Language Compiler - Frontend + Optimizer

Four-phase compiler frontend: **Lexer → Parser → Semantic Analyzer → Optimizer**

## Structure

```
compiler/
├── src/main/java/com/compiler/
│   ├── Main.java                         # entry point
│   ├── Lexer.java                        # lexical analysis (JFlex)
│   ├── ImperativeLangParser.java         # syntactic analysis (CUP)
│   ├── semantic/                         # semantic analysis
│   │   ├── SemanticAnalyzer.java         # checks (from Base)
│   │   ├── SymbolTable.java             # scope management
│   │   └── SemanticError.java           # error representation
│   ├── optimizer/                        # AST optimizations
│   │   └── Optimizer.java               # 3 optimizations
│   └── [AST nodes...]                    # Expression, Statement, etc.
├── tests/                                # test files (.txt)
├── tests/optimization/                   # optimization demos
└── lib/                                  # dependencies
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

# with optimization
java -cp "lib/*:target/classes" com.compiler.Main tests/test01_simple_variables.txt --optimize

# with both debug and optimization
java -cp "lib/*:target/classes" com.compiler.Main tests/test01_simple_variables.txt --debug --optimize

# all tests
java -cp "lib/*:target/classes" com.compiler.Main --test-all

# all tests with optimization
java -cp "lib/*:target/classes" com.compiler.Main --test-all --optimize
```

## Features

### Phase 1: Lexical Analysis
Tokenization of code: keywords, identifiers, literals, operators.

### Phase 2: Syntactic Analysis
Building the AST from tokens, checking grammatical correctness.

### Phase 3: Semantic Analysis
- checking variable declarations before use
- type control during assignment and operations
- function parameter validation
- array index checking (type + bounds for constants)
- record field access control
- scope management
- correct keyword usage (loops context)

### Phase 4: Optimization

Three AST-modifying optimizations implemented:

#### 1. Constant Folding
Simplifies constant expressions at compile time.

**Example:**
```
// before optimization
var x: integer is 5 + 3;
var y: boolean is 10 > 5;

// after optimization (AST modified)
var x: integer is 8;
var y: boolean is true;
```

**Operations:**
- Arithmetic: `+`, `-`, `*`, `/`, `%`
- Comparison: `>`, `<`, `>=`, `<=`, `=`, `!=`
- Logical: `and`, `or`, `xor`, `not`

#### 2. Dead Code Elimination
Removes code that will never execute.

**Example:**
```
// before optimization
routine foo(): integer is
    return 42;
    print("Never");    // unreachable
end;

if (true) then
    print("Always");
else
    print("Never");    // dead else branch
end;

while (false) loop
    print("Never");    // dead loop
end;

// after optimization (AST modified)
routine foo(): integer is
    return 42;
end;

print("Always");

// while loop completely removed
```

**Removes:**
- Code after `return` statements
- Unreachable `else` branches when `if (true)`
- Unreachable `then` branches when `if (false)`
- Entire `while (false)` loops

#### 3. Unused Variable Elimination
Removes variable declarations that are never used.

**Example:**
```
// before optimization
routine main() is
    var x: integer is 10;       // used
    var unused: integer is 20;  // not used
    var y: integer is 30;       // not used
    
    print(x);
end;

// after optimization (AST modified)
routine main() is
    var x: integer is 10;
    
    print(x);
end;
```

**Removes:**
- Global unused variables
- Local unused variables in routines
- Unused parameters (if safe)
