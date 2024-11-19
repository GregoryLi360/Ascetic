package parser.partial_parsers.expressions.pratt;

import errors.parser.UnexpectedTokenException;

@FunctionalInterface
public interface NullDenotation<ReturnType> {
    ReturnType invoke() throws UnexpectedTokenException;
}