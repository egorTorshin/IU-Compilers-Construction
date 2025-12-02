package com.compiler.ast;

import java.util.List;

public class WhileStatement extends Statement {
    private Expression condition;
    private List<Statement> body;

    public WhileStatement(Expression condition, List<Statement> body) {
        this.condition = condition;
        this.body = body;
    }

    public Expression getCondition() {
        return condition;
    }

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