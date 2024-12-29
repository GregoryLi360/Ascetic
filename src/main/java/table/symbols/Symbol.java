package table.symbols;

import table.scopes.Scope;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import ast.types.TypeSignature;
import interpreter.builtins.Builtin;

public abstract class Symbol {
    public String name;
    public Scope enclosingScope;
    public int referenceCount = 1; 
    public final long globalIndex;

    protected Symbol(long globalIndex, String name, Scope enclosingScope) {
        this.globalIndex = globalIndex;
        this.name = name;
        this.enclosingScope = enclosingScope;
    }

    public void incrementReferenceCount() {
        referenceCount++;
    }

    public void decrementReferenceCount() {
        referenceCount--;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Symbol otherSymbol) {
            return otherSymbol.name.equals(name) 
                && otherSymbol.enclosingScope.equals(enclosingScope);
        }
        return false;
    }

    public String getQualifiedName() {
        List<Scope> scopes = new ArrayList<>();
        for (var next = this.enclosingScope; next != null; next = next.parent) {
            scopes.add(next);
        }
        
        Collections.reverse(scopes);
        return String.join(".", scopes.stream().map(scope -> scope.getName()).toList()) 
            + "." + this.name;
    }
}