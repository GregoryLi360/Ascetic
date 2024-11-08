package lexer.tokens;

public final class Token {
    public TokenType type;
    public char[] text;
    public long start, end;

    public String toString() {
        return "Token { " + type + ", '" + String.valueOf(text) + 
            "', [" + start + ", " + end + "] }";
    }

    public boolean is(TokenType... tokenTypes) {
        for (TokenType otherType: tokenTypes) { 
            if (this.type == otherType) { return true; }
        }

        return false;
    }
}