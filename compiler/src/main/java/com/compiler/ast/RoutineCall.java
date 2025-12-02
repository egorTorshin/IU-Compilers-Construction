package com.compiler.ast;

import java.util.List;

/**
 * Represents a routine (function/procedure) call expression in the program.
 * A routine call consists of a routine name and a list of argument expressions.
 */
public class RoutineCall extends Expression {
    /** The name of the routine being called */
    private String name;
    
    /** The list of argument expressions passed to the routine */
    private List<Expression> arguments;

    /**
     * Creates a new routine call expression.
     * 
     * @param name The name of the routine to call
     * @param arguments The list of argument expressions to pass to the routine
     */
    public RoutineCall(String name, List<Expression> arguments) {
        this.name = name;
        this.arguments = arguments;
    }

    /**
     * Gets the name of the routine being called.
     * 
     * @return The routine name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the list of argument expressions.
     * 
     * @return The list of arguments passed to this routine call
     */
    public List<Expression> getArguments() {
        return arguments;
    }

    /**
     * Evaluates this routine call expression.
     * 
     * @return The result of executing the routine with the given arguments
     * @throws UnsupportedOperationException when evaluation is not yet implemented
     */
    @Override
    public Object evaluate() {
        // TODO: Implement routine call evaluation
        throw new UnsupportedOperationException("Routine call evaluation not implemented yet");
    }

    /**
     * Returns a string representation of this routine call in a tree format.
     * 
     * @return A formatted string showing the routine call structure
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Routine Call\n");
        sb.append("├── Name: ").append(name).append("\n");
        if (arguments.isEmpty()) {
            sb.append("└── Arguments: <none>\n");
        } else {
            sb.append("└── Arguments:\n");
            for (int i = 0; i < arguments.size(); i++) {
                Expression arg = arguments.get(i);
                String argStr = arg.toString().replace("\n", "\n    ");
                if (i == arguments.size() - 1) {
                    sb.append("    └── ").append(argStr).append("\n");
                } else {
                    sb.append("    ├── ").append(argStr).append("\n");
                }
            }
        }
        return sb.toString();
    }
}
