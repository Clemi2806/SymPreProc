package at.aau.serg.javaparser;

import com.github.javaparser.ast.body.MethodDeclaration;

import java.util.List;

public interface MethodParser<T> {
    void parse(MethodDeclaration method);
}
