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
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
        Predicate<JavaSootMethod> isStatic = method -> method.isStatic() && !method.getName().startsWith("<") && !method.isBuiltInMethod();
        Predicate<JavaSootMethod> isOfIntType = method -> PrimitiveType.isIntLikeType(method.getReturnType());

        Function<MethodSignature, JavaSootMethod> getSootMethodUsingSignature = signature -> getView().getMethod(signature).orElseThrow(() ->  new RuntimeException("Method " + signature.getName() + " not found"));
        Function<JavaSootMethod, StaticMethodCall> convertToStaticMethodCall = method -> new StaticMethodCall(method.getDeclaringClassType().getClassName(), method.getName(), (PrimitiveType) method.getReturnType());

        return getCallGraph().callsFrom(getMethod().getSignature()).stream()
                .map(getSootMethodUsingSignature)
                .filter(isStatic)
                .filter(isOfIntType)
                .map(convertToStaticMethodCall)
                .collect(Collectors.toSet());
    }
}
