package com.compiler;

import com.compiler.semantic.SemanticAnalyzer;
import com.compiler.semantic.SemanticError;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.Symbol;
import java.io.FileReader;
import java.util.List;
import java.io.*;
import java.util.ArrayList;

// compiler frontend: lexer → parser → semantic
// modes: single file, --test-all, --debug, --verbose
public class Main {
    private static boolean debug = false;
    private static boolean verbose = false;
    
    // ansi colors for output
    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";
    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";

    public static void main(String[] args) {
        if (args.length < 1) {
            printUsage();
            System.exit(1);
        }

        if (args[0].equals("--help") || args[0].equals("-h")) {
            printUsage();
            System.exit(0);
        }

        if (args[0].equals("--test-all")) {
            boolean verboseMode = args.length > 1 && (args[1].equals("--verbose") || args[1].equals("-v"));
            runAllTests(verboseMode);
            return;
        }

        String inputFilePath = args[0];
        debug = args.length > 1 && args[1].equals("--debug");
        
        compileSingleFile(inputFilePath);
    }

    private static void compileSingleFile(String inputFilePath) {
        try {
            File inputFile = new File(inputFilePath);
            if (!inputFile.exists()) {
                System.err.println(RED + "Error: File not found: " + inputFilePath + RESET);
                System.exit(1);
            }

            if (debug) {
                System.err.println("\n" + BOLD + "=== Lexical Analysis ===" + RESET);
            }
            
            String fileName = inputFile.getName();
            ComplexSymbolFactory symbolFactory = new ComplexSymbolFactory();
            Lexer lexer = new Lexer(new FileReader(inputFilePath), symbolFactory, fileName);
            
            if (debug) {
                Symbol token;
                while ((token = lexer.next_token()).sym != 0) {
                    System.err.println("Token: " + token);
                }
                lexer = new Lexer(new FileReader(inputFilePath), symbolFactory, fileName);
            }

            if (debug) {
                System.err.println("\n" + BOLD + "=== Syntactic Analysis ===" + RESET);
            }
            
            ImperativeLangParser parser = new ImperativeLangParser(lexer, symbolFactory);
            Symbol parseTree = parser.parse();
            Program program = (Program) parseTree.value;

            if (debug) {
                System.err.println("AST:");
                System.err.println(program.toString());
                System.err.println("\n" + BOLD + "=== Semantic Analysis ===" + RESET);
            }
            
            SemanticAnalyzer analyzer = new SemanticAnalyzer(debug);
            List<SemanticError> errors = analyzer.analyze(program);

            if (!errors.isEmpty()) {
                System.err.println(RED + "Semantic errors found:" + RESET);
                for (SemanticError error : errors) {
                    System.err.println("  " + error);
                }
                System.exit(1);
            }

            if (debug) {
                System.out.println("\n" + GREEN + "✓ All phases completed successfully!" + RESET);
            } else {
                System.out.println(GREEN + "✓ " + RESET + inputFile.getName() + " - OK");
            }

        } catch (Exception e) {
            if (debug) {
                System.err.println(RED + "Compilation failed:" + RESET);
                e.printStackTrace(System.err);
            } else {
                System.err.println(RED + "✗ " + RESET + new File(inputFilePath).getName() + " - " + e.getMessage());
            }
            System.exit(1);
        }
    }

    private static void runAllTests(boolean verboseMode) {
        verbose = verboseMode;
        
        System.out.println(BOLD + "Running Semantic Analyzer Tests" + RESET);
        System.out.println("─────────────────────────────────");
        
        File testsDir = new File("tests");
        if (!testsDir.exists() || !testsDir.isDirectory()) {
            System.err.println(RED + "Error: tests/ directory not found" + RESET);
            System.exit(1);
        }
        
        File[] testFiles = testsDir.listFiles((dir, name) -> name.endsWith(".txt"));
        if (testFiles == null || testFiles.length == 0) {
            System.err.println(RED + "Error: No test files found in tests/" + RESET);
            System.exit(1);
        }
        
        java.util.Arrays.sort(testFiles);
        
        int passed = 0;
        int failed = 0;
        List<String> failedTests = new ArrayList<>();
        
        for (File testFile : testFiles) {
            String testName = testFile.getName();
            
            try {
                ComplexSymbolFactory symbolFactory = new ComplexSymbolFactory();
                Lexer lexer = new Lexer(new FileReader(testFile), symbolFactory, testName);
                ImperativeLangParser parser = new ImperativeLangParser(lexer, symbolFactory);
                Symbol parseTree = parser.parse();
                Program program = (Program) parseTree.value;
                
                SemanticAnalyzer analyzer = new SemanticAnalyzer(false);
                List<SemanticError> errors = analyzer.analyze(program);
                
                if (errors.isEmpty()) {
                    System.out.println(GREEN + "✓ " + RESET + testName);
                    passed++;
                } else {
                    System.out.println(RED + "✗ " + RESET + testName);
                    if (verbose) {
                        for (SemanticError error : errors) {
                            System.out.println("    " + YELLOW + error + RESET);
                        }
                    }
                    failed++;
                    failedTests.add(testName);
                }
                
            } catch (Exception e) {
                System.out.println(RED + "✗ " + RESET + testName + " " + YELLOW + "(parse error)" + RESET);
                if (verbose) {
                    System.out.println("    " + e.getMessage());
                }
                failed++;
                failedTests.add(testName);
            }
        }
        
        System.out.println("─────────────────────────────────");
        System.out.println("Total: " + (passed + failed) + " | " + 
                           GREEN + "Passed: " + passed + RESET + " | " +
                           (failed > 0 ? RED : "") + "Failed: " + failed + RESET);
        
        if (failed > 0 && !verbose && !failedTests.isEmpty()) {
            System.out.println("\n" + YELLOW + "Failed tests:" + RESET);
            for (String test : failedTests) {
                System.out.println("  • " + test);
            }
            System.out.println("\n💡 Run with --verbose to see error details");
        }
        
        if (failed == 0) {
            System.out.println("\n" + GREEN + "✓ All tests passed!" + RESET);
        }
        
        System.exit(failed > 0 ? 1 : 0);
    }

    private static void printUsage() {
        System.out.println(BOLD + "Imperative Language Compiler - Frontend" + RESET);
        System.out.println();
        System.out.println("Usage:");
        System.out.println("  java -cp <classpath> com.compiler.Main <input-file> [--debug]");
        System.out.println("  java -cp <classpath> com.compiler.Main --test-all [--verbose]");
        System.out.println("  java -cp <classpath> com.compiler.Main --help");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  --debug        Show detailed compilation output");
        System.out.println("  --test-all     Run all tests in tests/ directory");
        System.out.println("  --verbose, -v  Show error details (with --test-all)");
        System.out.println("  --help, -h     Show this help message");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  # Compile single file");
        System.out.println("  java -cp \"lib/*:target/classes\" com.compiler.Main tests/test01.txt");
        System.out.println();
        System.out.println("  # With debug output");
        System.out.println("  java -cp \"lib/*:target/classes\" com.compiler.Main tests/test01.txt --debug");
        System.out.println();
        System.out.println("  # Run all tests");
        System.out.println("  java -cp \"lib/*:target/classes\" com.compiler.Main --test-all");
        System.out.println();
        System.out.println("  # Run all tests with error details");
        System.out.println("  java -cp \"lib/*:target/classes\" com.compiler.Main --test-all --verbose");
    }
}
