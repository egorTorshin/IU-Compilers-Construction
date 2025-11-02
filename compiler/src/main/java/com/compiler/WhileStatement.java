package com.compiler;

import java.util.List;

/**
 * Represents a while loop statement in the abstract syntax tree.
 * A while statement consists of a condition expression and a body of statements
 * that are executed repeatedly as long as the condition evaluates to true.
 */
public class WhileStatement extends Statement {
    /** The condition expression that controls the while loop */
    private Expression condition;
    
    /** The list of statements in the loop body */
    private List<Statement> body;

    /**
     * Constructs a new while statement with the given condition and body.
     *
     * @param condition the condition expression that controls the loop
     * @param body the list of statements in the loop body
     */
    public WhileStatement(Expression condition, List<Statement> body) {
        this.condition = condition;
        this.body = body;
    }

    /**
     * Gets the condition expression of this while statement.
     *
     * @return the condition expression
     */
    public Expression getCondition() {
        return condition;
    }

    /**
     * Gets the list of statements in the loop body.
     *
     * @return the list of body statements
     */
    public List<Statement> getBody() {
        return body;
    }

    /**
     * Sets a new condition expression for this while statement.
     *
     * @param condition the new condition expression
     */
    public void setCondition(Expression condition) {
        this.condition = condition;
    }

    /**
     * Returns a string representation of this while statement as an AST tree.
     *
     * @return a formatted string showing the AST structure
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("WhileStatement [Loop]\n");
        sb.append("├── condition: ").append(condition.toString().replace("\n", "\n│   ")).append("\n");
        sb.append("└── body: ");
        if (body.isEmpty()) {
            sb.append("[empty]");
        } else {
            sb.append("\n");
            for (int i = 0; i < body.size(); i++) {
                Statement stmt = body.get(i);
                String prefix = (i == body.size() - 1) ? "    └── " : "    ├── ";
                String continuation = (i == body.size() - 1) ? "    " : "    │   ";
                sb.append(prefix)
                  .append(stmt.toString().replace("\n", "\n" + continuation));
                if (i < body.size() - 1) {
                    sb.append("\n");
                }
            }
        }
        return sb.toString();
    }
}