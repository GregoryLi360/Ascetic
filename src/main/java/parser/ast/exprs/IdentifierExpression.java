package parser.ast.exprs;

import lexer.tokens.Token;
import parser.ast.ExpressionNode;
import parser.ast.annotations.TypeAnnotation;

public class IdentifierExpression extends ExpressionNode {
    public Token identifier;
    public TypeAnnotation typeAnnotation;
}