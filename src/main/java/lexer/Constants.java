package lexer;

import java.util.HashMap;

import lexer.tokens.TokenType;

public final class Constants {
    static final String[] primitiveTypes = new String[] {
        "Bool", "Int", "Float", "String", "Void", // "Any", "Type"
    };
    
    public static final HashMap<String, TokenType> keywords = new HashMap<>() {{
        put("let", TokenType.LET);
        // put("var", TokenType.VARIABLE_DECLARATOR);
        // put("struct", TokenType.STRUCT);
        // put("enum", TokenType.ENUM);
        // put("static", TokenType.VARIABLE_MODIFIER);
        // put("operator", TokenType.VARIABLE_MODIFIER);
        // put("mutating", TokenType.VARIABLE_MODIFIER);
        put("return", TokenType.RETURN);
        put("if", TokenType.IF);
        put("else", TokenType.ELSE);
        put("guard", TokenType.GUARD);
        put("true", TokenType.BOOLEAN_LITERAL);
        put("false", TokenType.BOOLEAN_LITERAL);
        // put("as", TokenType.CAST);

        for (String type: primitiveTypes) {
            put(type, TokenType.PRIMITIVE_TYPE);
        }
    }};

//     static final String[] c_keywords = new String[] {
//         "auto", "break", "case", "char", "const", "continue", "default", "do", "double",
//         "else", "enum", "extern", "float", "for", "goto", "if", "int", "long", "register",
//         "return", "short", "signed", "sizeof", "static", "struct", "switch", "typedef",
//         "union", "unsigned", "void", "volatile", "while", "alignas", "alignof", "and",
//         "and_eq", "asm", "atomic_cancel", "atomic_commit", "atomic_noexcept", "bitand",
//         "bitor", "bool", "catch", "char16_t", "char32_t", "char8_t", "class", "co_await",
//         "co_return", "co_yield", "compl", "concept", "const_cast", "consteval", "constexpr",
//         "constinit", "decltype", "delete", "dynamic_cast", "explicit", "export", "false",
//         "friend", "inline", "mutable", "namespace", "new", "noexcept", "not", "not_eq",
//         "nullptr", "operator", "or", "or_eq", "private", "protected", "public", "reflexpr",
//         "reinterpret_cast", "requires", "static_assert", "static_cast", "synchronized",
//         "template", "this", "thread_local", "throw", "true", "try", "typeid", "typename",
//         "using", "virtual", "wchar_t", "xor", "xor_eq", "let",
//     };
}
