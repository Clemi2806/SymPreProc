package at.aau.serg.soot;

import org.checkerframework.checker.units.qual.A;
import sootup.callgraph.CallGraph;
import sootup.callgraph.CallGraphAlgorithm;
import sootup.callgraph.ClassHierarchyAnalysisAlgorithm;
import sootup.core.graph.StmtGraph;
import sootup.core.inputlocation.AnalysisInputLocation;
import sootup.core.jimple.basic.NoPositionInformation;
import sootup.core.jimple.common.ref.JFieldRef;
import sootup.core.jimple.common.ref.JStaticFieldRef;
import sootup.core.jimple.common.stmt.Stmt;
import sootup.core.signatures.MethodSignature;
import sootup.core.types.PrimitiveType;
import sootup.core.types.VoidType;
import sootup.java.bytecode.inputlocation.DefaultRTJarAnalysisInputLocation;
import sootup.java.bytecode.inputlocation.JavaClassPathAnalysisInputLocation;
import sootup.java.core.JavaSootClass;
import sootup.java.core.JavaSootField;
import sootup.java.core.JavaSootMethod;
import sootup.java.core.types.JavaClassType;
import sootup.java.core.views.JavaView;

import java.util.*;
import java.util.stream.Collectors;

public class SootAnalysis {
    private JavaView view;
    private JavaSootMethod javaSootMethod;
    private CallGraph callGraph;
    private StmtGraph<?> stmtGraph;

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
        javaSootMethod = (JavaSootMethod) class_.get().getMethodsByName(methodName).toArray()[0];

        callGraph = getCallGraph();

        stmtGraph = javaSootMethod.getBody().getStmtGraph();
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

    public Set<StaticVariableReference> getStaticVariableReferences() {
        return stmtGraph.getStmts().stream()
                .filter(Stmt::containsFieldRef)
                .map(Stmt::getFieldRef)
                .filter(fr -> fr instanceof JStaticFieldRef)
                .filter(sfr -> {
                    Optional<JavaSootField> field = view.getField(sfr.getFieldSignature());
                    if (!field.isPresent()) return false;
                    JavaSootField javaSootField = field.get();
                    return !javaSootField.isFinal();
                })
                .map(JFieldRef::getFieldSignature)
                .map(fieldSignature -> new StaticVariableReference(fieldSignature.getDeclClassType().getClassName(), fieldSignature.getName()))
                .collect(Collectors.toSet());
    }

    private CallGraph getCallGraph() {

        MethodSignature entryMethodSignature = javaSootMethod.getSignature();

        CallGraphAlgorithm cga = new ClassHierarchyAnalysisAlgorithm(view);

        return cga.initialize(Collections.singletonList(entryMethodSignature));
    }
}
