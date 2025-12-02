package com.compiler;

import com.compiler.semantic.SemanticAnalyzer;
import com.compiler.semantic.SemanticError;
import com.compiler.ast.Program;
import com.compiler.optimizer.Optimizer;
import com.compiler.visualization.VisualizationGenerator;
import com.compiler.visualization.GraphvizGenerator;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.Symbol;
import java.io.FileReader;
import java.util.List;
import java.io.*;
import java.util.ArrayList;
import java.util.jar.Manifest;
import java.util.jar.JarOutputStream;
import java.util.jar.JarEntry;

public class Main {
    private static boolean debug = false;
    private static boolean verbose = false;
    private static boolean optimize = false;
    private static boolean visualize = false;
    
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
            if (args[i].equals("--visualize") || args[i].equals("-V")) {
                visualize = true;
            }
        }
        
        compileSingleFile(inputFilePath);
    }

    private static void compileSingleFile(String inputFilePath) {
        VisualizationGenerator vizGen = visualize ? new VisualizationGenerator(debug) : null;
        
        try {
            File inputFile = new File(inputFilePath);
            if (!inputFile.exists()) {
                System.err.println("Error: File not found: " + inputFilePath);
                System.exit(1);
            }
            
            if (visualize) {
                StringBuilder sourceCode = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new FileReader(inputFile))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sourceCode.append(line).append("\n");
                    }
                }
                vizGen.setSourceCode(sourceCode.toString());
            }

            if (debug) {
                System.err.println("\n" + "=== Lexical Analysis ===");
            }
            
            long phaseStart = System.currentTimeMillis();
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
            
            if (visualize) {
                vizGen.addPhaseTiming("Lexical Analysis", System.currentTimeMillis() - phaseStart);
            }

            if (debug) {
                System.err.println("\n" + "=== Syntactic Analysis ===");
            }
            
            phaseStart = System.currentTimeMillis();
            ImperativeLangParser parser = new ImperativeLangParser(lexer, symbolFactory);
            Symbol parseTree = parser.parse();
            Program program = (Program) parseTree.value;
            
            if (visualize) {
                vizGen.addPhaseTiming("Syntactic Analysis", System.currentTimeMillis() - phaseStart);
                vizGen.setProgram(program);
            }

            if (debug) {
                System.err.println("AST:");
                System.err.println(program.toString());
                System.err.println("\n" + "=== Semantic Analysis ===");
            }
            
            phaseStart = System.currentTimeMillis();
            SemanticAnalyzer analyzer = new SemanticAnalyzer(debug);
            List<SemanticError> errors = analyzer.analyze(program);

            if (!errors.isEmpty()) {
                System.err.println("Semantic errors found:");
                for (SemanticError error : errors) {
                    System.err.println("  " + error);
                }
                System.exit(1);
            }
            
            if (visualize) {
                vizGen.addPhaseTiming("Semantic Analysis", System.currentTimeMillis() - phaseStart);
                vizGen.setSymbolTable(analyzer.getSymbolTable());
            }
            
            int optimizationCount = 0;
            if (optimize) {
                // save AST before optimization for visualization
                if (visualize) {
                    vizGen.setOriginalAST(program.toString());
                }
                
                if (debug) {
                    System.err.println("\n" + "=== Optimization ===");
                }
                
                phaseStart = System.currentTimeMillis();
                Optimizer optimizer = new Optimizer(debug);
                optimizationCount = optimizer.optimize(program);
                
                if (visualize) {
                    vizGen.addPhaseTiming("Optimization", System.currentTimeMillis() - phaseStart);
                    vizGen.setOptimizationCount(optimizationCount);
                    vizGen.setOptimizedAST(program.toString());
                    for (com.compiler.optimizer.OptimizationDetail detail : optimizer.getOptimizationDetails()) {
                        vizGen.addOptimizationDetailObject(detail);
                    }
                }
                
                if (debug) {
                    System.err.println("\nOptimized AST:");
                    System.err.println(program.toString());
                }
            }

            if (debug) {
                System.err.println("\n" + "=== Code Generation ===");
            }
            
            phaseStart = System.currentTimeMillis();
            
            File outputDir = new File("output");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            JasminCodeGenerator codeGen = new JasminCodeGenerator(analyzer.getSymbolTable(), debug);
            String jasminCode = codeGen.generate(program);

            if (debug) {
                System.err.println("\nGenerated Jasmin code:");
                System.err.println("----------------------------");
                System.err.println(jasminCode);
                System.err.println("----------------------------\n");
            }

            String mainJasminFile = outputDir + "/Main.j";
            try (FileWriter writer = new FileWriter(mainJasminFile)) {
                writer.write(jasminCode);
            }

            // compile record files first, then Main.j
            File[] jasminFiles = outputDir.listFiles((dir, name) -> name.endsWith(".j"));
            if (jasminFiles != null) {
                for (File jasminFile : jasminFiles) {
                    if (!jasminFile.getName().equals("Main.j")) {
                        compileJasminFile(jasminFile.getPath(), outputDir.getPath());
                    }
                }
                compileJasminFile(mainJasminFile, outputDir.getPath());
            }

            if (visualize) {
                vizGen.addPhaseTiming("Code Generation", System.currentTimeMillis() - phaseStart);
            }
            
            if (debug) {
                System.err.println("\n" + "=== Creating Executable ===");
            }
            String executableJar = createExecutableJar(outputDir, inputFile.getName());
            
            if (visualize) {
                String baseName = inputFile.getName().replaceFirst("[.][^.]+$", "");
                
                String vizPath = outputDir + "/" + baseName + "_report.html";
                vizGen.generateHTMLReport(vizPath);
                
                String dotPath = outputDir + "/" + baseName + "_ast.dot";
                GraphvizGenerator graphviz = new GraphvizGenerator();
                graphviz.generateDOT(program, dotPath);
                
                String graphvizHtml = outputDir + "/" + baseName + "_ast_graphviz.html";
                graphviz.generateInteractiveSVG(program, graphvizHtml);
                
                if (debug) {
                    System.out.println("📊 Visualization files generated:");
                    System.out.println("  - HTML Report: " + new File(vizPath).getAbsolutePath());
                    System.out.println("  - AST DOT: " + new File(dotPath).getAbsolutePath());
                    System.out.println("  - AST Graphviz: " + new File(graphvizHtml).getAbsolutePath());
                } else {
                    System.out.println("📊 Visualizations created:");
                    System.out.println("  ├─ 📄 " + new File(vizPath).getName());
                    System.out.println("  ├─ 🌳 " + new File(dotPath).getName());
                    System.out.println("  └─ 🎨 " + new File(graphvizHtml).getName());
                }
            }

            if (debug) {
                String optMsg = optimize ? " (" + optimizationCount + " optimizations applied)" : "";
                System.out.println("\n" + "Compilation completed successfully" + optMsg);
                System.out.println("Executable created: " + executableJar);
                System.out.println("Run with: java -jar " + new File(executableJar).getName());
            } else {
                String optMsg = optimize ? " [" + optimizationCount + " optimizations]" : "";
                System.out.println(inputFile.getName() + " - OK" + optMsg);
                System.out.println("Executable: " + new File(executableJar).getName());
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
        System.out.println("  --visualize, -V   Generate HTML visualization report");
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
        System.out.println("  # With visualization report");
        System.out.println("  java -jar target/IL-compiler.jar tests/test01.txt --visualize");
        System.out.println();
        System.out.println("  # With all features");
        System.out.println("  java -jar target/IL-compiler.jar tests/test01.txt --optimize --visualize");
        System.out.println();
        System.out.println("  # Run all tests");
        System.out.println("  java -jar target/IL-compiler.jar --test-all");
        System.out.println();
        System.out.println("  # Run all tests with optimization");
        System.out.println("  java -jar target/IL-compiler.jar --test-all --optimize");
    }

    // creates executable JAR from compiled .class files
    private static String createExecutableJar(File outputDir, String sourceFileName) {
        try {
            String baseName = sourceFileName.replaceFirst("[.][^.]+$", "");
            String jarFileName = baseName + ".jar";
            String jarPath = outputDir.getPath() + File.separator + jarFileName;
            
            try {
                Manifest manifest = new Manifest();
                manifest.getMainAttributes().put(java.util.jar.Attributes.Name.MANIFEST_VERSION, "1.0");
                manifest.getMainAttributes().put(java.util.jar.Attributes.Name.MAIN_CLASS, "Main");
                manifest.getMainAttributes().put(new java.util.jar.Attributes.Name("Created-By"), "Imperative Language Compiler");
                
                try (JarOutputStream jarOut = new JarOutputStream(new FileOutputStream(jarPath), manifest)) {
                    addClassFilesToJar(outputDir, outputDir, jarOut);
                }
                
                if (debug) {
                    System.err.println("Created executable JAR: " + jarFileName);
                }
                return jarPath;
            } catch (Exception apiException) {
                if (debug) {
                    System.err.println("Java API method failed, trying jar command...");
                }
                
                String manifestPath = outputDir.getPath() + File.separator + "MANIFEST.MF";
                try (FileWriter manifestWriter = new FileWriter(manifestPath)) {
                    manifestWriter.write("Manifest-Version: 1.0\n");
                    manifestWriter.write("Main-Class: Main\n");
                    manifestWriter.write("Created-By: Imperative Language Compiler\n");
                }
                
                ProcessBuilder pb = new ProcessBuilder(
                    "jar", "cfm", jarPath, manifestPath, "-C", outputDir.getPath(), "."
                );
                
                Process process = pb.start();
                int exitCode = process.waitFor();
                
                if (exitCode == 0) {
                    if (debug) {
                        System.err.println("Created executable JAR: " + jarFileName);
                    }
                    new File(manifestPath).delete();
                    return jarPath;
                } else {
                    throw new Exception("jar command failed with exit code: " + exitCode);
                }
            }
        } catch (Exception e) {
            if (debug) {
                System.err.println("Warning: Could not create JAR file: " + e.getMessage());
                System.err.println(".class files can still be run with: java -cp " + outputDir.getPath() + " Main");
            }
            return outputDir.getPath();
        }
    }
    
    // recursively adds .class files to JAR
    private static void addClassFilesToJar(File rootDir, File currentDir, JarOutputStream jarOut) throws IOException {
        File[] files = currentDir.listFiles();
        if (files == null) return;
        
        for (File file : files) {
            if (file.isDirectory()) {
                addClassFilesToJar(rootDir, file, jarOut);
            } else if (file.getName().endsWith(".class")) {
                String entryName = rootDir.toPath().relativize(file.toPath()).toString().replace("\\", "/");
                jarOut.putNextEntry(new JarEntry(entryName));
                
                try (FileInputStream fis = new FileInputStream(file)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = fis.read(buffer)) != -1) {
                        jarOut.write(buffer, 0, bytesRead);
                    }
                }
                
                jarOut.closeEntry();
            }
        }
    }

    // compiles .j file to .class using jasmin
    private static void compileJasminFile(String jasminFile, String outputDir) {
        try {
            File compilerDir = new File(".");
            File jasminJar = new File(compilerDir, "lib/jasmin.jar");
            if (!jasminJar.exists()) {
                jasminJar = new File("compiler/lib/jasmin.jar");
            }
            
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
