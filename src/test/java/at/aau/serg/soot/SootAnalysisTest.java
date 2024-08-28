package at.aau.serg.soot;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import at.aau.serg.cli.Configurations;
import at.aau.serg.soot.analysisTypes.*;
import at.aau.serg.soot.decorators.StaticMethodCallAnalysis;
import at.aau.serg.soot.decorators.StaticVariableReferenceAnalysis;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import sootup.core.types.PrimitiveType;

import sootup.core.types.Type;

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

    @Test
    public void testObjectFieldWrite() {
        AnalysisBuilder analysisBuilder = new AnalysisBuilder(new SootAnalysis(CLASS_PATH, "testfiles.objects.A", "snippet"));
        Analysis analysis = analysisBuilder.objectFieldWrite().build();

        Set<AnalysisResult> results = analysis.analyse();

        assertEquals(1, results.size());
        assertEquals(1, results.stream().map(ObjectFieldReference.class::cast).filter(obj -> obj.getObjectName().equals("bStatic")).count());

        assertEquals("V_B_bStatic_y", results.stream().map(ObjectFieldReference.class::cast).filter(obj -> obj.getObjectName().equals("bStatic")).findFirst().get().getNewVariableName());
    }

    @Test
    public void testObjectFieldRead() {
        AnalysisBuilder analysisBuilder = new AnalysisBuilder(new SootAnalysis(CLASS_PATH, "testfiles.objects.A", "snippet"));
        Analysis analysis = analysisBuilder.objectFieldRead().build();

        Set<AnalysisResult> results = analysis.analyse();

        assertEquals(1, results.size());
        ObjectFieldReference ofr = (ObjectFieldReference) results.iterator().next();
        assertEquals("V_B_bStatic_y", ofr.getNewVariableName());
        assertEquals(ReferenceType.READ,ofr.getReferenceType());
    }

    @Test
    public void markedMethodsTest() {
        AnalysisBuilder analysisBuilder = new AnalysisBuilder(new SootAnalysis(CLASS_PATH, "testfiles.markedMethods.A", "snippet"));
        Analysis analysis = analysisBuilder.markedMethodCall().build();

        try (MockedStatic<Configurations> configurationsClassMock = mockStatic(Configurations.class)) {

            Configurations configurationsMock = mock(Configurations.class);
            when(configurationsMock.getPropertyAsStringArray("methods")).thenReturn(new String[]{"java.io.PrintStream#println"});
            configurationsClassMock.when(Configurations::getInstance).thenReturn(configurationsMock);
            configurationsClassMock.when(Configurations::exists).thenReturn(true);

            Set<AnalysisResult> results = analysis.analyse();

            assertEquals(1, results.size());
            MarkedMethod mm = (MarkedMethod) results.iterator().next();
            assertEquals(1, mm.getParameterTypes().size());
            assertEquals("java.lang.String",mm.getParameterTypes().get(0).toString());
            assertEquals("M_PrintStream_println", mm.getNewVariableName());
        }

    }

    @Test
    public void markedMethodsMultipleArgsTest() {
        AnalysisBuilder analysisBuilder = new AnalysisBuilder(new SootAnalysis(CLASS_PATH, "testfiles.markedMethods.MultipleArgs", "snippet"));
        Analysis analysis = analysisBuilder.markedMethodCall().build();

        try (MockedStatic<Configurations> configurationsClassMock = mockStatic(Configurations.class)) {

            Configurations configurationsMock = mock(Configurations.class);
            when(configurationsMock.getPropertyAsStringArray("methods")).thenReturn(new String[]{"java.io.PrintStream#printf", "java.lang.Math#copySign"});
            configurationsClassMock.when(Configurations::getInstance).thenReturn(configurationsMock);
            configurationsClassMock.when(Configurations::exists).thenReturn(true);

            Set<AnalysisResult> results = analysis.analyse();

            assertEquals(1, results.size());
            MarkedMethod mm = (MarkedMethod) results.iterator().next();
            assertEquals(2, mm.getParameterTypes().size());
            assertEquals("float",mm.getParameterTypes().get(0).toString());
            assertEquals("float",mm.getParameterTypes().get(1).toString());
            assertEquals("M_Math_copySign", mm.getNewVariableName());
        }

    }

    @Test
    public void markedMethodsObjectMethodTest() {
        AnalysisBuilder analysisBuilder = new AnalysisBuilder(new SootAnalysis(CLASS_PATH, "testfiles.markedMethods.ObjectMethod", "snippet"));
        Analysis analysis = analysisBuilder.markedMethodCall().build();

        try (MockedStatic<Configurations> configurationsClassMock = mockStatic(Configurations.class)) {

            Configurations configurationsMock = mock(Configurations.class);
            when(configurationsMock.getPropertyAsStringArray("methods")).thenReturn(new String[]{"java.io.PrintStream#printf", "testfiles.markedMethods.ObjectMethod#setX"});
            configurationsClassMock.when(Configurations::getInstance).thenReturn(configurationsMock);
            configurationsClassMock.when(Configurations::exists).thenReturn(true);

            Set<AnalysisResult> results = analysis.analyse();

            assertEquals(1, results.size());
            MarkedMethod mm = (MarkedMethod) results.iterator().next();
            assertEquals(1, mm.getParameterTypes().size());
            assertEquals("int",mm.getParameterTypes().get(0).toString());
            assertEquals("M_ObjectMethod_setX", mm.getNewVariableName());
        }

    }
}
