package at.aau.serg.soot;

import java.util.Objects;

public class StaticVariableReference {
    private String className;
    private String variableName;

    public StaticVariableReference(String className, String variableName) {
        this.className = className;
        this.variableName = variableName;
    }

    public String getClassName() {
        return className;
    }

    public String getVariableName() {
        return variableName;
    }

    @Override
    public String toString() {
        return "StaticVariableReference{" +
                "className='" + className + '\'' +
                ", variableName='" + variableName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StaticVariableReference that = (StaticVariableReference) o;
        return Objects.equals(className, that.className) && Objects.equals(variableName, that.variableName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(className, variableName);
    }
}
