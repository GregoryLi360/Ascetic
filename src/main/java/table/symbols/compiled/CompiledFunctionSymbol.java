package table.symbols.compiled;

import interpreter.builtins.Builtin;
import table.scopes.Scope;

public class CompiledFunctionSymbol extends CompiledSymbol {
    public Scope effectiveScope;

    public Builtin.Function value;

    public CompiledFunctionSymbol(long globalIndex, String name, Scope enclosingScope, Builtin.Function value, Scope functionScope) {
        super(globalIndex, name, enclosingScope, value);
        this.value = value;
        this.effectiveScope = functionScope;
    }
}