package at.aau.serg.javaparser;

import at.aau.serg.soot.analysisTypes.AnalysisResult;
import at.aau.serg.soot.analysisTypes.MarkedMethod;
import at.aau.serg.soot.analysisTypes.ObjectFieldReference;
import at.aau.serg.soot.analysisTypes.ReferenceType;
import at.aau.serg.utils.MethodInfo;
import at.aau.serg.utils.TypeAdapter;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.stmt.ReturnStmt;
import org.junit.jupiter.api.Test;
import sootup.core.types.PrimitiveType;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class JParserMarkedMethodTest {
    @Test
    void markedMethodCallTest() {
        MethodInfo info = new MethodInfo("src/test/resources/javaparser", "", "MarkedMethodCall.snippet");
        JParser parser = assertDoesNotThrow(() -> new JParser(info));
        Set<AnalysisResult> results = new HashSet<>();

        List<TypeAdapter> parameterTypes = new ArrayList<>();
        parameterTypes.add(new TypeAdapter(PrimitiveType.getInt()));

        results.add(new MarkedMethod("Math", "abs", new TypeAdapter(PrimitiveType.getInt()), parameterTypes));

        assertDoesNotThrow(() -> parser.parse(results));

        MethodDeclaration method = parser.getMethod();

        System.out.println(method);

        assertEquals(2, method.getParameters().size());
        assertEquals("ReturnValues", method.getType().asClassOrInterfaceType().getNameAsString());

        boolean checkReturnStmts = method.findAll(ReturnStmt.class).stream().allMatch(stmt -> {
            if(!stmt.getExpression().isPresent()) return false;
            ObjectCreationExpr objectCreationExpr = stmt.getExpression().get().asObjectCreationExpr();
            return objectCreationExpr.getArguments().size() == 2
                    && objectCreationExpr.getArguments().get(0).asNameExpr().getNameAsString().equals("M_Math_abs_ret0")
                    && objectCreationExpr.getArguments().get(1).asNameExpr().getNameAsString().equals("M_Math_abs0_arg0");

        });

        assertTrue(checkReturnStmts);
        String newParameter = method.getParameter(1).getNameAsString();
        assertEquals("M_Math_abs_ret0", newParameter);
    }
}

