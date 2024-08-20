package at.aau.serg.soot;

import static org.junit.jupiter.api.Assertions.*;

import at.aau.serg.soot.analysisTypes.AnalysisResult;
import at.aau.serg.soot.analysisTypes.ReferenceType;
import at.aau.serg.soot.analysisTypes.StaticMethodCall;
import at.aau.serg.soot.analysisTypes.StaticVariableReference;
import at.aau.serg.soot.decorators.StaticMethodCallAnalysis;
import at.aau.serg.soot.decorators.StaticVariableReferenceAnalysis;
import org.junit.jupiter.api.Test;
import sootup.core.types.PrimitiveType;

import java.util.Set;

public class SootAnalysisTest {
    static final String CLASS_PATH = "target/test-classes/";
    static final String CLASS_IDENTIFIER = "testfiles.A";
    static final String METHOD_NAME = "calculate";

    @Test
    public void testStaticMethodCalls() {
        SootAnalysis sootAnalysis = new SootAnalysis(CLASS_PATH, CLASS_IDENTIFIER, METHOD_NAME);

        Set<AnalysisResult> calls = new StaticMethodCallAnalysis(sootAnalysis).analyse();

        assertEquals(1, calls.size());
        assertTrue((calls.iterator().next() instanceof StaticMethodCall));
        assertEquals(new StaticMethodCall("B", "x", PrimitiveType.getInt()), calls.iterator().next());
    }

    @Test
    public void testStaticVariableReferences() {
        SootAnalysis sootAnalysis = new SootAnalysis(CLASS_PATH, CLASS_IDENTIFIER, METHOD_NAME);

        Set<AnalysisResult> references = new StaticVariableReferenceAnalysis(sootAnalysis).analyse();

        assertEquals(1, references.size());
        assertTrue((references.iterator().next() instanceof StaticVariableReference));
        StaticVariableReference expected = new StaticVariableReference("B", "y", PrimitiveType.getInt(), ReferenceType.READ);

        assertEquals(expected, references.iterator().next());
    }

    @Test
    public void testStaticVariableWrite() {
        AnalysisBuilder analysisBuilder = new AnalysisBuilder(new SootAnalysis(CLASS_PATH, "testfiles.staticVars.A", "snippet"));
        Analysis analysis = analysisBuilder.staticVariableWrite().build();

        Set<AnalysisResult> results = analysis.analyse();

        assertEquals(1, results.size());
        assertEquals(new StaticVariableReference("B", "x", PrimitiveType.getInt(), ReferenceType.WRITE), results.iterator().next());
    }

    @Test
    public void testNoStaticVariableWrite() {
        AnalysisBuilder analysisBuilder = new AnalysisBuilder(new SootAnalysis(CLASS_PATH, "testfiles.staticVars.NoStaticWrite", "snippet"));
        Analysis analysis = analysisBuilder.staticVariableWrite().build();

        Set<AnalysisResult> results = analysis.analyse();

        assertTrue(results.isEmpty());
    }
}
