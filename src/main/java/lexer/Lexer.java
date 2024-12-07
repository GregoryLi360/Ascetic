package lexer;

import java.util.ArrayList;
import java.util.List;

import lexer.sources.Source;
import lexer.tokens.Token;
import lexer.tokens.TokenType;

public class Lexer {
    private Source source;
    
    public Lexer(Source source) {
        this.source = source;
    }

    public List<Token> tokenizeAll() {
        source.restart();
        return new ArrayList<Token>() {{
            for (Token token = next();; token = next()) {
                add(token);
                if (token.is(TokenType.END)) break;
            }
        }};
    }

    public static int trimLeft(char[] str, int cursor) {
        int len = str.length;
        for(; cursor < len && Character.isWhitespace(str[cursor]); cursor++) {}
        return cursor;
    }

    public static boolean isIdentifierOrKeywordStart(char c) {
        return Character.isLetter(c) || c == '_';
    }

    public static boolean isIdentifierOrKeywordContinuation(char c) {
        return Character.isLetterOrDigit(c) || c == '_';
    }

    public static boolean isNumberStart(char currChar, char peekedChar) {
        return Character.isDigit(currChar) || currChar == '.' && Character.isDigit(peekedChar);
    }

    public boolean isEOF() {
        return source.isEOF();
    }

    public char peek() {
        return source.peek();
    }

    public char peek(long n) {
        return source.peek(n);
    }

    public char consume() {
        return source.consume();
    }

    public Token next() {
        Token token = new Token();
        token.type = TokenType.END;
        token.text = new char[] {};
        token.start = source.skipWhitespace();
        token.end = token.start;
        
        if (isEOF()) return token;

        token.type = TokenType.INVALID;
        char currChar = consume();

        if (isIdentifierOrKeywordStart(currChar)) { tokenizeIdentifierOrKeyword(currChar, token, token.start); } 
        else if (isNumberStart(currChar, peek())) { tokenizeNumber(currChar, token); } 
        else { tokenizeOther(currChar, token); }

        token.end = source.getIndex();
        token.text = source.copyOfRange(token.start, token.end).getChars();

        return token;
    }

    /**
     * Tokenizes numbers as defined in grammar.md
     * @param currChar the last consumed character
     * @param token inout, only token.type is altered
     * @param cursorStart the index of the last consumed character
     * @return modified token
     */
    private void tokenizeIdentifierOrKeyword(char currChar, Token token, Token.Location cursorStart) {
        while (!isEOF() && isIdentifierOrKeywordContinuation(peek())) { consume(); }
        char[] tokenChars = source.copyFrom(cursorStart).getChars();
        token.type = Constants.keywords.getOrDefault(String.valueOf(tokenChars), TokenType.IDENTIFIER);
    }

    /**
     * Tokenizes numbers as defined in grammar.md
     * @param currChar the last consumed character
     * @param token inout, only token.type is altered
     * @return modified token
     */
    private void tokenizeNumber(char currChar, Token token) {
        boolean encounteredPeriod = currChar == '.';
        token.type = encounteredPeriod ? TokenType.FLOAT_LITERAL : TokenType.INTEGER_LITERAL;

        while (!isEOF()) {
            boolean isFirstPeriod = !encounteredPeriod && peek() == '.';
            if (!Character.isDigit(peek()) && !isFirstPeriod) { return; }
            
            if (isFirstPeriod) {
                if (!Character.isDigit(peek(2))) { return; }

                encounteredPeriod = true;
                token.type = TokenType.FLOAT_LITERAL;
                currChar = consume();
            }

            currChar = consume();
        }
    }

    /**
     * Tokenizes predefined symbols that are not keywords, identifiers, or numbers
     * @param currChar the last consumed character
     * @param token inout, only token.type is altered if any symbol is matched
     * @return modified token
     */
    private void tokenizeOther(char currChar, Token token) {
        switch (currChar) {
            case ',': token.type = TokenType.COMMA; break;
            case '.': token.type = TokenType.PERIOD; break;
            case ';': token.type = TokenType.SEMICOLON; break;
            case ':': token.type = TokenType.COLON; break;

            case '/':
                token.type = TokenType.COMMENT;
                if (peek() == '/') {
                    while (!isEOF() && peek() != '\n') { consume(); }
                    break;
                } else if (peek() == '*') {
                    consume();
                    while (!isEOF() && !(currChar == '*' && peek() == '/')) { currChar = consume(); }
                    if (isEOF()) { token.type = TokenType.INVALID; break; }
                    consume();
                    break;
                }

                token.type = TokenType.DIVIDE;
            break;

            case '-':
                token.type = TokenType.MINUS;
                if (peek() == '>') { consume(); token.type = TokenType.ARROW; }
            break;

            case '+':
                token.type = TokenType.PLUS;
            break;

            case '*':
                token.type = TokenType.MULTIPLY;
            break;

            case '%':
                token.type = TokenType.MODULO;
            break;

            case '=': 
                token.type = TokenType.ASSIGNMENT;
                if (peek() == '=') { consume(); token.type = TokenType.EQUALS_TO; }
            break;
            
            case '<':
                token.type = TokenType.LESS_THAN;
                if (peek() == '=') { consume(); token.type = TokenType.LESS_THAN_OR_EQUALS_TO; }
            break;

            case '>':
                token.type = TokenType.GREATER_THAN;
                if (peek() == '=') { consume(); token.type = TokenType.GREATER_THAN_OR_EQUALS_TO; }
            break;

            case '!':
                token.type = TokenType.BANG;
                if (peek() == '=') { consume(); token.type = TokenType.NOT_EQUALS_TO; }
            break;

            case '&':
                if (peek() == '&') { consume(); token.type = TokenType.AND; }
            break;

            case '|':
                if (peek() == '|') { consume(); token.type = TokenType.OR; }
            break;

            case '?':
                token.type = TokenType.QUESTION_MARK;
                if (peek() == '?') { consume(); token.type = TokenType.COALESCE; }
                else if (peek() == '.') { consume(); token.type = TokenType.OPTIONAL_MEMBER; }
            break;

            case '"':
                while (!isEOF()) {
                    char nextChar = consume();
                    if (nextChar == '\\') { consume(); } 
                    else if (nextChar == '"') {
                        token.type = TokenType.STRING_LITERAL;
                        break;
                    }
                }
            break;

            case '(': token.type = TokenType.OPEN_PAREN; break;
            case ')': token.type = TokenType.CLOSED_PAREN; break;

            case '{': token.type = TokenType.OPEN_CURLY; break;
            case '}': token.type = TokenType.CLOSED_CURLY; break;

            case '[': token.type = TokenType.OPEN_BRACKET; break;
            case ']': token.type = TokenType.CLOSED_BRACKET; break;
        }
    }
}