package parser.ast.exprs;

import lexer.tokens.Token;
import parser.ast.ExpressionNode;

public class PostfixExpression extends ExpressionNode {
    public Token postfix;
    public ExpressionNode expression;
}