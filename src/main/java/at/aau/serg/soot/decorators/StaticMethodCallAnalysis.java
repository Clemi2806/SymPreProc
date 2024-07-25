package at.aau.serg.soot.decorators;

import at.aau.serg.soot.Analysis;
import at.aau.serg.soot.analysisTypes.AnalysisResult;
import at.aau.serg.soot.analysisTypes.StaticMethodCall;
import sootup.core.signatures.MethodSignature;
import sootup.core.types.PrimitiveType;
import sootup.java.core.JavaSootMethod;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class StaticMethodCallAnalysis extends AnalysisDecorator{
    public StaticMethodCallAnalysis(Analysis analysis) {
        super(analysis);
    }

    @Override
    public Set<AnalysisResult> analyse() {
        Set<AnalysisResult> results = new HashSet<>(super.analyse());
        results.addAll(getStaticMethodCalls());
        return results;
    }

    private Set<StaticMethodCall> getStaticMethodCalls() {
        Set<StaticMethodCall> staticMethodCalls = new HashSet<>();

        for (MethodSignature methodSignature : getCallGraph().callsFrom(getMethod().getSignature())) {
            Optional<JavaSootMethod> methodOptional = getView().getMethod(methodSignature);

            if(!methodOptional.isPresent())
                throw new RuntimeException("Method " + methodSignature.getName() + " not found");

            JavaSootMethod method = methodOptional.get();

            if(!method.isStatic() || method.getName().startsWith("<") || method.isBuiltInMethod())
                continue;

            if(!PrimitiveType.isIntLikeType(method.getReturnType()))
                throw new RuntimeException("Method " + method.getName() + " is not int-like type");

            staticMethodCalls.add(new StaticMethodCall(method.getDeclaringClassType().getClassName(), method.getName(), (PrimitiveType) method.getReturnType()));
        }

        return staticMethodCalls;
    }
}
