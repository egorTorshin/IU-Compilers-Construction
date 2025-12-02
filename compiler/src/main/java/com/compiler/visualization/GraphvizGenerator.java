package com.compiler.visualization;

import com.compiler.*;
import com.compiler.ast.Assignment;
import com.compiler.ast.BinaryExpression;
import com.compiler.ast.BooleanLiteral;
import com.compiler.ast.Expression;
import com.compiler.ast.ForLoop;
import com.compiler.ast.IfStatement;
import com.compiler.ast.IntegerLiteral;
import com.compiler.ast.PrintStatement;
import com.compiler.ast.Program;
import com.compiler.ast.ReadStatement;
import com.compiler.ast.RealLiteral;
import com.compiler.ast.ReturnStatement;
import com.compiler.ast.RoutineCall;
import com.compiler.ast.Statement;
import com.compiler.ast.StringLiteral;
import com.compiler.ast.UnaryExpression;
import com.compiler.ast.VariableDeclaration;
import com.compiler.ast.VariableReference;
import com.compiler.ast.WhileStatement;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Generates Graphviz DOT format visualizations of the AST.
 * The output can be rendered using Graphviz tools (dot, circo, etc.)
 * or online viewers like https://dreampuf.github.io/GraphvizOnline/
 */
public class GraphvizGenerator {
    private StringBuilder dot;
    private AtomicInteger nodeCounter;
    
    public GraphvizGenerator() {
        this.dot = new StringBuilder();
        this.nodeCounter = new AtomicInteger(0);
    }
    
    /**
     * Generates a DOT file for the given program AST.
     */
    public void generateDOT(Program program, String outputPath) throws IOException {
        dot.setLength(0); // Clear previous content
        nodeCounter.set(0);
        
        dot.append("digraph AST {\n");
        dot.append("    // Graph styling\n");
        dot.append("    rankdir=TB;\n");
        dot.append("    node [shape=box, style=filled, fillcolor=lightblue, fontname=\"Arial\"];\n");
        dot.append("    edge [fontname=\"Arial\", fontsize=10];\n");
        dot.append("    bgcolor=white;\n\n");
        
        // Create root node
        int rootId = nodeCounter.getAndIncrement();
        dot.append(String.format("    node%d [label=\"Program\", fillcolor=\"#667eea\", fontcolor=white, style=\"filled,rounded\"];\n", rootId));
        
        // Process all statements
        List<Statement> statements = program.getStatements();
        for (int i = 0; i < statements.size(); i++) {
            Statement stmt = statements.get(i);
            int stmtId = processStatement(stmt, i);
            dot.append(String.format("    node%d -> node%d [label=\"stmt_%d\"];\n", rootId, stmtId, i));
        }
        
        dot.append("}\n");
        
        // Write to file with UTF-8 encoding
        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(outputPath), StandardCharsets.UTF_8)) {
            writer.write(dot.toString());
        }
    }
    
    /**
     * Generates an interactive HTML file with embedded SVG visualization.
     */
    public void generateInteractiveSVG(Program program, String outputPath) throws IOException {
        // First generate the DOT content
        dot.setLength(0);
        nodeCounter.set(0);
        
        StringBuilder dotContent = new StringBuilder();
        dotContent.append("digraph AST {\n");
        dotContent.append("    rankdir=TB;\n");
        dotContent.append("    node [shape=box, style=filled, fillcolor=lightblue];\n\n");
        
        int rootId = nodeCounter.getAndIncrement();
        dotContent.append(String.format("    node%d [label=\"Program\", fillcolor=\"#667eea\", fontcolor=white];\n", rootId));
        
        List<Statement> statements = program.getStatements();
        for (int i = 0; i < statements.size(); i++) {
            Statement stmt = statements.get(i);
            int stmtId = processStatement(stmt, i);
            dotContent.append(String.format("    node%d -> node%d;\n", rootId, stmtId));
        }
        
        dotContent.append("}\n");
        
        // Create HTML with embedded DOT
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>\n<html>\n<head>\n");
        html.append("    <meta charset=\"UTF-8\">\n");
        html.append("    <title>AST Visualization - Graphviz</title>\n");
        html.append("    <style>\n");
        html.append("        body { margin: 0; padding: 20px; font-family: Arial, sans-serif; background: #f5f5f5; }\n");
        html.append("        .container { max-width: 1200px; margin: 0 auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 4px 6px rgba(0,0,0,0.1); }\n");
        html.append("        h1 { color: #667eea; text-align: center; }\n");
        html.append("        .instructions { background: #e3f2fd; padding: 15px; border-radius: 5px; margin: 20px 0; }\n");
        html.append("        .code-block { background: #1e1e1e; color: #d4d4d4; padding: 20px; border-radius: 5px; overflow-x: auto; font-family: monospace; }\n");
        html.append("        .btn { background: #667eea; color: white; padding: 10px 20px; border: none; border-radius: 5px; cursor: pointer; margin: 10px 5px; }\n");
        html.append("        .btn:hover { background: #5568d3; }\n");
        html.append("    </style>\n");
        html.append("</head>\n<body>\n");
        html.append("    <div class=\"container\">\n");
        html.append("        <h1>🌳 AST Visualization</h1>\n");
        html.append("        <div class=\"instructions\">\n");
        html.append("            <strong>📝 How to visualize:</strong><br>\n");
        html.append("            1. Copy the DOT code below<br>\n");
        html.append("            2. Visit <a href=\"https://dreampuf.github.io/GraphvizOnline/\" target=\"_blank\">Graphviz Online</a><br>\n");
        html.append("            3. Paste the code and see your AST come to life! 🎨\n");
        html.append("        </div>\n");
        html.append("        <button class=\"btn\" onclick=\"copyDOT()\">📋 Copy DOT Code</button>\n");
        html.append("        <button class=\"btn\" onclick=\"window.open('https://dreampuf.github.io/GraphvizOnline/', '_blank')\">🌐 Open Graphviz Online</button>\n");
        html.append("        <div class=\"code-block\" id=\"dotCode\">\n");
        html.append(escapeHtml(dotContent.toString()));
        html.append("        </div>\n");
        html.append("    </div>\n");
        html.append("    <script>\n");
        html.append("        function copyDOT() {\n");
        html.append("            const code = document.getElementById('dotCode').innerText;\n");
        html.append("            navigator.clipboard.writeText(code).then(() => {\n");
        html.append("                alert('✅ DOT code copied to clipboard!');\n");
        html.append("            });\n");
        html.append("        }\n");
        html.append("    </script>\n");
        html.append("</body>\n</html>\n");
        
        // Write to file with UTF-8 encoding
        try (OutputStreamWriter writer = new OutputStreamWriter(
                new FileOutputStream(outputPath), StandardCharsets.UTF_8)) {
            writer.write(html.toString());
        }
    }
    
    private int processStatement(Statement stmt, int index) {
        int nodeId = nodeCounter.getAndIncrement();
        String label = getStatementLabel(stmt);
        String color = getNodeColor(stmt);
        
        dot.append(String.format("    node%d [label=\"%s\", fillcolor=\"%s\"];\n", 
            nodeId, escapeLabel(label), color));
        
        // Process children based on statement type
        processStatementChildren(stmt, nodeId);
        
        return nodeId;
    }
    
    private void processStatementChildren(Statement stmt, int parentId) {
        if (stmt instanceof VariableDeclaration) {
            VariableDeclaration varDecl = (VariableDeclaration) stmt;
            int typeId = nodeCounter.getAndIncrement();
            dot.append(String.format("    node%d [label=\"Type: %s\", fillcolor=\"#ffeb3b\"];\n", 
                typeId, varDecl.getType()));
            dot.append(String.format("    node%d -> node%d [label=\"type\"];\n", parentId, typeId));
        } else if (stmt instanceof Assignment) {
            Assignment assign = (Assignment) stmt;
            int valueId = processExpression(assign.getValue());
            dot.append(String.format("    node%d -> node%d [label=\"value\"];\n", parentId, valueId));
        } else if (stmt instanceof IfStatement) {
            IfStatement ifStmt = (IfStatement) stmt;
            int condId = processExpression(ifStmt.getCondition());
            dot.append(String.format("    node%d -> node%d [label=\"condition\"];\n", parentId, condId));
            
            int thenId = nodeCounter.getAndIncrement();
            dot.append(String.format("    node%d [label=\"Then Block\", fillcolor=\"#c8e6c9\"];\n", thenId));
            dot.append(String.format("    node%d -> node%d [label=\"then\"];\n", parentId, thenId));
        } else if (stmt instanceof WhileStatement) {
            WhileStatement whileStmt = (WhileStatement) stmt;
            int condId = processExpression(whileStmt.getCondition());
            dot.append(String.format("    node%d -> node%d [label=\"condition\"];\n", parentId, condId));
            
            int bodyId = nodeCounter.getAndIncrement();
            dot.append(String.format("    node%d [label=\"Loop Body\", fillcolor=\"#b3e5fc\"];\n", bodyId));
            dot.append(String.format("    node%d -> node%d [label=\"body\"];\n", parentId, bodyId));
        } else if (stmt instanceof ForLoop) {
            int rangeId = nodeCounter.getAndIncrement();
            dot.append(String.format("    node%d [label=\"For Loop Range\", fillcolor=\"#ffccbc\"];\n", rangeId));
            dot.append(String.format("    node%d -> node%d [label=\"range\"];\n", parentId, rangeId));
        } else if (stmt instanceof PrintStatement) {
            PrintStatement print = (PrintStatement) stmt;
            int exprId = processExpression(print.getExpression());
            dot.append(String.format("    node%d -> node%d [label=\"expr\"];\n", parentId, exprId));
        }
    }
    
    private int processExpression(Expression expr) {
        int nodeId = nodeCounter.getAndIncrement();
        String label = getExpressionLabel(expr);
        String color = "#fff9c4";
        
        dot.append(String.format("    node%d [label=\"%s\", fillcolor=\"%s\", shape=ellipse];\n", 
            nodeId, escapeLabel(label), color));
        
        if (expr instanceof BinaryExpression) {
            BinaryExpression binExpr = (BinaryExpression) expr;
            int leftId = processExpression(binExpr.getLeft());
            int rightId = processExpression(binExpr.getRight());
            dot.append(String.format("    node%d -> node%d [label=\"left\"];\n", nodeId, leftId));
            dot.append(String.format("    node%d -> node%d [label=\"right\"];\n", nodeId, rightId));
        }
        
        return nodeId;
    }
    
    private String getStatementLabel(Statement stmt) {
        if (stmt instanceof VariableDeclaration) {
            VariableDeclaration varDecl = (VariableDeclaration) stmt;
            return "VarDecl: " + varDecl.getName();
        } else if (stmt instanceof Assignment) {
            return "Assignment";
        } else if (stmt instanceof IfStatement) {
            return "If Statement";
        } else if (stmt instanceof WhileStatement) {
            return "While Loop";
        } else if (stmt instanceof ForLoop) {
            return "For Loop";
        } else if (stmt instanceof PrintStatement) {
            return "Print";
        } else if (stmt instanceof ReadStatement) {
            return "Read";
        } else if (stmt instanceof ReturnStatement) {
            return "Return";
        }
        return stmt.getClass().getSimpleName();
    }
    
    private String getExpressionLabel(Expression expr) {
        if (expr instanceof IntegerLiteral) {
            return "Int: " + ((IntegerLiteral) expr).getValue();
        } else if (expr instanceof RealLiteral) {
            return "Real: " + ((RealLiteral) expr).getValue();
        } else if (expr instanceof BooleanLiteral) {
            return "Bool: " + ((BooleanLiteral) expr).getValue();
        } else if (expr instanceof StringLiteral) {
            String val = ((StringLiteral) expr).getValue();
            return "String: \"" + (val.length() > 20 ? val.substring(0, 20) + "..." : val) + "\"";
        } else if (expr instanceof VariableReference) {
            return "Var: " + ((VariableReference) expr).getName();
        } else if (expr instanceof BinaryExpression) {
            return "BinOp: " + ((BinaryExpression) expr).getOperator();
        } else if (expr instanceof UnaryExpression) {
            return "UnaryOp: " + ((UnaryExpression) expr).getOperator();
        } else if (expr instanceof RoutineCall) {
            return "Call: " + ((RoutineCall) expr).getName();
        }
        return expr.getClass().getSimpleName();
    }
    
    private String getNodeColor(Statement stmt) {
        if (stmt instanceof VariableDeclaration) {
            return "#bbdefb";
        } else if (stmt instanceof Assignment) {
            return "#c5cae9";
        } else if (stmt instanceof IfStatement) {
            return "#c8e6c9";
        } else if (stmt instanceof WhileStatement || stmt instanceof ForLoop) {
            return "#b3e5fc";
        } else if (stmt instanceof PrintStatement) {
            return "#ffe0b2";
        } else if (stmt instanceof ReturnStatement) {
            return "#f8bbd0";
        }
        return "#e0e0e0";
    }
    
    private String escapeLabel(String label) {
        return label.replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "");
    }
    
    private String escapeHtml(String text) {
        return text.replace("&", "&amp;")
                   .replace("<", "&lt;")
                   .replace(">", "&gt;")
                   .replace("\"", "&quot;");
    }
}

