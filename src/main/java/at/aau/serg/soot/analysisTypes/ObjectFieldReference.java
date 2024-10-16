package at.aau.serg.soot.analysisTypes;

import at.aau.serg.utils.TypeAdapter;

import java.util.Objects;

public class ObjectFieldReference extends AnalysisResult{
    private final String className;
    private final String objectName;
    private final String fieldName;
    private final TypeAdapter type;
    private final ReferenceType referenceType;

    public ObjectFieldReference(String className, String objectName, String fieldName, TypeAdapter type, ReferenceType referenceType) {
        this.className = className;
        this.objectName = objectName;
        this.fieldName = fieldName;
        this.type = type;
        this.referenceType = referenceType;
    }

    public String getClassName() {return className;}
    public String getFieldName() {return fieldName;}
    public String getObjectName() {return objectName;}
    public TypeAdapter getType() {return type;}
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

}
