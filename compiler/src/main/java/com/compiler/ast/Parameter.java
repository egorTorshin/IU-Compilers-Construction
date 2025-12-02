package com.compiler.ast;

public class Parameter {
    private String name;
    private Type type;

    public Parameter(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return String.format("Parameter[name=%s, type=%s]", name, type);
    }
}
