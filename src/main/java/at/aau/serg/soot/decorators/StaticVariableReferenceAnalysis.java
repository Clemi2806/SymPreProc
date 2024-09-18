package at.aau.serg.soot.decorators;

import at.aau.serg.soot.Analysis;
import at.aau.serg.soot.analysisTypes.AnalysisResult;
import at.aau.serg.soot.analysisTypes.ReferenceType;
import at.aau.serg.soot.analysisTypes.StaticVariableReference;
import at.aau.serg.utils.TypeAdapter;
import sootup.core.jimple.common.ref.JFieldRef;
import sootup.core.jimple.common.ref.JStaticFieldRef;
import sootup.core.jimple.common.stmt.Stmt;
import sootup.core.signatures.FieldSignature;
import sootup.java.core.JavaSootField;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Finds uses of static variables (reads) and adds them to the analysis report
 */
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
        Predicate<JFieldRef> isNotFinal = sfr -> {
            Optional<JavaSootField> field = getView().getField(sfr.getFieldSignature());
            if (!field.isPresent()) return false;
            JavaSootField javaSootField = field.get();
            return !javaSootField.isFinal();
        };
        Predicate<JFieldRef> isStaticFieldRef = fr -> fr instanceof JStaticFieldRef;
        Predicate<JFieldRef> isParsable = fr -> isValidType(fr.getType());

        Function<FieldSignature, StaticVariableReference> convertToStaticVariableRef = fieldSignature -> new StaticVariableReference(fieldSignature.getDeclClassType().getClassName(), fieldSignature.getName(), new TypeAdapter(fieldSignature.getType()), ReferenceType.READ);

        return getStmtGraph().getStmts().stream()
                .filter(Stmt::containsFieldRef)
                .map(Stmt::getFieldRef)
                .filter(isStaticFieldRef)
                .filter(isNotFinal)
                .filter(isParsable)
                .map(JFieldRef::getFieldSignature)
                .map(convertToStaticVariableRef)
                .collect(Collectors.toSet());
    }
}
