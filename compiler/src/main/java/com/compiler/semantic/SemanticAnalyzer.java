package com.compiler.semantic;

import com.compiler.*;
import com.compiler.ast.ArrayAccess;
import com.compiler.ast.ArrayDecl;
import com.compiler.ast.ArrayType;
import com.compiler.ast.Assignment;
import com.compiler.ast.BinaryExpression;
import com.compiler.ast.BooleanLiteral;
import com.compiler.ast.Expression;
import com.compiler.ast.ForLoop;
import com.compiler.ast.IfStatement;
import com.compiler.ast.IntegerLiteral;
import com.compiler.ast.Parameter;
import com.compiler.ast.PrintStatement;
import com.compiler.ast.Program;
import com.compiler.ast.RealLiteral;
import com.compiler.ast.RecordAccess;
import com.compiler.ast.RecordType;
import com.compiler.ast.ReturnStatement;
import com.compiler.ast.RoutineCall;
import com.compiler.ast.RoutineCallStatement;
import com.compiler.ast.RoutineDecl;
import com.compiler.ast.SimpleType;
import com.compiler.ast.Statement;
import com.compiler.ast.StringLiteral;
import com.compiler.ast.Type;
import com.compiler.ast.TypeCast;
import com.compiler.ast.TypeDecl;
import com.compiler.ast.UnaryExpression;
import com.compiler.ast.VarDecl;
import com.compiler.ast.VariableReference;
import com.compiler.ast.WhileStatement;

import java.util.*;

// performs semantic analysis: type checking, scoping, validation
public class SemanticAnalyzer {
    private SymbolTable symbolTable;
    private List<SemanticError> errors;
    private Stack<Type> expectedReturnTypes;
    private Set<String> usedVariables;
    private boolean insideLoop;
    private boolean debug;

    public SemanticAnalyzer(boolean debug) {
        this.symbolTable = new SymbolTable();
        this.errors = new ArrayList<>();
        this.expectedReturnTypes = new Stack<>();
        this.usedVariables = new HashSet<>();
        this.insideLoop = false;
        this.debug = debug;
    }

    public SemanticAnalyzer() {
        this(false);
    }

    private void debugLog(String message) {
        if (debug) {
            System.err.println("[DEBUG] Semantic Analysis: " + message);
        }
    }

    // main entry: analyze program AST
    public List<SemanticError> analyze(Program program) {
        debugLog("Starting semantic analysis");
        errors.clear();
        symbolTable.clear();
        usedVariables.clear();
        
        symbolTable.enterScope();
        debugLog("Entered global scope");
        
        // pass 1: collect routine declarations
        debugLog("First pass: collecting routine declarations");
        for (Statement stmt : program.getStatements()) {
            if (stmt instanceof RoutineDecl) {
                RoutineDecl routine = (RoutineDecl) stmt;
                debugLog("Found routine declaration: " + routine.getName());
                if (!symbolTable.declareRoutine(routine.getName(), routine)) {
                    errors.add(new SemanticError("Routine " + routine.getName() + " is already defined"));
                }
            }
        }
        
        // pass 2: collect type declarations
        debugLog("Second pass: collecting type declarations");
        for (Statement stmt : program.getStatements()) {
            if (stmt instanceof TypeDecl) {
                debugLog("Processing type declaration");
                visitTypeDecl((TypeDecl) stmt);
            }
        }
        
        // pass 3: collect variable declarations
        debugLog("Third pass: collecting variable declarations");
        for (Statement stmt : program.getStatements()) {
            if (stmt instanceof VarDecl || stmt instanceof ArrayDecl) {
                debugLog("Processing variable/array declaration");
                visitStatement(stmt);
            }
        }
        
        // pass 4: analyze routine bodies
        debugLog("Fourth pass: analyzing routine bodies");
        for (Statement stmt : program.getStatements()) {
            if (stmt instanceof RoutineDecl) {
                debugLog("Analyzing routine body: " + ((RoutineDecl) stmt).getName());
                visitRoutineBody((RoutineDecl) stmt);
            }
        }
        
        // pass 5: analyze remaining statements
        debugLog("Fifth pass: analyzing remaining statements");
        for (Statement stmt : program.getStatements()) {
            if (!(stmt instanceof TypeDecl) && 
                !(stmt instanceof VarDecl) && 
                !(stmt instanceof ArrayDecl) && 
                !(stmt instanceof RoutineDecl)) {
                visitStatement(stmt);
            }
        }
        
        symbolTable.exitScope();
        debugLog("Exited global scope");
        
        if (errors.isEmpty()) {
            debugLog("Semantic analysis completed successfully");
        } else {
            debugLog("Semantic analysis completed with " + errors.size() + " errors");
        }
        
        return errors;
    }

    // dispatch to appropriate visit method based on statement type
    private void visitStatement(Statement stmt) {
        debugLog("Visiting statement: " + stmt.getClass().getSimpleName());
        if (stmt instanceof VarDecl) {
            visitVarDecl((VarDecl) stmt);
        } else if (stmt instanceof ArrayDecl) {
            visitArrayDecl((ArrayDecl) stmt);
        } else if (stmt instanceof Assignment) {
            visitAssignment((Assignment) stmt);
        } else if (stmt instanceof IfStatement) {
            visitIfStatement((IfStatement) stmt);
        } else if (stmt instanceof WhileStatement) {
            visitWhileStatement((WhileStatement) stmt);
        } else if (stmt instanceof ForLoop) {
            visitForLoop((ForLoop) stmt);
        } else if (stmt instanceof PrintStatement) {
            visitPrintStatement((PrintStatement) stmt);
        } else if (stmt instanceof ReturnStatement) {
            visitReturnStatement((ReturnStatement) stmt);
        } else if (stmt instanceof RoutineCallStatement) {
            visitRoutineCallStatement((RoutineCallStatement) stmt);
        }
    }

    // check variable declarations
    private void visitVarDecl(VarDecl decl) {
        debugLog("Visiting variable declaration: " + decl.getName());
        if (symbolTable.isDefinedInCurrentScope(decl.getName())) {
            errors.add(new SemanticError("Variable " + decl.getName() + " is already declared in this scope"));
            return;
        }

        Type declaredType = decl.getType();
        
        if (declaredType instanceof SimpleType) {
            String typeName = ((SimpleType) declaredType).getName();
            Type actualType = symbolTable.getTypeDefinition(typeName);
            if (actualType != null) {
                declaredType = actualType;
            }
        }
        
        if (!isValidType(declaredType)) {
            errors.add(new SemanticError("Unknown type " + declaredType));
            return;
        }

        // Declare the variable first so initializers can reference previously declared names
        symbolTable.declareVariable(decl.getName(), declaredType);

        // If there is an initializer, validate its type and trigger expression checks
        Expression initializer = decl.getInitializer();
        if (initializer != null) {
            Type valueType = getExpressionType(initializer);
            if (!isTypeCompatible(declaredType, valueType)) {
                errors.add(new SemanticError(
                        "Type mismatch in initialization: cannot assign value of type " +
                        valueType + " to variable of type " + declaredType));
            }
        }
    }

    // check assignments
    private void visitAssignment(Assignment assign) {
        String target = assign.getTarget();
        
        // array assignment
        if (assign.getIndex() != null) {
            if (!symbolTable.isDefined(target)) {
                errors.add(new SemanticError("Undefined array variable " + target));
                return;
            }
            
            Type arrayType = symbolTable.getType(target);
            if (!(arrayType instanceof ArrayType)) {
                errors.add(new SemanticError("Variable " + target + " is not an array"));
                return;
            }
            
            Type indexType = getExpressionType(assign.getIndex());
            if (!(indexType instanceof SimpleType && ((SimpleType)indexType).getName().equals("integer"))) {
                errors.add(new SemanticError("Array index must be an integer"));
                return;
            }
            
            // array bound checking for constant indices
            checkArrayBounds(assign.getIndex(), (ArrayType) arrayType, target);
            
            Type elementType = ((ArrayType)arrayType).getElementType();
            Type valueType = getExpressionType(assign.getValue());
            
            if (!isTypeCompatible(elementType, valueType)) {
                errors.add(new SemanticError("Type mismatch in array assignment: cannot assign value of type " +
                        valueType + " to array element of type " + elementType));
            }
            
            usedVariables.add(target);
            return;
        }
        
        // record field assignment
        if (target.contains(".")) {
            String[] parts = target.split("\\.");
            String recordName = parts[0];
            String fieldName = parts[1];
            
            if (!symbolTable.isDefined(recordName)) {
                errors.add(new SemanticError("Undefined record variable " + recordName));
                return;
            }
            
            Type recordType = symbolTable.getType(recordName);
            
            if (recordType instanceof SimpleType) {
                String typeName = ((SimpleType) recordType).getName();
                Type actualType = symbolTable.getTypeDefinition(typeName);
                if (actualType instanceof RecordType) {
                    recordType = actualType;
                } else {
                    errors.add(new SemanticError("Variable " + recordName + " is not a record"));
                    return;
                }
            }
            
            if (!(recordType instanceof RecordType)) {
                errors.add(new SemanticError("Variable " + recordName + " is not a record"));
                return;
            }
            
            RecordType record = (RecordType) recordType;
            if (!record.hasField(fieldName)) {
                errors.add(new SemanticError("Field " + fieldName + " does not exist in record " + recordName));
                return;
            }
            
            Type fieldType = record.getFieldType(fieldName);
            Type valueType = getExpressionType(assign.getValue());
            
            if (!isTypeCompatible(fieldType, valueType)) {
                errors.add(new SemanticError("Type mismatch in assignment: cannot assign value of type " +
                        valueType + " to field of type " + fieldType));
            }
            
            usedVariables.add(recordName);
            return;
        }
        
        // regular variable assignment
        if (!symbolTable.isDefined(target)) {
            errors.add(new SemanticError("Undefined variable " + target));
            return;
        }

        Type targetType = symbolTable.getType(target);
        Type valueType = getExpressionType(assign.getValue());

        if (!isTypeCompatible(targetType, valueType)) {
            errors.add(new SemanticError("Type mismatch in assignment: cannot assign value of type " +
                    valueType + " to variable of type " + targetType));
        }

        usedVariables.add(target);
    }

    // check if statements
    private void visitIfStatement(IfStatement ifStmt) {
        Type conditionType = getExpressionType(ifStmt.getCondition());
        if (!isBoolean(conditionType)) {
            errors.add(new SemanticError("If statement condition must be a boolean expression"));
        }

        for (Statement stmt : ifStmt.getThenStatements()) {
            visitStatement(stmt);
        }

        if (ifStmt.getElseStatements() != null) {
            for (Statement stmt : ifStmt.getElseStatements()) {
                visitStatement(stmt);
            }
        }
    }

    // check while loops
    private void visitWhileStatement(WhileStatement whileStmt) {
        Type conditionType = getExpressionType(whileStmt.getCondition());
        if (!isBoolean(conditionType)) {
            errors.add(new SemanticError("While statement condition must be a boolean expression"));
        }

        // enter loop context
        boolean wasInsideLoop = insideLoop;
        insideLoop = true;

        for (Statement stmt : whileStmt.getBody()) {
            visitStatement(stmt);
        }

        // restore previous loop context
        insideLoop = wasInsideLoop;
    }

    // check for loops
    private void visitForLoop(ForLoop forLoop) {
        if (!symbolTable.isDefined(forLoop.getVariable())) {
            errors.add(new SemanticError("Undefined loop variable " + forLoop.getVariable()));
        }

        // enter loop context
        boolean wasInsideLoop = insideLoop;
        insideLoop = true;

        for (Statement stmt : forLoop.getBody()) {
            visitStatement(stmt);
        }

        // restore previous loop context
        insideLoop = wasInsideLoop;
    }

    // check return statements
    private void visitReturnStatement(ReturnStatement returnStmt) {
        if (expectedReturnTypes.isEmpty()) {
            errors.add(new SemanticError("Return statement not allowed in this context"));
            return;
        }

        Type expectedType = expectedReturnTypes.peek();
        Type actualType = getExpressionType(returnStmt.getExpression());

        if (expectedType == null && actualType != null) {
            errors.add(new SemanticError("Unexpected return value in routine without return type"));
            return;
        }

        if (expectedType != null && actualType == null) {
            errors.add(new SemanticError("Missing return value for routine with return type " + expectedType));
            return;
        }

        if (!isTypeCompatible(expectedType, actualType)) {
            errors.add(new SemanticError("Return type mismatch: expected " + expectedType +
                    ", but got " + actualType));
        }
    }

    // check print statements
    private void visitPrintStatement(PrintStatement printStmt) {
        Expression expr = printStmt.getExpression();
        Type exprType = getExpressionType(expr);
        
        if (exprType == null) {
            errors.add(new SemanticError("Invalid expression in print statement"));
            return;
        }

        if (expr instanceof ArrayAccess) {
            ArrayAccess access = (ArrayAccess) expr;
            String arrayName = access.getArray();
            
            if (!symbolTable.isDefined(arrayName)) {
                errors.add(new SemanticError("Undefined array " + arrayName));
                return;
            }
            
            Type arrayType = symbolTable.getType(arrayName);
            if (!(arrayType instanceof ArrayType)) {
                errors.add(new SemanticError("Variable " + arrayName + " is not an array"));
                return;
            }
            
            Type indexType = getExpressionType(access.getIndex());
            if (!(indexType instanceof SimpleType && ((SimpleType)indexType).getName().equals("integer"))) {
                errors.add(new SemanticError("Array index must be an integer"));
                return;
            }
        }
    }

    private boolean hasReturnStatement(List<Statement> statements) {
        for (Statement stmt : statements) {
            if (stmt instanceof ReturnStatement) {
                return true;
            }
            if (stmt instanceof IfStatement) {
                IfStatement ifStmt = (IfStatement) stmt;
                if (hasReturnStatement(ifStmt.getThenStatements()) &&
                        (ifStmt.getElseStatements() == null || hasReturnStatement(ifStmt.getElseStatements()))) {
                    return true;
                }
            }
        }
        return false;
    }

    // determine type of expression through recursive analysis
    private Type getExpressionType(Expression expr) {
        if (expr == null) {
            return null;
        }

        if (expr instanceof ArrayAccess) {
            ArrayAccess access = (ArrayAccess) expr;
            String arrayName = access.getArray();
            
            if (!symbolTable.isDefined(arrayName)) {
                errors.add(new SemanticError("Undefined array " + arrayName));
                return null;
            }
            
            Type arrayType = symbolTable.getType(arrayName);
            if (!(arrayType instanceof ArrayType)) {
                errors.add(new SemanticError("Variable " + arrayName + " is not an array"));
                return null;
            }
            
            Type indexType = getExpressionType(access.getIndex());
            if (!(indexType instanceof SimpleType && ((SimpleType)indexType).getName().equals("integer"))) {
                errors.add(new SemanticError("Array index must be an integer"));
                return null;
            }
            
            // array bound checking for constant indices
            checkArrayBounds(access.getIndex(), (ArrayType) arrayType, arrayName);
            
            usedVariables.add(arrayName);
            return ((ArrayType)arrayType).getElementType();
        }

        if (expr instanceof VariableReference) {
            String varName = ((VariableReference) expr).getName();
            if (!symbolTable.isDefined(varName)) {
                errors.add(new SemanticError("Undefined variable '" + varName + "'"));
                return null;
            }
            Type type = symbolTable.getType(varName);
            usedVariables.add(varName);
            return type;
        }

        if (expr instanceof RecordAccess) {
            RecordAccess access = (RecordAccess) expr;
            Type recordType = symbolTable.getType(access.getRecord());
            
            if (recordType == null) {
                errors.add(new SemanticError("Undefined variable " + access.getRecord()));
                return null;
            }
            
            if (recordType instanceof SimpleType) {
                String typeName = ((SimpleType) recordType).getName();
                Type actualType = symbolTable.getTypeDefinition(typeName);
                if (actualType instanceof RecordType) {
                    recordType = actualType;
                } else {
                    errors.add(new SemanticError("Variable " + access.getRecord() + " is not a record"));
                    return null;
                }
            }
            
            if (!(recordType instanceof RecordType)) {
                errors.add(new SemanticError("Variable " + access.getRecord() + " is not a record"));
                return null;
            }
            
            RecordType record = (RecordType) recordType;
            if (!record.hasField(access.getField())) {
                errors.add(new SemanticError("Field " + access.getField() + " does not exist in record " + access.getRecord()));
                return null;
            }
            
            return record.getFieldType(access.getField());
        }

        if (expr instanceof TypeCast) {
            TypeCast cast = (TypeCast) expr;
            Type sourceType = getExpressionType(cast.getExpression());
            Type targetType = cast.getTargetType();

            if (isValidCast(sourceType, targetType)) {
                return targetType;
            } else {
                errors.add(new SemanticError("Invalid type cast from " + sourceType + " to " + targetType));
                return null;
            }
        }

        if (expr instanceof RoutineCall) {
            return getRoutineCallType((RoutineCall) expr);
        }

        if (expr instanceof IntegerLiteral) {
            return new SimpleType("integer");
        } else if (expr instanceof RealLiteral) {
            String value = expr.toString();
            if (value.contains(".")) {
                return new SimpleType("float");
            }
            return new SimpleType("real");
        } else if (expr instanceof BooleanLiteral) {
            return new SimpleType("boolean");
        } else if (expr instanceof StringLiteral) {
            return new SimpleType("string");
        } else if (expr instanceof BinaryExpression) {
            BinaryExpression binExpr = (BinaryExpression) expr;
            Type leftType = getExpressionType(binExpr.getLeft());
            Type rightType = getExpressionType(binExpr.getRight());
            String operator = binExpr.getOperator();

            // comparison operators always return boolean
            if (operator.equals(">") || operator.equals("<") ||
                    operator.equals(">=") || operator.equals("<=") ||
                    operator.equals("=") || operator.equals("!=")) {
                if (isNumeric(leftType) && isNumeric(rightType)) {
                    return new SimpleType("boolean");
                }
                return new SimpleType("boolean");
            }

            // logical operators require boolean operands and return boolean
            if (operator.equals("and") || operator.equals("or") || operator.equals("xor")) {
                if (!isBoolean(leftType) || !isBoolean(rightType)) {
                    errors.add(new SemanticError("Logical operators require boolean operands"));
                }
                return new SimpleType("boolean");
            }

            // arithmetic operations
            if (leftType instanceof SimpleType && rightType instanceof SimpleType) {
                String leftName = ((SimpleType) leftType).getName();
                String rightName = ((SimpleType) rightType).getName();

                if (leftName.equals("integer") && rightName.equals("integer")) {
                    return new SimpleType("integer");
                }
                if ((leftName.equals("integer") || leftName.equals("real")) &&
                        (rightName.equals("integer") || rightName.equals("real"))) {
                    return new SimpleType("real");
                }
            }
            return leftType;
        } else if (expr instanceof UnaryExpression) {
            UnaryExpression unaryExpr = (UnaryExpression) expr;
            Type operandType = getExpressionType(unaryExpr.getExpression());

            if (unaryExpr.getOperator().equals("not")) {
                if (!isBoolean(operandType)) {
                    errors.add(new SemanticError("Not operator requires boolean operand"));
                }
                return new SimpleType("boolean");
            }

            return operandType;
        }
        return null;
    }

    private boolean isNumeric(Type type) {
        if (type instanceof SimpleType) {
            String typeName = ((SimpleType) type).getName();
            return typeName.equals("integer") || typeName.equals("real");
        }
        return false;
    }

    // check routine calls
    private Type getRoutineCallType(RoutineCall call) {
        RoutineDecl routine = symbolTable.getRoutine(call.getName());
        if (routine == null) {
            errors.add(new SemanticError("Undefined routine " + call.getName()));
            return null;
        }

        List<Parameter> params = routine.getParameters();
        int expectedCount = params != null ? params.size() : 0;
        int actualCount = call.getArguments() != null ? call.getArguments().size() : 0;

        if (expectedCount != actualCount) {
            errors.add(new SemanticError("Wrong number of arguments in call to " + call.getName() +
                    ": expected " + expectedCount + ", got " + actualCount));
            return null;
        }

        if (params != null) {
            for (int i = 0; i < params.size(); i++) {
                Type expectedType = params.get(i).getType();
                Type actualType = getExpressionType(call.getArguments().get(i));
                if (!isTypeCompatible(expectedType, actualType)) {
                    errors.add(new SemanticError("Type mismatch in argument " + (i + 1) +
                            " of call to " + call.getName() + ": expected " + expectedType +
                            ", got " + actualType));
                }
            }
        }

        return routine.getReturnType();
    }

    private boolean isValidType(Type type) {
        if (type == null) {
            return false;
        }

        if (type instanceof SimpleType) {
            String typeName = ((SimpleType) type).getName();
            return typeName.equals("integer") || 
                   typeName.equals("real") || 
                   typeName.equals("float") || 
                   typeName.equals("boolean") || 
                   typeName.equals("string") ||
                   symbolTable.isTypeDefined(typeName);
        } else if (type instanceof ArrayType) {
            ArrayType arrayType = (ArrayType) type;
            return isValidType(arrayType.getElementType());
        } else if (type instanceof RecordType) {
            RecordType recordType = (RecordType) type;
            for (Map.Entry<String, Type> field : recordType.getFieldEntries()) {
                if (!isValidType(field.getValue())) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    // check if two types are compatible for assignment/comparison
    private boolean isTypeCompatible(Type expected, Type actual) {
        if (expected == null || actual == null) {
            return false;
        }

        if (expected instanceof ArrayType && actual instanceof ArrayType) {
            ArrayType expectedArray = (ArrayType) expected;
            ArrayType actualArray = (ArrayType) actual;
            return expectedArray.getSize() == actualArray.getSize() &&
                   isTypeCompatible(expectedArray.getElementType(), actualArray.getElementType());
        }

        if (expected instanceof SimpleType && actual instanceof SimpleType) {
            String expectedName = ((SimpleType) expected).getName();
            String actualName = ((SimpleType) actual).getName();

            if (expectedName.equals(actualName)) {
                return true;
            }

            // numeric type conversions
            if (expectedName.equals("float")) {
                return actualName.equals("integer") || actualName.equals("real");
            }
            if (expectedName.equals("real")) {
                return actualName.equals("integer") || actualName.equals("float");
            }
        }

        return false;
    }

    private boolean isBoolean(Type type) {
        if (type instanceof SimpleType) {
            return ((SimpleType) type).getName().equals("boolean");
        }
        return false;
    }

    // check array bounds for constant indices
    private void checkArrayBounds(Expression indexExpr, ArrayType arrayType, String arrayName) {
        // only check if index is a constant integer literal
        if (indexExpr instanceof IntegerLiteral) {
            int index = ((IntegerLiteral) indexExpr).getValue();
            int arraySize = arrayType.getSize();
            
            if (index < 0) {
                errors.add(new SemanticError("Array index " + index + " is negative for array " + arrayName));
            } else if (index > arraySize) {
                errors.add(new SemanticError("Array index " + index + " is out of bounds for array " + 
                    arrayName + " (size: " + arraySize + ")"));
            }
        }
    }

    private boolean isValidCast(Type sourceType, Type targetType) {
        if (sourceType == null || targetType == null) {
            return false;
        }

        if (sourceType instanceof SimpleType && targetType instanceof SimpleType) {
            String sourceName = ((SimpleType) sourceType).getName();
            String targetName = ((SimpleType) targetType).getName();

            if (sourceName.equals(targetName)) {
                return true;
            }

            // numeric conversions (both ways)
            if ((sourceName.equals("integer") || sourceName.equals("real")) &&
                    (targetName.equals("integer") || targetName.equals("real"))) {
                return true;
            }

            // boolean conversions
            if (sourceName.equals("boolean") && targetName.equals("integer")) {
                return true;
            }
            if (sourceName.equals("integer") && targetName.equals("boolean")) {
                return true;
            }
            if (sourceName.equals("real") && targetName.equals("boolean")) {
                return true;
            }
            if (sourceName.equals("boolean") && targetName.equals("real")) {
                return true;
            }
        }

        return false;
    }

    private void visitTypeDecl(TypeDecl typeDecl) {
        String typeName = typeDecl.getName();
        Type type = typeDecl.getType();
        
        if (symbolTable.isTypeDefined(typeName)) {
            errors.add(new SemanticError("Type " + typeName + " is already defined"));
            return;
        }
        
        if (type instanceof RecordType) {
            RecordType recordType = (RecordType) type;
            for (Map.Entry<String, Type> field : recordType.getFieldEntries()) {
                if (!isValidType(field.getValue())) {
                    errors.add(new SemanticError("Invalid field type " + field.getValue() + 
                        " in record " + typeName));
                    return;
                }
            }
        }
        
        symbolTable.defineType(typeName, type);
    }

    private void visitArrayDecl(ArrayDecl decl) {
        if (symbolTable.isDefinedInCurrentScope(decl.getName())) {
            errors.add(new SemanticError("Array " + decl.getName() + " is already declared in this scope"));
            return;
        }

        ArrayType arrayType = (ArrayType) decl.getType();
        
        Type elementType = arrayType.getElementType();
        if (!isValidType(elementType)) {
            errors.add(new SemanticError("Invalid array element type: " + elementType));
            return;
        }
        
        if (arrayType.getSize() <= 0) {
            errors.add(new SemanticError("Array size must be positive"));
            return;
        }

        symbolTable.declareVariable(decl.getName(), arrayType);
    }

    private void visitRoutineBody(RoutineDecl routine) {
        symbolTable.enterScope();

        if (routine.getParameters() != null) {
            for (Parameter param : routine.getParameters()) {
                if (!symbolTable.declare(param.getName(), param.getType())) {
                    errors.add(new SemanticError("Parameter " + param.getName() + " is already defined"));
                }
            }
        }

        if (routine.getReturnType() != null) {
            expectedReturnTypes.push(routine.getReturnType());
        }

        for (Statement stmt : routine.getBody()) {
            visitStatement(stmt);
        }

        if (routine.getReturnType() != null) {
            // ensure routines with a return type have at least one return in body (conservative)
            if (!hasReturnStatement(routine.getBody())) {
                errors.add(new SemanticError("Routine '" + routine.getName() + "' with return type " +
                        routine.getReturnType() + " must have a return statement"));
            }
            expectedReturnTypes.pop();
        }

        symbolTable.exitScope();
    }

    private void visitRoutineCallStatement(RoutineCallStatement stmt) {
        RoutineDecl routine = symbolTable.getRoutine(stmt.getName());
        if (routine == null) {
            errors.add(new SemanticError("Undefined routine " + stmt.getName()));
            return;
        }

        List<Parameter> params = routine.getParameters();
        List<Expression> args = stmt.getArguments();
        
        if (params.size() != args.size()) {
            errors.add(new SemanticError("Wrong number of arguments for routine " + stmt.getName() +
                ". Expected " + params.size() + ", got " + args.size()));
            return;
        }

        for (int i = 0; i < params.size(); i++) {
            Type paramType = params.get(i).getType();
            Type argType = getExpressionType(args.get(i));
            if (!isTypeCompatible(paramType, argType)) {
                errors.add(new SemanticError("Argument " + (i + 1) + " type mismatch in call to " + 
                    stmt.getName() + ". Expected " + paramType + ", got " + argType));
            }
        }
    }

    public SymbolTable getSymbolTable() {
        return symbolTable;
    }
}
