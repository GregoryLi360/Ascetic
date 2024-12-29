package parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

import ast.ProgramNode;
import ast.StatementNode;
import errors.parser.UnexpectedTokenException;
import lexer.tokens.Token;
import lexer.tokens.TokenType;
import parser.partial_parsers.expressions.ExpressionParser;
import parser.partial_parsers.expressions.pratt.BindingPower;
import parser.partial_parsers.expressions.pratt.LiteralExpressionLookups;
import parser.partial_parsers.statements.StatementParser;

public class Parser {
    private int index = 0;
    private List<Token> tokens;
    private List<Error> errors = new ArrayList<>();

    private StatementParser statementParser;
    private ExpressionParser expressionParser;

    public Parser(List<Token> tokens) {
        this.tokens = tokens;

        this.expressionParser = new ExpressionParser(this);
        this.statementParser = new StatementParser(this, expressionParser);
    }

    public int getIndex() { return index; }
    public void setIndex(int index) { this.index = index; }
    public List<Token> getTokens() { return tokens; }
    public List<Error> getErrors() { return errors; }
    public void curtailErrors(int index) { errors = errors.subList(0, index); }

    public boolean isEOF() { return index >= tokens.size(); }

    public Token peek() {
        if (isEOF()) {
            Token token = new Token();
            token.type = TokenType.END;
            return token;
        }
        return tokens.get(index);
    }

    public Token consume() { return tokens.get(index++); }
    
    public Token expect(TokenType... tokenTypes) throws UnexpectedTokenException {
        Token nextToken = peek();
        if (nextToken.is(tokenTypes)) { return consume(); }
        throw new UnexpectedTokenException(nextToken, tokenTypes);
    }

    public Token expectWithRecovery(TokenType expectedTokenType, UnexpectedTokenException.ErrorRecovery recoveryStrategy) throws UnexpectedTokenException {
        return expectWithRecovery(List.of(expectedTokenType), recoveryStrategy);
    }

    public Token expectWithRecovery(List<TokenType> expectedTokenTypes, UnexpectedTokenException.ErrorRecovery recoveryStrategy) throws UnexpectedTokenException {
        Token nextToken = peek();
        for (TokenType type: expectedTokenTypes) {
            if (nextToken.is(type)) { return consume(); }
        }
        
        throw new UnexpectedTokenException(nextToken, expectedTokenTypes,recoveryStrategy);
    }

    public StatementNode parseStatement() throws UnexpectedTokenException {
        return statementParser.parseStatement();
    }

    /* TODO: handle syntax errors 
     * 1. Insert/imagine the expected token
     * 2. Remove until finding expected token or semicolon
     * 3. idk bro
    */
    public ProgramNode build() throws UnexpectedTokenException {
        ProgramNode root = new ProgramNode();
        root.tokenStart = 0;
        root.tokenEnd = tokens.size();
        
        index = 0;
        for (Token nextToken = peek(); nextToken.type != TokenType.END; nextToken = peek()) {
            StatementNode nextStatement = statementParser.parseStatement();
            if (nextStatement == null) break;
            root.children.add(nextStatement);
        }

        return root;
    }
}