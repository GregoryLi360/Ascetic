package ast.exprs;

import ast.ExpressionNode;
import lexer.tokens.Token;

public class OperationExpression extends ExpressionNode {
    public Token operator;
    public ExpressionNode left;
    public ExpressionNode right;
}
