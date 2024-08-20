package at.aau.serg.soot.decorators;

import at.aau.serg.soot.Analysis;
import at.aau.serg.soot.analysisTypes.AnalysisResult;
import at.aau.serg.soot.analysisTypes.ReferenceType;
import at.aau.serg.soot.analysisTypes.StaticVariableReference;
import sootup.core.jimple.common.ref.JStaticFieldRef;
import sootup.core.jimple.common.stmt.JAssignStmt;
import sootup.core.signatures.FieldSignature;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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

    private Set<StaticVariableReference> getStaticVariableWrites() {
        Function<FieldSignature, StaticVariableReference> convertToStaticVariableWrite = fs -> new StaticVariableReference(fs.getDeclClassType().getClassName(),fs.getName(), fs.getType(), ReferenceType.WRITE);

        return getStmtGraph().getStmts().stream()
                .filter(stmt -> stmt instanceof JAssignStmt)
                .map(stmt -> ((JAssignStmt) stmt).getLeftOp())
                .filter(leftOp -> leftOp instanceof JStaticFieldRef)
                .map(leftOp -> ((JStaticFieldRef) leftOp).getFieldSignature())
                .map(convertToStaticVariableWrite)
                .collect(Collectors.toSet());
    }
}
