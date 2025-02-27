package at.aau.serg.soot.analysisTypes;

import at.aau.serg.utils.TypeAdapter;

import java.util.Objects;

public class StaticMethodCall extends AnalysisResult{
    private final String methodName;
    private final TypeAdapter returnType;
    private final String className;

    public StaticMethodCall(String className, String methodName, TypeAdapter returnType) {
        this.className = className;
        this.methodName = methodName;
        this.returnType = returnType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StaticMethodCall that = (StaticMethodCall) o;
        return Objects.equals(methodName, that.methodName) && Objects.equals(returnType, that.returnType) && Objects.equals(className, that.className);
    }

    @Override
    public int hashCode() {
        return Objects.hash(methodName, returnType, className);
    }

    @Override
    public String toString() {
        return "StaticMethodCall{" +
                "methodName='" + methodName + '\'' +
                ", returnType=" + returnType +
                '}';
    }

    public String getMethodName() {
        return methodName;
    }

    public TypeAdapter getReturnType() {
        return returnType;
    }

    public String getClassName() {
        return className;
    }

    public String getCallString() {
        return className + "." + methodName;
    }

    @Override
    public String getNewVariableName() {
        return "M"+className+methodName;
    }
}
