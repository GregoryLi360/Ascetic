package ast.types;

import lexer.tokens.Token;

public class PrimitiveType extends TypeSignature {
    public enum Value {
        Bool, Int, Float, String, Void, Any, Type;

        public static Value fromToken(Token token) {
            switch (token.type) {
                case INTEGER_LITERAL: { return Int; }
                case FLOAT_LITERAL: { return Float; }
                case STRING_LITERAL: { return String; }
                case BOOLEAN_LITERAL: {
                    String booleanLiteral = java.lang.String.valueOf(token.text);
                    switch (booleanLiteral) {
                        case "true", "false": { return Bool; }
                        default: { return null; }
                    }
                }
                case PRIMITIVE_TYPE: {
                    String keyword = java.lang.String.valueOf(token.text);
                    switch (keyword) {
                        case "Bool": { return Bool; }
                        case "Int": { return Int; }
                        case "Float": { return Float; }
                        case "String": { return String; }
                        case "Void": { return Void; }
                        case "Any": { return Any; }
                        case "Type": { return Type;}
                    }
                }

                default: { return null; }
            }
        }
    }

    public Value primitiveType;

    @Override
    public boolean allowsPrefix(Token prefix) {
        switch(prefix.type) {
            case BANG: {
                switch(primitiveType) {
                    case Bool: { return true; }
                    default: { return false; }
                }
            }

            case MINUS: {
                switch(primitiveType) {
                    case Int, Float: { return true; }
                    default: { return false; }
                }
            }

            default: { return false; }                
        }
    }
}