package at.aau.serg.soot;

import at.aau.serg.soot.analysisTypes.AnalysisResult;
import at.aau.serg.utils.MethodInfo;
import sootup.callgraph.CallGraph;
import sootup.callgraph.CallGraphAlgorithm;
import sootup.callgraph.ClassHierarchyAnalysisAlgorithm;
import sootup.core.graph.StmtGraph;
import sootup.core.inputlocation.AnalysisInputLocation;
import sootup.core.signatures.MethodSignature;
import sootup.java.bytecode.inputlocation.DefaultRTJarAnalysisInputLocation;
import sootup.java.bytecode.inputlocation.JavaClassPathAnalysisInputLocation;
import sootup.java.core.JavaSootClass;
import sootup.java.core.JavaSootMethod;
import sootup.java.core.types.JavaClassType;
import sootup.java.core.views.JavaView;

import java.util.*;

public class SootAnalysis implements Analysis{
    private final JavaView view;
    private final JavaSootMethod javaSootMethod;
    private final CallGraph callGraph;
    private final StmtGraph<?> stmtGraph;

    public SootAnalysis(final MethodInfo methodInfo) {
        List<AnalysisInputLocation> inputLocations = new ArrayList<>();
        inputLocations.add(new JavaClassPathAnalysisInputLocation(methodInfo.getClassPath()));
        inputLocations.add(new DefaultRTJarAnalysisInputLocation());
        view = new JavaView(inputLocations);

        JavaClassType classType = view.getIdentifierFactory().getClassType(methodInfo.getFullyQualifiedClassName());
        Optional<JavaSootClass> class_ = view.getClass(classType);
        if (!class_.isPresent()) {
            throw new RuntimeException("Could not find class " + methodInfo.getFullyQualifiedClassName());
        }
        // TODO: Specify method using full signature, to filter out overloaded methods
        System.out.printf("Selecting first method with name: %s%n", methodInfo.getMethodName());
        javaSootMethod = (JavaSootMethod) class_.get().getMethodsByName(methodInfo.getMethodName()).toArray()[0];

        callGraph = createCallGraph();

        stmtGraph = javaSootMethod.getBody().getStmtGraph();
    }

    private CallGraph createCallGraph() {

        MethodSignature entryMethodSignature = javaSootMethod.getSignature();

        CallGraphAlgorithm cga = new ClassHierarchyAnalysisAlgorithm(view);

        return cga.initialize(Collections.singletonList(entryMethodSignature));
    }

    @Override
    public Set<AnalysisResult> analyse() {
        return Collections.emptySet();
    }

    @Override
    public JavaView getView() {
        return this.view;
    }

    @Override
    public JavaSootMethod getMethod() {
        return this.javaSootMethod;
    }

    @Override
    public CallGraph getCallGraph() {
        return this.callGraph;
    }

    @Override
    public StmtGraph<?> getStmtGraph() {
        return this.stmtGraph;
    }
}
