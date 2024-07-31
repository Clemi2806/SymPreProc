package at.aau.serg.soot.decorators;

import at.aau.serg.soot.Analysis;
import at.aau.serg.soot.analysisTypes.AnalysisResult;
import at.aau.serg.soot.analysisTypes.StaticVariableWrite;
import sootup.core.jimple.common.ref.JStaticFieldRef;
import sootup.core.jimple.common.stmt.JAssignStmt;
import sootup.core.jimple.common.stmt.Stmt;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    private Set<StaticVariableWrite> getStaticVariableWrites() {
        return getStmtGraph().getStmts().stream()
                .filter(stmt -> stmt instanceof JAssignStmt)
                .map(stmt -> ((JAssignStmt) stmt).getLeftOp())
                .filter(leftOp -> leftOp instanceof JStaticFieldRef)
                .map(leftOp -> ((JStaticFieldRef) leftOp).getFieldSignature())
                .map(fs -> new StaticVariableWrite(fs.getDeclClassType().getClassName(),fs.getName(), fs.getType()))
                .collect(Collectors.toSet());
    }
}
