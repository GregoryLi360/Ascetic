package parser.ast.exprs;

import lexer.tokens.Token;
import parser.ast.ExpressionNode;

public class OperationExpression extends ExpressionNode {
    public Token operator;
    public ExpressionNode left;
    public ExpressionNode right;
}
