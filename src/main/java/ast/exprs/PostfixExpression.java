package ast.exprs;

import ast.ExpressionNode;
import lexer.tokens.Token;

public class PostfixExpression extends ExpressionNode {
    public Token postfix;
    public ExpressionNode expression;
}