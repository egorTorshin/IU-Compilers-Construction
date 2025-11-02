package com.compiler;

import java.util.List;

/**
 * Represents a complete program in the abstract syntax tree.
 * A program consists of a sequence of statements that will be executed in order.
 */
public class Program {
    /** The list of statements that make up this program */
    private List<Statement> statements;

    /**
     * Constructs a new Program with the specified list of statements.
     *
     * @param statements The list of statements that comprise the program
     */
    public Program(List<Statement> statements) {
        this.statements = statements;
    }

    /**
     * Gets the list of statements in this program.
     *
     * @return The list of statements
     */
    public List<Statement> getStatements() {
        return statements;
    }

    /**
     * Returns a string representation of this program using ASCII art tree structure.
     * Each statement is displayed hierarchically with appropriate connecting lines.
     * The last statement uses '└──' while other statements use '├──' to show the tree structure.
     *
     * @return A formatted string representation of the program and all its statements
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Program\n");
        
        for (int i = 0; i < statements.size(); i++) {
            Statement stmt = statements.get(i);
            String[] lines = stmt.toString().split("\n");
            boolean isLast = (i == statements.size() - 1);
            
            // First line with branch symbol
            sb.append(isLast ? "└── " : "├── ")
              .append(lines[0])
              .append("\n");
            
            // Subsequent lines with appropriate indentation
            for (int j = 1; j < lines.length; j++) {
                sb.append(isLast ? "    " : "│   ")
                  .append(lines[j])
                  .append("\n");
            }
        }
        return sb.toString();
    }
}
