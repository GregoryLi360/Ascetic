import java.io.*;
import java.util.*;
import java.util.stream.*;

import com.google.gson.GsonBuilder;

import errors.parser.UnexpectedTokenException;
import lexer.Lexer;
import lexer.sources.ArraySource;
import lexer.sources.Source;
import lexer.tokens.Token;
import parser.Parser;

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

    public static String toJson(Object obj) {
        return new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().serializeNulls().create().toJson(obj);
    }

    public static void main(String[] args) throws IOException, UnexpectedTokenException {
        var filePath = "./src/Test.ascetic";
        String text = readFile(filePath);

        Source source = ArraySource.fromString(text);
        Lexer lexer = new Lexer(source);
        List<Token> tokens = lexer.tokenizeAll();
        System.out.println(tokens);
        
        Parser parser = new Parser(tokens);
        var AST = parser.build();
        writeFile("./AST.json", toJson(AST));
    }
}