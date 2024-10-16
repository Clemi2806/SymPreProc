package at.aau.serg.soot.analysisTypes;

import at.aau.serg.utils.TypeAdapter;

import java.util.List;
import java.util.Objects;

public class MarkedMethod extends AnalysisResult {
    private final String className;
    private final String methodName;
    private final TypeAdapter returnType;
    private final List<TypeAdapter> parameterTypes;
    private final boolean hasVarArgs;

    public MarkedMethod(String className, String methodName, TypeAdapter returnType, List<TypeAdapter> parameterTypes) {
        this.className = className;
        this.methodName = methodName;
        this.returnType = returnType;
        this.parameterTypes = parameterTypes;
        this.hasVarArgs = false;
    }

    public MarkedMethod(String className, String methodName, TypeAdapter returnType, List<TypeAdapter> parameterTypes, boolean hasVarArgs) {
        this.className = className;
        this.methodName = methodName;
        this.returnType = returnType;
        this.parameterTypes = parameterTypes;
        this.hasVarArgs = hasVarArgs;
    }

    public String getClassName() {
        return className;
    }

    public String getMethodName() {
        return methodName;
    }

    public TypeAdapter getReturnType() {
        return returnType;
    }

    public List<TypeAdapter> getParameterTypes() {
        return parameterTypes;
    }

    public boolean hasVarArgs() {
        return hasVarArgs;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        MarkedMethod that = (MarkedMethod) object;
        return Objects.equals(className, that.className) && Objects.equals(methodName, that.methodName) && Objects.equals(returnType, that.returnType) && Objects.equals(parameterTypes, that.parameterTypes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(className, methodName, returnType, parameterTypes);
    }

    @Override
    public String getNewVariableName() {
        return "M_" + className + "_" + methodName;
    }
}
