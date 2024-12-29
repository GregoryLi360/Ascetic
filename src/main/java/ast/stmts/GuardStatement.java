package ast.stmts;

import ast.ExpressionNode;
import ast.StatementNode;
import ast.exprs.CodeBlockExpression;
import ast.types.TypeSignature;

public class GuardStatement extends StatementNode {
    public TypeSignature actualReturnType;
    public ExpressionNode clause;
    public CodeBlockExpression body;
}