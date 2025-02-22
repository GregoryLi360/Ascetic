package lexer.tokens;

/* Find definitions in grammar.md */
public enum TokenType {
    END, INVALID, COMMENT,
    LET, 
    RETURN,
    IF, ELSE, GUARD,
    IDENTIFIER, 
    PRIMITIVE_TYPE,
    ASSIGNMENT, 
    BOOLEAN_LITERAL, INTEGER_LITERAL, FLOAT_LITERAL, STRING_LITERAL,
    OPEN_PAREN, CLOSED_PAREN, OPEN_CURLY, CLOSED_CURLY, OPEN_BRACKET, CLOSED_BRACKET, 
    COLON, SEMICOLON, PERIOD, COMMA, BANG, ARROW,
    PLUS, MINUS, MULTIPLY, DIVIDE, MODULO,
    AND, OR,
    QUESTION_MARK, OPTIONAL_MEMBER, COALESCE,
    EQUALS_TO, NOT_EQUALS_TO, LESS_THAN, LESS_THAN_OR_EQUALS_TO, GREATER_THAN, GREATER_THAN_OR_EQUALS_TO
}