package at.aau.serg.soot.decorators;

import at.aau.serg.soot.Analysis;
import at.aau.serg.soot.analysisTypes.AnalysisResult;
import sootup.callgraph.CallGraph;
import sootup.core.graph.StmtGraph;
import sootup.core.jimple.common.ref.JFieldRef;
import sootup.core.jimple.common.ref.JInstanceFieldRef;
import sootup.core.jimple.common.stmt.JAssignStmt;
import sootup.core.types.Type;
import sootup.java.core.JavaSootMethod;
import sootup.java.core.views.JavaView;

import java.util.Set;

public abstract class AnalysisDecorator implements Analysis {
    private final Analysis analysis;

    public AnalysisDecorator(Analysis analysis) {
        this.analysis = analysis;
    }

    @Override
    public Set<AnalysisResult> analyse() {
        return analysis.analyse();
    }

    @Override
    public JavaView getView() {
        return analysis.getView();
    }

    @Override
    public JavaSootMethod getMethod() {
        return analysis.getMethod();
    }

    @Override
    public CallGraph getCallGraph() {
        return analysis.getCallGraph();
    }

    @Override
    public StmtGraph<?> getStmtGraph() {
        return analysis.getStmtGraph();
    }

    public static boolean isValidType(Type type) {
        switch (type.toString()) {
            case "boolean":
            case "int":
            case "short":
            case "byte":
            case "long":
            case "float":
            case "double":
            case "java.lang.String":
            case "void":
                return true;
            default:
                return false;
        }
    }

    protected String getNameOfObject(JInstanceFieldRef instanceFieldRef) {
        return ((JFieldRef) getStmtGraph().getStmts().stream()
                .filter(JAssignStmt.class::isInstance).map(JAssignStmt.class::cast)
                .filter(s -> s.getLeftOp().equals(instanceFieldRef.getBase()))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Could not find name of the object"))
                .getRightOp()).getFieldSignature().getName();
    }
}
