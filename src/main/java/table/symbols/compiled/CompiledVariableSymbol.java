package table.symbols.compiled;

import interpreter.builtins.Builtin;
import table.scopes.Scope;

public class CompiledVariableSymbol extends CompiledSymbol {
    public CompiledVariableSymbol(long globalIndex, String name, Scope enclosingScope, Builtin.Any value) {
        super(globalIndex, name, enclosingScope, value);
    }
}