package table.symbols.semantic;

import ast.types.TypeSignature;
import table.scopes.Scope;

public class SemanticVariableSymbol extends SemanticSymbol {
    public SemanticVariableSymbol(long globalIndex, String name, Scope enclosingScope, TypeSignature type) {
        super(globalIndex, name, enclosingScope, type);
    }
}
