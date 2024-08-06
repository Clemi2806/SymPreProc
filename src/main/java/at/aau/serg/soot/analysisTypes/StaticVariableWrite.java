package at.aau.serg.soot.analysisTypes;

import sootup.core.types.Type;

import java.util.Objects;

public class StaticVariableWrite extends AnalysisResult {
    private String className;
    private String variableName;
    private Type type;

    public StaticVariableWrite(String className, String variableName, Type type) {
        this.className = className;
        this.variableName = variableName;
        this.type = type;
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
        return "StaticVariableWrite{" +
                "className='" + className + '\'' +
                ", variableName='" + variableName + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StaticVariableWrite that = (StaticVariableWrite) o;
        return Objects.equals(className, that.className) && Objects.equals(variableName, that.variableName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(className, variableName);
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
        return "V" + className + variableName;
    }
}
