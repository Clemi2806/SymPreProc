package at.aau.serg.soot;

import at.aau.serg.soot.analysisTypes.AnalysisResult;
import at.aau.serg.soot.analysisTypes.ReferenceType;
import at.aau.serg.soot.analysisTypes.StaticVariableReference;
import at.aau.serg.soot.decorators.StaticVariableReferenceAnalysis;
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
        SootAnalysis sootAnalysis = new SootAnalysis(CLASS_PATH, CLASS_IDENTIFIER, METHOD_NAME);

        Set<AnalysisResult> references = new StaticVariableReferenceAnalysis(sootAnalysis).analyse();

        assertEquals(1, references.size());
        assertTrue((references.iterator().next() instanceof StaticVariableReference));
        StaticVariableReference expected = new StaticVariableReference("B", "y", new TypeAdapter(PrimitiveType.getInt()), ReferenceType.READ);

        assertEquals(expected, references.iterator().next());
    }

    @Test
    public void testStaticVariableWrite() {
        AnalysisBuilder analysisBuilder = new AnalysisBuilder(new SootAnalysis(CLASS_PATH, "testfiles.staticVars.A", "snippet"));
        Analysis analysis = analysisBuilder.staticVariableWrite().build();

        Set<AnalysisResult> results = analysis.analyse();

        assertEquals(1, results.size());
        assertEquals(new StaticVariableReference("B", "x", new TypeAdapter(PrimitiveType.getInt()), ReferenceType.WRITE), results.iterator().next());
    }

    @Test
    public void testNoStaticVariableWrite() {
        AnalysisBuilder analysisBuilder = new AnalysisBuilder(new SootAnalysis(CLASS_PATH, "testfiles.staticVars.NoStaticWrite", "snippet"));
        Analysis analysis = analysisBuilder.staticVariableWrite().build();

        Set<AnalysisResult> results = analysis.analyse();

        assertTrue(results.isEmpty());
    }
}
