package at.aau.serg.utils;

import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.resolution.types.ResolvedType;
import sootup.core.signatures.PackageName;
import sootup.core.types.*;
import sootup.java.core.types.JavaClassType;

import java.util.Objects;

public class TypeAdapter {
    private Type sootType = null;
    private com.github.javaparser.ast.type.Type jParserType = null;

    public TypeAdapter(Type sootType) {
        this.sootType = sootType;
        jParserType = fromSootToJP(sootType);
    }

    public TypeAdapter(ResolvedType rt) {
        sootType = fromResolvedTypeToSootType(rt);
        jParserType = fromSootToJP(sootType);
    }

    public TypeAdapter(LiteralExpr literalExpr) {
        this.sootType = fromLiteralExprToSootType(literalExpr);
        this.jParserType = fromSootToJP(sootType);
    }

    private Type fromLiteralExprToSootType(LiteralExpr literalExpr) {
        if (literalExpr instanceof BooleanLiteralExpr) {
            return PrimitiveType.getBoolean();
        } else if(literalExpr instanceof NullLiteralExpr) {
            return NullType.getInstance();
        } else {
            if (literalExpr instanceof CharLiteralExpr) {
                return PrimitiveType.getChar();
            } else if(literalExpr instanceof DoubleLiteralExpr) {
                return PrimitiveType.getDouble();
            } else if (literalExpr instanceof IntegerLiteralExpr) {
                return PrimitiveType.getInt();
            } else if (literalExpr instanceof LongLiteralExpr) {
                return PrimitiveType.getLong();
            } else if (literalExpr instanceof StringLiteralExpr || literalExpr instanceof TextBlockLiteralExpr) {
                return new JavaClassType("String", new PackageName("java.lang"));
            }
        }

        throw new IllegalArgumentException("Unsupported literalExpr: " + literalExpr);
    }

    private Type fromResolvedTypeToSootType(ResolvedType rt) {
        if(rt.isArray()) {
            return ArrayType.createArrayType(fromResolvedTypeToSootType(rt.asArrayType().getComponentType()), rt.arrayLevel());
        } else if (rt.isPrimitive()) {
            switch (rt.asPrimitive()) {
                case BYTE:
                    return PrimitiveType.getByte();
                case SHORT:
                    return PrimitiveType.getShort();
                case CHAR:
                    return PrimitiveType.getChar();
                case INT:
                    return PrimitiveType.getInt();
                case LONG:
                    return PrimitiveType.getLong();
                case BOOLEAN:
                    return PrimitiveType.getBoolean();
                case FLOAT:
                    return PrimitiveType.getFloat();
                case DOUBLE:
                    return PrimitiveType.getDouble();
            }
        } else if (rt.isNull()) {
            return NullType.getInstance();
        } else if (rt.isVoid()) {
            return VoidType.getInstance();
        } else if (rt.isReferenceType()) {
            String fullyQualifiedName = rt.asReferenceType().getQualifiedName();
            String className = fullyQualifiedName.substring(fullyQualifiedName.lastIndexOf('.')+1);
            String packageName = fullyQualifiedName.substring(0, fullyQualifiedName.lastIndexOf('.'));
            return new JavaClassType(className, new PackageName(packageName));
        }

        throw new IllegalArgumentException("Unsupported type: " + rt);
    }

    private com.github.javaparser.ast.type.Type fromSootToJP(Type type) {
        if(type instanceof ArrayType) {
            return new com.github.javaparser.ast.type.ArrayType(new TypeAdapter(((ArrayType) type).getElementType()).asJavaParserType());
        } else if (type instanceof ClassType) {
            return new ClassOrInterfaceType(null, ((ClassType) type).getFullyQualifiedName());
        } else if (type instanceof NullType) {
            return new ClassOrInterfaceType();
        } else if (type instanceof PrimitiveType) {
            switch (type.toString()) {
                case "boolean":
                    return com.github.javaparser.ast.type.PrimitiveType.booleanType();
                case "byte":
                    return com.github.javaparser.ast.type.PrimitiveType.byteType();
                case "char":
                    return com.github.javaparser.ast.type.PrimitiveType.charType();
                case "short":
                    return com.github.javaparser.ast.type.PrimitiveType.shortType();
                case "int":
                    return com.github.javaparser.ast.type.PrimitiveType.intType();
                case "long":
                    return com.github.javaparser.ast.type.PrimitiveType.longType();
                case "float":
                    return com.github.javaparser.ast.type.PrimitiveType.floatType();
                case "double":
                    return com.github.javaparser.ast.type.PrimitiveType.doubleType();
            }
        } else if (type instanceof VoidType) {
            return new com.github.javaparser.ast.type.VoidType();
        }

        throw new IllegalArgumentException("Unsupported type: " + type);
    }

    public Type asSootType() {
        return sootType;
    }

    public com.github.javaparser.ast.type.Type asJavaParserType() {
        return jParserType;
    }

    public Expression getDefaultJavaParserExpression() {
        switch (sootType.toString()) {
            case "boolean":
                return new BooleanLiteralExpr(false);
            case "byte":
            case "char":
            case "short":
            case "int":
            case "long":
            case "float":
                return new IntegerLiteralExpr("0");
            case "double":
                return new DoubleLiteralExpr(0);
            default:
                return new NullLiteralExpr();
        }
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        TypeAdapter that = (TypeAdapter) object;
        return Objects.equals(sootType, that.sootType) && Objects.equals(jParserType, that.jParserType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sootType, jParserType);
    }
}
