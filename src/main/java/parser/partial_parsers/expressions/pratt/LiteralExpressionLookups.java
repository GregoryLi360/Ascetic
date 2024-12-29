package parser.partial_parsers.expressions.pratt;

import java.util.HashMap;

import ast.*;
import lexer.tokens.TokenType;
import parser.partial_parsers.expressions.ExpressionParser;

public class LiteralExpressionLookups {
    private ExpressionParser parser;
    public final HashMap<TokenType, Integer> bindingPowers = new HashMap<>();
    public final HashMap<TokenType, NullDenotation<ExpressionNode>> nullDenotationHandlers = new HashMap<>();
    public final HashMap<TokenType, LeftDenotation<Integer, ExpressionNode, ExpressionNode>> leftDenotationHandlers = new HashMap<>();

    private void addNullDenotation(TokenType type, NullDenotation<ExpressionNode> handler) {
        bindingPowers.put(type, BindingPower.HIGHEST.ordinal());
        nullDenotationHandlers.put(type, handler);
    }

    private void addLeftDenotation(TokenType type, LeftDenotation<Integer, ExpressionNode, ExpressionNode> handler, BindingPower bindingPower) {
        bindingPowers.put(type, bindingPower.ordinal());
        leftDenotationHandlers.put(type, handler);
    }

    public LiteralExpressionLookups(ExpressionParser parser) {
        this.parser = parser;
        addNullDenotations();
        addLeftDenotations();
        this.bindingPowers.remove(TokenType.OPEN_CURLY);
    }

    public void addLeftDenotations() {
        addLeftDenotation(TokenType.ASSIGNMENT, parser::parseAssignmentExpression, BindingPower.ASSIGNMENT);

        addLeftDenotation(TokenType.OR, parser::parseInfixExpression, BindingPower.LOGICAL_OR);
        addLeftDenotation(TokenType.AND, parser::parseInfixExpression, BindingPower.LOGICAL_AND);

        addLeftDenotation(TokenType.EQUALS_TO, parser::parseInfixExpression, BindingPower.COMPARISON);
        addLeftDenotation(TokenType.NOT_EQUALS_TO, parser::parseInfixExpression, BindingPower.COMPARISON);
        addLeftDenotation(TokenType.LESS_THAN, parser::parseInfixExpression, BindingPower.COMPARISON);
        addLeftDenotation(TokenType.LESS_THAN_OR_EQUALS_TO, parser::parseInfixExpression, BindingPower.COMPARISON);
        addLeftDenotation(TokenType.GREATER_THAN, parser::parseInfixExpression, BindingPower.COMPARISON);
        addLeftDenotation(TokenType.GREATER_THAN_OR_EQUALS_TO, parser::parseInfixExpression, BindingPower.COMPARISON);

        addLeftDenotation(TokenType.PLUS, parser::parseInfixExpression, BindingPower.ADDITIVE);
        addLeftDenotation(TokenType.MINUS, parser::parseInfixExpression, BindingPower.ADDITIVE);
        addLeftDenotation(TokenType.MULTIPLY, parser::parseInfixExpression, BindingPower.MULTIPLICATIVE);
        addLeftDenotation(TokenType.DIVIDE, parser::parseInfixExpression, BindingPower.MULTIPLICATIVE);
        addLeftDenotation(TokenType.MODULO, parser::parseInfixExpression, BindingPower.MULTIPLICATIVE);
        
        addLeftDenotation(TokenType.OPEN_PAREN, parser::parseFunctionCallExpression, BindingPower.FUNCTION_CALL);
        
        addLeftDenotation(TokenType.BANG, parser::parsePostfixExpression, BindingPower.PREFIX);
    }

    public void addNullDenotations() {
        addNullDenotation(TokenType.BOOLEAN_LITERAL, parser::parseLiteralExpression);
        addNullDenotation(TokenType.INTEGER_LITERAL, parser::parseLiteralExpression);
        addNullDenotation(TokenType.FLOAT_LITERAL, parser::parseLiteralExpression);
        addNullDenotation(TokenType.STRING_LITERAL, parser::parseLiteralExpression);
        addNullDenotation(TokenType.IDENTIFIER, parser::parseIdentifierExpression);

        addNullDenotation(TokenType.MINUS, parser::parsePrefixExpression);
        addNullDenotation(TokenType.BANG, parser::parsePrefixExpression);

        addNullDenotation(TokenType.IF, parser::parseIfExpression);

        addNullDenotation(TokenType.OPEN_PAREN, parser::handleAmbiguousOpenParentheses);
        addNullDenotation(TokenType.OPEN_CURLY, parser::parseCodeBlockExpression);
    }
}