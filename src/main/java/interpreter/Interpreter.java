package interpreter;

import errors.analyzer.DuplicateIdentifierException;
import errors.analyzer.UndeclaredIdentifierException;
import interpreter.builtins.Builtin;
import lexer.tokens.Token;
import table.scopes.ProgramScope;
import table.scopes.Scope;
import table.symbols.compiled.CompiledFunctionSymbol;
import table.symbols.compiled.CompiledSymbol;
import table.symbols.semantic.SemanticFunctionSymbol;
import table.symbols.semantic.SemanticSymbol;

import java.util.ArrayList;

import ast.*;
import ast.exprs.*;
import ast.stmts.*;

public class Interpreter {
    private ProgramNode AST; 
    private ProgramScope programScope;
    public Scope currentScope;

    public Interpreter(ProgramNode AST, ProgramScope programScope) {
        this.AST = AST;
        this.programScope = programScope;
        this.currentScope = programScope;
    }

    public String run() throws UndeclaredIdentifierException, DuplicateIdentifierException {
        for (StatementNode statement: AST.children) {
            Builtin.Any result = interpret(statement);
            if (statement instanceof ReturnStatement 
                || statement instanceof GuardStatement && result != null) {
                return result.toString();
            }
        }

        return "";
    }

    public Builtin.Any interpret(StatementNode statement) throws UndeclaredIdentifierException, DuplicateIdentifierException {
        if (statement instanceof CommentStatement) {
            return Builtin.Void.value();
        }

        if (statement instanceof ExpressionStatement expr) {
            return evaluate(expr.expression);
        }

        if (statement instanceof ReturnStatement ret) {
            return evaluate(ret.returnExpression);
        }

        if (statement instanceof GuardStatement guard) {
            var clauseResult = evaluate(guard.clause);
            if (!(clauseResult instanceof Builtin.Bool clause)) {
                throw new RuntimeException("Guard statement clause does not evaluate to a boolean: " + guard);
            }
    
            return clause == Builtin.Bool.TRUE ? evaluate(guard.body) : null;
        }

        if (statement instanceof VariableDeclaration varDecl) {
            var value = evaluate(varDecl.assignmentExpression.right);
            currentScope.declareAndDefineValue(varDecl.identifier, value);
            return value;
        }

        throw new UnsupportedOperationException();
    }

    public Builtin.Any evaluate(ExpressionNode expression) throws UndeclaredIdentifierException, DuplicateIdentifierException {
        if (expression instanceof LiteralExpression literal) {
            return evaluateLiteral(literal);
        }

        if (expression instanceof IdentifierExpression identifier) {
            CompiledSymbol symbol = (CompiledSymbol) currentScope.find(identifier);
            return symbol.value;
        }

        if (expression instanceof OperationExpression operation) {
            return evaluateOperation(operation);
        }

        if (expression instanceof GroupedExpression grouped) {
            return evaluate(grouped.expression);
        }

        if (expression instanceof PrefixExpression prefixed) {
            return evaluatePrefixed(prefixed);
        }

        if (expression instanceof PostfixExpression postfixed) {

        }

        if (expression instanceof CodeBlockExpression codeBlock) {
            currentScope = currentScope.addAnonymousCodeBlock();
            Builtin.Any result = evaluateCodeBlock(codeBlock);
            currentScope = currentScope.parent;
            return result;
        }

        if (expression instanceof IfExpression ifExpression) {
            return evaluateIfExpression(ifExpression);
        }

        if (expression instanceof FunctionExpression function) {
            return evaluateFunction(function);
        }

        if (expression instanceof FunctionCallExpression functionCall) {
            return evaluateFunctionCall(functionCall);
        }

        throw new IllegalStateException("Missing eval for expression: " + expression.getClass());
    }

    private Builtin.Any evaluateLiteral(LiteralExpression literal) {
        switch (literal.value.type) {
            case BOOLEAN_LITERAL: { return Builtin.Bool.of(literal); }
            case INTEGER_LITERAL: { return Builtin.Int.of(literal); }
            case FLOAT_LITERAL: { return Builtin.Float.of(literal); }
            case STRING_LITERAL: { return Builtin.String.of(literal); } 

            default: { throw new IllegalStateException("Missing eval for literal type: " + literal.value.type); }
        }
    }

    private Builtin.Any evaluatePrefixed(PrefixExpression prefixed) throws UndeclaredIdentifierException, DuplicateIdentifierException {
        var result = evaluate(prefixed.expression);

        switch (prefixed.prefix.type) {
            case BANG: {
                if (result instanceof Builtin.Bool bool) { return bool.negate(); }
                throw new RuntimeException("Cannot evaluate: " + prefixed.prefix.type + result);
            }

            case MINUS: {
                if (result instanceof Builtin.Int i) { return i.negate(); }
                if (result instanceof Builtin.Float f) { return f.negate(); }
                throw new RuntimeException("Cannot evaluate: " + prefixed.prefix.type + result);
            }

            default: { throw new IllegalStateException("Missing eval for prefix type: " + prefixed.prefix.type); }
        }
    }

    private Builtin.Any evaluateOperation(OperationExpression operation) throws UndeclaredIdentifierException, DuplicateIdentifierException {
        var left = evaluate(operation.left);
        var right = evaluate(operation.right);

        if (!left.getClass().equals(right.getClass())) {
            throw new RuntimeException("Cannot evaluate: " + left.getClass() + " " + operation.operator + " " + right.getClass());
        }

        switch (operation.operator.type) {
            case PLUS: {
                if (left instanceof Builtin.Int leftInt) { return leftInt.plus((Builtin.Int) right); } 
                if (left instanceof Builtin.Float leftFloat) { return leftFloat.plus((Builtin.Float) right); }
                if (left instanceof Builtin.String leftString) { return leftString.concat((Builtin.String) right); }
                break;
            } 
            
            case MINUS: {
                if (left instanceof Builtin.Int leftInt) { return leftInt.minus((Builtin.Int) right); }
                if (left instanceof Builtin.Float leftFloat) { return leftFloat.minus((Builtin.Float) right); }
                break;
            }

            case MULTIPLY: {
                if (left instanceof Builtin.Int leftInt) { return leftInt.multiply((Builtin.Int) right); }
                if (left instanceof Builtin.Float leftFloat) { return leftFloat.multiply((Builtin.Float) right); }
                break;
            }
            
            case DIVIDE: {
                if (left instanceof Builtin.Int leftInt) { return leftInt.divide((Builtin.Int) right); }
                if (left instanceof Builtin.Float leftFloat) { return leftFloat.divide((Builtin.Float) right); }
                break;
            }
            
            case MODULO: {
                if (left instanceof Builtin.Int leftInt) { return leftInt.modulo((Builtin.Int) right); }
                break;
            }
            
            case LESS_THAN: {
                if (left instanceof Builtin.Int leftInt) { return leftInt.lessThan((Builtin.Int) right); }
                if (left instanceof Builtin.Float leftFloat) { return leftFloat.lessThan((Builtin.Float) right); }
                break;
            }
            
            case LESS_THAN_OR_EQUALS_TO: {
                if (left instanceof Builtin.Int leftInt) { return leftInt.lessThanOrEqualsTo((Builtin.Int) right); }
                if (left instanceof Builtin.Float leftFloat) { return leftFloat.lessThanOrEqualsTo((Builtin.Float) right); }
                break;
            }
            
            case GREATER_THAN: {
                if (left instanceof Builtin.Int leftInt) { return leftInt.greaterThan((Builtin.Int) right); }
                if (left instanceof Builtin.Float leftFloat) { return leftFloat.greaterThan((Builtin.Float) right); }
                break;
            }
            
            case GREATER_THAN_OR_EQUALS_TO: {
                if (left instanceof Builtin.Int leftInt) { return leftInt.greaterThanOrEqualsTo((Builtin.Int) right); }
                if (left instanceof Builtin.Float leftFloat) { return leftFloat.greaterThanOrEqualsTo((Builtin.Float) right); }
                break;
            }

            case AND: {
                if (left instanceof Builtin.Bool leftBool) { return leftBool.and((Builtin.Bool) right); }
                break;
            }

            case OR: {
                if (left instanceof Builtin.Bool leftBool) { return leftBool.or((Builtin.Bool) right); }
                break;
            }

            case EQUALS_TO: {
                if (left instanceof Builtin.Bool leftBool) { return leftBool.equalsTo((Builtin.Bool) right); }
                if (left instanceof Builtin.Int leftInt) { return leftInt.equalsTo((Builtin.Int) right); }
                if (left instanceof Builtin.Float leftFloat) { return leftFloat.equalsTo((Builtin.Float) right); }
                if (left instanceof Builtin.String leftString) { return leftString.equalsTo((Builtin.String) right); }
                break;
            }

            case NOT_EQUALS_TO: {
                if (left instanceof Builtin.Bool leftBool) { return leftBool.notEqualsTo((Builtin.Bool) right); }
                if (left instanceof Builtin.Int leftInt) { return leftInt.notEqualsTo((Builtin.Int) right); }
                if (left instanceof Builtin.Float leftFloat) { return leftFloat.notEqualsTo((Builtin.Float) right); }
                if (left instanceof Builtin.String leftString) { return leftString.notEqualsTo((Builtin.String) right); }
                break;
            }

            default: { throw new IllegalStateException("Missing eval for operator type: " + operation.operator.type); }
        }

        throw new UnsupportedOperationException("Operator " + operation.operator.type + " not defined on " + left.getClass());
    }

    /** 
     * NOTE: Assumes that the caller will handle scoping, create new scope on enter, return to old scope on exit.
     */
    private Builtin.Any evaluateCodeBlock(CodeBlockExpression codeBlock) throws UndeclaredIdentifierException, DuplicateIdentifierException {
        for (StatementNode statement: codeBlock.children) {
            Builtin.Any result = interpret(statement);
            
            if (statement instanceof ReturnStatement 
                || statement instanceof GuardStatement && result != null) {
                return result;
            }
        }

        return Builtin.Void.value();
    }

    private Builtin.Any evaluateIfExpression(IfExpression ifExpression) throws UndeclaredIdentifierException, DuplicateIdentifierException {
        int i;
        for (i = 0; i < ifExpression.conditions.size(); i++) {
            var clauseResult = evaluate(ifExpression.conditions.get(i));
            if (!(clauseResult instanceof Builtin.Bool clause)) {
                throw new RuntimeException("If expression clause does not evaluate to a boolean: " + ifExpression.conditions.get(i));
            }

            if (clause == Builtin.Bool.TRUE) {
                return evaluate(ifExpression.blocks.get(i));
            }
        }

        if (i < ifExpression.blocks.size()) {
            return evaluate(ifExpression.blocks.get(i));
        }

        return Builtin.Void.value();
    }

    private Builtin.Any evaluateFunction(FunctionExpression function) throws UndeclaredIdentifierException, DuplicateIdentifierException {
        return Builtin.Function.of(function, this.currentScope.addAnonymousCodeBlock());
    }

    private Builtin.Any evaluateFunctionCall(FunctionCallExpression functionCall) throws UndeclaredIdentifierException, DuplicateIdentifierException {
        Builtin.Any value = evaluate(functionCall.functionIdentifier);
        
        if (!(value instanceof Builtin.Function function)) {
            throw new RuntimeException(functionCall.functionIdentifier + " is not a function.");
        }

        if (function.arity() != functionCall.params.size()) {
            throw new RuntimeException(functionCall.functionIdentifier + " expects " + function.arity() + " arguments. " + 
                functionCall.params.size() + " arguments were provided at call site: " + functionCall);
        }

        var args = new ArrayList<Builtin.Any>();
        for (ExpressionNode param: functionCall.params) {
            args.add(evaluate(param));
        }

        Scope previousScope = this.currentScope;

        if (functionCall.functionIdentifier instanceof IdentifierExpression identifier) {
            CompiledFunctionSymbol symbol = (CompiledFunctionSymbol) currentScope.find(identifier);
            this.currentScope = symbol.effectiveScope;
        } else {
            this.currentScope = function.scope;
        }

        for (int i = 0; i < args.size(); i++) {
            this.currentScope.declareAndDefineValue(function.params.get(i), args.get(i));
        }
        
        Builtin.Any result = evaluateCodeBlock(function.block);
        this.currentScope = previousScope;

        return result;
    }
}
