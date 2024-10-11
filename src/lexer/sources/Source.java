package lexer.sources;

public abstract class Source {
    /** restart the source by setting the index to 0 */
    public abstract void restart();

    /** @return current index in the source */
    public abstract long getIndex();

    /** @return if the source has no more characters */
    public abstract boolean isEOF();

    /** @return the next character without incrementing the index */
    public abstract char peek();

    /** @return the next character while incrementing the index */
    public abstract char consume();

    /** @return the character after decrementing the index */
    public abstract char undo();
    
    /** @return index of next non-whitespace character */
    public abstract long skipWhitespace();

    /**
     * @param start the initial index of the range to be copied, inclusive
     * @param end the final index of the range to be copied, exclusive
     * @return a new {@link Source} object with the specified range of the current source
     */
    public abstract Source copyOfRange(long start, long end);

    /**
     * @param start the initial index of the range to be copied, inclusive
     * @param end the final index of the range to be copied, exclusive
     * @return a new {@link Source} object with the span of the current source from start to current index
     */
    public abstract Source copyFrom(long start);

    /** @return String representation of source text */
    public abstract String getText();

    /** @return char[] representation of source text */
    public abstract char[] getChars();

    @Override
    public String toString() {
        return this.getClass().getName() + " [\n" + this.getText() + "\n]";
    }
}
