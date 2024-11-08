package parser.ast.exprs;

import java.util.ArrayList;
import java.util.List;

import parser.ast.ExpressionNode;

public class FunctionCallExpression extends ExpressionNode {
    public ExpressionNode functionIdentifier;
    public List<ExpressionNode> params = new ArrayList<>();
}