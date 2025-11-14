package com.compiler.optimizer;

import com.compiler.*;
import java.util.*;

public class Optimizer {
    private boolean debug;
    private int optimizationCount;
    
    public Optimizer(boolean debug) {
        this.debug = debug;
        this.optimizationCount = 0;
    }
    
    public Optimizer() {
        this(false);
    }
    
    private void debugLog(String message) {
        if (debug) {
            System.err.println("[debug] optimizer: " + message);
        }
    }
    
    public int optimize(Program program) {
        debugLog("starting optimization");
        optimizationCount = 0;
        
        debugLog("pass 1: constant folding");
        optimizeConstantFolding(program);
        
        debugLog("pass 2: dead code elimination");
        optimizeDeadCode(program);
        
        debugLog("pass 3: unused variable elimination");
        optimizeUnusedVariables(program);
        
        debugLog("optimization completed: " + optimizationCount + " transformations applied");
        return optimizationCount;
    }
    
    private void optimizeConstantFolding(Program program) {
        List<Statement> statements = program.getStatements();
        for (int i = 0; i < statements.size(); i++) {
            Statement stmt = statements.get(i);
            Statement optimized = foldConstantsInStatement(stmt);
            if (optimized != stmt) {
                statements.set(i, optimized);
            }
        }
    }
    
    private Statement foldConstantsInStatement(Statement stmt) {
        if (stmt instanceof VarDecl) {
            VarDecl decl = (VarDecl) stmt;
            if (decl.getInitializer() != null) {
                Expression optimized = foldConstants(decl.getInitializer());
                if (optimized != decl.getInitializer()) {
                    optimizationCount++;
                    debugLog("folded constant in variable declaration: " + decl.getName());
                    return new VarDecl(decl.getName(), decl.getType(), optimized);
                }
            }
            return decl;
        }
        
        if (stmt instanceof Assignment) {
            Assignment assign = (Assignment) stmt;
            Expression optimizedValue = foldConstants(assign.getValue());
            Expression optimizedIndex = assign.getIndex() != null ? foldConstants(assign.getIndex()) : null;
            
            if (optimizedValue != assign.getValue() || optimizedIndex != assign.getIndex()) {
                optimizationCount++;
                debugLog("folded constant in assignment");
                return new Assignment(assign.getTarget(), optimizedIndex, optimizedValue);
            }
            return assign;
        }
        
        if (stmt instanceof IfStatement) {
            IfStatement ifStmt = (IfStatement) stmt;
            Expression optimizedCond = foldConstants(ifStmt.getCondition());
            
            List<Statement> optimizedThen = new ArrayList<>();
            for (Statement s : ifStmt.getThenStatements()) {
                optimizedThen.add(foldConstantsInStatement(s));
            }
            
            List<Statement> optimizedElse = null;
            if (ifStmt.getElseStatements() != null) {
                optimizedElse = new ArrayList<>();
                for (Statement s : ifStmt.getElseStatements()) {
                    optimizedElse.add(foldConstantsInStatement(s));
                }
            }
            
            if (optimizedCond != ifStmt.getCondition()) {
                optimizationCount++;
                debugLog("folded constant in if condition");
            }
            
            return new IfStatement(optimizedCond, optimizedThen, optimizedElse);
        }
        
        if (stmt instanceof WhileStatement) {
            WhileStatement whileStmt = (WhileStatement) stmt;
            Expression optimizedCond = foldConstants(whileStmt.getCondition());
            
            List<Statement> optimizedBody = new ArrayList<>();
            for (Statement s : whileStmt.getBody()) {
                optimizedBody.add(foldConstantsInStatement(s));
            }
            
            if (optimizedCond != whileStmt.getCondition()) {
                optimizationCount++;
                debugLog("folded constant in while condition");
            }
            
            return new WhileStatement(optimizedCond, optimizedBody);
        }
        
        if (stmt instanceof ForLoop) {
            ForLoop forLoop = (ForLoop) stmt;
            List<Statement> optimizedBody = new ArrayList<>();
            for (Statement s : forLoop.getBody()) {
                optimizedBody.add(foldConstantsInStatement(s));
            }
            return new ForLoop(
                forLoop.getVariable(),
                forLoop.getReverse(),
                forLoop.getRangeStart(),
                forLoop.getRangeEnd(),
                optimizedBody
            );
        }
        
        if (stmt instanceof PrintStatement) {
            PrintStatement printStmt = (PrintStatement) stmt;
            Expression optimized = foldConstants(printStmt.getExpression());
            if (optimized != printStmt.getExpression()) {
                optimizationCount++;
                debugLog("folded constant in print statement");
                return new PrintStatement(optimized);
            }
            return printStmt;
        }
        
        if (stmt instanceof ReturnStatement) {
            ReturnStatement returnStmt = (ReturnStatement) stmt;
            if (returnStmt.getExpression() != null) {
                Expression optimized = foldConstants(returnStmt.getExpression());
                if (optimized != returnStmt.getExpression()) {
                    optimizationCount++;
                    debugLog("folded constant in return statement");
                    return new ReturnStatement(optimized);
                }
            }
            return returnStmt;
        }
        
        if (stmt instanceof RoutineDecl) {
            RoutineDecl routine = (RoutineDecl) stmt;
            List<Statement> optimizedBody = new ArrayList<>();
            for (Statement s : routine.getBody()) {
                optimizedBody.add(foldConstantsInStatement(s));
            }
            return new RoutineDecl(
                routine.getName(),
                routine.getParameters(),
                routine.getReturnType(),
                optimizedBody
            );
        }
        
        if (stmt instanceof RoutineCallStatement) {
            RoutineCallStatement call = (RoutineCallStatement) stmt;
            List<Expression> optimizedArgs = new ArrayList<>();
            boolean changed = false;
            for (Expression arg : call.getArguments()) {
                Expression optimized = foldConstants(arg);
                optimizedArgs.add(optimized);
                if (optimized != arg) {
                    changed = true;
                }
            }
            if (changed) {
                optimizationCount++;
                debugLog("folded constants in routine call arguments");
                return new RoutineCallStatement(call.getName(), optimizedArgs);
            }
            return call;
        }
        
        return stmt;
    }
    
    private Expression foldConstants(Expression expr) {
        if (expr == null) {
            return null;
        }
        
        if (expr instanceof BinaryExpression) {
            BinaryExpression bin = (BinaryExpression) expr;
            Expression left = foldConstants(bin.getLeft());
            Expression right = foldConstants(bin.getRight());
            
            if (left instanceof IntegerLiteral && right instanceof IntegerLiteral) {
                int leftVal = ((IntegerLiteral) left).getValue();
                int rightVal = ((IntegerLiteral) right).getValue();
                String op = bin.getOperator();
                
                if (op.equals("+")) return new IntegerLiteral(leftVal + rightVal);
                if (op.equals("-")) return new IntegerLiteral(leftVal - rightVal);
                if (op.equals("*")) return new IntegerLiteral(leftVal * rightVal);
                if (op.equals("/") && rightVal != 0) return new IntegerLiteral(leftVal / rightVal);
                if (op.equals("%") && rightVal != 0) return new IntegerLiteral(leftVal % rightVal);
                
                if (op.equals(">")) return new BooleanLiteral(leftVal > rightVal);
                if (op.equals("<")) return new BooleanLiteral(leftVal < rightVal);
                if (op.equals(">=")) return new BooleanLiteral(leftVal >= rightVal);
                if (op.equals("<=")) return new BooleanLiteral(leftVal <= rightVal);
                if (op.equals("=")) return new BooleanLiteral(leftVal == rightVal);
                if (op.equals("!=")) return new BooleanLiteral(leftVal != rightVal);
            }
            
            if (left instanceof RealLiteral && right instanceof RealLiteral) {
                double leftVal = ((RealLiteral) left).getValue();
                double rightVal = ((RealLiteral) right).getValue();
                String op = bin.getOperator();
                
                if (op.equals("+")) return new RealLiteral(leftVal + rightVal);
                if (op.equals("-")) return new RealLiteral(leftVal - rightVal);
                if (op.equals("*")) return new RealLiteral(leftVal * rightVal);
                if (op.equals("/") && rightVal != 0) return new RealLiteral(leftVal / rightVal);
                
                if (op.equals(">")) return new BooleanLiteral(leftVal > rightVal);
                if (op.equals("<")) return new BooleanLiteral(leftVal < rightVal);
                if (op.equals(">=")) return new BooleanLiteral(leftVal >= rightVal);
                if (op.equals("<=")) return new BooleanLiteral(leftVal <= rightVal);
                if (op.equals("=")) return new BooleanLiteral(leftVal == rightVal);
                if (op.equals("!=")) return new BooleanLiteral(leftVal != rightVal);
            }
            
            if ((left instanceof IntegerLiteral && right instanceof RealLiteral) ||
                (left instanceof RealLiteral && right instanceof IntegerLiteral)) {
                double leftVal = left instanceof IntegerLiteral ? 
                    ((IntegerLiteral) left).getValue() : ((RealLiteral) left).getValue();
                double rightVal = right instanceof IntegerLiteral ? 
                    ((IntegerLiteral) right).getValue() : ((RealLiteral) right).getValue();
                String op = bin.getOperator();
                
                if (op.equals("+")) return new RealLiteral(leftVal + rightVal);
                if (op.equals("-")) return new RealLiteral(leftVal - rightVal);
                if (op.equals("*")) return new RealLiteral(leftVal * rightVal);
                if (op.equals("/") && rightVal != 0) return new RealLiteral(leftVal / rightVal);
            }
            
            if (left instanceof BooleanLiteral && right instanceof BooleanLiteral) {
                boolean leftVal = ((BooleanLiteral) left).getValue();
                boolean rightVal = ((BooleanLiteral) right).getValue();
                String op = bin.getOperator();
                
                if (op.equals("and")) return new BooleanLiteral(leftVal && rightVal);
                if (op.equals("or")) return new BooleanLiteral(leftVal || rightVal);
                if (op.equals("xor")) return new BooleanLiteral(leftVal ^ rightVal);
            }
            
            if (left != bin.getLeft() || right != bin.getRight()) {
                return new BinaryExpression(left, bin.getOperator(), right);
            }
            
            return bin;
        }
        
        if (expr instanceof UnaryExpression) {
            UnaryExpression unary = (UnaryExpression) expr;
            Expression operand = foldConstants(unary.getExpression());
            
            if (unary.getOperator().equals("not") && operand instanceof BooleanLiteral) {
                boolean val = ((BooleanLiteral) operand).getValue();
                return new BooleanLiteral(!val);
            }
            
            if (unary.getOperator().equals("-") && operand instanceof IntegerLiteral) {
                int val = ((IntegerLiteral) operand).getValue();
                return new IntegerLiteral(-val);
            }
            
            if (unary.getOperator().equals("-") && operand instanceof RealLiteral) {
                double val = ((RealLiteral) operand).getValue();
                return new RealLiteral(-val);
            }
            
            if (operand != unary.getExpression()) {
                return new UnaryExpression(unary.getOperator(), operand);
            }
            
            return unary;
        }
        
        if (expr instanceof ArrayAccess) {
            ArrayAccess access = (ArrayAccess) expr;
            Expression optimizedIndex = foldConstants(access.getIndex());
            if (optimizedIndex != access.getIndex()) {
                return new ArrayAccess(access.getArray(), optimizedIndex);
            }
            return access;
        }
        
        if (expr instanceof RoutineCall) {
            RoutineCall call = (RoutineCall) expr;
            List<Expression> optimizedArgs = new ArrayList<>();
            boolean changed = false;
            for (Expression arg : call.getArguments()) {
                Expression optimized = foldConstants(arg);
                optimizedArgs.add(optimized);
                if (optimized != arg) {
                    changed = true;
                }
            }
            if (changed) {
                return new RoutineCall(call.getName(), optimizedArgs);
            }
            return call;
        }
        
        return expr;
    }
    
    private void optimizeDeadCode(Program program) {
        List<Statement> statements = program.getStatements();
        for (int i = 0; i < statements.size(); i++) {
            Statement stmt = statements.get(i);
            Statement optimized = eliminateDeadCode(stmt);
            if (optimized != stmt) {
                statements.set(i, optimized);
            }
        }
    }
    
    private Statement eliminateDeadCode(Statement stmt) {
        if (stmt instanceof RoutineDecl) {
            RoutineDecl routine = (RoutineDecl) stmt;
            List<Statement> body = routine.getBody();
            List<Statement> optimizedBody = new ArrayList<>();
            
            boolean foundReturn = false;
            for (Statement s : body) {
                if (foundReturn) {
                    optimizationCount++;
                    debugLog("removed unreachable code after return in routine " + routine.getName());
                    break;
                }
                
                Statement optimized = eliminateDeadCode(s);
                optimizedBody.add(optimized);
                
                if (optimized instanceof ReturnStatement) {
                    foundReturn = true;
                }
            }
            
            return new RoutineDecl(
                routine.getName(),
                routine.getParameters(),
                routine.getReturnType(),
                optimizedBody
            );
        }
        
        if (stmt instanceof IfStatement) {
            IfStatement ifStmt = (IfStatement) stmt;
            Expression condition = ifStmt.getCondition();
            
            // if (true) - keep only then branch
            if (condition instanceof BooleanLiteral && ((BooleanLiteral) condition).getValue()) {
                optimizationCount++;
                debugLog("eliminated if(true): keeping then branch, removing else branch");
                
                List<Statement> optimizedThen = new ArrayList<>();
                for (Statement s : ifStmt.getThenStatements()) {
                    optimizedThen.add(eliminateDeadCode(s));
                }
                
                return new IfStatement(condition, optimizedThen, null);
            }
            
            // if (false) - keep only else branch
            if (condition instanceof BooleanLiteral && !((BooleanLiteral) condition).getValue()) {
                optimizationCount++;
                debugLog("eliminated if(false): keeping else branch, removing then branch");
                
                if (ifStmt.getElseStatements() != null) {
                    List<Statement> optimizedElse = new ArrayList<>();
                    for (Statement s : ifStmt.getElseStatements()) {
                        optimizedElse.add(eliminateDeadCode(s));
                    }
                    return new IfStatement(condition, new ArrayList<>(), optimizedElse);
                } else {
                    return new IfStatement(condition, new ArrayList<>(), null);
                }
            }
            
            List<Statement> optimizedThen = new ArrayList<>();
            for (Statement s : ifStmt.getThenStatements()) {
                optimizedThen.add(eliminateDeadCode(s));
            }
            
            List<Statement> optimizedElse = null;
            if (ifStmt.getElseStatements() != null) {
                optimizedElse = new ArrayList<>();
                for (Statement s : ifStmt.getElseStatements()) {
                    optimizedElse.add(eliminateDeadCode(s));
                }
            }
            
            return new IfStatement(condition, optimizedThen, optimizedElse);
        }
        
        if (stmt instanceof WhileStatement) {
            WhileStatement whileStmt = (WhileStatement) stmt;
            Expression condition = whileStmt.getCondition();
            
            // while (false) - entire loop is dead
            if (condition instanceof BooleanLiteral && !((BooleanLiteral) condition).getValue()) {
                optimizationCount++;
                debugLog("eliminated while(false): removing entire loop");
                return new WhileStatement(condition, new ArrayList<>());
            }
            
            List<Statement> optimizedBody = new ArrayList<>();
            for (Statement s : whileStmt.getBody()) {
                optimizedBody.add(eliminateDeadCode(s));
            }
            
            return new WhileStatement(condition, optimizedBody);
        }
        
        if (stmt instanceof ForLoop) {
            ForLoop forLoop = (ForLoop) stmt;
            List<Statement> optimizedBody = new ArrayList<>();
            for (Statement s : forLoop.getBody()) {
                optimizedBody.add(eliminateDeadCode(s));
            }
            return new ForLoop(
                forLoop.getVariable(),
                forLoop.getReverse(),
                forLoop.getRangeStart(),
                forLoop.getRangeEnd(),
                optimizedBody
            );
        }
        
        return stmt;
    }
    
    private void optimizeUnusedVariables(Program program) {
        Set<String> usedVars = new HashSet<>();
        collectUsedVariables(program, usedVars);
        
        List<Statement> statements = program.getStatements();
        List<Statement> filtered = new ArrayList<>();
        
        for (Statement stmt : statements) {
            if (stmt instanceof VarDecl) {
                VarDecl decl = (VarDecl) stmt;
                if (!usedVars.contains(decl.getName())) {
                    optimizationCount++;
                    debugLog("removed unused variable: " + decl.getName());
                    continue;
                }
            }
            
            if (stmt instanceof ArrayDecl) {
                ArrayDecl decl = (ArrayDecl) stmt;
                if (!usedVars.contains(decl.getName())) {
                    optimizationCount++;
                    debugLog("removed unused array: " + decl.getName());
                    continue;
                }
            }
            
            if (stmt instanceof RoutineDecl) {
                filtered.add(filterUnusedInRoutine((RoutineDecl) stmt, usedVars));
            } else {
                filtered.add(stmt);
            }
        }
        
        statements.clear();
        statements.addAll(filtered);
    }
    
    private Statement filterUnusedInRoutine(RoutineDecl routine, Set<String> globalUsed) {
        Set<String> localUsed = new HashSet<>();
        for (Statement stmt : routine.getBody()) {
            collectUsedInStatement(stmt, localUsed);
        }
        
        List<Statement> filtered = new ArrayList<>();
        for (Statement stmt : routine.getBody()) {
            if (stmt instanceof VarDecl) {
                VarDecl decl = (VarDecl) stmt;
                if (!localUsed.contains(decl.getName()) && !globalUsed.contains(decl.getName())) {
                    optimizationCount++;
                    debugLog("removed unused local variable in " + routine.getName() + ": " + decl.getName());
                    continue;
                }
            }
            
            if (stmt instanceof ArrayDecl) {
                ArrayDecl decl = (ArrayDecl) stmt;
                if (!localUsed.contains(decl.getName()) && !globalUsed.contains(decl.getName())) {
                    optimizationCount++;
                    debugLog("removed unused local array in " + routine.getName() + ": " + decl.getName());
                    continue;
                }
            }
            
            filtered.add(stmt);
        }
        
        return new RoutineDecl(
            routine.getName(),
            routine.getParameters(),
            routine.getReturnType(),
            filtered
        );
    }
    
    private void collectUsedVariables(Program program, Set<String> used) {
        for (Statement stmt : program.getStatements()) {
            collectUsedInStatement(stmt, used);
        }
    }
    
    private void collectUsedInStatement(Statement stmt, Set<String> used) {
        if (stmt instanceof Assignment) {
            Assignment assign = (Assignment) stmt;
            String target = assign.getTarget();
            
            if (target.contains(".")) {
                target = target.split("\\.")[0];
            }
            
            used.add(target);
            collectUsedInExpression(assign.getValue(), used);
            if (assign.getIndex() != null) {
                collectUsedInExpression(assign.getIndex(), used);
            }
        }
        
        if (stmt instanceof PrintStatement) {
            PrintStatement print = (PrintStatement) stmt;
            collectUsedInExpression(print.getExpression(), used);
        }
        
        if (stmt instanceof ReturnStatement) {
            ReturnStatement ret = (ReturnStatement) stmt;
            if (ret.getExpression() != null) {
                collectUsedInExpression(ret.getExpression(), used);
            }
        }
        
        if (stmt instanceof IfStatement) {
            IfStatement ifStmt = (IfStatement) stmt;
            collectUsedInExpression(ifStmt.getCondition(), used);
            for (Statement s : ifStmt.getThenStatements()) {
                collectUsedInStatement(s, used);
            }
            if (ifStmt.getElseStatements() != null) {
                for (Statement s : ifStmt.getElseStatements()) {
                    collectUsedInStatement(s, used);
                }
            }
        }
        
        if (stmt instanceof WhileStatement) {
            WhileStatement whileStmt = (WhileStatement) stmt;
            collectUsedInExpression(whileStmt.getCondition(), used);
            for (Statement s : whileStmt.getBody()) {
                collectUsedInStatement(s, used);
            }
        }
        
        if (stmt instanceof ForLoop) {
            ForLoop forLoop = (ForLoop) stmt;
            used.add(forLoop.getVariable());
            for (Statement s : forLoop.getBody()) {
                collectUsedInStatement(s, used);
            }
        }
        
        if (stmt instanceof RoutineDecl) {
            RoutineDecl routine = (RoutineDecl) stmt;
            for (Statement s : routine.getBody()) {
                collectUsedInStatement(s, used);
            }
        }
        
        if (stmt instanceof RoutineCallStatement) {
            RoutineCallStatement call = (RoutineCallStatement) stmt;
            for (Expression arg : call.getArguments()) {
                collectUsedInExpression(arg, used);
            }
        }
        
        if (stmt instanceof VarDecl) {
            VarDecl decl = (VarDecl) stmt;
            if (decl.getInitializer() != null) {
                collectUsedInExpression(decl.getInitializer(), used);
            }
        }
    }
    
    private void collectUsedInExpression(Expression expr, Set<String> used) {
        if (expr instanceof VariableReference) {
            used.add(((VariableReference) expr).getName());
        }
        
        if (expr instanceof BinaryExpression) {
            BinaryExpression bin = (BinaryExpression) expr;
            collectUsedInExpression(bin.getLeft(), used);
            collectUsedInExpression(bin.getRight(), used);
        }
        
        if (expr instanceof UnaryExpression) {
            UnaryExpression unary = (UnaryExpression) expr;
            collectUsedInExpression(unary.getExpression(), used);
        }
        
        if (expr instanceof ArrayAccess) {
            ArrayAccess access = (ArrayAccess) expr;
            used.add(access.getArray());
            collectUsedInExpression(access.getIndex(), used);
        }
        
        if (expr instanceof RecordAccess) {
            RecordAccess access = (RecordAccess) expr;
            used.add(access.getRecord());
        }
        
        if (expr instanceof RoutineCall) {
            RoutineCall call = (RoutineCall) expr;
            for (Expression arg : call.getArguments()) {
                collectUsedInExpression(arg, used);
            }
        }
        
        if (expr instanceof TypeCast) {
            TypeCast cast = (TypeCast) expr;
            collectUsedInExpression(cast.getExpression(), used);
        }
    }
    
    public int getOptimizationCount() {
        return optimizationCount;
    }
}

