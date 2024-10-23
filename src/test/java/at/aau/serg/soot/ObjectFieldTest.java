package at.aau.serg.soot;

import at.aau.serg.soot.analysisTypes.AnalysisResult;
import at.aau.serg.soot.analysisTypes.ObjectFieldReference;
import at.aau.serg.soot.analysisTypes.ReferenceType;
import at.aau.serg.utils.MethodInfo;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static at.aau.serg.soot.SootAnalysisTests.CLASS_PATH;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ObjectFieldTest {

    @Test
    public void testObjectFieldWrite() {
        MethodInfo methodInfo = new MethodInfo("", CLASS_PATH, "testfiles.objects.A.snippet");
        AnalysisBuilder analysisBuilder = new AnalysisBuilder(new SootAnalysis(methodInfo));
        Analysis analysis = analysisBuilder.objectFieldWrite().build();

        Set<AnalysisResult> results = analysis.analyse();

        assertEquals(1, results.size());
        assertEquals(1, results.stream().map(ObjectFieldReference.class::cast).filter(obj -> obj.getObjectName().equals("bStatic")).count());

        assertEquals("V_B_bStatic_y", results.stream().map(ObjectFieldReference.class::cast).filter(obj -> obj.getObjectName().equals("bStatic")).findFirst().get().getNewVariableName());
    }

    @Test
    public void testObjectFieldRead() {
        MethodInfo methodInfo = new MethodInfo("", CLASS_PATH, "testfiles.objects.A.snippet");
        AnalysisBuilder analysisBuilder = new AnalysisBuilder(new SootAnalysis(methodInfo));
        Analysis analysis = analysisBuilder.objectFieldRead().build();

        Set<AnalysisResult> results = analysis.analyse();

        assertEquals(1, results.size());
        ObjectFieldReference ofr = (ObjectFieldReference) results.iterator().next();
        assertEquals("V_B_bStatic_y", ofr.getNewVariableName());
        assertEquals(ReferenceType.READ,ofr.getReferenceType());
    }
}
