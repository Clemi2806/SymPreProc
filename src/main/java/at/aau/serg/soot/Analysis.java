package at.aau.serg.soot;

import at.aau.serg.soot.analysisTypes.AnalysisResult;
import sootup.callgraph.CallGraph;
import sootup.core.graph.StmtGraph;
import sootup.java.core.JavaSootMethod;
import sootup.java.core.views.JavaView;

import java.util.Set;

public interface Analysis {
    Set<AnalysisResult> analyse();
    default JavaView getView() {return null;}
    default JavaSootMethod getMethod(){return null;}
    default CallGraph getCallGraph(){return null;}
    default StmtGraph<?> getStmtGraph(){return null;}

}
