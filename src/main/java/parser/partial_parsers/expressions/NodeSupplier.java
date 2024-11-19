package parser.partial_parsers.expressions;

import errors.parser.UnexpectedTokenException;

@FunctionalInterface
public interface NodeSupplier<ReturnType> {
    ReturnType get() throws UnexpectedTokenException;
}
