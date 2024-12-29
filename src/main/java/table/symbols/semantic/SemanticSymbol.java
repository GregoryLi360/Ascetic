package table.symbols.semantic;

import table.scopes.Scope;
import table.symbols.Symbol;

import ast.types.TypeSignature;

public abstract class SemanticSymbol extends Symbol {
    public TypeSignature type;

    public SemanticSymbol(long globalIndex, String name, Scope enclosingScope, TypeSignature type) {
        super(globalIndex, name, enclosingScope);
        this.type = type;
    }
}