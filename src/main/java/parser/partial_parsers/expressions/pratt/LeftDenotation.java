package parser.partial_parsers.expressions.pratt;

import errors.parser.UnexpectedTokenException;

@FunctionalInterface
public interface LeftDenotation<Arg1, Arg2, ReturnType> {
    ReturnType invoke(Arg1 arg1, Arg2 arg2) throws UnexpectedTokenException;
}