package parser.ast;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import lexer.tokens.Token;

public class ASTNodes {
    public static abstract class AbstractSyntaxTreeNode extends JsonRepresentable {
        public final String nodeType = this.getClass().getSimpleName();
        public int tokenStart, tokenEnd;
        public boolean isErroneous = false;
    }

    public static class ProgramNode extends AbstractSyntaxTreeNode {
        public List<StatementNode> children = new ArrayList<>();
    }

    /* Does not produce value */
    public static abstract class StatementNode extends AbstractSyntaxTreeNode {}
    public static class ExpressionStatement extends StatementNode {
        public ExpressionNode expression;
    }
    public static class ReturnStatement extends StatementNode {
        public ExpressionNode returnExpression;
    }
    public static class VariableDeclaration extends StatementNode {
        public List<Token> modifiers = new ArrayList<>();
        public VariableDeclarationType declarationType;
        public IdentifierExpression identifier;
        public AssignmentExpression assignmentExpression;
    }
    public static class GuardStatement extends StatementNode {
        public TypeSignature actualReturnType;
        public ExpressionNode clause;
        public CodeBlockExpression body;
    }
    public static class CommentStatement extends StatementNode {
        public Token comment;
    }

    /* Produces value */
    public static abstract class ExpressionNode extends AbstractSyntaxTreeNode {
        public TypeSignature typeSignature;
    }

    public static class LiteralExpression extends ExpressionNode {
        public Token value;
    }
    public static class IdentifierExpression extends ExpressionNode {
        public Token identifier;
        public TypeAnnotation typeAnnotation;
    }
    public static class OperationExpression extends ExpressionNode {
        public Token operator;
        public ExpressionNode left;
        public ExpressionNode right;
    }
    public static class AssignmentExpression extends OperationExpression {}
    public static class GroupedExpression extends ExpressionNode {
        public ExpressionNode expression;
    }
    public static class PrefixExpression extends ExpressionNode {
        public Token prefix;
        public ExpressionNode expression;
    }
    public static class PostfixExpression extends ExpressionNode {
        public Token postfix;
        public ExpressionNode expression;
    }
    public static class CodeBlockExpression extends ExpressionNode {
        public List<StatementNode> children = new ArrayList<>();
    }
    public static class IfExpression extends ExpressionNode {
        public List<ExpressionNode> conditions = new ArrayList<>();
        public List<CodeBlockExpression> blocks = new ArrayList<>();
    }
    public static class FunctionExpression extends ExpressionNode {
        public List<IdentifierExpression> params = new ArrayList<>();
        public TypeSignature annotatedReturnType;
        public TypeSignature returnType;
        public CodeBlockExpression block;
    }
    public static class FunctionCallExpression extends ExpressionNode {
        public ExpressionNode functionIdentifier;
        public List<ExpressionNode> params = new ArrayList<>();
    }
    public static class TypeAnnotation extends AbstractSyntaxTreeNode {
        public TypeSignature typeSignature;
    }

    public static abstract class TypeSignature extends JsonRepresentable {
        public final String nodeType = this.getClass().getSimpleName();
        public boolean optional = false;

        public boolean allowsPrefix(Token prefix) { return false; }
        public boolean allowsPostfix(Token postfix) { 
            switch (postfix.type) {
                case BANG: { return optional; }
                default: { return false; }
            }
        }

        @Override
        public boolean equals(Object other) {
            if (!(other instanceof TypeSignature)) { return false; }

            Class<?> thisClass = this.getClass();
            Class<?> otherClass = other.getClass();
            if (!thisClass.getSimpleName().equals(otherClass.getSimpleName())) { return false; }
            
            while (thisClass != TypeSignature.class && otherClass != TypeSignature.class) {
                Field[] thisFields = thisClass.getDeclaredFields();
                Field[] otherFields = otherClass.getDeclaredFields();
                
                if (thisFields.length != otherFields.length) { return false; }
                
                for (int i = 0; i < thisFields.length; i++) {
                    if (!thisFields[i].equals(otherFields[i])) { return false; }
                    
                    thisFields[i].setAccessible(true);
                    otherFields[i].setAccessible(true);
                    
                    try {
                        if (!Objects.equals(thisFields[i].get(this), otherFields[i].get(other))) { return false; }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();;
                    }
                }

                thisClass = thisClass.getSuperclass();
                otherClass = otherClass.getSuperclass();
            }

            return true;
        }
    }

    public static class PrimitiveType extends TypeSignature {
        public Primitive primitiveType;

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

    /* only used for user-declared types (eg. structs, enums, type aliases) */
    public static class IdentifierType extends TypeSignature {
        public String name;
        public static IdentifierType fromToken(Token token) {
            var typeSignature = new IdentifierType();
            typeSignature.name = String.valueOf(token.text);
            return typeSignature;
        }
    }
    public static class MemberType extends TypeSignature { 
        public TypeSignature context;
        public IdentifierType member;
    }
    public static class FunctionType extends TypeSignature {
        public List<TypeSignature> paramTypes = new ArrayList<>();
        public TypeSignature returnType;
    }

    public enum Primitive {
        Bool, Int, Float, String, Void, Any, Type;

        public static Primitive fromToken(Token token) {
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

    public enum VariableDeclarationType {
        let,
        var;
    
        public static VariableDeclarationType fromToken(Token token) {
            switch (String.valueOf(token.text)) {
                case "let": { return let; }
                case "var": { return var; }
                default: { return null; }
            }
        }
    }

    // public static class AnyType extends TypeSignature {}

    public static class UnknownType extends TypeSignature {}
}

abstract class JsonRepresentable {
    public String toString(int depth) {
        String tab = String.format("%" + (2 * (depth + 1)) + "s", " ");
        String tabLessDepth = depth == 0 ? "" : String.format("%" + (2 * depth) + "s", " ");
        Class<?> classMirror = this.getClass();

        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append(classMirror.getSimpleName()).append(": {\n");

        while (classMirror != null) {
            Field[] fields = classMirror.getDeclaredFields();

            for (Field field : fields) {
                field.setAccessible(true);
                try {
                    Object value = field.get(this);
                    jsonBuilder.append(tab).append(field.getName()).append(": ");
                    if (value == null) {
                        jsonBuilder.append("null");
                    } else {
                        if (value instanceof Iterable<?>) {
                            jsonBuilder.append("[");
                            Iterable<?> iterable = (Iterable<?>) value;
                            boolean nonEmpty = false;
                            for (Object element : iterable) {
                                if (element instanceof JsonRepresentable) {
                                    jsonBuilder.append(((JsonRepresentable) element).toString(depth + 1));
                                } else if (element == null){
                                    jsonBuilder.append("null");
                                } else {
                                    jsonBuilder.append(element.toString());
                                }
                                jsonBuilder.append(", ");
                                nonEmpty = true;
                            }

                            if (nonEmpty) jsonBuilder.delete(jsonBuilder.length() - 2, jsonBuilder.length());
                            jsonBuilder.append("]");
                        } else if (value instanceof JsonRepresentable) {
                            jsonBuilder.append(((JsonRepresentable) value).toString(depth + 1));
                        } else {
                            jsonBuilder.append(value.toString());
                        }
                    }

                    jsonBuilder.append("\n");
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }

            // Move to the superclass for inherited fields
            classMirror = classMirror.getSuperclass();
        }

        jsonBuilder.append(tabLessDepth).append("}");
        return jsonBuilder.toString();
    }

    @Override
    public String toString() {
        return toString(0);
    }
}