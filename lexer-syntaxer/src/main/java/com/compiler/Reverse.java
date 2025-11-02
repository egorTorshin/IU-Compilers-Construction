package com.compiler;

/**
 * Represents a reverse operation flag in the compiler.
 * This class is used to indicate whether an operation should be performed in reverse order.
 */
public class Reverse {
    /** Flag indicating if the operation should be reversed */
    private boolean isReverse;

    /**
     * Constructs a new Reverse object with the specified reverse flag.
     * @param isReverse true if the operation should be reversed, false otherwise
     */
    public Reverse(boolean isReverse) {
        this.isReverse = isReverse;
    }

    /**
     * Checks if this operation should be performed in reverse.
     * @return true if the operation should be reversed, false otherwise
     */
    public boolean isReverse() {
        return isReverse;
    }

    /**
     * Returns a string representation of this reverse operation in a tree format.
     * @return A formatted string showing the reverse operation in tree structure
     */
    @Override
    public String toString() {
        return String.format("Reverse%n└── isReverse: %b", isReverse);
    }
}
