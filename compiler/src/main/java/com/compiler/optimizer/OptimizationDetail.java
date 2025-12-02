package com.compiler.optimizer;

/**
 * Stores details about a single optimization transformation.
 * Used for visualization to show before/after of each optimization.
 */
public class OptimizationDetail {
    private String type;        // Type of optimization (e.g., "Constant Folding")
    private String description; // Description of what was optimized
    private String before;      // Code before optimization
    private String after;       // Code after optimization
    private int lineNumber;     // Line number where optimization occurred
    
    public OptimizationDetail(String type, String description, String before, String after, int lineNumber) {
        this.type = type;
        this.description = description;
        this.before = before;
        this.after = after;
        this.lineNumber = lineNumber;
    }
    
    public OptimizationDetail(String type, String description, String before, String after) {
        this(type, description, before, after, -1);
    }
    
    public String getType() {
        return type;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getBefore() {
        return before;
    }
    
    public String getAfter() {
        return after;
    }
    
    public int getLineNumber() {
        return lineNumber;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("[").append(type).append("] ").append(description);
        if (lineNumber >= 0) {
            sb.append(" (line ").append(lineNumber).append(")");
        }
        sb.append("\n  Before: ").append(before);
        sb.append("\n  After:  ").append(after);
        return sb.toString();
    }
}

