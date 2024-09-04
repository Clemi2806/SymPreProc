package at.aau.serg.soot;

import at.aau.serg.cli.Configurations;
import at.aau.serg.soot.analysisTypes.AnalysisResult;
import at.aau.serg.soot.analysisTypes.MarkedMethod;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.Set;

import static at.aau.serg.soot.SootAnalysisTests.CLASS_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class MarkedMethodsTest {
    @Test
    public void markedMethodsTest() {
        AnalysisBuilder analysisBuilder = new AnalysisBuilder(new SootAnalysis(CLASS_PATH, "testfiles.markedMethods.MM", "snippet"));
        Analysis analysis = analysisBuilder.markedMethodCall().build();

        try (MockedStatic<Configurations> configurationsClassMock = mockStatic(Configurations.class)) {

            Configurations configurationsMock = mock(Configurations.class);
            when(configurationsMock.getPropertyAsStringArray("methods")).thenReturn(new String[]{"java.io.PrintStream.println"});
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
            when(configurationsMock.getPropertyAsStringArray("methods")).thenReturn(new String[]{"java.io.PrintStream.printf", "java.lang.Math.copySign"});
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
            when(configurationsMock.getPropertyAsStringArray("methods")).thenReturn(new String[]{"java.io.PrintStream.printf", "testfiles.markedMethods.ObjectMethod.setX"});
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
