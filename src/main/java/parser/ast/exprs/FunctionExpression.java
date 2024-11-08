package parser.ast.exprs;

import java.util.ArrayList;
import java.util.List;

import parser.ast.ExpressionNode;
import parser.ast.types.TypeSignature;

public class FunctionExpression extends ExpressionNode {
    public List<IdentifierExpression> params = new ArrayList<>();
    public TypeSignature annotatedReturnType;
    public TypeSignature returnType;
    public CodeBlockExpression block;
}