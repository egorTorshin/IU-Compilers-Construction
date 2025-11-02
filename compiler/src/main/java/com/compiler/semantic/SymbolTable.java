package com.compiler.semantic;

import com.compiler.Type;
import com.compiler.SimpleType;
import com.compiler.RoutineDecl;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

// symbol table: variables, routines, types
// stack of scopes for nested visibility
public class SymbolTable {
    private List<Map<String, Type>> scopes;         // stack of scopes
    private Map<String, RoutineDecl> routines;      // routine declarations
    private Map<String, Type> types;                // user-defined types
    private Set<String> builtInTypes;               // built-in types

    public SymbolTable() {
        this.scopes = new ArrayList<>();
        this.scopes.add(new HashMap<>());
        this.routines = new HashMap<>();
        this.types = new HashMap<>();
        this.builtInTypes = new HashSet<>();
        initializeBuiltInTypes();
    }

    private void initializeBuiltInTypes() {
        builtInTypes.add("integer");
        builtInTypes.add("real");
        builtInTypes.add("boolean");
        builtInTypes.add("string");
        builtInTypes.add("void");
    }

    public void enterScope() {
        scopes.add(new HashMap<>());
    }

    public void exitScope() {
        if (scopes.size() > 1) {
            scopes.remove(scopes.size() - 1);
        }
    }

    public void declareVariable(String name, Type type) {
        scopes.get(scopes.size() - 1).put(name, type);
    }

    // check in any scope
    public boolean isDefined(String name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name)) {
                return true;
            }
        }
        return false;
    }

    // check only in current scope
    public boolean isDefinedInCurrentScope(String name) {
        return scopes.get(scopes.size() - 1).containsKey(name);
    }

    public Type getType(String name) {
        for (int i = scopes.size() - 1; i >= 0; i--) {
            Type type = scopes.get(i).get(name);
            if (type != null) {
                return type;
            }
        }
        return null;
    }

    // returns false if already defined
    public boolean declareRoutine(String name, RoutineDecl routine) {
        if (routines.containsKey(name)) {
            return false;
        }
        routines.put(name, routine);
        return true;
    }

    public RoutineDecl getRoutine(String name) {
        return routines.get(name);
    }

    public boolean isRoutineDefined(String name) {
        return routines.containsKey(name);
    }

    public void defineType(String name, Type type) {
        types.put(name, type);
    }

    public boolean isTypeDefined(String typeName) {
        return builtInTypes.contains(typeName) || types.containsKey(typeName);
    }

    public boolean isTypeDefined(Type type) {
        if (type instanceof SimpleType) {
            String typeName = ((SimpleType) type).getName();
            return builtInTypes.contains(typeName) || types.containsKey(typeName);
        }
        return true; // array, record - always valid
    }

    public void clear() {
        scopes.clear();
        scopes.add(new HashMap<>());
        routines.clear();
        types.clear();
        initializeBuiltInTypes();
    }

    public Type getTypeDefinition(String typeName) {
        return types.get(typeName);
    }

    public boolean declare(String name, Type type) {
        if (isDefinedInCurrentScope(name)) {
            return false;
        }
        scopes.get(scopes.size() - 1).put(name, type);
        return true;
    }
}
