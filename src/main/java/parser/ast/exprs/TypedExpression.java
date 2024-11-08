package parser.ast.exprs;

import parser.ast.types.TypeSignature;

public interface TypedExpression {
    public TypeSignature getTypeSignature();
}