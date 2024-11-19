package parser.partial_parsers.expressions.pratt;

/* 
 * Adopted from Swift, see:
 * https://www.programiz.com/swift-programming/operator-precedence-associativity
 */
public enum BindingPower {
    LOWEST,
    COMMA,
    ASSIGNMENT,
    LOGICAL_OR,
    LOGICAL_AND,
    COMPARISON,
    COALESCE,
    CAST,
    ADDITIVE,
    MULTIPLICATIVE,
    PREFIX,
    FUNCTION_CALL,
    MEMBER,
    PRIMARY,
    HIGHEST;
}