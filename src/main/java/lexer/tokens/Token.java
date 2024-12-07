package lexer.tokens;

public final class Token {
    public TokenType type;
    public char[] text;
    public Location start, end;

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

    public static class Location {
        public long line;
        public long column;
        public long offset;

        public Location(long offset) {
            this.line = 0;
            this.column = offset;
            this.offset = offset;
        }

        public Location(long line, long column, long offset) {
            this.line = line;
            this.column = column;
            this.offset = offset;
        }

        public String toString() {
            return "[" + line + ", " + column + "; " + offset + "]";
        }
    }
}