package com.compiler;

/**
 * Represents a record field access expression in the form "record.field"
 */
public class RecordAccess extends Expression {
    private String record;
    private String field;

    /**
     * Creates a new record access expression
     * @param record The record identifier
     * @param field The field being accessed on the record
     */
    public RecordAccess(String record, String field) {
        this.record = record;
        this.field = field;
    }

    /**
     * Gets the record identifier
     * @return The record identifier
     */
    public String getRecord() {
        return record;
    }

    /**
     * Gets the field being accessed
     * @return The field identifier
     */
    public String getField() {
        return field;
    }

    /**
     * Evaluates this record access expression
     * @return The value of the accessed field
     */
    @Override
    public Object evaluate() {
        return null;
    }

    /**
     * Returns a string representation of this record access
     * @return A string in the format "record.field"
     */
    @Override
    public String toString() {
        return String.format("%s.%s", record, field);
    }
}
