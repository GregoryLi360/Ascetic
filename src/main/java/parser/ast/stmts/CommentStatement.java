package parser.ast.stmts;

import lexer.tokens.Token;
import parser.ast.StatementNode;

public class CommentStatement extends StatementNode {
    public Token comment;
}