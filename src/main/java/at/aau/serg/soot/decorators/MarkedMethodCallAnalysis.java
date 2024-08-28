package at.aau.serg.soot.decorators;

import at.aau.serg.cli.Configurations;
import at.aau.serg.soot.Analysis;
import at.aau.serg.soot.analysisTypes.AnalysisResult;
import at.aau.serg.soot.analysisTypes.MarkedMethod;
import sootup.core.jimple.basic.Immediate;
import sootup.core.jimple.common.expr.AbstractInvokeExpr;
import sootup.core.jimple.common.stmt.JInvokeStmt;
import sootup.core.signatures.MethodSignature;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
        String[] markedMethods = Configurations.getInstance().getPropertyAsStringArray("methods");
        if(markedMethods == null) return Collections.EMPTY_SET;

        Predicate<AbstractInvokeExpr> isMarkedMethod = invokeExpr -> {
            MethodSignature methodSignature = invokeExpr.getMethodSignature();
            String s = String.format("%s#%s", methodSignature.getDeclClassType().getFullyQualifiedName(), methodSignature.getSubSignature().getName());
            return Arrays.stream(markedMethods).anyMatch(x -> x.equals(s));
        };

        Function<AbstractInvokeExpr, MarkedMethod> toMarkedMethod = invokeExpr -> {
            MethodSignature methodSignature = invokeExpr.getMethodSignature();
            return new MarkedMethod(methodSignature.getDeclClassType().getClassName(), methodSignature.getName(), invokeExpr.getType(), invokeExpr.getArgs().stream().map(Immediate::getType).collect(Collectors.toList()));
        };

        return getStmtGraph().getStmts().stream()
                .filter(JInvokeStmt.class::isInstance).map(JInvokeStmt.class::cast).map(JInvokeStmt::getInvokeExpr)
                .filter(isMarkedMethod)
                .map(toMarkedMethod)
                .collect(Collectors.toSet());
    }
}
