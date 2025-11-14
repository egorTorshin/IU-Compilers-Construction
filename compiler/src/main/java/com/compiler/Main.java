package com.compiler;

import com.compiler.semantic.SemanticAnalyzer;
import com.compiler.semantic.SemanticError;
import com.compiler.optimizer.Optimizer;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.Symbol;
import java.io.FileReader;
import java.util.List;
import java.io.*;
import java.util.ArrayList;

public class Main {
    private static boolean debug = false;
    private static boolean verbose = false;
    private static boolean optimize = false;
    
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
            boolean verboseMode = false;
            boolean optimizeMode = false;
            for (int i = 1; i < args.length; i++) {
                if (args[i].equals("--verbose") || args[i].equals("-v")) {
                    verboseMode = true;
                }
                if (args[i].equals("--optimize") || args[i].equals("-O")) {
                    optimizeMode = true;
                }
            }
            runAllTests(verboseMode, optimizeMode);
            return;
        }

        String inputFilePath = args[0];
        
        for (int i = 1; i < args.length; i++) {
            if (args[i].equals("--debug")) {
                debug = true;
            }
            if (args[i].equals("--optimize") || args[i].equals("-O")) {
                optimize = true;
            }
        }
        
        compileSingleFile(inputFilePath);
    }

    private static void compileSingleFile(String inputFilePath) {
        try {
            File inputFile = new File(inputFilePath);
            if (!inputFile.exists()) {
                System.err.println("Error: File not found: " + inputFilePath);
                System.exit(1);
            }

            if (debug) {
                System.err.println("\n" + "=== Lexical Analysis ===");
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
                System.err.println("\n" + "=== Syntactic Analysis ===");
            }
            
            ImperativeLangParser parser = new ImperativeLangParser(lexer, symbolFactory);
            Symbol parseTree = parser.parse();
            Program program = (Program) parseTree.value;

            if (debug) {
                System.err.println("AST:");
                System.err.println(program.toString());
                System.err.println("\n" + "=== Semantic Analysis ===");
            }
            
            SemanticAnalyzer analyzer = new SemanticAnalyzer(debug);
            List<SemanticError> errors = analyzer.analyze(program);

            if (!errors.isEmpty()) {
                System.err.println("Semantic errors found:");
                for (SemanticError error : errors) {
                    System.err.println("  " + error);
                }
                System.exit(1);
            }
            
            int optimizationCount = 0;
            if (optimize) {
                if (debug) {
                    System.err.println("\n" + "=== Optimization ===");
                }
                
                Optimizer optimizer = new Optimizer(debug);
                optimizationCount = optimizer.optimize(program);
                
                if (debug) {
                    System.err.println("\nOptimized AST:");
                    System.err.println(program.toString());
                }
            }

            // Code Generation
            if (debug) {
                System.err.println("\n" + "=== Code Generation ===");
            }
            
            // Create output directory if it doesn't exist
            File outputDir = new File("output");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            // Generate Jasmin assembly code
            JasminCodeGenerator codeGen = new JasminCodeGenerator(analyzer.getSymbolTable(), debug);
            String jasminCode = codeGen.generate(program);

            if (debug) {
                System.err.println("\nGenerated Jasmin code:");
                System.err.println("----------------------------");
                System.err.println(jasminCode);
                System.err.println("----------------------------\n");
            }

            // Write Main.j assembly to file
            String mainJasminFile = outputDir + "/Main.j";
            try (FileWriter writer = new FileWriter(mainJasminFile)) {
                writer.write(jasminCode);
            }

            // Compile all .j files in the output directory
            File[] jasminFiles = outputDir.listFiles((dir, name) -> name.endsWith(".j"));
            if (jasminFiles != null) {
                // First compile record type files
                for (File jasminFile : jasminFiles) {
                    if (!jasminFile.getName().equals("Main.j")) {
                        compileJasminFile(jasminFile.getPath(), outputDir.getPath());
                    }
                }
                
                // Then compile Main.j
                compileJasminFile(mainJasminFile, outputDir.getPath());
            }

            if (debug) {
                String optMsg = optimize ? " (" + optimizationCount + " optimizations applied)" : "";
                System.out.println("\n" + "All phases completed successfully" + optMsg);
            } else {
                String optMsg = optimize ? " [" + optimizationCount + " optimizations]" : "";
                System.out.println(inputFile.getName() + " - OK" + optMsg);
            }

        } catch (Exception e) {
            if (debug) {
                System.err.println("Compilation failed:");
                e.printStackTrace(System.err);
            } else {
                System.err.println(new File(inputFilePath).getName() + " - " + e.getMessage());
            }
            System.exit(1);
        }
    }

    private static void runAllTests(boolean verboseMode, boolean optimizeMode) {
        verbose = verboseMode;
        optimize = optimizeMode;
        
        String title = optimize ? "Running Tests (with optimization)" : "Running Semantic Analyzer Tests";
        System.out.println(title);
        System.out.println("─────────────────────────────────");
        
        File testsDir = new File("tests");
        if (!testsDir.exists() || !testsDir.isDirectory()) {
            System.err.println("Error: tests/ directory not found");
            System.exit(1);
        }
        
        File[] testFiles = testsDir.listFiles((dir, name) -> name.endsWith(".txt"));
        if (testFiles == null || testFiles.length == 0) {
            System.err.println("Error: No test files found in tests/");
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
                    if (optimize) {
                        Optimizer optimizer = new Optimizer(false);
                        optimizer.optimize(program);
                    }
                    
                    System.out.println(testName);
                    passed++;
                } else {
                    System.out.println(testName);
                    if (verbose) {
                        for (SemanticError error : errors) {
                            System.out.println("    " + error);
                        }
                    }
                    failed++;
                    failedTests.add(testName);
                }
                
            } catch (Exception e) {
                System.out.println(testName + " " + "(parse error)");
                if (verbose) {
                    System.out.println("    " + e.getMessage());
                }
                failed++;
                failedTests.add(testName);
            }
        }
        
        System.out.println("─────────────────────────────────");
        System.out.println("Total: " + (passed + failed) + " | " + 
                           "Passed: " + passed + " | " +
                           (failed > 0 ? "" : "") + "Failed: " + failed);
        
        if (failed > 0 && !verbose && !failedTests.isEmpty()) {
            System.out.println("\n" + "Failed tests:");
            for (String test : failedTests) {
                System.out.println("  • " + test);
            }
            System.out.println("\n Run with --verbose to see error details");
        }
        
        if (failed == 0) {
            System.out.println("\n" + "All tests passed!");
        }
        
        System.exit(failed > 0 ? 1 : 0);
    }

    private static void printUsage() {
        System.out.println("Imperative Language Compiler - Frontend + Optimizer");
        System.out.println();
        System.out.println("Usage:");
        System.out.println("  java -jar target/IL-compiler.jar <input-file> [options]");
        System.out.println("  java -jar target/IL-compiler.jar --test-all [options]");
        System.out.println("  java -jar target/IL-compiler.jar --help");
        System.out.println();
        System.out.println("Options:");
        System.out.println("  --debug           Show detailed compilation output");
        System.out.println("  --optimize, -O    Enable AST optimizations");
        System.out.println("  --test-all        Run all tests in tests/ directory");
        System.out.println("  --verbose, -v     Show error details (with --test-all)");
        System.out.println("  --help, -h        Show this help message");
        System.out.println();
        System.out.println("Optimizations:");
        System.out.println("  1. Constant Folding       - Simplify constant expressions");
        System.out.println("  2. Dead Code Elimination  - Remove unreachable code");
        System.out.println("  3. Unused Variables       - Remove unused declarations");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  # Compile single file");
        System.out.println("  java -jar target/IL-compiler.jar tests/test01.txt");
        System.out.println();
        System.out.println("  # With debug output");
        System.out.println("  java -jar target/IL-compiler.jar tests/test01.txt --debug");
        System.out.println();
        System.out.println("  # With optimization");
        System.out.println("  java -jar target/IL-compiler.jar tests/test01.txt --optimize");
        System.out.println();
        System.out.println("  # With debug and optimization");
        System.out.println("  java -jar target/IL-compiler.jar tests/test01.txt --debug --optimize");
        System.out.println();
        System.out.println("  # Run all tests");
        System.out.println("  java -jar target/IL-compiler.jar --test-all");
        System.out.println();
        System.out.println("  # Run all tests with optimization");
        System.out.println("  java -jar target/IL-compiler.jar --test-all --optimize");
    }

    /**
     * Compiles a Jasmin assembly file into JVM bytecode.
     * Uses the Jasmin assembler to convert the .j file into a .class file.
     *
     * @param jasminFile Path to the input Jasmin assembly file (.j)
     * @param outputDir Directory where the compiled .class file should be placed
     */
    private static void compileJasminFile(String jasminFile, String outputDir) {
        try {
            // Get the absolute path to jasmin.jar relative to the compiler directory
            File compilerDir = new File(".");
            File jasminJar = new File(compilerDir, "lib/jasmin.jar");
            if (!jasminJar.exists()) {
                // Try alternative path
                jasminJar = new File("compiler/lib/jasmin.jar");
            }
            
            // Use ProcessBuilder to run jasmin.jar
            ProcessBuilder pb = new ProcessBuilder(
                "java", 
                "-jar", 
                jasminJar.getAbsolutePath(), 
                "-d", 
                outputDir, 
                jasminFile
            );
            
            Process process = pb.start();
            int exitCode = process.waitFor();
            
            if (exitCode == 0 && debug) {
                System.err.println("Successfully compiled " + jasminFile);
            } else if (exitCode != 0) {
                System.err.println("Error compiling " + jasminFile + ". Exit code: " + exitCode);
                
                // Print error output if any
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.err.println(line);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error compiling " + jasminFile + ": " + e.getMessage());
            if (debug) {
                e.printStackTrace(System.err);
            }
        }
    }
}
