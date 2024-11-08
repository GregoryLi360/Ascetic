package errors.parser;

import java.util.Arrays;
import java.util.List;

import lexer.tokens.Token;
import lexer.tokens.TokenType;

public class UnexpectedTokenException extends Exception {
    private List<TokenType> expectedTypes;
    private Token received;

    private ErrorRecovery recoveryStrategy = null;

    public UnexpectedTokenException(Token received, TokenType expectedType) {
        this(received, List.of(expectedType));
    }

    public UnexpectedTokenException(Token received, TokenType[] expectedTypes) {
        this(received, Arrays.asList(expectedTypes));
    }

    public UnexpectedTokenException(Token received, List<TokenType> expectedTypes) {
        super("Unexpected token: " + received + ". Expected token types: " + expectedTypes);
        this.expectedTypes = expectedTypes;
        this.received = received;
    }

    public UnexpectedTokenException(Token received, List<TokenType> expectedTypes, ErrorRecovery recoveryStrategy) {
        super("Unexpected token: " + received + ". Expected token types: " + expectedTypes);
        this.expectedTypes = expectedTypes;
        this.received = received;
        this.recoveryStrategy = recoveryStrategy;
    }

    public List<TokenType> getExpectedTypes() { return expectedTypes; }
    public Token getToken() { return received; }

    public UnexpectedTokenException withRecoveryStrategy(ErrorRecovery recoveryStrategy) {
        this.recoveryStrategy = recoveryStrategy; 
        return this;
    }

    public boolean recoverableFrom(ErrorRecovery recoveryStrategy) {
        return this.recoveryStrategy == recoveryStrategy;
    }

    public boolean isUnexpectedEOF() {
        return received.type == TokenType.END;
    }

    public static enum ErrorRecovery {
        AmbiguousOpenParentheses;
    }
}