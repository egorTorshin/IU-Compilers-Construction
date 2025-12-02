package com.compiler;

import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.Symbol;
import java.io.FileReader;
import java.io.StringReader;
import java.util.Scanner;

import com.compiler.ast.Program;

/**
 * Test class for Lexer and Parser components.
 * This class provides methods to test the lexical analysis and parsing functionality
 * of the Imperative Language Compiler.
 */
public class LexerParserTest {
    
    private static final ComplexSymbolFactory symbolFactory = new ComplexSymbolFactory();
    
    /**
     * Tests the lexer by tokenizing input and displaying all tokens.
     * 
     * @param input The input string to tokenize
     * @throws Exception if lexer encounters an error
     */
    public static void testLexer(String input) throws Exception {
        System.out.println("=== LEXER TEST ===");
        System.out.println("Input: " + input);
        System.out.println("Tokens:");
        
        Lexer lexer = new Lexer(new StringReader(input), symbolFactory);
        
        Symbol token;
        int tokenCount = 0;
        while ((token = lexer.next_token()).sym != 0) { // 0 is EOF
            System.out.println("  " + token);
            tokenCount++;
        }
        System.out.println("Total tokens: " + tokenCount);
        System.out.println();
    }
    
    /**
     * Tests the parser by parsing input and displaying the AST.
     * 
     * @param input The input string to parse
     * @throws Exception if parser encounters an error
     */
    public static void testParser(String input) throws Exception {
        System.out.println("=== PARSER TEST ===");
        System.out.println("Input: " + input);
        
        Lexer lexer = new Lexer(new StringReader(input), symbolFactory);
        ImperativeLangParser parser = new ImperativeLangParser(lexer, symbolFactory);
        
        Symbol parseTree = parser.parse();
        Program program = (Program) parseTree.value;
        
        System.out.println("AST:");
        System.out.println(program.toString());
        System.out.println();
    }
    
    /**
     * Tests both lexer and parser with the given input.
     * 
     * @param input The input string to test
     * @throws Exception if either lexer or parser encounters an error
     */
    public static void testBoth(String input) throws Exception {
        testLexer(input);
        testParser(input);
        System.out.println("==================================================");
    }
    
    /**
     * Tests the lexer and parser with a file.
     * 
     * @param filePath Path to the file to test
     * @throws Exception if file cannot be read or parsing fails
     */
    public static void testFile(String filePath) throws Exception {
        System.out.println("=== FILE TEST: " + filePath + " ===");
        
        Lexer lexer = new Lexer(new FileReader(filePath), symbolFactory);
        ImperativeLangParser parser = new ImperativeLangParser(lexer, symbolFactory);
        
        Symbol parseTree = parser.parse();
        Program program = (Program) parseTree.value;
        
        System.out.println("File parsed successfully!");
        System.out.println("AST:");
        System.out.println(program.toString());
        System.out.println();
    }
    
    /**
     * Interactive testing mode where user can enter code to test.
     */
    public static void interactiveMode() {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("Interactive Lexer/Parser Test");
        System.out.println("Enter 'quit' to exit, 'file <path>' to test a file");
        
        while (true) {
            System.out.print("Enter code to test: ");
            String input = scanner.nextLine();
            
            if (input.equals("quit")) {
                break;
            } else if (input.startsWith("file ")) {
                String filePath = input.substring(5).trim();
                try {
                    testFile(filePath);
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            } else {
                try {
                    testBoth(input);
                } catch (Exception e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        }
        
        scanner.close();
    }
    
    /**
     * Runs predefined test cases to verify lexer and parser functionality.
     */
    public static void runTestCases() throws Exception {
        System.out.println("=== RUNNING PREDEFINED TEST CASES ===");
        
        String[] testCases = {
            "routine main() is print(\"Hello\"); end;",
            "var x: integer is 5;",
            "var y: real is 3.14;",
            "var flag: boolean is true;",
            "if x > 0 then print(x); end;",
            "while x > 0 loop x := x - 1; end;",
            "for i in 1..5 loop print(i); end;",
            "for i in reverse 5..1 loop print(i); end;",
            "var arr: array [5] integer;",
            "type Person is record var name: string; var age: integer; end;"
        };
        
        for (int i = 0; i < testCases.length; i++) {
            System.out.println("Test Case " + (i + 1) + ":");
            testBoth(testCases[i]);
        }
    }
    
    /**
     * Main method to run the test program.
     * 
     * @param args Command line arguments:
     *             - "interactive" for interactive mode
     *             - "testcases" to run predefined test cases
     *             - file path to test a specific file
     *             - no arguments for interactive mode
     */
    public static void main(String[] args) {
        try {
            if (args.length == 0) {
                interactiveMode();
            } else if (args[0].equals("interactive")) {
                interactiveMode();
            } else if (args[0].equals("testcases")) {
                runTestCases();
            } else {
                // Treat as file path
                testFile(args[0]);
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
