package at.aau.serg.soot;

import org.checkerframework.checker.units.qual.A;
import sootup.callgraph.CallGraph;
import sootup.callgraph.CallGraphAlgorithm;
import sootup.callgraph.ClassHierarchyAnalysisAlgorithm;
import sootup.core.inputlocation.AnalysisInputLocation;
import sootup.core.jimple.basic.NoPositionInformation;
import sootup.core.signatures.MethodSignature;
import sootup.core.types.PrimitiveType;
import sootup.core.types.VoidType;
import sootup.java.bytecode.inputlocation.DefaultRTJarAnalysisInputLocation;
import sootup.java.bytecode.inputlocation.JavaClassPathAnalysisInputLocation;
import sootup.java.core.JavaSootClass;
import sootup.java.core.JavaSootMethod;
import sootup.java.core.types.JavaClassType;
import sootup.java.core.views.JavaView;

import java.util.*;

public class SootAnalysis {
    private String classPath;
    private String methodName;
    private String classIdentifier;
    private JavaView view;
    private JavaSootMethod javaSootMethod;
    private CallGraph callGraph;

    public SootAnalysis(String classPath, String classIdentifier, String methodName) {
        this.classPath = classPath;
        this.methodName = methodName;
        this.classIdentifier = classIdentifier;

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
        javaSootMethod = (JavaSootMethod) class_.get().getMethodsByName(methodName).toArray()[0];

        callGraph = getCallGraph();
    }

    public Set<StaticMethodCall> getStaticMethodCalls() {
        Set<StaticMethodCall> staticMethodCalls = new HashSet<>();

        for (MethodSignature methodSignature : callGraph.callsFrom(javaSootMethod.getSignature())) {
            Optional<JavaSootMethod> methodOptional = view.getMethod(methodSignature);

            if(!methodOptional.isPresent())
                throw new RuntimeException("Method " + methodSignature.getName() + " not found");

            JavaSootMethod method = methodOptional.get();

            if(!method.isStatic() || method.getName().startsWith("<") || method.isBuiltInMethod())
                continue;

            if(!PrimitiveType.isIntLikeType(method.getReturnType()))
                throw new RuntimeException("Method " + method.getName() + " is not int-like type");

            staticMethodCalls.add(new StaticMethodCall(method.getDeclaringClassType().getClassName(), method.getName(), (PrimitiveType) method.getReturnType()));
        }

        return staticMethodCalls;
    }

    private CallGraph getCallGraph() {

        MethodSignature entryMethodSignature = javaSootMethod.getSignature();

        CallGraphAlgorithm cga = new ClassHierarchyAnalysisAlgorithm(view);

        return cga.initialize(Collections.singletonList(entryMethodSignature));
    }
}
