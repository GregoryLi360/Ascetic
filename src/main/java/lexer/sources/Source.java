package lexer.sources;

import lexer.tokens.Token;

public abstract class Source {
    /** restart the source by setting the index to 0 */
    public abstract void restart();

    /** @return current location in the source */
    public abstract Token.Location getIndex();

    /** @return if the source has no more characters */
    public abstract boolean isEOF();

    /** @return the next character without incrementing the index */
    public abstract char peek();

    /** 
     * note: peek(1) == peek()
     * @param n the number of characters to look ahead of
     * @return the nth character after the current without incrementing the index 
     */
    public abstract char peek(long n);

    /** @return the next character while incrementing the index */
    public abstract char consume();

    /** @return location of next non-whitespace character */
    public abstract Token.Location skipWhitespace();

    /**
     * @param start the initial location of the range to be copied, inclusive
     * @param end the final location of the range to be copied, exclusive
     * @return a new {@link Source} object with the specified range of the current source
     */
    public abstract Source copyOfRange(Token.Location start, Token.Location end);

    /**
     * @param start the initial location of the range to be copied, inclusive
     * @return a new {@link Source} object with the span of the current source from start to current index
     */
    public abstract Source copyFrom(Token.Location start);

    /** @return String representation of source text */
    public abstract String getText();

    /** @return char[] representation of source text */
    public abstract char[] getChars();

    @Override
    public String toString() {
        return this.getClass().getName() + " [\n" + this.getText() + "\n]";
    }
}