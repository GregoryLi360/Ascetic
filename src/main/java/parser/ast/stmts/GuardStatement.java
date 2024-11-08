package parser.ast.stmts;

import parser.ast.ExpressionNode;
import parser.ast.StatementNode;
import parser.ast.exprs.CodeBlockExpression;
import parser.ast.types.TypeSignature;

public class GuardStatement extends StatementNode {
    public TypeSignature actualReturnType;
    public ExpressionNode clause;
    public CodeBlockExpression body;
}