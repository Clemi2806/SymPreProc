package at.aau.serg.soot.analysisTypes;

import at.aau.serg.utils.TypeAdapter;
import sootup.core.types.Type;

import java.util.Objects;

public class StaticVariableReference extends AnalysisResult {
    private String className;
    private String variableName;
    private TypeAdapter type;
    private ReferenceType referenceType;

    public StaticVariableReference(String className, String variableName, TypeAdapter type, ReferenceType referenceType) {
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

    public TypeAdapter getType() {
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
    @Override
    public String getNewVariableName() {
        return "V"+className+variableName;
    }
}
