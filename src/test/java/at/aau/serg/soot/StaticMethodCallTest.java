package at.aau.serg.soot;

import at.aau.serg.soot.analysisTypes.AnalysisResult;
import at.aau.serg.soot.analysisTypes.StaticMethodCall;
import at.aau.serg.soot.decorators.StaticMethodCallAnalysis;
import at.aau.serg.utils.MethodInfo;
import at.aau.serg.utils.TypeAdapter;
import org.junit.jupiter.api.Test;
import sootup.core.types.PrimitiveType;

import java.util.Set;

import static at.aau.serg.soot.SootAnalysisTests.CLASS_IDENTIFIER;
import static at.aau.serg.soot.SootAnalysisTests.CLASS_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class StaticMethodCallTest {
    private static final String METHOD_NAME = "calculate";

    @Test
    public void testStaticMethodCalls() {
        SootAnalysis sootAnalysis = new SootAnalysis(new MethodInfo("", CLASS_PATH, CLASS_IDENTIFIER+ "." + METHOD_NAME));

        Set<AnalysisResult> calls = new StaticMethodCallAnalysis(sootAnalysis).analyse();

        assertEquals(1, calls.size());
        assertTrue((calls.iterator().next() instanceof StaticMethodCall));
        assertEquals(new StaticMethodCall("B", "x", new TypeAdapter(PrimitiveType.getInt())), calls.iterator().next());
    }
}
