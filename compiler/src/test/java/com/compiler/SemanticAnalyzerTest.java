package com.compiler;

import com.compiler.semantic.SemanticAnalyzer;
import com.compiler.semantic.SemanticError;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.Symbol;
import org.junit.Test;
import static org.junit.Assert.*;

import java.io.StringReader;
import java.util.List;

/**
 * Test suite for the SemanticAnalyzer class.
 * Tests various semantic analysis scenarios including type checking,
 * variable declarations, and scope management.
 */
public class SemanticAnalyzerTest {

    /**
     * Helper method to parse a program from a string and run semantic analysis
     */
    private List<SemanticError> analyzeProgram(String programText) throws Exception {
        ComplexSymbolFactory symbolFactory = new ComplexSymbolFactory();
        Lexer lexer = new Lexer(new StringReader(programText), symbolFactory);
        ImperativeLangParser parser = new ImperativeLangParser(lexer, symbolFactory);
        Symbol parseTree = parser.parse();
        Program program = (Program) parseTree.value;
        
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        return analyzer.analyze(program);
    }

    @Test
    public void testSimpleVariableDeclaration() throws Exception {
        String program = 
            "routine main() is\n" +
            "    var x: integer is 42;\n" +
            "    print(x);\n" +
            "end;";
        
        List<SemanticError> errors = analyzeProgram(program);
        assertTrue("Expected no semantic errors", errors.isEmpty());
    }

    @Test
    public void testUndefinedVariable() throws Exception {
        String program = 
            "routine main() is\n" +
            "    print(x);\n" +
            "end;";
        
        List<SemanticError> errors = analyzeProgram(program);
        assertFalse("Expected semantic error for undefined variable", errors.isEmpty());
        assertTrue("Error should mention undefined variable", 
            errors.get(0).toString().contains("Undefined variable"));
    }

    @Test
    public void testTypeMismatch() throws Exception {
        String program = 
            "routine main() is\n" +
            "    var x: integer is 42;\n" +
            "    x := true;\n" +
            "end;";
        
        List<SemanticError> errors = analyzeProgram(program);
        assertFalse("Expected semantic error for type mismatch", errors.isEmpty());
        assertTrue("Error should mention type mismatch", 
            errors.get(0).toString().contains("Type mismatch"));
    }

    @Test
    public void testDuplicateVariableDeclaration() throws Exception {
        String program = 
            "routine main() is\n" +
            "    var x: integer is 42;\n" +
            "    var x: integer is 10;\n" +
            "end;";
        
        List<SemanticError> errors = analyzeProgram(program);
        assertFalse("Expected semantic error for duplicate declaration", errors.isEmpty());
        assertTrue("Error should mention duplicate declaration", 
            errors.get(0).toString().contains("already declared"));
    }

    @Test
    public void testArrayAccess() throws Exception {
        String program = 
            "routine main() is\n" +
            "    var arr: array[5] integer;\n" +
            "    arr[0] := 42;\n" +
            "    print(arr[0]);\n" +
            "end;";
        
        List<SemanticError> errors = analyzeProgram(program);
        assertTrue("Expected no semantic errors for valid array access", errors.isEmpty());
    }

    @Test
    public void testInvalidArrayIndex() throws Exception {
        String program = 
            "routine main() is\n" +
            "    var arr: array[5] integer;\n" +
            "    arr[true] := 42;\n" +
            "end;";
        
        List<SemanticError> errors = analyzeProgram(program);
        assertFalse("Expected semantic error for invalid array index", errors.isEmpty());
        assertTrue("Error should mention array index type", 
            errors.get(0).toString().contains("Array index must be an integer"));
    }

    @Test
    public void testRoutineCall() throws Exception {
        String program = 
            "routine foo(x: integer) : integer is\n" +
            "    return x + 1;\n" +
            "end;\n" +
            "routine main() is\n" +
            "    var result: integer is foo(42);\n" +
            "    print(result);\n" +
            "end;";
        
        List<SemanticError> errors = analyzeProgram(program);
        assertTrue("Expected no semantic errors for valid routine call", errors.isEmpty());
    }

    @Test
    public void testUndefinedRoutine() throws Exception {
        String program = 
            "routine main() is\n" +
            "    var result: integer is foo(42);\n" +
            "end;";
        
        List<SemanticError> errors = analyzeProgram(program);
        assertFalse("Expected semantic error for undefined routine", errors.isEmpty());
        assertTrue("Error should mention undefined routine", 
            errors.get(0).toString().contains("Undefined routine"));
    }

    @Test
    public void testWrongNumberOfArguments() throws Exception {
        String program = 
            "routine foo(x: integer, y: integer) : integer is\n" +
            "    return x + y;\n" +
            "end;\n" +
            "routine main() is\n" +
            "    var result: integer is foo(42);\n" +
            "end;";
        
        List<SemanticError> errors = analyzeProgram(program);
        assertFalse("Expected semantic error for wrong number of arguments", errors.isEmpty());
        assertTrue("Error should mention wrong number of arguments", 
            errors.get(0).toString().contains("Wrong number of arguments"));
    }

    @Test
    public void testIfStatementCondition() throws Exception {
        String program = 
            "routine main() is\n" +
            "    var x: integer is 42;\n" +
            "    if x then\n" +
            "        print(x);\n" +
            "    end;\n" +
            "end;";
        
        List<SemanticError> errors = analyzeProgram(program);
        assertFalse("Expected semantic error for non-boolean condition", errors.isEmpty());
        assertTrue("Error should mention boolean expression", 
            errors.get(0).toString().contains("boolean expression"));
    }

    @Test
    public void testValidIfStatement() throws Exception {
        String program = 
            "routine main() is\n" +
            "    var x: integer is 42;\n" +
            "    if x > 0 then\n" +
            "        print(x);\n" +
            "    end;\n" +
            "end;";
        
        List<SemanticError> errors = analyzeProgram(program);
        assertTrue("Expected no semantic errors for valid if statement", errors.isEmpty());
    }

    @Test
    public void testRecordTypeDeclaration() throws Exception {
        String program = 
            "type Person is record\n" +
            "    var name: string;\n" +
            "    var age: integer;\n" +
            "end;\n" +
            "routine main() is\n" +
            "    var p: Person;\n" +
            "    p.age := 25;\n" +
            "    print(p.age);\n" +
            "end;";
        
        List<SemanticError> errors = analyzeProgram(program);
        assertTrue("Expected no semantic errors for valid record usage", errors.isEmpty());
    }

    @Test
    public void testInvalidFieldAccess() throws Exception {
        String program = 
            "type Person is record\n" +
            "    var name: string;\n" +
            "    var age: integer;\n" +
            "end;\n" +
            "routine main() is\n" +
            "    var p: Person;\n" +
            "    p.height := 180;\n" +
            "end;";
        
        List<SemanticError> errors = analyzeProgram(program);
        assertFalse("Expected semantic error for invalid field access", errors.isEmpty());
        assertTrue("Error should mention field does not exist", 
            errors.get(0).toString().contains("does not exist"));
    }
}

