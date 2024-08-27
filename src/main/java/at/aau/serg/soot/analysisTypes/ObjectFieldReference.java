package at.aau.serg.soot.analysisTypes;

import sootup.core.types.Type;

import java.util.Objects;

public class ObjectFieldReference extends AnalysisResult{
    private String className;
    private String objectName;
    private String fieldName;
    private Type type;
    private ReferenceType referenceType;

    public ObjectFieldReference(String className, String objectName, String fieldName, Type type, ReferenceType referenceType) {
        this.className = className;
        this.objectName = objectName;
        this.fieldName = fieldName;
        this.type = type;
        this.referenceType = referenceType;
    }

    public String getClassName() {return className;}
    public String getFieldName() {return fieldName;}
    public String getObjectName() {return objectName;}
    public Type getType() {return type;}
    public ReferenceType getReferenceType() {return referenceType;}

    @Override
    public String getNewVariableName() {
        return "V_" + className + "_" + objectName + "_" + fieldName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ObjectFieldReference that = (ObjectFieldReference) o;
        return Objects.equals(className, that.className) && Objects.equals(objectName, that.objectName) && Objects.equals(fieldName, that.fieldName) && Objects.equals(type, that.type) && referenceType == that.referenceType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(className, objectName, fieldName, type, referenceType);
    }

    @Override
    public String toString() {
        return "ObjectFieldReference{" +
                "className='" + className + '\'' +
                ", objectName='" + objectName + '\'' +
                ", fieldName='" + fieldName + '\'' +
                ", type=" + type +
                ", referenceType=" + referenceType +
                '}';
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
}
