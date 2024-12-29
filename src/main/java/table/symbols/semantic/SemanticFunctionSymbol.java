package table.symbols.semantic;

import ast.types.FunctionType;
import table.scopes.Scope;

public class SemanticFunctionSymbol extends SemanticSymbol {
    public Scope effectiveScope;

    public FunctionType type;

    public SemanticFunctionSymbol(long globalIndex, String name, Scope enclosingScope, FunctionType type, Scope functionScope) {
        super(globalIndex, name, enclosingScope, type);
        this.effectiveScope = functionScope;
        this.type = type;
    }
}