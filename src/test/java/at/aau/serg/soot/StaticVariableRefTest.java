package at.aau.serg.soot;

import at.aau.serg.soot.analysisTypes.AnalysisResult;
import at.aau.serg.soot.analysisTypes.ReferenceType;
import at.aau.serg.soot.analysisTypes.StaticVariableReference;
import at.aau.serg.soot.decorators.StaticVariableReferenceAnalysis;
import at.aau.serg.utils.MethodInfo;
import at.aau.serg.utils.TypeAdapter;
import org.junit.jupiter.api.Test;
import sootup.core.types.PrimitiveType;

import java.util.Set;

import static at.aau.serg.soot.SootAnalysisTests.CLASS_IDENTIFIER;
import static at.aau.serg.soot.SootAnalysisTests.CLASS_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StaticVariableRefTest {
    static final String METHOD_NAME = "calculate";

    @Test
    public void testStaticVariableReferences() {
        MethodInfo methodInfo = new MethodInfo("", CLASS_PATH, CLASS_IDENTIFIER + "." + METHOD_NAME);
        SootAnalysis sootAnalysis = new SootAnalysis(methodInfo);

        Set<AnalysisResult> references = new StaticVariableReferenceAnalysis(sootAnalysis).analyse();

        assertEquals(1, references.size());
        assertTrue((references.iterator().next() instanceof StaticVariableReference));
        StaticVariableReference expected = new StaticVariableReference("B", "y", new TypeAdapter(PrimitiveType.getInt()), ReferenceType.READ);

        assertEquals(expected, references.iterator().next());
    }

    @Test
    public void testStaticVariableWrite() {
        MethodInfo methodInfo = new MethodInfo("", CLASS_PATH, "testfiles.staticVars.A.snippet");
        AnalysisBuilder analysisBuilder = new AnalysisBuilder(new SootAnalysis(methodInfo));
        Analysis analysis = analysisBuilder.staticVariableWrite().build();

        Set<AnalysisResult> results = analysis.analyse();

        assertEquals(1, results.size());
        assertEquals(new StaticVariableReference("B", "x", new TypeAdapter(PrimitiveType.getInt()), ReferenceType.WRITE), results.iterator().next());
    }

    @Test
    public void testNoStaticVariableWrite() {
        MethodInfo methodInfo = new MethodInfo("", CLASS_PATH, "testfiles.staticVars.NoStaticWrite.snippet");
        AnalysisBuilder analysisBuilder = new AnalysisBuilder(new SootAnalysis(methodInfo));
        Analysis analysis = analysisBuilder.staticVariableWrite().build();

        Set<AnalysisResult> results = analysis.analyse();

        assertTrue(results.isEmpty());
    }
}
