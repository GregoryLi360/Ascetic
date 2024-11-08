package parser;

import java.util.List;

import errors.parser.UnexpectedTokenException;
import lexer.tokens.*;
import parser.ast.*;
import parser.partial_parsers.expressions.ExpressionParser;
import parser.partial_parsers.statements.StatementParser;

public class Parser {
    private int index = 0;
    private List<Token> tokens;

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

    public ProgramNode build() throws UnexpectedTokenException {
        ProgramNode root = new ProgramNode();
        root.tokenStart = 0;
        root.tokenEnd = tokens.size();
        
        index = 0;
        for (Token nextToken = peek(); nextToken.type != TokenType.END; nextToken = peek()) {
            StatementNode nextStatement = statementParser.parseStatement();
            root.children.add(nextStatement);
        }

        return root;
    }
}