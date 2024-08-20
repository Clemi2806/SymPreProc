package at.aau.serg.soot.analysisTypes;

import sootup.core.types.Type;

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
}
