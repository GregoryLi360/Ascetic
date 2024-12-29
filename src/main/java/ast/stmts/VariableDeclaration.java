package ast.stmts;

import java.util.ArrayList;
import java.util.List;

import ast.StatementNode;
import ast.exprs.AssignmentExpression;
import ast.exprs.IdentifierExpression;
import lexer.tokens.Token;

public class VariableDeclaration extends StatementNode {
    public enum Type {
        let,
        var;
    
        public static Type fromToken(Token token) {
            switch (String.valueOf(token.text)) {
                case "let": { return let; }
                case "var": { return var; }
                default: { return null; }
            }
        }
    }

    public List<Token> modifiers = new ArrayList<>();
    public Type declarationType;
    public IdentifierExpression identifier;
    public AssignmentExpression assignmentExpression;
}