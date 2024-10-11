package lexer;

import java.util.HashMap;

import lexer.tokens.TokenType;

public final class Constants {
    static final String[] primitiveTypes = new String[] {
        "Bool", "Int", "Float", "String", "Void",
    };
    
    static final HashMap<String, TokenType> keywords = new HashMap<>() {{
        put("let", TokenType.LET);
        put("return", TokenType.RETURN);
        put("if", TokenType.IF);
        put("else", TokenType.ELSE);
        put("guard", TokenType.GUARD);
        put("true", TokenType.BOOLEAN_LITERAL);
        put("false", TokenType.BOOLEAN_LITERAL);

        for (String type: primitiveTypes) {
            put(type, TokenType.PRIMITIVE_TYPE);
        }
    }};
}