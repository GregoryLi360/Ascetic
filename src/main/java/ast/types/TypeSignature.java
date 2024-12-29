package ast.types;

import java.lang.reflect.Field;
import java.util.Objects;

import ast.JsonPrintable;
import lexer.tokens.Token;

public abstract class TypeSignature extends JsonPrintable {
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