package ast.exprs;

import ast.ExpressionNode;
import ast.annotations.TypeAnnotation;
import lexer.tokens.Token;

public class IdentifierExpression extends ExpressionNode {
    public Token identifier;
    public TypeAnnotation typeAnnotation;
}