package at.aau.serg.soot;

import sootup.callgraph.CallGraph;
import sootup.callgraph.CallGraphAlgorithm;
import sootup.callgraph.ClassHierarchyAnalysisAlgorithm;
import sootup.core.inputlocation.AnalysisInputLocation;
import sootup.core.signatures.MethodSignature;
import sootup.core.types.VoidType;
import sootup.java.bytecode.inputlocation.DefaultRTJarAnalysisInputLocation;
import sootup.java.bytecode.inputlocation.JavaClassPathAnalysisInputLocation;
import sootup.java.core.JavaSootClass;
import sootup.java.core.JavaSootMethod;
import sootup.java.core.types.JavaClassType;
import sootup.java.core.views.JavaView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class SootAnalysis {
    private String classPath;
    private String methodName;
    private String classIdentifier;
    private JavaView view;

    public SootAnalysis(String classPath, String classIdentifier, String methodName) {
        this.classPath = classPath;
        this.methodName = methodName;
        this.classIdentifier = classIdentifier;

        List<AnalysisInputLocation> inputLocations = new ArrayList<>();
        inputLocations.add(new JavaClassPathAnalysisInputLocation(classPath));
        inputLocations.add(new DefaultRTJarAnalysisInputLocation());
        view = new JavaView(inputLocations);
    }

    public void runAnalysis() {
        CallGraph callGraph = getCallGraph();


    }

    private CallGraph getCallGraph() {
        JavaClassType classType = view.getIdentifierFactory().getClassType(classIdentifier);
        Optional<JavaSootClass> class_ = view.getClass(classType);
        if (!class_.isPresent()) {
            throw new RuntimeException("Could not find class " + classIdentifier);
        }
        // TODO: Specify method using full signature, to filter out overloaded methods
        JavaSootMethod javaSootMethod = (JavaSootMethod) class_.get().getMethodsByName(methodName).toArray()[0];

        MethodSignature entryMethodSignature = javaSootMethod.getSignature();

        CallGraphAlgorithm cga = new ClassHierarchyAnalysisAlgorithm(view);

        return cga.initialize(Collections.singletonList(entryMethodSignature));
    }
}
