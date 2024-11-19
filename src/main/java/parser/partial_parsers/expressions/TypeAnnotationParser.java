package parser.partial_parsers.expressions;

import java.util.ArrayList;
import java.util.List;

import errors.parser.UnexpectedTokenException;
import errors.parser.UnexpectedTokenException.ErrorRecovery;
import lexer.tokens.Token;
import lexer.tokens.TokenType;
import parser.ast.annotations.TypeAnnotation;
import parser.ast.types.*;
import parser.partial_parsers.expressions.pratt.BindingPower;
import parser.partial_parsers.expressions.pratt.TypeExpressionLookups;
import parser.Parser;

public class TypeAnnotationParser {
    private Parser parser;
    private TypeExpressionLookups lookups;

    public TypeAnnotationParser(Parser parser) {
        this.parser = parser;
        this.lookups = new TypeExpressionLookups(this); 
    }

    public TypeAnnotation parseTypeAnnotation() throws UnexpectedTokenException {
        TypeAnnotation node = new TypeAnnotation();
        node.tokenStart = parser.getIndex();

        parser.expect(TokenType.COLON);
        node.typeSignature = parseTypeSignature(BindingPower.LOWEST.ordinal());

        node.tokenEnd = parser.getIndex();
        return node;
    }

    public TypeSignature parseTypeSignature(Integer bindingPower) throws UnexpectedTokenException {
        Token token = parser.peek();

        var nullHandler = lookups.nullDenotationHandlers.get(token.type);
        if (nullHandler == null) { throw new UnexpectedTokenException(token, lookups.nullDenotationHandlers.keySet().stream().toList()); }
        TypeSignature left = nullHandler.invoke();

        token = parser.peek();
        Integer nextBindingPower = lookups.bindingPowers.getOrDefault(token.type, -1);

        while (nextBindingPower > bindingPower) {
            var leftHandler = lookups.leftDenotationHandlers.get(token.type);
            if (leftHandler == null) { throw new UnexpectedTokenException(token, lookups.leftDenotationHandlers.keySet().stream().toList()); }
            left = leftHandler.invoke(nextBindingPower, left);
            
            token = parser.peek();
            nextBindingPower = lookups.bindingPowers.getOrDefault(token.type, -1);
        }

        return left;
    }

    public TypeSignature parseOptionalType(Integer bindingPower, TypeSignature left) throws UnexpectedTokenException {
        parser.expect(TokenType.QUESTION_MARK);
        left.optional = true;
        return left;
    }

    public PrimitiveType parsePrimitiveType() throws UnexpectedTokenException {
        PrimitiveType node = new PrimitiveType();
        Token token = parser.expect(TokenType.PRIMITIVE_TYPE);
        node.primitiveType = PrimitiveType.Value.fromToken(token);
        return node;
    }

    /** user-defined types, eg. structs */
    // public IdentifierType parseIdentifierType() throws UnexpectedTokenException {
    //     Token token = parser.expect(TokenType.IDENTIFIER);
    //     return IdentifierType.fromToken(token);
    // }

    // public MemberType parseMemberType(Integer bindingPower, TypeSignature left) throws UnexpectedTokenException {
    //     MemberType node = new MemberType();
    //     parser.expect(TokenType.PERIOD);

    //     var right = parseIdentifierType();
    //     node.context = left;
    //     node.member = right;

    //     return node;
    // }

    public TypeSignature handleAmbiguousOpenParentheses() throws UnexpectedTokenException {
        int beforeParsingParamsIndex = parser.getIndex();

        try { return parseFunctionType(); } 
        catch (UnexpectedTokenException exception) {
            if (!exception.recoverableFrom(ErrorRecovery.AmbiguousOpenParentheses)) { throw exception; }
            parser.setIndex(beforeParsingParamsIndex);
            return parseGroupedType();
        }
    }

    public TypeSignature parseGroupedType() throws UnexpectedTokenException {
        parser.expect(TokenType.OPEN_PAREN);
        TypeSignature node = parseTypeSignature(BindingPower.LOWEST.ordinal());
        parser.expect(TokenType.CLOSED_PAREN);
        return node;
    }

    public List<TypeSignature> parseFunctionParamTypes() throws UnexpectedTokenException {
        List<TypeSignature> paramTypes = new ArrayList<>();
        try {
            parser.expect(TokenType.OPEN_PAREN);
            Token token = parser.expect(TokenType.CLOSED_PAREN, TokenType.PRIMITIVE_TYPE);
            switch (token.type) {
                case CLOSED_PAREN: { return paramTypes; }
                case PRIMITIVE_TYPE: { paramTypes.add(parseTypeSignature(BindingPower.LOWEST.ordinal())); break; }
                default: { throw new RuntimeException("Unreachable state, expected token types should have been exhausted."); }
            }

            while (true) {
                token = parser.expect(TokenType.CLOSED_PAREN, TokenType.COMMA);
                switch (token.type) {
                    case CLOSED_PAREN: { return paramTypes; }
                    case COMMA: { paramTypes.add(parseTypeSignature(BindingPower.COMMA.ordinal())); break; }
                    default: { throw new RuntimeException("Unreachable state, expected token types should have been exhausted."); }
                }
            }
        } catch (UnexpectedTokenException exception) {
            throw new UnexpectedTokenException(exception.getToken(), exception.getExpectedTypes(), ErrorRecovery.AmbiguousOpenParentheses);
        }
    }

    public FunctionType parseFunctionType() throws UnexpectedTokenException {
        FunctionType node = new FunctionType();
        node.paramTypes = parseFunctionParamTypes();
        parser.expect(TokenType.ARROW);
        node.returnType = parseTypeSignature(BindingPower.LOWEST.ordinal());
        return node;
    }
}