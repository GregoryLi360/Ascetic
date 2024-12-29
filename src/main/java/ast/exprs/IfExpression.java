package ast.exprs;

import java.util.ArrayList;
import java.util.List;

import ast.ExpressionNode;

public class IfExpression extends ExpressionNode {
    public List<ExpressionNode> conditions = new ArrayList<>();
    public List<CodeBlockExpression> blocks = new ArrayList<>();
}