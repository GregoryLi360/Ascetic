package ast.exprs;

import ast.ExpressionNode;
import lexer.tokens.Token;

public class PrefixExpression extends ExpressionNode {
    public Token prefix;
    public ExpressionNode expression;
}