package com.compiler.ast;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

/**
 * Represents a record type in the compiler type system.
 * A record type contains named fields, each with its own type.
 */
public class RecordType extends Type {
    private final Map<String, Type> fields;

    /**
     * Creates a new record type with the specified name and field definitions.
     *
     * @param name The name of the record type
     * @param fields Map of field names to their corresponding types
     */
    public RecordType(String name, Map<String, Type> fields) {
        super("record " + name);
        this.fields = fields;
    }

    /**
     * Creates a new record type from a list of variable declarations.
     *
     * @param fields List of variable declarations defining the record's fields
     */
    public RecordType(List<VariableDeclaration> fields) {
        super("record");
        this.fields = new HashMap<>();
        for (VariableDeclaration field : fields) {
            this.fields.put(field.getName(), field.getType());
        }
    }

    /**
     * Gets all fields in this record type.
     *
     * @return Map of field names to their types
     */
    public Map<String, Type> getFields() {
        return fields;
    }

    /**
     * Gets the type of a specific field.
     *
     * @param fieldName Name of the field
     * @return Type of the field, or null if field doesn't exist
     */
    public Type getFieldType(String fieldName) {
        return fields.get(fieldName);
    }

    /**
     * Checks if a field exists in this record type.
     *
     * @param fieldName Name of the field to check
     * @return true if the field exists, false otherwise
     */
    public boolean hasField(String fieldName) {
        return fields.containsKey(fieldName);
    }

    /**
     * Makes the fields iterable by providing access to the field entries.
     *
     * @return Iterable of field name and type entries
     */
    public Iterable<Map.Entry<String, Type>> getFieldEntries() {
        return fields.entrySet();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        boolean first = true;
        for (Map.Entry<String, Type> field : fields.entrySet()) {
            if (!first) {
                sb.append(", ");
            }
            sb.append(field.getKey()).append(": ").append(field.getValue());
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }
}
