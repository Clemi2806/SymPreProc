package at.aau.serg.soot;

import at.aau.serg.soot.analysisTypes.AnalysisResult;
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

    public SootAnalysis(String classPath, String classIdentifier, String methodName) {
        List<AnalysisInputLocation> inputLocations = new ArrayList<>();
        inputLocations.add(new JavaClassPathAnalysisInputLocation(classPath));
        inputLocations.add(new DefaultRTJarAnalysisInputLocation());
        view = new JavaView(inputLocations);

        JavaClassType classType = view.getIdentifierFactory().getClassType(classIdentifier);
        Optional<JavaSootClass> class_ = view.getClass(classType);
        if (!class_.isPresent()) {
            throw new RuntimeException("Could not find class " + classIdentifier);
        }
        // TODO: Specify method using full signature, to filter out overloaded methods
        System.out.printf("Selecting first method with name: %s%n", methodName);
        javaSootMethod = (JavaSootMethod) class_.get().getMethodsByName(methodName).toArray()[0];

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
