package at.aau.serg.soot.decorators;

import at.aau.serg.soot.Analysis;
import at.aau.serg.soot.analysisTypes.AnalysisResult;
import at.aau.serg.soot.analysisTypes.StaticVariableReference;
import sootup.core.jimple.common.ref.JFieldRef;
import sootup.core.jimple.common.ref.JStaticFieldRef;
import sootup.core.jimple.common.stmt.Stmt;
import sootup.java.core.JavaSootField;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class StaticVariableReferenceAnalysis extends AnalysisDecorator{
    public StaticVariableReferenceAnalysis(Analysis analysis) {
        super(analysis);
    }

    @Override
    public Set<AnalysisResult> analyse() {
        Set<AnalysisResult> results = new HashSet<>(super.analyse());
        results.addAll(getStaticVariableReferences());
        return results;
    }

    public Set<StaticVariableReference> getStaticVariableReferences() {
        return getStmtGraph().getStmts().stream()
                .filter(Stmt::containsFieldRef)
                .map(Stmt::getFieldRef)
                .filter(fr -> fr instanceof JStaticFieldRef)
                .filter(sfr -> {
                    Optional<JavaSootField> field = getView().getField(sfr.getFieldSignature());
                    if (!field.isPresent()) return false;
                    JavaSootField javaSootField = field.get();
                    return !javaSootField.isFinal();
                })
                .map(JFieldRef::getFieldSignature)
                .map(fieldSignature -> new StaticVariableReference(fieldSignature.getDeclClassType().getClassName(), fieldSignature.getName(), fieldSignature.getType()))
                .collect(Collectors.toSet());
    }
}
