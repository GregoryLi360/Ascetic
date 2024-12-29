package table.symbols.compiled;

import interpreter.builtins.Builtin;
import table.scopes.Scope;
import table.symbols.Symbol;

public abstract class CompiledSymbol extends Symbol {
    public Builtin.Any value;

    public CompiledSymbol(long globalIndex, String name, Scope enclosingScope, Builtin.Any value) {
        super(globalIndex, name, enclosingScope);
        this.value = value;
    }
}