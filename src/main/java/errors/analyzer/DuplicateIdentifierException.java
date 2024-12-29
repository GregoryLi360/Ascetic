package errors.analyzer;

import ast.exprs.IdentifierExpression;

public class DuplicateIdentifierException extends AnalyzerException {
    public IdentifierExpression identifier;
    
    public DuplicateIdentifierException(IdentifierExpression node) {
        super("Duplicate identifier: " + node);
        this.identifier = node;
    }
}
