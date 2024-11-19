package parser.partial_parsers.expressions;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import errors.parser.UnexpectedTokenException;
import errors.parser.UnexpectedTokenException.ErrorRecovery;
import lexer.tokens.Token;
import lexer.tokens.TokenType;
import parser.Parser;
import parser.ast.*;
import parser.ast.exprs.*;
import parser.partial_parsers.expressions.pratt.BindingPower;
import parser.partial_parsers.expressions.pratt.LiteralExpressionLookups;

public class ExpressionParser {
    private Parser parser;
    private TypeAnnotationParser typeAnnotationParser;
    private IdentifierMemberParser identifierMemberParser;
    private LiteralExpressionLookups lookups;

    public ExpressionParser(Parser parser) {
        this.parser = parser;
        this.typeAnnotationParser = new TypeAnnotationParser(parser);
        this.identifierMemberParser = new IdentifierMemberParser(parser, typeAnnotationParser);
        this.lookups = new LiteralExpressionLookups(this);
    }

    public ExpressionNode parseExpression(Integer bindingPower) throws UnexpectedTokenException {
        Token token = parser.peek();

        var nullHandler = lookups.nullDenotationHandlers.get(token.type);
        if (nullHandler == null) { throw new UnexpectedTokenException(token, lookups.nullDenotationHandlers.keySet().stream().toList()); }
        ExpressionNode left = nullHandler.invoke();

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

    public ExpressionNode parseConditional(Integer bindingPower) throws UnexpectedTokenException {
        Token token = parser.peek();

        var nullHandler = lookups.nullDenotationHandlers.get(token.type);
        if (nullHandler == null) { throw new UnexpectedTokenException(token, lookups.nullDenotationHandlers.keySet().stream().toList()); }
        ExpressionNode left = nullHandler.invoke();

        token = parser.peek();
        if (token.is(TokenType.OPEN_CURLY)) { return left; }
        Integer nextBindingPower = lookups.bindingPowers.getOrDefault(token.type, -1);

        while (nextBindingPower > bindingPower) {
            var leftHandler = lookups.leftDenotationHandlers.get(token.type);
            if (leftHandler == null) { throw new UnexpectedTokenException(token, lookups.leftDenotationHandlers.keySet().stream().toList()); }
            left = leftHandler.invoke(nextBindingPower, left);
            
            token = parser.peek();
            if (token.is(TokenType.OPEN_CURLY)) { return left; }
            nextBindingPower = lookups.bindingPowers.getOrDefault(token.type, -1);
        }

        return left;
    }

    public PrefixExpression parsePrefixExpression() throws UnexpectedTokenException {
        PrefixExpression node = new PrefixExpression();
        node.tokenStart = parser.getIndex();
        node.prefix = parser.consume();

        ExpressionNode right = parseExpression(BindingPower.PREFIX.ordinal());
        node.expression = right;

        node.tokenEnd = parser.getIndex();
        return node;
    }

    public OperationExpression parseInfixExpression(Integer bindingPower, ExpressionNode left) throws UnexpectedTokenException {
        OperationExpression node = new OperationExpression();
        node.tokenStart = left.tokenStart;
        node.operator = parser.consume();

        node.left = left;
        ExpressionNode right = parseExpression(bindingPower);
        node.right = right;

        node.tokenEnd = parser.getIndex();
        return node;
    }

    public PostfixExpression parsePostfixExpression(Integer bindingPower, ExpressionNode left) throws UnexpectedTokenException {
        PostfixExpression node = new PostfixExpression();
        node.tokenStart = parser.getIndex();
        node.expression = left;
        node.postfix = parser.consume();

        node.tokenEnd = parser.getIndex();
        return node;
    }

    public IdentifierExpression parseIdentifierExpression() throws UnexpectedTokenException {
        return identifierMemberParser.parseIdentifierExpression();
    }

    public IdentifierExpression parseIdentifierWithOptionalType() throws UnexpectedTokenException {
        return identifierMemberParser.parseIdentifierWithOptionalType();
    }

    public CodeBlockExpression parseCodeBlockExpression() throws UnexpectedTokenException {
        CodeBlockExpression node = new CodeBlockExpression();
        node.tokenStart = parser.getIndex();

        parser.expect(TokenType.OPEN_CURLY);
        while (parser.peek().type != TokenType.CLOSED_CURLY) { node.children.add(parser.parseStatement()); }
        parser.expect(TokenType.CLOSED_CURLY);

        node.tokenEnd = parser.getIndex();
        return node;
    }

    public IfExpression parseIfExpression() throws UnexpectedTokenException {
        IfExpression node = new IfExpression();
        node.tokenStart = parser.getIndex();

        while (true) {
            parser.expect(TokenType.IF);

            // var clause = parseExpression(BindingPower.LOWEST.ordinal());
            var clause = parseConditional(BindingPower.LOWEST.ordinal());
            node.conditions.add(clause);
            node.blocks.add(parseCodeBlockExpression());

            /* stop parsing if there is not another `else` */
            Token token = parser.peek();
            if (token.type != TokenType.ELSE) { break; }
            parser.consume();


            /* if this is an `else` block and not an `else if` block, 
                parse the final block expression of the if expression */
            token = parser.peek();
            if (token.type != TokenType.IF) { 
                node.blocks.add(parseCodeBlockExpression());
                break;
            }
        }

        node.tokenEnd = parser.getIndex();
        return node;
    }

    /*
     * Two possible versions of using open parentheses:
     * 1. (a, b) -> {};
     * 2. (a + b);
     * 
     * Check for the first case then proceed to the second case as fallback.
     */
    public ExpressionNode handleAmbiguousOpenParentheses() throws UnexpectedTokenException {
        int beforeParsingParamsIndex = parser.getIndex();

        try { return parseFunctionExpression(); } 
        catch (UnexpectedTokenException exception) {
            if (!exception.recoverableFrom(ErrorRecovery.AmbiguousOpenParentheses)) { throw exception; }
            parser.setIndex(beforeParsingParamsIndex);
            return parseGroupedExpression();
        }
    }


    public FunctionExpression parseFunctionExpression() throws UnexpectedTokenException {
        FunctionExpression node = new FunctionExpression();
        node.tokenStart = parser.getIndex();
        node.params = parseFunctionParamsDefinition();
        try {
            parser.expect(TokenType.ARROW);
        } catch (UnexpectedTokenException exception) {
            throw exception.withRecoveryStrategy(ErrorRecovery.AmbiguousOpenParentheses);
        }

        if (parser.peek().type != TokenType.OPEN_CURLY) {
            node.annotatedReturnType = typeAnnotationParser.parseTypeSignature(BindingPower.LOWEST.ordinal());
        }

        node.block = parseCodeBlockExpression();
        node.tokenEnd = parser.getIndex();
        return node;
    }


    /** 
     * Function parameters must all be identifiers, followed by an optional type
     */
    public List<IdentifierExpression> parseFunctionParamsDefinition() throws UnexpectedTokenException {
        try {
            return parseTupleExpression(() -> identifierMemberParser.parseIdentifierWithOptionalType());
        } catch (UnexpectedTokenException exception) {
            throw exception.withRecoveryStrategy(ErrorRecovery.AmbiguousOpenParentheses);
        }
    }
    
    /**
     * Parses unofficial tuple expression, tuples are not real expressions because of possible ambiguity 
     * 
     * Account for all possible parameter definitions:
     * <ol>
     * <li> () </li>
     * <li> (a) </li>
     * <li> (a, b, c) </li>
     * </ol>
     * else throw an error
     */
    public <T extends ExpressionNode> List<T> parseTupleExpression(NodeSupplier<T> supplier) throws UnexpectedTokenException {
        List<T> expressions = new ArrayList<>();

        parser.expect(TokenType.OPEN_PAREN);

        switch (parser.peek().type) {
            case CLOSED_PAREN: { parser.consume(); return expressions; }
            default: { expressions.add(supplier.get()); }
        }

        while (true) {
            Token token = parser.expect(TokenType.CLOSED_PAREN, TokenType.COMMA);
            switch (token.type) {
                case CLOSED_PAREN: { return expressions; }
                case COMMA: { expressions.add(supplier.get()); break; }
                default: { throw new RuntimeException("Unreachable state, expected token types should have been exhausted."); }
            }
        }
    }

    public FunctionCallExpression parseFunctionCallExpression(Integer bindingPower, ExpressionNode left) throws UnexpectedTokenException {
        FunctionCallExpression node = new FunctionCallExpression();
        node.tokenStart = left.tokenStart;
        node.functionIdentifier = left;
        node.params = parseFunctionCallParams();
        node.tokenEnd = parser.getIndex();
        return node;
    }

    public List<ExpressionNode> parseFunctionCallParams() throws UnexpectedTokenException {
        return parseTupleExpression(() -> parseExpression(BindingPower.LOWEST.ordinal()));
    }

    public GroupedExpression parseGroupedExpression() throws UnexpectedTokenException {
        GroupedExpression node = new GroupedExpression();
        node.tokenStart = parser.getIndex();
        parser.expect(TokenType.OPEN_PAREN);
        node.expression = parseExpression(BindingPower.LOWEST.ordinal());
        parser.expect(TokenType.CLOSED_PAREN);
        node.tokenEnd = parser.getIndex();
        return node;
    }

    public LiteralExpression parseLiteralExpression() {
        LiteralExpression node = new LiteralExpression();
        node.tokenStart = parser.getIndex();
        node.value = parser.consume();
        node.tokenEnd = parser.getIndex();
        return node;
    }

    public AssignmentExpression parseAssignmentExpression(Integer bindingPower, ExpressionNode left) throws UnexpectedTokenException {
        AssignmentExpression node = new AssignmentExpression();
        node.tokenStart = left.tokenStart;

        node.operator = parser.expect(TokenType.ASSIGNMENT);

        ExpressionNode right = parseExpression(bindingPower);
        node.left = left;
        node.right = right;

        node.tokenEnd = parser.getIndex();
        return node;
    }
}