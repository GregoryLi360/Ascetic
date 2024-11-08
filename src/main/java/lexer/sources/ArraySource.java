package lexer.sources;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ArraySource extends Source {
    private char[] source;
    private long cursor = 0;

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
    public long getIndex() {
        return cursor;
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

    @Override
    public char consume() {
        int position = Math.toIntExact(cursor);
        cursor++;
        return source[position];
    }

    @Override
    public long skipWhitespace() {
        for(; cursor < source.length && Character.isWhitespace(source[Math.toIntExact(cursor)]); cursor++);
        return cursor;
    }

    @Override
    public Source copyOfRange(long start, long end) {
        return ArraySource.fromCharArray(Arrays.copyOfRange(source, Math.toIntExact(start), Math.toIntExact(end)));
    }

    @Override
    public Source copyFrom(long start) {
        return ArraySource.fromCharArray(Arrays.copyOfRange(source, Math.toIntExact(start), Math.toIntExact(cursor)));
    }

    @Override
    public String getText() {
        return String.valueOf(source);
    }

    @Override
    public char[] getChars() {
        return source;
    }

    @Override
    public char undo() {
        cursor--;
        return source[Math.toIntExact(cursor)];
    }
}