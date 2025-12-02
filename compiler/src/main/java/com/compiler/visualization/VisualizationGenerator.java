package com.compiler.visualization;

import com.compiler.*;
import com.compiler.ast.Program;
import com.compiler.semantic.SymbolTable;
import com.compiler.optimizer.OptimizationDetail;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Generates beautiful HTML visualization reports for compilation results.
 * Creates interactive AST trees, statistics, and compilation timelines.
 */
public class VisualizationGenerator {
    private String sourceCode;
    private Program program;
    private SymbolTable symbolTable;
    private int optimizationCount;
    private Map<String, Long> phaseTiming;
    private List<String> optimizationDetails;
    private List<OptimizationDetail> optimizationDetailObjects;
    private boolean debugMode;
    private String originalAST;
    private String optimizedAST;
    
    public VisualizationGenerator(boolean debugMode) {
        this.phaseTiming = new LinkedHashMap<>();
        this.optimizationDetails = new ArrayList<>();
        this.optimizationDetailObjects = new ArrayList<>();
        this.debugMode = debugMode;
    }
    
    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }
    
    public void setProgram(Program program) {
        this.program = program;
    }
    
    public void setSymbolTable(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }
    
    public void setOptimizationCount(int count) {
        this.optimizationCount = count;
    }
    
    public void setOriginalAST(String ast) {
        this.originalAST = ast;
    }
    
    public void setOptimizedAST(String ast) {
        this.optimizedAST = ast;
    }
    
    public void addPhaseTiming(String phase, long milliseconds) {
        phaseTiming.put(phase, milliseconds);
    }
    
    public void addOptimizationDetail(String detail) {
        optimizationDetails.add(detail);
    }
    
    public void addOptimizationDetailObject(OptimizationDetail detail) {
        optimizationDetailObjects.add(detail);
    }
    
    /**
     * Generates a comprehensive HTML visualization report.
     */
    public void generateHTMLReport(String outputPath) throws IOException {
        StringBuilder html = new StringBuilder();
        
        html.append("<!DOCTYPE html>\n<html lang=\"en\">\n<head>\n");
        html.append("    <meta charset=\"UTF-8\">\n");
        html.append("    <meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n");
        html.append("    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        html.append("    <title>Compilation Report - Imperative Language Compiler</title>\n");
        
        // Add styles
        html.append(getStyles());
        
        html.append("</head>\n<body>\n");
        
        // Header
        html.append(generateHeader());
        
        // Statistics Dashboard
        html.append(generateStatsDashboard());
        
        // Phase Timeline
        html.append(generatePhaseTimeline());
        
        // Source Code Section
        html.append(generateSourceCodeSection());
        
        // Original AST (before optimization)
        if (originalAST != null && optimizationCount > 0) {
            html.append(generateOriginalASTSection());
        }
        
        // Optimization Details
        if (optimizationCount > 0) {
            html.append(generateOptimizationSection());
        }
        
        // Optimized AST (after optimization)
        if (optimizedAST != null && optimizationCount > 0) {
            html.append(generateOptimizedASTSection());
        }
        
        // AST Visualization (for non-optimized builds)
        if (optimizationCount == 0) {
            html.append(generateASTVisualization());
        }
        
        // Symbol Table
        html.append(generateSymbolTableSection());
        
        // Footer
        html.append(generateFooter());
        
        // Add JavaScript
        html.append(getJavaScript());
        
        html.append("</body>\n</html>");
        
        // Write to file with UTF-8 encoding
        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(outputPath), StandardCharsets.UTF_8)) {
            writer.write(html.toString());
        }
    }
    
    private String getStyles() {
        StringBuilder css = new StringBuilder();
        css.append("<style>\n");
        css.append("* { margin: 0; padding: 0; box-sizing: border-box; }\n");
        css.append("body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: #333; padding: 20px; min-height: 100vh; }\n");
        css.append(".container { max-width: 1400px; margin: 0 auto; }\n");
        css.append(".header { background: white; border-radius: 20px; padding: 40px; margin-bottom: 30px; box-shadow: 0 20px 60px rgba(0,0,0,0.3); text-align: center; }\n");
        css.append(".header h1 { font-size: 3em; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); -webkit-background-clip: text; -webkit-text-fill-color: transparent; margin-bottom: 10px; }\n");
        css.append(".header .subtitle { color: #666; font-size: 1.2em; }\n");
        css.append(".stats-dashboard { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 20px; margin-bottom: 30px; }\n");
        css.append(".stat-card { background: white; border-radius: 15px; padding: 30px; box-shadow: 0 10px 30px rgba(0,0,0,0.2); transition: transform 0.3s ease, box-shadow 0.3s ease; }\n");
        css.append(".stat-card:hover { transform: translateY(-5px); box-shadow: 0 15px 40px rgba(0,0,0,0.3); }\n");
        css.append(".stat-card .stat-value { font-size: 3em; font-weight: bold; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); -webkit-background-clip: text; -webkit-text-fill-color: transparent; }\n");
        css.append(".stat-card .stat-label { color: #666; font-size: 1.1em; margin-top: 10px; }\n");
        css.append(".section { background: white; border-radius: 15px; padding: 30px; margin-bottom: 30px; box-shadow: 0 10px 30px rgba(0,0,0,0.2); }\n");
        css.append(".section h2 { color: #667eea; margin-bottom: 20px; font-size: 2em; border-bottom: 3px solid #667eea; padding-bottom: 10px; }\n");
        css.append(".timeline { position: relative; padding: 20px 0; }\n");
        css.append(".timeline-item { display: flex; align-items: center; margin-bottom: 20px; opacity: 1; }\n");
        css.append(".timeline-icon { width: 50px; height: 50px; border-radius: 50%; background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); display: flex; align-items: center; justify-content: center; color: white; font-size: 1.5em; margin-right: 20px; flex-shrink: 0; }\n");
        css.append(".timeline-content { flex: 1; background: #f8f9fa; padding: 15px 20px; border-radius: 10px; }\n");
        css.append(".timeline-phase { font-weight: bold; font-size: 1.2em; color: #333; }\n");
        css.append(".timeline-duration { color: #666; font-size: 0.9em; margin-top: 5px; }\n");
        css.append(".code-container { background: #1e1e1e; border-radius: 10px; padding: 20px; overflow-x: auto; }\n");
        css.append(".code-container pre { color: #d4d4d4; font-family: 'Consolas', 'Monaco', monospace; font-size: 0.95em; line-height: 1.6; margin: 0; }\n");
        css.append(".ast-tree { background: #f8f9fa; border-radius: 10px; padding: 20px; overflow-x: auto; font-family: 'Consolas', 'Monaco', monospace; font-size: 0.9em; line-height: 1.8; }\n");
        css.append(".optimization-item { background: #e8f5e9; border-left: 4px solid #4caf50; padding: 15px; margin-bottom: 15px; border-radius: 5px; }\n");
        css.append(".optimization-item .opt-title { font-weight: bold; color: #2e7d32; margin-bottom: 5px; }\n");
        css.append(".symbol-table { overflow-x: auto; }\n");
        css.append(".symbol-table table { width: 100%; border-collapse: collapse; }\n");
        css.append(".symbol-table th { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 15px; text-align: left; font-weight: 600; }\n");
        css.append(".symbol-table td { padding: 12px 15px; border-bottom: 1px solid #e0e0e0; }\n");
        css.append(".symbol-table tr:hover { background: #f5f5f5; }\n");
        css.append(".footer { text-align: center; color: white; margin-top: 30px; padding: 20px; font-size: 0.9em; }\n");
        css.append(".success-badge { display: inline-block; background: #4caf50; color: white; padding: 8px 16px; border-radius: 20px; font-size: 0.9em; margin-top: 10px; }\n");
        css.append(".toggle-btn { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; border: none; padding: 10px 20px; border-radius: 5px; cursor: pointer; font-size: 1em; margin-top: 10px; }\n");
        css.append(".toggle-btn:hover { transform: scale(1.05); }\n");
        css.append(".hidden { display: none; }\n");
        css.append("</style>\n");
        return css.toString();
    }
    
    private String generateHeader() {
        StringBuilder html = new StringBuilder();
        html.append("<div class=\"container\">\n");
        html.append("    <div class=\"header\">\n");
        html.append("        <h1>Compilation Report</h1>\n");  //  as HTML entity
        html.append("        <div class=\"subtitle\">Imperative Language Compiler</div>\n");
        html.append("        <div class=\"success-badge\">&#10003; Compilation Successful</div>\n");  // ✓ as HTML entity
        html.append("    </div>\n");
        return html.toString();
    }
    
    private String generateStatsDashboard() {
        int totalStatements = program != null ? countStatements(program) : 0;
        long totalTime = phaseTiming.values().stream().mapToLong(Long::longValue).sum();
        
        StringBuilder html = new StringBuilder();
        html.append("<div class=\"stats-dashboard\">\n");
        html.append("    <div class=\"stat-card\">\n");
        html.append("        <div class=\"stat-value\">").append(totalStatements).append("</div>\n");
        html.append("        <div class=\"stat-label\">Statements</div>\n");
        html.append("    </div>\n");
        html.append("    <div class=\"stat-card\">\n");
        html.append("        <div class=\"stat-value\">").append(optimizationCount).append("</div>\n");
        html.append("        <div class=\"stat-label\">Optimizations</div>\n");
        html.append("    </div>\n");
        html.append("    <div class=\"stat-card\">\n");
        html.append("        <div class=\"stat-value\">").append(totalTime).append("ms</div>\n");
        html.append("        <div class=\"stat-label\">Total Time</div>\n");
        html.append("    </div>\n");
        html.append("    <div class=\"stat-card\">\n");
        html.append("        <div class=\"stat-value\">").append(phaseTiming.size()).append("</div>\n");
        html.append("        <div class=\"stat-label\">Phases</div>\n");
        html.append("    </div>\n");
        html.append("</div>\n");
        return html.toString();
    }
    
    private String generatePhaseTimeline() {
        StringBuilder timeline = new StringBuilder();
        timeline.append("<div class=\"section\">\n");
        timeline.append("    <h2>&#128202; Compilation Timeline</h2>\n");  // 📊
        timeline.append("    <div class=\"timeline\">\n");
        
        String[] icons = {"🔍", "🌳", "✓", "⚡", "🔧"};
        int iconIndex = 0;
        
        for (Map.Entry<String, Long> entry : phaseTiming.entrySet()) {
            String icon = icons[iconIndex % icons.length];
            iconIndex++;
            
            timeline.append("        <div class=\"timeline-item\">\n");
            timeline.append("            <div class=\"timeline-icon\">").append(icon).append("</div>\n");
            timeline.append("            <div class=\"timeline-content\">\n");
            timeline.append("                <div class=\"timeline-phase\">").append(entry.getKey()).append("</div>\n");
            timeline.append("                <div class=\"timeline-duration\">Duration: ").append(entry.getValue()).append(" ms</div>\n");
            timeline.append("            </div>\n");
            timeline.append("        </div>\n");
        }
        
        timeline.append("    </div>\n");
        timeline.append("</div>\n");
        
        return timeline.toString();
    }
    
    private String generateSourceCodeSection() {
        String escapedSource = escapeHtml(sourceCode != null ? sourceCode : "// Source code not available");
        
        StringBuilder html = new StringBuilder();
        html.append("<div class=\"section\">\n");
        html.append("    <h2>&#128221; Source Code</h2>\n");  // 📝
        html.append("    <div class=\"code-container\">\n");
        html.append("        <pre>").append(escapedSource).append("</pre>\n");
        html.append("    </div>\n");
        html.append("</div>\n");
        return html.toString();
    }
    
    private String generateASTVisualization() {
        String astString = program != null ? escapeHtml(program.toString()) : "AST not available";
        
        StringBuilder html = new StringBuilder();
        html.append("<div class=\"section\">\n");
        html.append("    <h2>&#127795; Abstract Syntax Tree</h2>\n");  // 🌳
        html.append("    <button class=\"toggle-btn\" onclick=\"toggleAST()\">Toggle AST View</button>\n");
        html.append("    <div id=\"ast-content\" class=\"ast-tree\" style=\"margin-top: 15px;\">\n");
        html.append("        <pre>").append(astString).append("</pre>\n");
        html.append("    </div>\n");
        html.append("</div>\n");
        return html.toString();
    }
    
    private String generateOriginalASTSection() {
        String astString = originalAST != null ? escapeHtml(originalAST) : "AST not available";
        
        StringBuilder html = new StringBuilder();
        html.append("<div class=\"section\">\n");
        html.append("    <h2>&#127795; Abstract Syntax Tree (Before Optimization)</h2>\n");  // 🌳
        html.append("    <button class=\"toggle-btn\" onclick=\"toggleOriginalAST()\">Toggle AST View</button>\n");
        html.append("    <div id=\"original-ast-content\" class=\"ast-tree\" style=\"margin-top: 15px;\">\n");
        html.append("        <pre>").append(astString).append("</pre>\n");
        html.append("    </div>\n");
        html.append("</div>\n");
        return html.toString();
    }
    
    private String generateOptimizedASTSection() {
        String astString = optimizedAST != null ? escapeHtml(optimizedAST) : "AST not available";
        
        StringBuilder html = new StringBuilder();
        html.append("<div class=\"section\">\n");
        html.append("    <h2>&#127881; Optimized Abstract Syntax Tree</h2>\n");  // 🎉
        html.append("    <button class=\"toggle-btn\" onclick=\"toggleOptimizedAST()\">Toggle AST View</button>\n");
        html.append("    <div id=\"optimized-ast-content\" class=\"ast-tree\" style=\"margin-top: 15px;\">\n");
        html.append("        <pre>").append(astString).append("</pre>\n");
        html.append("    </div>\n");
        html.append("</div>\n");
        return html.toString();
    }
    
    private String generateOptimizationSection() {
        StringBuilder opts = new StringBuilder();
        opts.append("<div class=\"section\">\n");
        opts.append("    <h2>&#9889; Optimizations Applied</h2>\n");  // ⚡
        opts.append("    <p style=\"margin-bottom: 20px;\">Total optimizations: <strong>");
        opts.append(optimizationCount);
        opts.append("</strong></p>\n");
        
        if (!optimizationDetailObjects.isEmpty()) {
            // Show detailed before/after for each optimization
            for (OptimizationDetail detail : optimizationDetailObjects) {
                opts.append("    <div class=\"optimization-item\">\n");
                opts.append("        <div class=\"opt-title\">&#10003; ").append(escapeHtml(detail.getType())).append("</div>\n");  // ✓
                opts.append("        <div style=\"margin-top: 8px; color: #555;\">").append(escapeHtml(detail.getDescription())).append("</div>\n");
                opts.append("        <div style=\"margin-top: 12px; display: grid; grid-template-columns: 1fr 1fr; gap: 15px;\">\n");
                opts.append("            <div>\n");
                opts.append("                <div style=\"font-weight: bold; color: #d32f2f; margin-bottom: 5px;\">&#10060; Before:</div>\n");  // ❌
                opts.append("                <div style=\"background: #ffebee; padding: 10px; border-radius: 5px; font-family: monospace; font-size: 0.9em;\">");
                opts.append(escapeHtml(detail.getBefore()));
                opts.append("</div>\n");
                opts.append("            </div>\n");
                opts.append("            <div>\n");
                opts.append("                <div style=\"font-weight: bold; color: #388e3c; margin-bottom: 5px;\">&#10003; After:</div>\n");  // ✓
                opts.append("                <div style=\"background: #e8f5e9; padding: 10px; border-radius: 5px; font-family: monospace; font-size: 0.9em;\">");
                opts.append(escapeHtml(detail.getAfter()));
                opts.append("</div>\n");
                opts.append("            </div>\n");
                opts.append("        </div>\n");
                opts.append("    </div>\n");
            }
        } else if (!optimizationDetails.isEmpty()) {
            // Fallback to simple string details
            for (String detail : optimizationDetails) {
                opts.append("    <div class=\"optimization-item\">\n");
                opts.append("        <div class=\"opt-title\">&#10003; ").append(escapeHtml(detail)).append("</div>\n");  // ✓
                opts.append("    </div>\n");
            }
        } else {
            // Show generic information
            opts.append("    <div class=\"optimization-item\">\n");
            opts.append("        <div class=\"opt-title\">Constant Folding</div>\n");
            opts.append("        <div>Simplified constant expressions at compile time</div>\n");
            opts.append("    </div>\n");
            opts.append("    <div class=\"optimization-item\">\n");
            opts.append("        <div class=\"opt-title\">Dead Code Elimination</div>\n");
            opts.append("        <div>Removed unreachable code paths</div>\n");
            opts.append("    </div>\n");
            opts.append("    <div class=\"optimization-item\">\n");
            opts.append("        <div class=\"opt-title\">Unused Variable Removal</div>\n");
            opts.append("        <div>Eliminated unused variable declarations</div>\n");
            opts.append("    </div>\n");
        }
        
        opts.append("</div>\n");
        return opts.toString();
    }
    
    @SuppressWarnings("unchecked")
    private String generateSymbolTableSection() {
        StringBuilder html = new StringBuilder();
        html.append("<div class=\"section\">\n");
        html.append("    <h2>&#128203; Symbol Table</h2>\n");  // 📋
        
        if (!debugMode) {
            html.append("    <div style=\"text-align: center; padding: 20px; color: #999;\">\n");
            html.append("        <p>Symbol table visualization available in debug mode</p>\n");
            html.append("        <p style=\"font-size: 0.9em; margin-top: 10px;\">Run with: <code>--debug --visualize</code></p>\n");
            html.append("    </div>\n");
        } else if (symbolTable == null) {
            html.append("    <div style=\"text-align: center; padding: 20px; color: #999;\">\n");
            html.append("        <p>Symbol table not available</p>\n");
            html.append("    </div>\n");
        } else {
            try {
                Map<String, Object> data = symbolTable.getVisualizationData();
                List<Map<String, String>> variables = (List<Map<String, String>>) data.get("variables");
                List<String> routines = (List<String>) data.get("routines");
                Map<String, String> types = (Map<String, String>) data.get("types");
                
                boolean hasData = !variables.isEmpty() || !routines.isEmpty() || !types.isEmpty();
                
                if (!hasData) {
                    html.append("    <div style=\"text-align: center; padding: 20px; color: #999;\">\n");
                    html.append("        <p>No symbols defined</p>\n");
                    html.append("    </div>\n");
                } else {
                    html.append("    <div class=\"symbol-table\">\n");
                    
                    // Variables table
                    if (!variables.isEmpty()) {
                        html.append("        <h3 style=\"margin-top: 0;\">Variables</h3>\n");
                        html.append("        <table>\n");
                        html.append("            <thead>\n");
                        html.append("                <tr>\n");
                        html.append("                    <th>Name</th>\n");
                        html.append("                    <th>Type</th>\n");
                        html.append("                    <th>Scope</th>\n");
                        html.append("                </tr>\n");
                        html.append("            </thead>\n");
                        html.append("            <tbody>\n");
                        for (Map<String, String> var : variables) {
                            html.append("                <tr>\n");
                            html.append("                    <td>").append(escapeHtml(var.get("name"))).append("</td>\n");
                            html.append("                    <td>").append(escapeHtml(var.get("type"))).append("</td>\n");
                            html.append("                    <td>").append(escapeHtml(var.get("scope"))).append("</td>\n");
                            html.append("                </tr>\n");
                        }
                        html.append("            </tbody>\n");
                        html.append("        </table>\n");
                    } else {
                        // Show message if no global variables
                        html.append("        <h3 style=\"margin-top: 0;\">Variables</h3>\n");
                        html.append("        <div style=\"background: #e3f2fd; padding: 15px; border-radius: 5px; color: #1976d2;\">\n");
                        html.append("            <p style=\"margin: 0;\">&#8505; No global variables found.</p>\n");  // ℹ️
                        html.append("            <p style=\"margin: 5px 0 0 0; font-size: 0.9em;\">Variables may be declared locally within routines.</p>\n");
                        html.append("        </div>\n");
                    }
                    
                    // Routines table
                    if (routines instanceof List) {
                        List<?> routineListRaw = (List<?>) routines;
                        if (!routineListRaw.isEmpty() && routineListRaw.get(0) instanceof Map) {
                            html.append("        <h3 style=\"margin-top: 20px;\">Routines</h3>\n");
                            html.append("        <table>\n");
                            html.append("            <thead>\n");
                            html.append("                <tr>\n");
                            html.append("                    <th>Name</th>\n");
                            html.append("                    <th>Parameters</th>\n");
                            html.append("                    <th>Return Type</th>\n");
                            html.append("                </tr>\n");
                            html.append("            </thead>\n");
                            html.append("            <tbody>\n");
                            for (Object routineObj : routineListRaw) {
                                Map<String, String> routine = (Map<String, String>) routineObj;
                                html.append("                <tr>\n");
                                html.append("                    <td>").append(escapeHtml(routine.get("name"))).append("()</td>\n");
                                String params = routine.get("parameters");
                                html.append("                    <td>").append(params != null && !params.isEmpty() ? escapeHtml(params) : "<i>none</i>").append("</td>\n");
                                html.append("                    <td>").append(escapeHtml(routine.get("returnType"))).append("</td>\n");
                                html.append("                </tr>\n");
                            }
                            html.append("            </tbody>\n");
                            html.append("        </table>\n");
                        } else if (!routineListRaw.isEmpty()) {
                            // Fallback for simple string list
                            html.append("        <h3 style=\"margin-top: 20px;\">Routines</h3>\n");
                            html.append("        <table>\n");
                            html.append("            <thead>\n");
                            html.append("                <tr>\n");
                            html.append("                    <th>Name</th>\n");
                            html.append("                </tr>\n");
                            html.append("            </thead>\n");
                            html.append("            <tbody>\n");
                            for (Object routine : routineListRaw) {
                                html.append("                <tr>\n");
                                html.append("                    <td>").append(escapeHtml(routine.toString())).append("()</td>\n");
                                html.append("                </tr>\n");
                            }
                            html.append("            </tbody>\n");
                            html.append("        </table>\n");
                        }
                    }
                    
                    // User types table
                    if (!types.isEmpty()) {
                        html.append("        <h3 style=\"margin-top: 20px;\">User-Defined Types</h3>\n");
                        html.append("        <table>\n");
                        html.append("            <thead>\n");
                        html.append("                <tr>\n");
                        html.append("                    <th>Name</th>\n");
                        html.append("                    <th>Definition</th>\n");
                        html.append("                </tr>\n");
                        html.append("            </thead>\n");
                        html.append("            <tbody>\n");
                        for (Map.Entry<String, String> entry : types.entrySet()) {
                            html.append("                <tr>\n");
                            html.append("                    <td>").append(escapeHtml(entry.getKey())).append("</td>\n");
                            html.append("                    <td>").append(escapeHtml(entry.getValue())).append("</td>\n");
                            html.append("                </tr>\n");
                        }
                        html.append("            </tbody>\n");
                        html.append("        </table>\n");
                    }
                    
                    html.append("    </div>\n");
                }
            } catch (Exception e) {
                // Fallback to toString()
                html.append("    <div style=\"padding: 20px;\">\n");
                html.append("        <pre style=\"margin: 0; font-size: 0.9em; background: #f8f9fa; padding: 15px; border-radius: 5px;\">");
                html.append(escapeHtml(symbolTable.toString()));
                html.append("</pre>\n");
                html.append("    </div>\n");
            }
        }
        
        html.append("</div>\n");
        return html.toString();
    }
    
    private String generateFooter() {
        StringBuilder html = new StringBuilder();
        html.append("</div>\n");
        html.append("<div class=\"footer\">\n");
        html.append("    Generated by Imperative Language Compiler | © 2025\n");
        html.append("</div>\n");
        return html.toString();
    }
    
    private String getJavaScript() {
        StringBuilder js = new StringBuilder();
        js.append("<script>\n");
        js.append("function toggleAST() {\n");
        js.append("    const astContent = document.getElementById('ast-content');\n");
        js.append("    astContent.classList.toggle('hidden');\n");
        js.append("}\n");
        js.append("function toggleOriginalAST() {\n");
        js.append("    const astContent = document.getElementById('original-ast-content');\n");
        js.append("    astContent.classList.toggle('hidden');\n");
        js.append("}\n");
        js.append("function toggleOptimizedAST() {\n");
        js.append("    const astContent = document.getElementById('optimized-ast-content');\n");
        js.append("    astContent.classList.toggle('hidden');\n");
        js.append("}\n");
        js.append("console.log('%c🎉 Compilation Report Loaded Successfully!', 'font-size: 20px; color: #667eea; font-weight: bold;');\n");
        js.append("</script>\n");
        return js.toString();
    }
    
    private int countStatements(Program program) {
        return program.getStatements().size();
    }
    
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;")
                   .replace("'", "&#39;");
    }
}

