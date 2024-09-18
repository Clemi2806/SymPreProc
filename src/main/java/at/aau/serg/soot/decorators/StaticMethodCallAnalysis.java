package at.aau.serg.soot.decorators;

import at.aau.serg.soot.Analysis;
import at.aau.serg.soot.analysisTypes.AnalysisResult;
import at.aau.serg.soot.analysisTypes.StaticMethodCall;
import at.aau.serg.utils.TypeAdapter;
import sootup.core.signatures.MethodSignature;
import sootup.core.types.PrimitiveType;
import sootup.java.core.JavaSootMethod;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Finds calls to static methods and adds them to the analysis report
 * TODO: Does it make sense to keep this around because MarkedMethods can do this more precisely
 */
public class StaticMethodCallAnalysis extends AnalysisDecorator{
    private static final List<String> EXCLUDED_SCOPES = Arrays.asList("java.lang", "java.util");

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
        Predicate<JavaSootMethod> isStatic = method -> method.isStatic() && !method.getName().startsWith("<");
        Predicate<JavaSootMethod> isUsableType = method -> isValidType(method.getReturnType());
        Predicate<JavaSootMethod> isNotMethodOfExcludedScope = method ->  EXCLUDED_SCOPES.stream().noneMatch(s -> method.getDeclaringClassType().getFullyQualifiedName().toString().startsWith(s));
        Predicate<JavaSootMethod> isNotMethodOfSameClass = method -> !method.getDeclaringClassType().equals(getMethod().getDeclaringClassType());

        Function<MethodSignature, JavaSootMethod> getSootMethodUsingSignature = signature -> getView().getMethod(signature).orElseThrow(() ->  new RuntimeException("Method " + signature.getName() + " not found"));
        Function<JavaSootMethod, StaticMethodCall> convertToStaticMethodCall = method -> new StaticMethodCall(method.getDeclaringClassType().getClassName(), method.getName(),new TypeAdapter((PrimitiveType) method.getReturnType()));

        return getCallGraph().callsFrom(getMethod().getSignature()).stream()
                .map(getSootMethodUsingSignature)
                .filter(isStatic)
                .filter(isUsableType)
                .filter(isNotMethodOfExcludedScope)
                .filter(isNotMethodOfSameClass)
                .map(convertToStaticMethodCall)
                .collect(Collectors.toSet());
    }
}
