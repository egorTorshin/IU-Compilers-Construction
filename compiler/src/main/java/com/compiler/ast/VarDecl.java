package com.compiler.ast;

public class VarDecl extends Statement implements VariableDeclaration {
    private String name;
    private Type type;
    private Expression initializer; // can be null

    public VarDecl(String name, Type type, Expression initializer) {
        this.name = name;
        this.type = type;
        this.initializer = initializer;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public Expression getInitializer() {
        return initializer;
    }

    /**
     * Sets a new initializer expression for this variable.
     * @param initializer The new initializer expression
     */
    public void setInitializer(Expression initializer) {
        this.initializer = initializer;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("VarDecl\n");
        sb.append("├── name: ").append(name).append("\n");
        sb.append("├── type: ").append(type).append("\n");
        sb.append("│   └── ").append(type.getClass().getSimpleName()).append("\n");
        if (initializer != null) {
            String[] initLines = initializer.toString().split("\n");
            for (int i = 0; i < initLines.length; i++) {
                if (i == 0) {
                    sb.append("└── initializer: ").append(initLines[i]).append("\n");
                } else {
                    sb.append("    ").append(initLines[i]).append("\n");
                }
            }
        } else {
            sb.append("└── initializer: null\n");
        }
        return sb.toString();
    }
}
