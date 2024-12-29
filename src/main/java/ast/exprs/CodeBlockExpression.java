package ast.exprs;

import java.util.ArrayList;
import java.util.List;

import ast.ExpressionNode;
import ast.StatementNode;

public class CodeBlockExpression extends ExpressionNode {
    public List<StatementNode> children = new ArrayList<>();
}