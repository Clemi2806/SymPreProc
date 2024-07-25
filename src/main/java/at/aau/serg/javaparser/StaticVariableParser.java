package at.aau.serg.javaparser;

import at.aau.serg.soot.analysisTypes.StaticVariableReference;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.NameExpr;

import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class StaticVariableParser extends Parser {
    Set<StaticVariableReference> staticVariableReferences;

    public StaticVariableParser(Set<StaticVariableReference> staticVariableReferences) {
        this.staticVariableReferences = staticVariableReferences;
    }

    @Override
    public void parse(MethodDeclaration method) {
        if(!method.getBody().isPresent()) return;

        AtomicInteger c = new AtomicInteger();
        method.getBody().get().findAll(FieldAccessExpr.class).forEach(fae -> {
            this.staticVariableReferences.forEach(ref -> {
                if(match(fae, ref)){
                    // Replace
                    System.out.println("Replacing static variable " + fae);

                    String newVariableName = ref.getClassName()+ref.getVariableName()+c;
                    Parameter newParam = new Parameter(ref.getReturnTypeAsJavaParserType(), newVariableName);
                    method.addParameter(newParam);
                    fae.replace(new NameExpr(newVariableName));
                    c.getAndIncrement();
                }
            });
        });

        Optional<CompilationUnit> ocu = method.findCompilationUnit();
        if(!ocu.isPresent()) return;
        CompilationUnit cu = ocu.get();
        cu.findAll(MethodCallExpr.class).forEach(callExpr -> {
            if(callExpr.getName().equals(method.getName())) {
                callExpr.replace(
                        new MethodCallExpr(
                                callExpr.getScope().orElse(null),
                                callExpr.getNameAsString(),
                                new NodeList<Expression>(
                                        method.getParameters()
                                                .stream().map(StaticCallParser::convertParameter)
                                                .collect(Collectors.toList()))));
            }
        });
    }

    private static boolean match(FieldAccessExpr fae, StaticVariableReference ref) {
        return ref.getClassName().equals(fae.getScope().toString()) && ref.getVariableName().equals(fae.getNameAsString());
    }
}
