package at.aau.serg.soot.decorators;

import at.aau.serg.cli.Configurations;
import at.aau.serg.javaparser.MethodNotFoundException;
import at.aau.serg.soot.Analysis;
import at.aau.serg.soot.analysisTypes.AnalysisResult;
import at.aau.serg.soot.analysisTypes.MarkedMethod;
import at.aau.serg.utils.TypeAdapter;
import jdk.nashorn.internal.runtime.regexp.joni.Config;
import sootup.core.jimple.basic.Immediate;
import sootup.core.jimple.common.expr.AbstractInvokeExpr;
import sootup.core.jimple.common.stmt.JInvokeStmt;
import sootup.core.jimple.common.stmt.Stmt;
import sootup.core.model.MethodModifier;
import sootup.core.signatures.MethodSignature;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Finds all methods that are marked using the configuration file and adds them to the analysis report
 */
public class MarkedMethodCallAnalysis extends AnalysisDecorator {

    public MarkedMethodCallAnalysis(Analysis analysis) {
        super(analysis);
    }

    @Override
    public Set<AnalysisResult> analyse() {
        Set<AnalysisResult> results = new HashSet<>(super.analyse());
        results.addAll(getMarkedMethodCalls());
        return results;
    }

    private Set<AnalysisResult> getMarkedMethodCalls() {
        if(!Configurations.exists()) return Collections.EMPTY_SET;
        Configurations configurations = Configurations.getInstance();
        String[] markedMethods = configurations.getPropertyAsStringArray("methods");
        String[] markedClasses = configurations.getPropertyAsStringArray("classes");

        if(markedMethods == null && markedClasses == null) return Collections.EMPTY_SET;

        Predicate<AbstractInvokeExpr> isMarkedMethod = invokeExpr -> {
            MethodSignature methodSignature = invokeExpr.getMethodSignature();
            String s = String.format("%s.%s", methodSignature.getDeclClassType().getFullyQualifiedName(), methodSignature.getSubSignature().getName());
            return (markedMethods != null && Arrays.stream(markedMethods).anyMatch(x -> x.equals(s))) || (markedClasses != null && Arrays.stream(markedClasses).anyMatch(x -> x.equals(methodSignature.getDeclClassType().getFullyQualifiedName())));
        };

        Predicate<AbstractInvokeExpr> isParsable = aie -> isValidType(aie.getType()) && aie.getArgs().stream().allMatch(i -> isValidType(i.getType()));

        Function<AbstractInvokeExpr, MarkedMethod> toMarkedMethod = invokeExpr -> {
            MethodSignature methodSignature = invokeExpr.getMethodSignature();
            boolean hasVariableArguments = getView().getMethod(methodSignature).orElseThrow(() -> new IllegalStateException("Cannot check method for variable arguments")).getModifiers().contains(MethodModifier.VARARGS);
            return new MarkedMethod(methodSignature.getDeclClassType().getClassName(), methodSignature.getName(), new TypeAdapter(invokeExpr.getType()), invokeExpr.getArgs().stream().map(Immediate::getType).map(TypeAdapter::new).collect(Collectors.toList()), hasVariableArguments);
        };

        return getStmtGraph().getStmts().stream()
                .filter(Stmt::containsInvokeExpr).map(Stmt::getInvokeExpr)
                .filter(isMarkedMethod)
                //.filter(isParsable)
                .map(toMarkedMethod)
                .collect(Collectors.toSet());
    }
}
