package at.aau.serg.soot.decorators;

import at.aau.serg.soot.Analysis;
import at.aau.serg.soot.analysisTypes.AnalysisResult;
import at.aau.serg.soot.analysisTypes.ReferenceType;
import at.aau.serg.soot.analysisTypes.StaticVariableReference;
import at.aau.serg.utils.TypeAdapter;
import sootup.core.jimple.common.ref.JFieldRef;
import sootup.core.jimple.common.ref.JStaticFieldRef;
import sootup.core.jimple.common.stmt.JAssignStmt;
import sootup.core.signatures.FieldSignature;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Finds uses of static variables (writes) and adds them to the analysis report
 */
public class StaticVariableWriteAnalysis extends AnalysisDecorator {

    public StaticVariableWriteAnalysis(Analysis analysis) {
        super(analysis);
    }

    @Override
    public Set<AnalysisResult> analyse() {
        Set<AnalysisResult> results = new HashSet<>(super.analyse());
        results.addAll(getStaticVariableWrites());
        return results;
    }

    private Set<AnalysisResult> getStaticVariableWrites() {
        Function<FieldSignature, StaticVariableReference> convertToStaticVariableWrite = fs -> new StaticVariableReference(fs.getDeclClassType().getClassName(),fs.getName(), new TypeAdapter(fs.getType()), ReferenceType.WRITE);

        Predicate<JStaticFieldRef> isParsable = fr -> isValidType(fr.getType());

        return getStmtGraph().getStmts().stream()
                .filter(stmt -> stmt instanceof JAssignStmt)
                .map(stmt -> ((JAssignStmt) stmt).getLeftOp())
                .filter(leftOp -> leftOp instanceof JStaticFieldRef)
                .map(JStaticFieldRef.class::cast)
                .filter(isParsable)
                .map(JFieldRef::getFieldSignature)
                .map(convertToStaticVariableWrite)
                .collect(Collectors.toSet());
    }
}
