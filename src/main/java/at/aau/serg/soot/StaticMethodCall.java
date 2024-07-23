package at.aau.serg.soot;

import sootup.core.types.PrimitiveType;

import java.util.Objects;

public class StaticMethodCall {
    private String methodName;
    private PrimitiveType returnType;
    private String className;

    public StaticMethodCall(String className, String methodName, PrimitiveType returnType) {
        this.className = className;
        this.methodName = methodName;
        this.returnType = returnType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StaticMethodCall that = (StaticMethodCall) o;
        return Objects.equals(methodName, that.methodName) && Objects.equals(returnType, that.returnType);
    }

    @Override
    public int hashCode() {
        return Objects.hash(methodName, returnType);
    }

    @Override
    public String toString() {
        return "StaticMethodCall{" +
                "methodName='" + methodName + '\'' +
                ", returnType=" + returnType +
                '}';
    }
}
