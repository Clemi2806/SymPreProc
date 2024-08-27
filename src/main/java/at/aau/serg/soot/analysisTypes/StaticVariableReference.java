package at.aau.serg.soot.analysisTypes;

import sootup.core.types.Type;

import java.util.Objects;

public class StaticVariableReference extends AnalysisResult {
    private String className;
    private String variableName;
    private Type type;
    private ReferenceType referenceType;

    public StaticVariableReference(String className, String variableName, Type type, ReferenceType referenceType) {
        this.className = className;
        this.variableName = variableName;
        this.type = type;
        this.referenceType = referenceType;
    }

    public ReferenceType getReferenceType() {
        return referenceType;
    }

    public String getClassName() {
        return className;
    }

    public String getVariableName() {
        return variableName;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return "StaticVariableReference{" +
                "className='" + className + '\'' +
                ", variableName='" + variableName + '\'' +
                ", type=" + type +
                ", referenceType=" + referenceType +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StaticVariableReference that = (StaticVariableReference) o;
        return Objects.equals(className, that.className) && Objects.equals(variableName, that.variableName) && Objects.equals(type, that.type) && referenceType == that.referenceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(className, variableName, type, referenceType);
    }

    public com.github.javaparser.ast.type.Type getReturnTypeAsJavaParserType() {
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
        return null;
    }

    @Override
    public String getNewVariableName() {
        return "V"+className+variableName;
    }
}
