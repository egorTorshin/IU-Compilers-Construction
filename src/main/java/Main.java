public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.exit(1);
        }
        testFile(args[0]);
    }
    
    public static void testFile(String filePath) {
        try {
            String content = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(filePath)));
            System.out.println("\n=== Testing file: " + filePath + " ===");
            
            Scanner scanner = new Scanner(content);
            Token token;
            int tokenCount = 0;
            
            do {
                token = scanner.nextToken();
                tokenCount++;
                System.out.println("  " + tokenCount + ". " + token.type + 
                                 (token.value != null ? " (" + token.value + ")" : "") + 
                                 " at line " + token.line + ", col " + token.column);
                                
            } while (token.type != TokenCode.tk_EOF);
            
            int errorCount = scanner.getErrorCount();
            if (errorCount > 0) {
                System.out.println("\n=== LEXICAL ANALYSIS COMPLETE ===");
                System.out.println("Total errors found: " + errorCount);
                if (errorCount == 1) {
                    System.out.println("Fix this error and re-run the lexer.");
                } else {
                    System.out.println("Fix all errors and re-run the lexer.");
                }
            } else {
                System.out.println("\n=== LEXICAL ANALYSIS COMPLETE ===");
                System.out.println("No lexical errors found");
            }
            
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
    

    public static class Token {
        public TokenCode type;
        public String value;
        public int line;
        public int column;
        
        public Token(TokenCode type, String value, int line, int column) {
            this.type = type;
            this.value = value;
            this.line = line;
            this.column = column;
        }
        
        @Override
        public String toString() {
            return type + (value != null ? "(" + value + ")" : "");
        }
    }

    public enum TokenCode {
        tk_INTEGER,
        tk_REAL,
        tk_BOOLEAN,
        tk_STRING,
        tk_VAR,
        tk_PRINT,
        tk_IS,
        tk_TYPE,
        tk_END,
        tk_FOR,
        tk_DOT_DOT,
        tk_WHILE,
        tk_LOOP,
        tk_IN,
        tk_THEN,
        tk_ARRAY,
        tk_COLON,
        tk_COLON_EQUAL,
        tk_LEFT_SQUARE_BRACKET,
        tk_RIGHT_SQUARE_BRACKET,
        tk_LEFT_PARENTHESIS,
        tk_RIGHT_PARENTHESIS,
        tk_RECORD,
        tk_ADD,
        tk_SUBTRACT,
        tk_DIVIDE,
        tk_MULTIPLY,
        tk_LESS,
        tk_GREATER,
        tk_EQUAL,
        tk_NOT_EQUAL,
        tk_OR,
        tk_AND,
        tk_ROUTINE,
        tk_RETURN,
        tk_IF,
        tk_ELSE,
        tk_DOT,
        tk_COMMA,
        tk_IDENTIFIER,
        tk_INTEGER_LITERAL,
        tk_REAL_LITERAL,
        tk_BOOLEAN_LITERAL,
        tk_STRING_LITERAL,
        tk_REVERSE,
        tk_NOT,
        tk_LESS_EQUAL,
        tk_GREATER_EQUAL,
        tk_MODULO,
        tk_COMMENT,
        tk_ERROR,
        tk_EOF
    }

    public static class Scanner {
        private String input;
        private int position;
        private int line;
        private int column;
        private char lookahead;
        private String currentTokenValue;
        private int errorCount;
        
        public Scanner(String input) {
            this.input = input;
            this.position = 0;
            this.line = 1;
            this.column = 1;
            this.lookahead = input.length() > 0 ? input.charAt(0) : '\0';
            this.errorCount = 0;
        }
        
        public char get() {
            char ch = lookahead;
            
            if (position >= input.length()) {
                lookahead = '\0';
                return '\0';
            }
            
            position++;
            if (position < input.length()) {
                lookahead = input.charAt(position);
            } else {
                lookahead = '\0';
            }
            
            if (ch == '\n') {
                line++;
                column = 1;
            } else {
                column++;
            }
            
            return ch;
        }
        
        public char peek() {
            return lookahead;
        }
        
        private char peekNext() {
            int nextIndex = position + 1;
            if (nextIndex < input.length()) {
                return input.charAt(nextIndex);
            }
            return '\0';
        }
        
        public boolean isEOF() {
            return position >= input.length();
        }
        
        public int getErrorCount() {
            return errorCount;
        }
        
        private void reportError(String message, int line, int column) {
            errorCount++;
            System.err.println("ERROR at line " + line + ", col " + column + ": " + message);
        }
        
        
        public Token nextToken() {
            while (true) {
                if (isEOF()) {
                    return new Token(TokenCode.tk_EOF, null, line, column);
                }
                
                char ch = peek();
                if (ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n') {
                    get();
                    continue;
                }
                
                if (ch == '#') {
                    get();
                    while (peek() != '\n' && peek() != '\0') {
                        get();
                    }
                    continue;
                }
                
                break;
            }
            
            int tokenLine = line;
            int tokenColumn = column;
            
            TokenCode tokenType = recognizeToken();
            String tokenValue = getCurrentTokenValue();
            
            return new Token(tokenType, tokenValue, tokenLine, tokenColumn);
        }
        
        public int getLine() { return line; }
        public int getColumn() { return column; }
        
        private String getCurrentTokenValue() {
            return currentTokenValue;
        }
        
        public TokenCode recognizeToken() {
            char ch = get();
            
            switch (ch) {
                case '\0':
                    return TokenCode.tk_EOF;
                    
                case '+':
                    currentTokenValue = "+";
                    return TokenCode.tk_ADD;
                    
                case '-':
                    currentTokenValue = "-";
                    return TokenCode.tk_SUBTRACT;
                    
                case '*':
                    currentTokenValue = "*";
                    return TokenCode.tk_MULTIPLY;
                    
                case '/':
                    currentTokenValue = "/";
                    return TokenCode.tk_DIVIDE;
                    
                case '%':
                    currentTokenValue = "%";
                    return TokenCode.tk_MODULO;
                    
                case '=':
                    currentTokenValue = "=";
                    return TokenCode.tk_EQUAL;
                    
                case '<':
                    if (peek() == '=') {
                        get();
                        currentTokenValue = "<=";
                        return TokenCode.tk_LESS_EQUAL;
                    } else if (peek() == '>') {
                        get();
                        currentTokenValue = "<>";
                        return TokenCode.tk_NOT_EQUAL;
                    }
                    currentTokenValue = "<";
                    return TokenCode.tk_LESS;
                    
                case '>':
                    if (peek() == '=') {
                        get();
                        currentTokenValue = ">=";
                        return TokenCode.tk_GREATER_EQUAL;
                    }
                    currentTokenValue = ">";
                    return TokenCode.tk_GREATER;
                    
                case '!':
                    if (peek() == '=') {
                        get();
                        currentTokenValue = "!=";
                        return TokenCode.tk_NOT_EQUAL;
                    }
                    currentTokenValue = "!";
                    return TokenCode.tk_NOT;
                    
                case ':':
                    if (peek() == '=') {
                        get();
                        currentTokenValue = ":=";
                        return TokenCode.tk_COLON_EQUAL;
                    }
                    currentTokenValue = ":";
                    return TokenCode.tk_COLON;
                    
                case '.':
                    if (peek() == '.') {
                        get();
                        currentTokenValue = "..";
                        return TokenCode.tk_DOT_DOT;
                    }
                    currentTokenValue = ".";
                    return TokenCode.tk_DOT;
                    
                case '[':
                    currentTokenValue = "[";
                    return TokenCode.tk_LEFT_SQUARE_BRACKET;
                    
                case ']':
                    currentTokenValue = "]";
                    return TokenCode.tk_RIGHT_SQUARE_BRACKET;
                    
                case '(':
                    currentTokenValue = "(";
                    return TokenCode.tk_LEFT_PARENTHESIS;
                    
                case ')':
                    currentTokenValue = ")";
                    return TokenCode.tk_RIGHT_PARENTHESIS;
                    
                case ',':
                    currentTokenValue = ",";
                    return TokenCode.tk_COMMA;
                    
                case '"':
                    return handleStringLiteral();
                    
                case '\'':
                    return handleSingleQuotedString();
                    
                default:
                    if (Character.isDigit(ch)) {
                        return handleNumber(ch);
                    } else if (Character.isLetter(ch) || ch == '_') {
                        return handleIdentifier(ch);
                    } else {
                        currentTokenValue = String.valueOf(ch);
                        reportError("Unexpected character '" + ch + "'", line, column);
                        return TokenCode.tk_ERROR;
                    }
            }
        }
        
        private TokenCode handleStringLiteral() {
            StringBuilder sb = new StringBuilder();
            while (peek() != '"' && peek() != '\0' && peek() != '\n' && peek() != '#') {
                sb.append(get());
            }
            if (peek() == '"') {
                get();
                currentTokenValue = sb.toString();
                return TokenCode.tk_STRING_LITERAL;
            } else {
                reportError("Unterminated string literal", line, column);
                currentTokenValue = sb.toString();
                return TokenCode.tk_ERROR;
            }
        }
        
        private TokenCode handleSingleQuotedString() {
            StringBuilder sb = new StringBuilder();
            while (peek() != '\'' && peek() != '\0' && peek() != '\n' && peek() != '#') {
                sb.append(get());
            }
            if (peek() == '\'') {
                get();
                currentTokenValue = sb.toString();
                return TokenCode.tk_STRING_LITERAL;
            } else {
                reportError("Unterminated single-quoted string literal", line, column);
                currentTokenValue = sb.toString();
                return TokenCode.tk_ERROR;
            }
        }
        
        private TokenCode handleNumber(char firstDigit) {
            StringBuilder sb = new StringBuilder();
            sb.append(firstDigit);
            
            while (Character.isDigit(peek())) {
                sb.append(get());
            }
            
            if (peek() == '.') {
                sb.append(get());
                while (Character.isDigit(peek())) {
                    sb.append(get());
                }
                currentTokenValue = sb.toString();
                
                if (peek() == '.') {
                    reportError("Invalid real literal '" + sb.toString() + "' - multiple decimal points", line, column);
                    while (peek() != '\n' && peek() != '\0' && peek() != ' ' && peek() != '\t' && peek() != '\r') {
                        sb.append(get());
                    }
                    currentTokenValue = sb.toString();
                    return TokenCode.tk_ERROR;
                }
                return TokenCode.tk_REAL_LITERAL;
            }
            
            if (Character.isLetter(peek()) || peek() == '_') {
                StringBuilder invalidPart = new StringBuilder();
                while (Character.isLetterOrDigit(peek()) || peek() == '_') {
                    invalidPart.append(get());
                }
                String invalidNumber = sb.toString() + invalidPart.toString();
                reportError("Invalid number literal '" + invalidNumber + "' - numbers cannot contain letters", line, column);
                currentTokenValue = invalidNumber;
                return TokenCode.tk_ERROR;
            }
            
            currentTokenValue = sb.toString();
            return TokenCode.tk_INTEGER_LITERAL;
        }
        
        private TokenCode handleIdentifier(char firstChar) {
            StringBuilder sb = new StringBuilder();
            sb.append(firstChar);
            
            while (Character.isLetterOrDigit(peek()) || peek() == '_') {
                sb.append(get());
            }
            
            String identifier = sb.toString();
            currentTokenValue = identifier;

            // Detect illegal characters used as part of an identifier without whitespace
            // Example: name@var, total%sum
            if (peek() == '@') {
                StringBuilder invalid = new StringBuilder(identifier);
                invalid.append(get()); // consume '@'
                while (Character.isLetterOrDigit(peek()) || peek() == '_') {
                    invalid.append(get());
                }
                currentTokenValue = invalid.toString();
                reportError("Invalid identifier '" + currentTokenValue + "' - illegal character '@' in identifier", line, column);
                return TokenCode.tk_ERROR;
            }
            if (peek() == '%' && (Character.isLetterOrDigit(peekNext()) || peekNext() == '_')) {
                StringBuilder invalid = new StringBuilder(identifier);
                invalid.append(get()); // consume '%'
                while (Character.isLetterOrDigit(peek()) || peek() == '_') {
                    invalid.append(get());
                }
                currentTokenValue = invalid.toString();
                reportError("Invalid identifier '" + currentTokenValue + "' - illegal character '%' in identifier", line, column);
                return TokenCode.tk_ERROR;
            }
            
            switch (identifier) {
                case "var": return TokenCode.tk_VAR;
                case "type": return TokenCode.tk_TYPE;
                case "integer": return TokenCode.tk_INTEGER;
                case "real": return TokenCode.tk_REAL;
                case "boolean": return TokenCode.tk_BOOLEAN;
                case "string": return TokenCode.tk_STRING;
                case "is": return TokenCode.tk_IS;
                case "end": 
                    return TokenCode.tk_END;
                case "print": return TokenCode.tk_PRINT;
                case "for": return TokenCode.tk_FOR;
                case "while": return TokenCode.tk_WHILE;
                case "loop": return TokenCode.tk_LOOP;
                case "in": return TokenCode.tk_IN;
                case "if": return TokenCode.tk_IF;
                case "then": return TokenCode.tk_THEN;
                case "else": return TokenCode.tk_ELSE;
                case "array": return TokenCode.tk_ARRAY;
                case "record": return TokenCode.tk_RECORD;
                case "routine": return TokenCode.tk_ROUTINE;
                case "return": return TokenCode.tk_RETURN;
                case "reverse": return TokenCode.tk_REVERSE;
                case "and": return TokenCode.tk_AND;
                case "or": return TokenCode.tk_OR;
                case "not": return TokenCode.tk_NOT;
                case "true": return TokenCode.tk_BOOLEAN_LITERAL;
                case "false": return TokenCode.tk_BOOLEAN_LITERAL;
                default:
                    return TokenCode.tk_IDENTIFIER;
            }
        }
    }
    
}