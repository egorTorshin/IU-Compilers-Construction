package com.compiler.ast;

import java.util.List;

public class IfStatement extends Statement {
    private Expression condition;
    private List<Statement> thenBody;
    private List<Statement> elseBody; // can be null

    public IfStatement(Expression condition, List<Statement> thenBody, List<Statement> elseBody) {
        this.condition = condition;
        this.thenBody = thenBody;
        this.elseBody = elseBody;
    }

    public Expression getCondition() {
        return condition;
    }

    public List<Statement> getThenStatements() {
        return thenBody;
    }

    public List<Statement> getElseStatements() {
        return elseBody;
    }

    /**
     * Sets the condition expression of this if statement.
     *
     * @param condition The new condition expression
     */
    public void setCondition(Expression condition) {
        this.condition = condition;
    }

    /**
     * Returns a string representation of this if statement using ASCII art tree structure.
     * The output includes the condition, then block, and else block (if present) in a
     * hierarchical format.
     *
     * @return A formatted string representation of the if statement
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("IF\n");
        sb.append("├── Condition: ").append(condition).append("\n");
        sb.append("├── Then:\n");
        
        // Handle then block statements
        for (Statement stmt : thenBody) {
            String[] lines = stmt.toString().split("\n");
            for (String line : lines) {
                sb.append("│   ").append(line).append("\n");
            }
        }
        
        // Handle optional else block
        if (elseBody != null && !elseBody.isEmpty()) {
            sb.append("└── Else:\n");
            for (Statement stmt : elseBody) {
                String[] lines = stmt.toString().split("\n");
                for (String line : lines) {
                    sb.append("    ").append(line).append("\n");
                }
            }
        } else {
            sb.append("└── [No Else Block]");
        }
        
        return sb.toString().trim();
    }
}
