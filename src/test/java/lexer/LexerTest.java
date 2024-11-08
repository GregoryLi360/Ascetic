package lexer;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import lexer.sources.*;
import lexer.tokens.*;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

public class LexerTest {
    @Nested 
    class Keywords {
        @Test
        public void testKeywords() {
            Constants.keywords.entrySet().forEach((entry) -> {
                String keyword = entry.getKey();
                Source source = ArraySource.fromString(keyword);
                Token token = new Lexer(source).next();

                TokenType expectedType = entry.getValue();
                assertEquals(expectedType, token.type);
                assertEquals(0, token.start);
                assertEquals(keyword.length(), token.end);
                assertArrayEquals(keyword.toCharArray(), token.text);
            });
        }
    }

    @Nested
    class Comments {
        @Test
        public void testSingleLineComment() {
            String comment = "// Single-line comment";
            Source source = ArraySource.fromString(comment);
            Token token = new Lexer(source).next();

            TokenType expectedType = TokenType.COMMENT;
            assertEquals(expectedType, token.type);
            assertEquals(0, token.start);
            assertEquals(comment.length(), token.end);
            assertArrayEquals(comment.toCharArray(), token.text);
        }

        @Test
        public void testTerminatedSingleLineComment() {
            String comment = "// Single-line comment\n";
            Source source = ArraySource.fromString(comment);
            Token token = new Lexer(source).next();

            TokenType expectedType = TokenType.COMMENT;
            assertEquals(expectedType, token.type);
            assertEquals(0, token.start);
            assertEquals(comment.length() - 1, token.end);
            assertArrayEquals(comment.substring(0, comment.length() - 1).toCharArray(), token.text);
        }

        @Test
        public void testMultiLineComment() {
            String comment = """
                /* 
                    Multi-line comment 
                    that can span 
                    several lines
                */
            """.trim();
            Source source = ArraySource.fromString(comment);
            Token token = new Lexer(source).next();

            TokenType expectedType = TokenType.COMMENT;
            assertEquals(expectedType, token.type);
            assertEquals(0, token.start);
            assertEquals(comment.length(), token.end);
            assertArrayEquals(comment.toCharArray(), token.text);
        }
        
        @Test
        public void testUnterminatedMultiLineComment() {
            String comment = """
            /* 
                Multi-line comment 
                that can span 
                several lines
            """.trim();
            Source source = ArraySource.fromString(comment);
            Token token = new Lexer(source).next();

            TokenType expectedType = TokenType.INVALID;
            assertEquals(expectedType, token.type);
        }
    }

    @Nested
    class Strings {
        @Test
        public void testStrings() {
            String string = "\"Hello world\"";
            Source source = ArraySource.fromString(string);
            Token token = new Lexer(source).next();

            TokenType expectedType = TokenType.STRING_LITERAL;
            assertEquals(expectedType, token.type);
            assertEquals(0, token.start);
            assertEquals(string.length(), token.end);
            assertArrayEquals(string.toCharArray(), token.text);
        }

        @Test
        @Disabled("String behavior is not finalized")
        public void testEscapedCharactersStrings() {

        }

        @Test
        @Disabled("String behavior is not finalized")
        public void testMultiLineStrings() {

        }
    }

    @Nested 
    class Identifiers {
        @Test
        public void testIdentifiers() {
            List<String> validIdentifiers = List.of(
                "variable",
                "x",
                "count",
                "VariableName",
                "myVar123",
                "TestCase",
                "my_variable",
                "variable_name_1",
                "_privateVariable",
                "a",
                "A",
                "z",
                "_start",
                "_tempVar",
                "var1",
                "counter42",
                "number123",
                "thisIsAVeryLongIdentifierNameThatExceedsTypicalLength",
                "variable_with_multiple_descriptive_words",
                "变量", 
                "vérité"
            );

            TokenType expectedType = TokenType.IDENTIFIER;

            validIdentifiers.forEach(identifier -> {
                Source source = ArraySource.fromString(identifier);
                Token token = new Lexer(source).next();

                assertEquals(expectedType, token.type);
                assertEquals(0, token.start);
                assertEquals(identifier.length(), token.end);
                assertArrayEquals(identifier.toCharArray(), token.text); 
            });
        }

    }

    @Nested 
    class Symbols {
        @Test
        public void testAssignment() {
            String symbols = "=";
            Source source = ArraySource.fromString(symbols);
            Lexer lexer = new Lexer(source);

            assertEquals(TokenType.ASSIGNMENT, lexer.next().type);
        }

        @Test
        public void testOperations() {
            String symbols = "+ - * / % && ||";
            Source source = ArraySource.fromString(symbols);
            Lexer lexer = new Lexer(source);

            assertEquals(TokenType.PLUS, lexer.next().type);
            assertEquals(TokenType.MINUS, lexer.next().type);
            assertEquals(TokenType.MULTIPLY, lexer.next().type);
            assertEquals(TokenType.DIVIDE, lexer.next().type);
            assertEquals(TokenType.MODULO, lexer.next().type);
            assertEquals(TokenType.AND, lexer.next().type);
            assertEquals(TokenType.OR, lexer.next().type);
        }

        @Test
        public void testComparisons() {
            String symbols = "< <= == >= >";
            Source source = ArraySource.fromString(symbols);
            Lexer lexer = new Lexer(source);

            assertEquals(TokenType.LESS_THAN, lexer.next().type);
            assertEquals(TokenType.LESS_THAN_OR_EQUALS_TO, lexer.next().type);
            assertEquals(TokenType.EQUALS_TO, lexer.next().type);
            assertEquals(TokenType.GREATER_THAN_OR_EQUALS_TO, lexer.next().type);
            assertEquals(TokenType.GREATER_THAN, lexer.next().type);
        }

        @Test
        public void testBrackets() {
            String symbols = "(){}[]";
            Source source = ArraySource.fromString(symbols);
            Lexer lexer = new Lexer(source); 

            assertEquals(TokenType.OPEN_PAREN, lexer.next().type);
            assertEquals(TokenType.CLOSED_PAREN, lexer.next().type);
            assertEquals(TokenType.OPEN_CURLY, lexer.next().type);
            assertEquals(TokenType.CLOSED_CURLY, lexer.next().type);
            assertEquals(TokenType.OPEN_BRACKET, lexer.next().type);
            assertEquals(TokenType.CLOSED_BRACKET, lexer.next().type);
        }

        @Test 
        public void testMiscellaneous() {
            String symbols = ": ; . , ! ->";
            Source source = ArraySource.fromString(symbols);
            Lexer lexer = new Lexer(source); 
            
            assertEquals(TokenType.COLON, lexer.next().type);
            assertEquals(TokenType.SEMICOLON, lexer.next().type);
            assertEquals(TokenType.PERIOD, lexer.next().type);
            assertEquals(TokenType.COMMA, lexer.next().type);
            assertEquals(TokenType.BANG, lexer.next().type);
            assertEquals(TokenType.ARROW,lexer.next().type);
        }
    }

    @Nested
    class Numbers {
        @Test
        public void testInts() {
            String ints = "123 456 789";
            Source source = ArraySource.fromString(ints);
            List<Token> tokens = new Lexer(source).tokenizeAll();

            TokenType expectedType = TokenType.INTEGER_LITERAL;
            assertEquals(4, tokens.size());
            tokens.forEach(token -> {
                if (token.type != TokenType.END) {
                    assertEquals(expectedType, token.type);
                }
            });
        }

        @Test
        public void testFloats() {
            String ints = "0.123 .456 7.89";
            Source source = ArraySource.fromString(ints);
            List<Token> tokens = new Lexer(source).tokenizeAll();

            TokenType expectedType = TokenType.FLOAT_LITERAL;
            assertEquals(4, tokens.size());
            tokens.forEach(token -> {
                if (token.type != TokenType.END) {
                    assertEquals(expectedType, token.type);
                }
            });
        }
    }
}