package at.aau.serg.soot.decorators;

import at.aau.serg.soot.Analysis;
import at.aau.serg.soot.analysisTypes.AnalysisResult;
import at.aau.serg.soot.analysisTypes.ObjectFieldReference;
import at.aau.serg.soot.analysisTypes.ReferenceType;
import at.aau.serg.utils.TypeAdapter;
import sootup.core.jimple.common.expr.JSpecialInvokeExpr;
import sootup.core.jimple.common.ref.JInstanceFieldRef;
import sootup.core.jimple.common.stmt.JInvokeStmt;
import sootup.core.jimple.common.stmt.Stmt;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Finds references to fields of global objects that are read in the method and adds them to the analysis report
 */
public class ObjectFieldReadAnalysis extends AnalysisDecorator{
    public ObjectFieldReadAnalysis(Analysis analysis) {
        super(analysis);
    }

    @Override
    public Set<AnalysisResult> analyse() {
        Set<AnalysisResult> results = new HashSet<>(super.analyse());
        results.addAll(getObjectFieldReads());
        return results;
    }

    private Set<AnalysisResult> getObjectFieldReads() {
        Function<JInstanceFieldRef, ObjectFieldReference> mapToObjectFieldRef = ifr -> new ObjectFieldReference(ifr.getFieldSignature().getDeclClassType().getClassName(), getNameOfObject(ifr), ifr.getFieldSignature().getName(), new TypeAdapter(ifr.getFieldSignature().getSubSignature().getType()), ReferenceType.READ);

        // In bytecode, local objects are initialized using specialinvoke statements, thus to find if an object is local a specialinvoke stmt must be there
        Predicate<JInstanceFieldRef> isLocalObject = fr -> getStmtGraph().getStmts().stream()
                .filter(JInvokeStmt.class::isInstance)
                .map(Stmt::getInvokeExpr)
                .filter(JSpecialInvokeExpr.class::isInstance)
                .map(JSpecialInvokeExpr.class::cast)
                .map(JSpecialInvokeExpr::getBase)
                .anyMatch(l -> fr.getBase().equals(l));

        Predicate<JInstanceFieldRef> isParsable = ifr -> isValidType(ifr.getType());

        return getStmtGraph().getStmts().stream()
                .flatMap(Stmt::getUses)
                .filter(JInstanceFieldRef.class::isInstance)
                .map(JInstanceFieldRef.class::cast)
                .filter(isParsable)
                .filter(isLocalObject.negate())
                .map(mapToObjectFieldRef)
                .collect(Collectors.toSet());

    }

}
