package parser.partial_parsers.expressions;

import java.util.List;

import ast.exprs.IdentifierExpression;
import errors.parser.UnexpectedTokenException;
import lexer.tokens.Token;
import lexer.tokens.TokenType;
import parser.Parser;

public class IdentifierMemberParser {
    private Parser parser;
    private TypeAnnotationParser typeAnnotationParser;

    public IdentifierMemberParser(Parser parser, TypeAnnotationParser typeAnnotationParser) {
        this.parser = parser;
        this.typeAnnotationParser = typeAnnotationParser;
    }

    public IdentifierExpression parseIdentifierExpression() throws UnexpectedTokenException {
        IdentifierExpression node = new IdentifierExpression();
        node.tokenStart = parser.getIndex();
        node.identifier = parser.expect(TokenType.IDENTIFIER);
        node.tokenEnd = parser.getIndex();
        return node;
    }

    public IdentifierExpression parseIdentifierWithOptionalType() throws UnexpectedTokenException {
        IdentifierExpression node = parseIdentifierExpression();
        if (parser.peek().is(TokenType.COLON)) {
            node.typeAnnotation = typeAnnotationParser.parseTypeAnnotation();
            node.tokenEnd = parser.getIndex();
        }

        return node;
    }

    public IdentifierExpression parseIdentifierWithType() throws UnexpectedTokenException {
        IdentifierExpression node = parseIdentifierExpression();
        node.typeAnnotation = typeAnnotationParser.parseTypeAnnotation();
        node.tokenEnd = parser.getIndex();
        return node;
    }
}