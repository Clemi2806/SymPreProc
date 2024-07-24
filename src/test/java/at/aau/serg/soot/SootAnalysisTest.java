package at.aau.serg.soot;

import static org.junit.jupiter.api.Assertions.*;
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

        Set<StaticMethodCall> calls = sootAnalysis.getStaticMethodCalls();

        assertEquals(1, calls.size());
        StaticMethodCall call = calls.iterator().next();
        assertEquals(new StaticMethodCall("B", "x", PrimitiveType.getInt()), call);
    }

    @Test
    public void testStaticVariableReferences() {
        SootAnalysis sootAnalysis = new SootAnalysis(CLASS_PATH, CLASS_IDENTIFIER, METHOD_NAME);

        Set<StaticVariableReference> references = sootAnalysis.getStaticVariableReferences();

        assertEquals(1, references.size());
        StaticVariableReference expected = new StaticVariableReference("B", "y");

        assertEquals(expected, references.iterator().next());
    }
}
