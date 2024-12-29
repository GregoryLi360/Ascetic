package ast;

public abstract class AbstractSyntaxTreeNode extends JsonPrintable {
    public final String nodeType = this.getClass().getSimpleName();
    public int tokenStart, tokenEnd;
    public boolean isErroneous = false;
}