package parser.ast.exprs;

import java.util.ArrayList;
import java.util.List;

import parser.ast.ExpressionNode;
import parser.ast.StatementNode;

public class CodeBlockExpression extends ExpressionNode {
    public List<StatementNode> children = new ArrayList<>();
}