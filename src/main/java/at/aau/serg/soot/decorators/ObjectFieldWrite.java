package at.aau.serg.soot.decorators;

import at.aau.serg.soot.Analysis;
import at.aau.serg.soot.analysisTypes.AnalysisResult;
import at.aau.serg.soot.analysisTypes.ObjectFieldReference;
import at.aau.serg.soot.analysisTypes.ReferenceType;
import sootup.core.jimple.basic.LValue;
import sootup.core.jimple.common.expr.JSpecialInvokeExpr;
import sootup.core.jimple.common.ref.JFieldRef;
import sootup.core.jimple.common.ref.JInstanceFieldRef;
import sootup.core.jimple.common.stmt.JAssignStmt;
import sootup.core.jimple.common.stmt.JInvokeStmt;
import sootup.core.jimple.common.stmt.Stmt;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class ObjectFieldWrite extends AnalysisDecorator {
    public ObjectFieldWrite(Analysis analysis) {
        super(analysis);
    }

    @Override
    public Set<AnalysisResult> analyse() {
        Set<AnalysisResult> results = new HashSet<>(super.analyse());
        results.addAll(getObjectWrites());
        return results;
    }

    private Set<AnalysisResult> getObjectWrites() {
        Function<JInstanceFieldRef, ObjectFieldReference> mapToObjectFieldRef = ifr -> new ObjectFieldReference(ifr.getFieldSignature().getDeclClassType().getClassName(), getNameOfObject(ifr), ifr.getFieldSignature().getName(), ifr.getFieldSignature().getSubSignature().getType(), ReferenceType.WRITE);

        // In bytecode, local objects are initialized using specialinvoke statements, thus to find if an object is local a specialinvoke stmt must be there
        Predicate<JInstanceFieldRef> isLocalObject = fr -> getStmtGraph().getStmts().stream()
                .filter(JInvokeStmt.class::isInstance)
                .map(Stmt::getInvokeExpr)
                .filter(JSpecialInvokeExpr.class::isInstance)
                .map(JSpecialInvokeExpr.class::cast)
                .map(JSpecialInvokeExpr::getBase)
                .anyMatch(l -> fr.getBase().equals(l));

        return getStmtGraph().getStmts().stream()
                .filter(Stmt::containsFieldRef)
                .map(Stmt::getDef)
                .filter(Optional::isPresent).map(Optional::get)
                .filter(JInstanceFieldRef.class::isInstance)
                .map(JInstanceFieldRef.class::cast)
                .filter(isLocalObject.negate())
                .map(mapToObjectFieldRef)
                .collect(Collectors.toSet());
    }

    // TODO: Fix: for static objects
    private String getNameOfObject(JInstanceFieldRef instanceFieldRef) {
        return ((JFieldRef) getStmtGraph().getStmts().stream()
                .filter(JAssignStmt.class::isInstance).map(JAssignStmt.class::cast)
                .filter(s -> s.getLeftOp().equals(instanceFieldRef.getBase()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Oh no"))
                .getRightOp()).getFieldSignature().getName();
    }
}
