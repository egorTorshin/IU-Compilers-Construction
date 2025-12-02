package com.compiler.semantic;

import com.compiler.ast.Parameter;
import com.compiler.ast.RoutineDecl;
import com.compiler.ast.SimpleType;
import com.compiler.ast.Type;

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
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        
        // Variables in scopes
        if (!scopes.isEmpty()) {
            sb.append("VARIABLES:\n");
            for (int i = 0; i < scopes.size(); i++) {
                Map<String, Type> scope = scopes.get(i);
                if (!scope.isEmpty()) {
                    sb.append("  Scope ").append(i).append(":\n");
                    for (Map.Entry<String, Type> entry : scope.entrySet()) {
                        sb.append("    ").append(entry.getKey())
                          .append(" : ").append(entry.getValue()).append("\n");
                    }
                }
            }
        }
        
        // Routines
        if (!routines.isEmpty()) {
            sb.append("\nROUTINES:\n");
            for (Map.Entry<String, RoutineDecl> entry : routines.entrySet()) {
                sb.append("  ").append(entry.getKey()).append("()\n");
            }
        }
        
        // User-defined types
        if (!types.isEmpty()) {
            sb.append("\nUSER-DEFINED TYPES:\n");
            for (Map.Entry<String, Type> entry : types.entrySet()) {
                sb.append("  ").append(entry.getKey())
                  .append(" = ").append(entry.getValue()).append("\n");
            }
        }
        
        if (sb.length() == 0) {
            return "No symbols defined";
        }
        
        return sb.toString();
    }
    
    // For HTML visualization - returns structured data
    public Map<String, Object> getVisualizationData() {
        Map<String, Object> data = new HashMap<>();
        
        // Collect all variables from all scopes
        List<Map<String, String>> variables = new ArrayList<>();
        for (int i = 0; i < scopes.size(); i++) {
            Map<String, Type> scope = scopes.get(i);
            String scopeName = (i == 0) ? "Global" : "Scope " + i;
            for (Map.Entry<String, Type> entry : scope.entrySet()) {
                Map<String, String> var = new HashMap<>();
                var.put("name", entry.getKey());
                var.put("type", entry.getValue().toString());
                var.put("scope", scopeName);
                variables.add(var);
            }
        }
        data.put("variables", variables);
        
        // Collect routines with detailed info
        List<Map<String, String>> routineList = new ArrayList<>();
        for (Map.Entry<String, RoutineDecl> entry : routines.entrySet()) {
            Map<String, String> routine = new HashMap<>();
            RoutineDecl decl = entry.getValue();
            routine.put("name", entry.getKey());
            
            // Build parameter list
            StringBuilder params = new StringBuilder();
            if (decl.getParameters() != null && !decl.getParameters().isEmpty()) {
                boolean first = true;
                for (Parameter param : decl.getParameters()) {
                    if (!first) params.append(", ");
                    params.append(param.getName()).append(": ").append(param.getType());
                    first = false;
                }
            }
            routine.put("parameters", params.toString());
            
            // Return type
            Type returnType = decl.getReturnType();
            routine.put("returnType", returnType != null ? returnType.toString() : "void");
            
            routineList.add(routine);
        }
        data.put("routines", routineList);
        
        // Collect user types
        Map<String, String> userTypes = new HashMap<>();
        for (Map.Entry<String, Type> entry : types.entrySet()) {
            userTypes.put(entry.getKey(), entry.getValue().toString());
        }
        data.put("types", userTypes);
        
        return data;
    }
    
    // Get public accessors for better visualization
    public List<Map<String, Type>> getScopes() {
        return scopes;
    }
    
    public Map<String, RoutineDecl> getRoutines() {
        return routines;
    }
    
    public Map<String, Type> getTypes() {
        return types;
    }
}
