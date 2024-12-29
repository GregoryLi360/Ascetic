package table.scopes;

public class FunctionScope extends Scope {
    protected FunctionScope(String scopeName, long globalIndex, int relativeIndex, Scope parent) {
        super(scopeName, globalIndex, relativeIndex, parent);
    }
}