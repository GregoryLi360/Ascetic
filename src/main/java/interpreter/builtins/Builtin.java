package interpreter.builtins;

import java.util.List;

import ast.exprs.*;
import interpreter.Interpreter;
import table.scopes.Scope;

public class Builtin {
    public static abstract class Any {}

    public static class Void extends Any {
        private static final Void VOID = new Void();
        private Void() {}

        public static Void value() {
            return VOID;
        }

        @Override
        public java.lang.String toString() {
            return "Void()";
        }
    }

    public static class Bool extends Any {
        public static final Bool TRUE = new Bool(true);
        public static final Bool FALSE = new Bool(false);

        public final Boolean value;
        private Bool(Boolean value) {
            this.value = value;
        }

        public static Bool of(LiteralExpression literal) {
            var text = java.lang.String.valueOf(literal.value.text);
            var value = Boolean.parseBoolean(text);
            return value ? TRUE : FALSE;
        }

        public Bool negate() { return value ? FALSE : TRUE; }
        public Bool and(Bool other) { return this.value && other.value ? TRUE : FALSE; }
        public Bool or(Bool other) { return this.value || other.value ? TRUE : FALSE; }

        public Bool equalsTo(Bool other) { return this.value == other.value ? Bool.TRUE : Bool.FALSE; }
        public Bool notEqualsTo(Bool other) { return this.value != other.value ? Bool.TRUE : Bool.FALSE; }        

        @Override
        public java.lang.String toString() {
            return "Bool(" + value + ")";
        }
    }
    
    public static class Int extends Any {
        public final Integer value;
        private Int(Integer value) {
            this.value = value;
        }

        public static Int of(LiteralExpression literal) {
            var text = java.lang.String.valueOf(literal.value.text);
            var value = Integer.parseInt(text);
            return new Int(value);
        }

        public Int negate() { return new Int(-this.value); }

        public Bool lessThan(Int other) { return this.value < other.value ? Bool.TRUE : Bool.FALSE; }
        public Bool lessThanOrEqualsTo(Int other) { return this.value <= other.value ? Bool.TRUE : Bool.FALSE; }
        public Bool greaterThan(Int other) { return this.value > other.value ? Bool.TRUE : Bool.FALSE; }
        public Bool greaterThanOrEqualsTo(Int other) { return this.value >= other.value ? Bool.TRUE : Bool.FALSE; }
        public Bool equalsTo(Int other) { return this.value == other.value ? Bool.TRUE : Bool.FALSE; }
        public Bool notEqualsTo(Int other) { return this.value != other.value ? Bool.TRUE : Bool.FALSE; }

        public Int plus(Int other) { return new Int(this.value + other.value); }
        public Int minus(Int other) { return new Int(this.value - other.value); }
        public Int multiply(Int other) { return new Int(this.value * other.value); }
        public Int divide(Int other) { return new Int(this.value / other.value); }
        public Int modulo(Int other) { return new Int(this.value % other.value); }

        public boolean equals(Builtin.Any other) {
            return other instanceof Builtin.Int otherInt 
                && this.value == otherInt.value;
        }

        @Override
        public java.lang.String toString() {
            return "Int(" + value + ")";
        }
    }

    public static class Float extends Any {
        public final java.lang.Float value;
        private Float(java.lang.Float value) {
            this.value = value;
        }

        public static Float of(LiteralExpression literal) {
            var text = java.lang.String.valueOf(literal.value.text);
            var value = java.lang.Float.parseFloat(text);
            return new Float(value);
        }

        public Float negate() { return new Float(-this.value); }

        public Bool lessThan(Float other) { return this.value < other.value ? Bool.TRUE : Bool.FALSE; }
        public Bool lessThanOrEqualsTo(Float other) { return this.value <= other.value ? Bool.TRUE : Bool.FALSE; }
        public Bool greaterThan(Float other) { return this.value > other.value ? Bool.TRUE : Bool.FALSE; }
        public Bool greaterThanOrEqualsTo(Float other) { return this.value >= other.value ? Bool.TRUE : Bool.FALSE; }
        public Bool equalsTo(Float other) { return this.value == other.value ? Bool.TRUE : Bool.FALSE; }
        public Bool notEqualsTo(Float other) { return this.value != other.value ? Bool.TRUE : Bool.FALSE; }

        public Float plus(Float other) { return new Float(this.value + other.value); }
        public Float minus(Float other) { return new Float(this.value - other.value); }
        public Float multiply(Float other) { return new Float(this.value * other.value); }
        public Float divide(Float other) { return new Float(this.value / other.value); }

        public boolean equals(Builtin.Any other) {
            return other instanceof Builtin.Float otherInt 
                && this.value == otherInt.value;
        }

        @Override
        public java.lang.String toString() {
            return "Float(" + value + ")";
        }
    }

    public static class String extends Any {
        public final java.lang.String value;
        private String(java.lang.String value) {
            this.value = value;
        }

        public static String of(LiteralExpression literal) {
            var text = java.lang.String.valueOf(literal.value.text);

            /* TODO: formatted strings */
            var value = text
                .substring(1, text.length() - 1) /* remove outside quotation marks "" */
                .translateEscapes(); /* translate common escapes like '\n' */

            return new String(value);
        }

        public String concat(String other) { return new String(this.value + other.value); }

        public Bool equalsTo(String other) { return this.value.equals(other.value) ? Bool.TRUE : Bool.FALSE; }
        public Bool notEqualsTo(String other) { return !this.value.equals(other.value) ? Bool.TRUE : Bool.FALSE; }

        public boolean equals(Builtin.Any other) {
            return other instanceof Builtin.String otherInt 
                && this.value.equals(otherInt.value);
        }

        @Override
        public java.lang.String toString() {
            return "String(" + value + ")";
        }
    }

    public static class Function extends Any {
        public Scope scope;
        public List<IdentifierExpression> params;
        public CodeBlockExpression block;

        private Function(List<IdentifierExpression> params, CodeBlockExpression block, Scope scope) {
            this.params = params;
            this.block = block;
            this.scope = scope;
        }

        public int arity() {
            return params.size();
        }

        public static Function of(FunctionExpression function, Scope effectiveScope) {
            return new Function(
                function.params,
                function.block,
                effectiveScope
            );
        }

        @Override
        public java.lang.String toString() {
            return "Function(" + params.stream().map(id -> java.lang.String.valueOf(id.identifier.text)).toList() + " -> ?)";
        }
    }
}