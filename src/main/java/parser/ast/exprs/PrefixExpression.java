package parser.ast.exprs;

import lexer.tokens.Token;
import parser.ast.ExpressionNode;

public class PrefixExpression extends ExpressionNode {
    public Token prefix;
    public ExpressionNode expression;
}