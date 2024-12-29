package table.scopes;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import ast.ExpressionNode;
import ast.exprs.IdentifierExpression;
import errors.analyzer.DuplicateIdentifierException;
import errors.analyzer.UndeclaredIdentifierException;
import interpreter.builtins.Builtin;
import table.symbols.Symbol;
import table.symbols.compiled.*;
import table.symbols.semantic.*;

public abstract class Scope {
    protected static long globalScopeCount = 1;
    protected static long globalIdentifierCount = 0;

    // protected Map<String, Symbol> members = new LinkedHashMap<>();
    public Map<String, Symbol> members = new LinkedHashMap<>();
    // protected Map<String, Scope> children = new LinkedHashMap<>();
    public Map<String, Scope> children = new LinkedHashMap<>();

    private String name;
    private final long globalIndex;
    private final int relativeIndex;
    public final Scope parent;

    protected Scope(String scopeName, long globalIndex, int relativeIndex, Scope parent) {
        this.name = scopeName;
        this.relativeIndex = relativeIndex;
        this.globalIndex = globalIndex;
        this.parent = parent;
    }

    public int getRelativeIndex() { return relativeIndex; }
    public long getGlobalIndex() { return globalIndex; }
    public String getName() { return name; }
    public void setName(String name) { 
        this.name = name; 
        this.parent.children.put(name, this);
    }

    public Symbol find(IdentifierExpression identifier) throws UndeclaredIdentifierException {
        String identifierName = String.valueOf(identifier.identifier.text);
        return this.find(identifierName, identifier);
    }

    protected Symbol find(String identifierName, IdentifierExpression identifier) throws UndeclaredIdentifierException {
        Symbol symbol = this.members.get(identifierName);
        if (symbol != null) { return symbol; }
        if (this.parent != null) { return this.parent.find(identifierName, identifier); }

        throw new UndeclaredIdentifierException(identifier);
    }

    public Symbol findIteratively(IdentifierExpression identifier) throws UndeclaredIdentifierException {
        String identifierName = String.valueOf(identifier.identifier.text);
        Scope scope = this;
        while (scope != null) {
            Symbol symbol = scope.members.get(identifierName);
            if (symbol != null) { return symbol; }
            else { scope = scope.parent; }
        }
        
        throw new UndeclaredIdentifierException(identifier);
    }


    /**
     * ONLY USE FOR READ-EVAL-PRINT-LOOP
     */
    public void remove(IdentifierExpression identifier) {
        String name = String.valueOf(identifier.identifier.text);
        this.children.remove(name);
        this.members.remove(name);
    }

    public Scope addFunctionScope(IdentifierExpression identifier) {
        long uniqueGlobalIndex = ++globalScopeCount;
        String scopeName = String.valueOf(identifier.identifier.text);

        Scope childScope = new FunctionScope(scopeName, uniqueGlobalIndex, children.size(), this);
        children.put(scopeName, childScope);

        return childScope;
    }

    public Scope addAnonymousCodeBlock() {
        long uniqueGlobalIndex = ++globalScopeCount;
        String scopeName = "$Anonymous" + uniqueGlobalIndex;

        Scope childScope = new FunctionScope(scopeName, uniqueGlobalIndex, children.size(), this);
        children.put(scopeName, childScope);

        return childScope;
    }

    // /**
    //  * 
    //  * @param identifier
    //  * @param definition an `ExpressionNode` filled with a type signature
    //  * @return
    //  * @throws DuplicateIdentifierException
    //  */
    // public Symbol declareAndDefineType(IdentifierExpression identifier, ExpressionNode definition) throws DuplicateIdentifierException {
    //     String identifierName = String.valueOf(identifier.identifier.text);
    //     if (this.members.containsKey(identifierName)) { throw new DuplicateIdentifierException(identifier); }
    //     Symbol symbol = new Symbol(identifierName, definition.typeSignature, this);
    //     this.members.put(identifierName, symbol);
    //     return symbol;
    // }

    public Symbol declareAndDefineValue(IdentifierExpression identifier, Builtin.Any definition) throws DuplicateIdentifierException {
        String identifierName = String.valueOf(identifier.identifier.text);
        if (this.members.containsKey(identifierName)) { throw new DuplicateIdentifierException(identifier); }
        
        Scope.globalIdentifierCount++;

        Symbol symbol = new CompiledVariableSymbol(Scope.globalIdentifierCount, identifierName, this, definition);
        
        if (definition instanceof Builtin.Function functionDefinition) {
            functionDefinition.scope.setName(identifierName);
            symbol = new CompiledFunctionSymbol(Scope.globalIdentifierCount, identifierName, this, functionDefinition, functionDefinition.scope);
        }
        
        this.members.put(identifierName, symbol);
        return symbol;
    }
}
