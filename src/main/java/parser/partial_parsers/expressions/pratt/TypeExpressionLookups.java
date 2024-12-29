package parser.partial_parsers.expressions.pratt;

import java.util.HashMap;

import ast.types.TypeSignature;
import lexer.tokens.TokenType;
import parser.partial_parsers.expressions.TypeAnnotationParser;

public class TypeExpressionLookups {
    private TypeAnnotationParser parser;
    public final HashMap<TokenType, Integer> bindingPowers = new HashMap<>();
    public final HashMap<TokenType, NullDenotation<TypeSignature>> nullDenotationHandlers = new HashMap<>();
    public final HashMap<TokenType, LeftDenotation<Integer, TypeSignature, TypeSignature>> leftDenotationHandlers = new HashMap<>();

    private void addNullDenotation(TokenType type,  NullDenotation<TypeSignature> handler) {
        bindingPowers.put(type, BindingPower.HIGHEST.ordinal());
        nullDenotationHandlers.put(type, handler);
    }

    private void addLeftDenotation(TokenType type, LeftDenotation<Integer, TypeSignature, TypeSignature> handler, BindingPower bindingPower) {
        bindingPowers.put(type, bindingPower.ordinal());
        leftDenotationHandlers.put(type, handler);
    }

    public TypeExpressionLookups(TypeAnnotationParser parser) {
        this.parser = parser;
        addNullDenotations();
        addLeftDenotations();
    }

    public void addLeftDenotations() {
        // addLeftDenotation(TokenType.PERIOD, parser::parseMemberType, BindingPower.MEMBER);
        addLeftDenotation(TokenType.QUESTION_MARK, parser::parseOptionalType, BindingPower.PREFIX);
    }

    public void addNullDenotations() {
        addNullDenotation(TokenType.OPEN_PAREN, parser::handleAmbiguousOpenParentheses);
        // addNullDenotation(TokenType.IDENTIFIER, parser::parseIdentifierType);
        addNullDenotation(TokenType.PRIMITIVE_TYPE, parser::parsePrimitiveType);
    }
}
