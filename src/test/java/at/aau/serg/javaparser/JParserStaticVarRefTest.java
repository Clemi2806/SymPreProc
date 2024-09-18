package at.aau.serg.javaparser;

import at.aau.serg.soot.analysisTypes.AnalysisResult;
import at.aau.serg.soot.analysisTypes.ReferenceType;
import at.aau.serg.soot.analysisTypes.StaticVariableReference;
import at.aau.serg.utils.TypeAdapter;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.ReturnStmt;
import org.junit.jupiter.api.Test;
import sootup.core.types.PrimitiveType;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

public class JParserStaticVarRefTest {
    @Test
    public void staticVarWriteTest() {
        JParser parser = assertDoesNotThrow(() -> new JParser("src/test/resources/javaparser", "StaticVariableReferenceWrite", "snippet"));
        Set<AnalysisResult> results = new HashSet<>();

        results.add(new StaticVariableReference("B", "x", new TypeAdapter(PrimitiveType.getInt()), ReferenceType.WRITE));

        assertDoesNotThrow(() -> parser.parse(results));

        MethodDeclaration method = parser.getMethod();

        assertEquals(2, method.getParameters().size());
        assertEquals("ReturnValues", method.getType().asClassOrInterfaceType().getNameAsString());

        boolean checkReturnStmts = method.findAll(ReturnStmt.class).stream().allMatch(stmt -> {
            if(!stmt.getExpression().isPresent()) return false;
            ObjectCreationExpr objectCreationExpr = stmt.getExpression().get().asObjectCreationExpr();
            return objectCreationExpr.getArguments().size() == 1
                    && objectCreationExpr.getArguments().getFirst().get().isNameExpr()
                    && objectCreationExpr.getArguments().getFirst().get().asNameExpr().getNameAsString().equals("VBx");
        });

        assertTrue(checkReturnStmts);
        String newParameter = method.getParameter(1).getNameAsString();
        assertEquals("VBx", newParameter);
    }

    @Test
    public void staticVarReadTest() {
        JParser parser = assertDoesNotThrow(() -> new JParser("src/test/resources/javaparser", "StaticVariableReferenceRead", "snippet"));
        Set<AnalysisResult> results = new HashSet<>();

        results.add(new StaticVariableReference("B", "x", new TypeAdapter(PrimitiveType.getInt()), ReferenceType.READ));

        assertDoesNotThrow(() -> parser.parse(results));

        MethodDeclaration method = parser.getMethod();

        assertEquals("int", method.getType().asString());
        assertEquals(1, method.getParameters().size());
        assertEquals("VBx", method.getParameter(0).getNameAsString());
        assertEquals(1, method.getBody().get().findAll(NameExpr.class).stream().filter(s -> s.getNameAsString().equals("VBx")).count());
    }
}
