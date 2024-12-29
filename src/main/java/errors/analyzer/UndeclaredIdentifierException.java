package errors.analyzer;

import ast.exprs.IdentifierExpression;

public class UndeclaredIdentifierException extends AnalyzerException {
    public IdentifierExpression identifier;    

    public UndeclaredIdentifierException(IdentifierExpression node) {
        super("Undeclared identifier: " + node);
        this.identifier = node;
    }
}
