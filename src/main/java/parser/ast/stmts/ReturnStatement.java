package parser.ast.stmts;

import parser.ast.ExpressionNode;
import parser.ast.StatementNode;

public class ReturnStatement extends StatementNode {
    public ExpressionNode returnExpression;
}