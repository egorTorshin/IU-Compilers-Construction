package com.compiler.ast;

import java.util.List;
import java.util.Collections;

/**
 * Represents a routine declaration in the AST.
 * A routine can have a name, parameters, return type and a body of statements.
 */
public class RoutineDecl extends Statement {
    private String name;
    private List<Parameter> parameters;
    private Type returnType;
    private List<Statement> body;

    /**
     * Creates a new routine declaration with all fields specified.
     *
     * @param name The name of the routine
     * @param parameters The list of parameters for the routine
     * @param returnType The return type of the routine
     * @param body The list of statements in the routine body
     */
    public RoutineDecl(String name, List<Parameter> parameters, Type returnType, List<Statement> body) {
        this.name = name;
        this.parameters = parameters;
        this.returnType = returnType;
        this.body = body;
    }

    /**
     * Creates a new routine declaration with only name and body.
     * Parameters will be empty and return type will be null.
     *
     * @param name The name of the routine
     * @param body The list of statements in the routine body
     */
    public RoutineDecl(String name, List<Statement> body) {
        this.name = name;
        this.parameters = Collections.emptyList();
        this.returnType = null;
        this.body = body;
    }

    /**
     * @return The name of the routine
     */
    public String getName() {
        return name;
    }

    /**
     * @return The list of statements in the routine body
     */
    public List<Statement> getBody() {
        return body;
    }

    /**
     * @return The list of parameters for the routine
     */
    public List<Parameter> getParameters() {
        return parameters;
    }

    /**
     * @return The return type of the routine
     */
    public Type getReturnType() {
        return returnType;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        // Add routine signature with parameters and return type
        sb.append("RoutineDecl(").append(name);
        if (!parameters.isEmpty()) {
            sb.append(" params:[");
            for (int i = 0; i < parameters.size(); i++) {
                if (i > 0) sb.append(", ");
                sb.append(parameters.get(i));
            }
            sb.append("]");
        }
        if (returnType != null) {
            sb.append(" returns:").append(returnType);
        }
        sb.append(")\n");

        // Add body statements with tree structure
        for (int i = 0; i < body.size(); i++) {
            String[] lines = body.get(i).toString().split("\n");
            boolean isLast = i == body.size() - 1;
            
            // First line with branch symbol
            sb.append(isLast ? "└── " : "├── ").append(lines[0]).append("\n");
            
            // Subsequent lines with proper indentation
            for (int j = 1; j < lines.length; j++) {
                sb.append(isLast ? "    " : "│   ").append(lines[j]).append("\n");
            }
        }
        return sb.toString();
    }
}