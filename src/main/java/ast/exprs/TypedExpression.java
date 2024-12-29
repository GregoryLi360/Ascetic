package ast.exprs;

import ast.types.TypeSignature;

public interface TypedExpression {
    public TypeSignature getTypeSignature();
}