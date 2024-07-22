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
    public void testSootAnalysis() {
        SootAnalysis sootAnalysis = new SootAnalysis(CLASS_PATH, CLASS_IDENTIFIER, METHOD_NAME);

        Set<StaticMethodCall> calls = sootAnalysis.getStaticMethodCalls();

        assertEquals(1, calls.size());
        StaticMethodCall call = calls.iterator().next();
        assertEquals(new StaticMethodCall("x", PrimitiveType.getInt()), call);
    }
}
