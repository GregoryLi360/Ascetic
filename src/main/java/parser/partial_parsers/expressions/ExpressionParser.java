package parser.partial_parsers.expressions;

import parser.Parser;
import parser.ast.*;
import parser.ast.exprs.*;

public class ExpressionParser {
    private Parser parser;

    public ExpressionParser(Parser parser) {
        this.parser = parser;
    }

    public ExpressionNode parseExpression()  {
        throw new UnsupportedOperationException();
    }

    public ExpressionNode parseConditional() {
        throw new UnsupportedOperationException();
    }

    public PrefixExpression parsePrefixExpression()  {
        throw new UnsupportedOperationException();
    }

    public OperationExpression parseInfixExpression()  {
        throw new UnsupportedOperationException();
    }

    public PostfixExpression parsePostfixExpression()  {
        throw new UnsupportedOperationException();
    }

    public IdentifierExpression parseIdentifierExpression()  {
        throw new UnsupportedOperationException();
    }

    public IdentifierExpression parseIdentifierWithOptionalType()  {
        throw new UnsupportedOperationException();
    }

    public CodeBlockExpression parseCodeBlockExpression()  {
        throw new UnsupportedOperationException();
    }

    public IfExpression parseIfExpression()  {
        throw new UnsupportedOperationException();
    }

    public FunctionExpression parseFunctionExpression()  {
        throw new UnsupportedOperationException();
    }

    public FunctionCallExpression parseFunctionCallExpression()  {
        throw new UnsupportedOperationException();
    }

    public GroupedExpression parseGroupedExpression()  {
        throw new UnsupportedOperationException();
    }

    public LiteralExpression parseLiteralExpression() {
        throw new UnsupportedOperationException();
    }

    public AssignmentExpression parseAssignmentExpression()  {
        throw new UnsupportedOperationException();
    }
}