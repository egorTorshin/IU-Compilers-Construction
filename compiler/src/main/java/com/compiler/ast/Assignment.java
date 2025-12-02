package com.compiler.ast;

public class Assignment extends Statement {
    private String target;
    private Expression value;
    private Expression index; // null for regular assignments

    public Assignment(String target, Expression value) {
        this.target = target;
        this.value = value;
        this.index = null;
    }

    public Assignment(String target, Expression index, Expression value) {
        this.target = target;
        this.index = index;
        this.value = value;
    }

    public String getTarget() {
        return target;
    }

    public Expression getValue() {
        return value;
    }

    public void setValue(Expression value) {
        this.value = value;
    }

    public Expression getIndex() {
        return index;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Assignment\n");
        sb.append("├── Target: ").append(target).append("\n");
        if (index != null) {
            sb.append("├── Index: ").append(index).append("\n");
        }
        sb.append("└── Value: ").append(value);
        return sb.toString();
    }
}
