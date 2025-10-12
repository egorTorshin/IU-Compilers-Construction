import com.compiler.*;
import java_cup.runtime.ComplexSymbolFactory;
import java.io.FileReader;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class TestAll {
    private static List<String> failedFiles = new ArrayList<>();
    
    public static void main(String[] args) {
        // Test all example files
        testAllExamples();
    }
    
    private static void testAllExamples() {
        String examplesDir = "examples";
        File dir = new File(examplesDir);
        
        
        File[] files = dir.listFiles((d, name) -> name.endsWith(".txt"));
        
        for (File file : files) {
            testFile(file.getName());
        }
    }
    
    private static void testFile(String filename) {
        String filePath = "examples/" + filename;
        
        System.out.println("Testing: " + filename);
        System.out.println("------------------------------------------------------------");
        
        try {
            ComplexSymbolFactory symbolFactory = new ComplexSymbolFactory();
            
            // Test lexer
            System.out.print("Lexer: ");
            Lexer lexer = new Lexer(new FileReader(filePath), symbolFactory);
            System.out.println("PASS");
            
            // Print lexer tokens
            System.out.println("Lexer Tokens:");
            Lexer tokenLexer = new Lexer(new FileReader(filePath), symbolFactory);
            java_cup.runtime.Symbol token;
            int tokenCount = 0;
            while ((token = tokenLexer.next_token()) != null && token.sym != 0) { // 0 is EOF
                tokenCount++;
                String tokenValue = token.value != null ? token.value.toString() : getTokenName(token.sym);
                System.out.println("  Token " + tokenCount + ": " + token.sym + " = '" + tokenValue + "'");
            }
            System.out.println("  Total tokens: " + tokenCount);
            
            // Test parser
            System.out.print("Parser: ");
            Lexer parserLexer = new Lexer(new FileReader(filePath), symbolFactory);
            ImperativeLangParser parser = new ImperativeLangParser(parserLexer, symbolFactory);
            
            java_cup.runtime.Symbol parseTree = parser.parse();
            Program program = (Program) parseTree.value;
            
            System.out.println("PASS");
            
            // Show AST structure (simplified)
            // Count statements
            int statementCount = program.getStatements().size();
            System.out.println("Statements parsed: " + statementCount);
            
            // Show lines of AST for context
            String astString = program.toString();
            String[] lines = astString.split("\n");
            System.out.println("AST Preview:");
            for (int i = 0; i < lines.length; i++) {
                System.out.println("      " + lines[i]);
            }

            System.out.println("result: success");
            
        } catch (Exception e) {
            System.out.println("fail");
            System.out.println("error: " + e.getMessage());
            failedFiles.add(filename);
            System.out.println("result: failed");
        }
        
        System.out.println();
    }
    
    private static String getTokenName(int tokenSym) {
        if (tokenSym >= 0 && tokenSym < sym.terminalNames.length) {
            return sym.terminalNames[tokenSym];
        }
        return "UNKNOWN(" + tokenSym + ")";
    }
    
}
