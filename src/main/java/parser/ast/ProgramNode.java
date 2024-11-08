package parser.ast;

import java.util.ArrayList;
import java.util.List;

public class ProgramNode extends AbstractSyntaxTreeNode {
    public List<StatementNode> children = new ArrayList<>();
}