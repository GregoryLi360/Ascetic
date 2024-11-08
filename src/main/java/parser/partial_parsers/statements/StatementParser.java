package parser.partial_parsers.statements;

import errors.parser.UnexpectedTokenException;
import lexer.tokens.Token;
import lexer.tokens.TokenType;
import parser.Parser;
import parser.ast.*;
import parser.ast.stmts.*;
import parser.partial_parsers.expressions.ExpressionParser;

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

    /**
     * Parses comments <p>
     * eg. {@code // hi } <p>
     * check {@code examples.md} for more examples
     * 
     * @return an {@code CommentStatement} representing the comment
     * @throws UnexpectedTokenException 
     */
    public CommentStatement parseCommentStatement() throws UnexpectedTokenException {
        CommentStatement node = new CommentStatement();
        node.comment = parser.expect(TokenType.COMMENT);
        return node;
    }

    /**
     * Parses an expression followed by a semicolon <p>
     * eg. {@code 1 + 1;}
     * 
     * @return an {@code ExpressionStatement} representing the expression statement
     * @throws UnexpectedTokenException 
     */
    public ExpressionStatement parseExpressionStatement() throws UnexpectedTokenException {
        ExpressionStatement node = new ExpressionStatement();
        node.tokenStart = parser.getIndex();
        node.expression = expressionParser.parseExpression();
        parser.expect(TokenType.SEMICOLON);
        node.tokenEnd = parser.getIndex();
        return node;
    }

    /**
     * Parses a guard statement<p>
     * eg. check {@code examples.md} for examples
     * 
     * @return an {@code GuardStatement} representing the guard statement
     * @throws UnexpectedTokenException 
     */
    public GuardStatement parseGuardStatement() throws UnexpectedTokenException {
        GuardStatement node = new GuardStatement();
        node.tokenStart = parser.getIndex();

        parser.expect(TokenType.GUARD);

        var clause = expressionParser.parseConditional();
        node.clause = clause;

        node.body = expressionParser.parseCodeBlockExpression();

        parser.expect(TokenType.SEMICOLON);
        node.tokenEnd = parser.getIndex();
        return node;
    }

    /**
     * Parses a return statement <p>
     * eg. {@code return x;}
     * 
     * @return an {@code ReturnStatement} representing the return statement
     * @throws UnexpectedTokenException 
     */
    public ReturnStatement parseReturnStatement() throws UnexpectedTokenException {
        ReturnStatement node = new ReturnStatement();
        node.tokenStart = parser.getIndex();
        parser.expect(TokenType.RETURN);

        node.returnExpression = expressionParser.parseExpression();
        parser.expect(TokenType.SEMICOLON);

        node.tokenEnd = parser.getIndex();
        return node;
    }

    /**
     * Parses a return statement<p>
     * eg. {@code let x = 0;} <p>
     * check {@code examples.md} for more examples
     * 
     * @return an {@code ReturnStatement} representing the return statement
     * @throws UnexpectedTokenException 
     */
    public VariableDeclaration parseVariableDeclaration() throws UnexpectedTokenException {
        VariableDeclaration node = new VariableDeclaration();
        node.tokenStart = parser.getIndex();
        node.declarationType = VariableDeclaration.Type.fromToken(parser.expect(TokenType.LET));

        var identifier = expressionParser.parseIdentifierWithOptionalType();
        var assignment = expressionParser.parseAssignmentExpression();
        node.identifier = identifier;
        node.assignmentExpression = assignment;

        parser.expect(TokenType.SEMICOLON);
        node.tokenEnd = parser.getIndex();
        return node;
    }
}