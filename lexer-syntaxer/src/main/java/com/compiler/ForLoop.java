package com.compiler;

import java.util.List;

/**
 * Represents a for loop statement in the programming language.
 * The loop can iterate either forward or backward through a range of integer values.
 */
public class ForLoop extends Statement {
    private String variable;      // Loop variable name
    private Reverse reverse;      // Direction of iteration
    private Expression start;     // Start of range
    private Expression end;       // End of range
    private List<Statement> body; // Loop body statements

    /**
     * Constructs a new ForLoop with the specified parameters.
     *
     * @param variable The name of the loop variable
     * @param reverse  The direction of iteration (forward/reverse)
     * @param start    The starting expression of the range
     * @param end      The ending expression of the range
     * @param body     The list of statements in the loop body
     */
    public ForLoop(String variable, Reverse reverse, Expression start, Expression end, List<Statement> body) {
        this.variable = variable;
        this.reverse = reverse;
        this.start = start;
        this.end = end;
        this.body = body;
    }

    /**
     * @return The loop variable name
     */
    public String getVariable() {
        return variable;
    }

    /**
     * @return The list of statements in the loop body
     */
    public List<Statement> getBody() {
        return body;
    }

    /**
     * @return The starting expression of the range
     */
    public Expression getRangeStart() {
        return start;
    }

    /**
     * @return The ending expression of the range
     */
    public Expression getRangeEnd() {
        return end;
    }

    /**
     * @return true if the loop iterates in reverse, false otherwise
     */
    public boolean isReverse() {
        return reverse.isReverse();
    }

    /**
     * @return The Reverse object indicating iteration direction
     */
    public Reverse getReverse() {
        return reverse;
    }

    /**
     * Executes the for loop, iterating through the range and executing body statements.
     */
    public void execute() {
        int startVal = ((Integer) start.evaluate());
        int endVal = ((Integer) end.evaluate());

        if (reverse.isReverse()) {
            for (int i = endVal; i >= startVal; i--) {
                // TODO: Set variable value to i in symbol table
                for (Statement stmt : body) {
                    // Execute each statement in the loop body
                    if (stmt instanceof PrintStatement) {
                        ((PrintStatement) stmt).execute();
                    }
                    // Add other statement type executions as needed
                }
            }
        } else {
            for (int i = startVal; i <= endVal; i++) {
                // TODO: Set variable value to i in symbol table
                for (Statement stmt : body) {
                    // Execute each statement in the loop body
                    if (stmt instanceof PrintStatement) {
                        ((PrintStatement) stmt).execute();
                    }
                    // Add other statement type executions as needed
                }
            }
        }
    }

    /**
     * Returns a string representation of the ForLoop in a tree-like structure.
     * Shows the loop variable, reverse flag, range bounds, and body statements.
     *
     * @return A formatted string representing the ForLoop structure
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ForLoop\n");
        sb.append("├── variable: ").append(variable).append("\n");
        sb.append("├── reverse: ").append(reverse.isReverse()).append("\n");
        sb.append("├── start: ").append(start).append("\n");
        sb.append("├── end: ").append(end).append("\n");
        sb.append("└── body:\n");
        
        // Format body statements with proper tree structure
        for (int i = 0; i < body.size(); i++) {
            Statement stmt = body.get(i);
            String[] lines = stmt.toString().split("\n");
            boolean isLast = (i == body.size() - 1);
            
            for (int j = 0; j < lines.length; j++) {
                boolean isFirstLine = (j == 0);
                if (isLast) {
                    sb.append(isFirstLine ? "    └── " : "        ");
                } else {
                    sb.append(isFirstLine ? "    ├── " : "    │   ");
                }
                sb.append(lines[j]).append("\n");
            }
        }
        return sb.toString();
    }
}
