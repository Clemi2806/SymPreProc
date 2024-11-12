package at.aau.serg.javaparser;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.SimpleName;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.Statement;

import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class MethodCleaner {
    public MethodCleaner() {}

    public void removeUnusedParameters(MethodDeclaration method) {
        if(!method.getBody().isPresent()) return;
        BlockStmt body = method.getBody().get();

        HashMap<String, Integer> occurrences = new HashMap<>();

        method.getParameters().forEach(p -> occurrences.put(p.getNameAsString(), 0));

        Consumer<NameExpr> updateOccurences = nameExpr -> {
            if(!occurrences.containsKey(nameExpr.getNameAsString())) return;
            occurrences.put(nameExpr.getNameAsString(), occurrences.get(nameExpr.getNameAsString()) + 1);
        };

        // Do a search using the AST
        body.findAll(NameExpr.class).forEach(updateOccurences);

        // Search through arguments of ObjectCreationExpr objects
        body.findAll(ObjectCreationExpr.class).forEach(
                objectCreationExpr -> {
                    objectCreationExpr.getArguments().forEach(arg -> {
                        arg.findAll(NameExpr.class).forEach(updateOccurences);
                    });
                }
        );

        method.getParameters().removeIf(p -> occurrences.get(p.getNameAsString()) == 0);
    }
}

