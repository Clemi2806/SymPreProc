package at.aau.serg.soot.analysisTypes;

import com.github.javaparser.ast.type.Type;
import sootup.core.types.PrimitiveType;

import java.util.Objects;

public class StaticMethodCall extends AnalysisResult{
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

    public PrimitiveType getReturnType() {
        return returnType;
    }

    public String getClassName() {
        return className;
    }

    public String getCallString() {
        return className + "." + methodName;
    }

    public Type getReturnTypeAsJavaParserType() {
        switch (returnType.getName()) {
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
        return "M"+className+methodName;
    }
}
