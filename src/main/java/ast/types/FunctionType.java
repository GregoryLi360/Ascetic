package ast.types;

import java.util.ArrayList;
import java.util.List;

public class FunctionType extends TypeSignature {
    public List<TypeSignature> paramTypes = new ArrayList<>();
    public TypeSignature returnType;
}