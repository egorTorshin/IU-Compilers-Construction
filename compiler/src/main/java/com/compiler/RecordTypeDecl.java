package com.compiler;

import java.util.List;

/**
 * Represents a record type declaration in the source code.
 * A record type is a user-defined data structure that contains a collection of named fields.
 */
public class RecordTypeDecl extends Statement {
    private final String name;
    private final List<VarDecl> fields;

    /**
     * Creates a new record type declaration.
     *
     * @param name   The name of the record type
     * @param fields The list of field declarations contained in this record
     */
    public RecordTypeDecl(String name, List<VarDecl> fields) {
        this.name = name;
        this.fields = fields;
    }

    /**
     * Gets the name of this record type.
     *
     * @return The record type name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the list of field declarations in this record type.
     *
     * @return The list of field declarations
     */
    public List<VarDecl> getFields() {
        return fields;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("RecordTypeDecl(name=").append(name)
          .append(", fields=[");
        
        for (int i = 0; i < fields.size(); i++) {
            if (i > 0) {
                sb.append(", ");
            }
            sb.append(fields.get(i));
        }
        sb.append("])");
        
        return sb.toString();
    }
} 