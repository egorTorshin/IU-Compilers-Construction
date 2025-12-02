package com.compiler.ast;

import java.util.List;

/**
 * Represents a block of statements in the source code.
 * A block statement contains a list of statements that are executed sequentially.
 */
public class BlockStatement extends Statement {
    /** The list of statements contained in this block */
    private List<Statement> statements;

    /**
     * Creates a new block statement with the given list of statements.
     * @param statements The list of statements to be contained in this block
     */
    public BlockStatement(List<Statement> statements) {
        this.statements = statements;
    }

    /**
     * Gets the list of statements in this block.
     * @return The list of statements
     */
    public List<Statement> getStatements() {
        return statements;
    }

    /**
     * Returns a string representation of this block statement in tree format.
     * Each statement in the block is indented and connected with tree-style lines.
     * @return A hierarchical string representation of the block and its statements
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("BlockStatement\n");
        sb.append("└── Statements:\n");
        for (int i = 0; i < statements.size(); i++) {
            Statement stmt = statements.get(i);
            String prefix = (i == statements.size() - 1) ? "    └── " : "    ├── ";
            String[] lines = stmt.toString().split("\n");
            sb.append(prefix).append(lines[0]).append("\n");
            for (int j = 1; j < lines.length; j++) {
                String continuationPrefix = (i == statements.size() - 1) ? "        " : "    │   ";
                sb.append(continuationPrefix).append(lines[j]).append("\n");
            }
        }
        return sb.toString();
    }
}
