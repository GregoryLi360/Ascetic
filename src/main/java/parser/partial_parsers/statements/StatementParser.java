package parser.partial_parsers.statements;

import java.util.List;

import ast.*;
import ast.stmts.*;
import errors.parser.UnexpectedTokenException;
import lexer.tokens.Token;
import lexer.tokens.TokenType;
import parser.Parser;
import parser.partial_parsers.expressions.ExpressionParser;
import parser.partial_parsers.expressions.pratt.BindingPower;

public class StatementParser {
    private Parser parser;
    private ExpressionParser expressionParser;

    public StatementParser(Parser parser, ExpressionParser expressionParser) {
        this.parser = parser;
        this.expressionParser = expressionParser;
    }

    public StatementNode parseStatement() throws UnexpectedTokenException {
        Token token = parser.peek();
        switch (token.type) {
            case COMMENT: { return parseCommentStatement(); }
            case LET: { return parseVariableDeclaration(); }
            case GUARD: { return parseGuardStatement(); }
            case RETURN: { return parseReturnStatement(); }
            default: { return parseExpressionStatement(); }
        }
    }

    /** Valid top-level (program level) statements */
    public StatementNode parseDeclarationOrComment() throws UnexpectedTokenException {
        Token token = parser.peek();
        switch (token.type) {
            case COMMENT: { return parseCommentStatement(); }
            case LET: { return parseVariableDeclaration(); }
            default: { throw new UnexpectedTokenException(token, List.of(TokenType.COMMENT, TokenType.LET)); }
        }
    }

    public CommentStatement parseCommentStatement() throws UnexpectedTokenException {
        CommentStatement node = new CommentStatement();
        node.comment = parser.expect(TokenType.COMMENT);
        return node;
    }

    /**
     * Parses an expression followed by a semicolon
     * <p>
     * eg. {@code 1+1;}
     * 
     * @return an {@code ExpressionStatement} representing the expression statement
     * @throws UnexpectedTokenException 
     */
    public ExpressionStatement parseExpressionStatement() throws UnexpectedTokenException {
        ExpressionStatement node = new ExpressionStatement();
        node.tokenStart = parser.getIndex();
        node.expression = expressionParser.parseExpression(BindingPower.LOWEST.ordinal());
        parser.expect(TokenType.SEMICOLON);
        node.tokenEnd = parser.getIndex();
        return node;
    }


    public GuardStatement parseGuardStatement() throws UnexpectedTokenException {
        GuardStatement node = new GuardStatement();
        node.tokenStart = parser.getIndex();

        parser.expect(TokenType.GUARD);

        // var clause = expressionParser.parseExpression(BindingPower.LOWEST.ordinal());
        var clause = expressionParser.parseConditional(BindingPower.LOWEST.ordinal());
        node.clause = clause;

        node.body = expressionParser.parseCodeBlockExpression();

        parser.expect(TokenType.SEMICOLON);
        node.tokenEnd = parser.getIndex();
        return node;
    }

    public ReturnStatement parseReturnStatement() throws UnexpectedTokenException {
        ReturnStatement node = new ReturnStatement();
        node.tokenStart = parser.getIndex();
        parser.expect(TokenType.RETURN);

        node.returnExpression = expressionParser.parseExpression(BindingPower.LOWEST.ordinal());
        parser.expect(TokenType.SEMICOLON);

        node.tokenEnd = parser.getIndex();
        return node;
    }

    public VariableDeclaration parseVariableDeclaration() throws UnexpectedTokenException {
        VariableDeclaration node = new VariableDeclaration();
        node.tokenStart = parser.getIndex();
        node.declarationType = VariableDeclaration.Type.fromToken(parser.expect(TokenType.LET));

        var identifier = expressionParser.parseIdentifierWithOptionalType();
        var assignment = expressionParser.parseAssignmentExpression(BindingPower.ASSIGNMENT.ordinal(), identifier);
        node.identifier = identifier;
        node.assignmentExpression = assignment;

        parser.expect(TokenType.SEMICOLON);
        node.tokenEnd = parser.getIndex();
        return node;
    }
}