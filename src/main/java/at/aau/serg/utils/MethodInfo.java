package at.aau.serg.utils;

public class MethodInfo {
    private String sourcePath;
    private String classPath;
    private String className;
    private String packageName;
    private String methodName;

    public MethodInfo(String sourcePath, String classPath, String methodReference) {
        this.classPath = classPath;
        this.sourcePath = sourcePath.endsWith("/") ? sourcePath.substring(0, sourcePath.length() - 1) : sourcePath;
        this.methodName = methodReference.substring(methodReference.lastIndexOf(".") + 1);
        methodReference = methodReference.substring(0, methodReference.lastIndexOf("."));
        if(methodReference.contains(".")) {
            this.className = methodReference.substring(methodReference.lastIndexOf(".") + 1);
            packageName = methodReference.substring(0, methodReference.lastIndexOf("."));
        } else {
            this.className = methodReference;
            this.packageName = "";
        }
    }

    public String getSourcePath() {
        return sourcePath;
    }

    public String getClassName() {
        return className;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getMethodName() {
        return methodName;
    }

    public String getClassPath() {
        return classPath;
    }

    public String getFullyQualifiedClassName() {
        return packageName + (packageName.isEmpty() ? "" : ".") + className;
    }

    public String getFullyQualifiedMethodName() {
        return packageName + (packageName.isEmpty() ? "" : ".") + className + "." + methodName;
    }

    public String getJavaFilePath() {
        return sourcePath+ (sourcePath.endsWith("/") ? "" : "/") + getFullyQualifiedClassName().replace(".", "/") + ".java";
    }
}
