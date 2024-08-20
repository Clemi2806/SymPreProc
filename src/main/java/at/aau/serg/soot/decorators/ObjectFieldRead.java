package at.aau.serg.soot.decorators;

import at.aau.serg.soot.Analysis;
import at.aau.serg.soot.analysisTypes.AnalysisResult;
import at.aau.serg.soot.analysisTypes.ObjectFieldReference;
import at.aau.serg.soot.analysisTypes.ReferenceType;
import sootup.core.jimple.common.ref.JInstanceFieldRef;
import sootup.core.jimple.common.stmt.JAssignStmt;
import sootup.core.jimple.common.stmt.Stmt;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ObjectFieldRead extends AnalysisDecorator{
    public ObjectFieldRead(Analysis analysis) {
        super(analysis);
    }

    @Override
    public Set<AnalysisResult> analyse() {
        return getObjectFieldReads();
    }

    private Set<AnalysisResult> getObjectFieldReads() {
        Function<JInstanceFieldRef, ObjectFieldReference> mapToObjectFieldRef = ifr -> new ObjectFieldReference(ifr.getFieldSignature().getDeclClassType().getClassName(), getNameOfObject(ifr), ifr.getFieldSignature().getName(), ifr.getType(), ReferenceType.READ);

        return getStmtGraph().getStmts().stream()
                .flatMap(Stmt::getUses)
                .filter(v -> v instanceof JInstanceFieldRef)
                .map(JInstanceFieldRef.class::cast)
                .map(mapToObjectFieldRef)
                .collect(Collectors.toSet());

    }

    private String getNameOfObject(JInstanceFieldRef instanceFieldRef) {
        return getStmtGraph().getStmts().stream()
                .filter(s -> s instanceof JAssignStmt).map(JAssignStmt.class::cast)
                .filter(s -> s.getRightOp().equals(instanceFieldRef.getBase()))
                .map(JAssignStmt::getLeftOp)
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No left value found"))
                .toString();
    }
}
