import java.io.*;
import java.util.*;
import java.util.stream.*;

import lexer.Lexer;
import lexer.sources.ArraySource;
import lexer.sources.Source;
import lexer.tokens.Token;

public class Main {
    public static String readFile(String filePath) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File(filePath)));
        String text = br.lines().collect(Collectors.joining("\n"));
        br.close();
        return text;
    }

    public static void writeFile(String filePath, String contents) throws IOException {
        FileWriter writer = new FileWriter(filePath);
        writer.write(contents);
        writer.close();
    }

    public static void main(String[] args) throws IOException {
        var filePath = "./src/Test.ascetic";
        String text = readFile(filePath);

        Source source = ArraySource.fromString(text);
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenizeAll();
        System.out.println(tokens);
    }
    
}
