package com.compiler.ast;

/**
 * Represents a string literal expression in the source code.
 * Handles string values with escape sequences like \n, \t, \", etc.
 */
public class StringLiteral extends Expression {
    /** The raw string value including quotes and escape sequences */
    private final String value;

    /**
     * Creates a new StringLiteral with the given raw string value.
     * @param value The raw string value including quotes and escape sequences
     */
    public StringLiteral(String value) {
        this.value = value;
    }

    /**
     * Gets the processed string value with quotes removed and escape sequences converted.
     * Handles common escape sequences like:
     * - \\ -> \
     * - \n -> newline
     * - \t -> tab
     * - \" -> "
     * - \r -> carriage return
     * 
     * @return The processed string value, or empty string if input is invalid
     */
    public String getValue() {
        if (value == null || value.length() < 2) {
            return "";
        }
        
        String unquoted = value.substring(1, value.length() - 1);
        
        String unescaped = unquoted
            .replace("\\\\", "\\")
            .replace("\\n", "\n")
            .replace("\\t", "\t")
            .replace("\\\"", "\"")
            .replace("\\r", "\r");
            
        return unescaped;
    }

    /**
     * Evaluates this string literal by returning its processed value.
     * @return The processed string value
     */
    @Override
    public Object evaluate() {
        return getValue();
    }

    /**
     * Returns a string representation of this string literal.
     * @return String in format "StringLiteral(value)"
     */
    @Override
    public String toString() {
        return "StringLiteral(" + value + ")";
    }
}
