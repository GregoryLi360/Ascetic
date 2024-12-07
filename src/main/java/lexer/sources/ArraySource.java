package lexer.sources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

import lexer.tokens.Token;

public class ArraySource extends Source {
    private char[] source;
    private long cursor = 0;
    private long line = 0;
    private long column = 0;

    private ArraySource(char[] chars) {
        this.source = chars;
    }

    public static ArraySource fromString(String str) {
        return new ArraySource(str.toCharArray());
    }

    public static ArraySource fromCharArray(char[] chars) {
        return new ArraySource(chars);
    }

    public static ArraySource fromFile(File file) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        String text = br.lines().collect(Collectors.joining("\n"));
        br.close();
        return new ArraySource(text.toCharArray());
    }

    @Override
    public void restart() {
        cursor = 0;
    }
    
    @Override
    public Token.Location getIndex() {
        return new Token.Location(line, column, cursor);
    }

    @Override
    public boolean isEOF() {
        return cursor >= source.length;
    }

    @Override
    public char peek() {
        if (isEOF()) { return '\0'; }
        return source[Math.toIntExact(cursor)];
    }

    public char peek(long n) {
        if (cursor + n - 1 >= source.length) { return '\0'; }
        return source[Math.toIntExact(cursor + n - 1)];
    }

    @Override
    public char consume() {
        int position = Math.toIntExact(cursor);
        cursor++;
        column++;
        return source[position];
    }

    @Override
    public Token.Location skipWhitespace() {
        for (; cursor < source.length; cursor++) {
            char currentChar = source[Math.toIntExact(cursor)];
            if (!Character.isWhitespace(currentChar)) { break; }

            boolean isNewLine = (currentChar == '\r' || currentChar == '\n');
            if (isNewLine) {
                line++;
                column = 0;
            } else {
                column++;
            }
        }
        return new Token.Location(line, column, cursor);
    }

    @Override
    public Source copyOfRange(Token.Location start, Token.Location end) {
        return ArraySource.fromCharArray(Arrays.copyOfRange(source, Math.toIntExact(start.offset), Math.toIntExact(end.offset)));
    }

    @Override
    public Source copyFrom(Token.Location start) {
        return ArraySource.fromCharArray(Arrays.copyOfRange(source, Math.toIntExact(start.offset), Math.toIntExact(cursor)));
    }

    @Override
    public String getText() {
        return String.valueOf(source);
    }

    @Override
    public char[] getChars() {
        return source;
    }
}