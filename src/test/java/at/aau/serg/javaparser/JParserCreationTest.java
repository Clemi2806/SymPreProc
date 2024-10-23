package at.aau.serg.javaparser;

import at.aau.serg.utils.MethodInfo;
import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

public class JParserCreationTest {

    @Test
    public void createJParserTest() {
        MethodInfo info1 = new MethodInfo("src/test/java", "","testfiles.A.calculate");
        MethodInfo info2 = new MethodInfo("src/test/java/", "","testfiles.A.calculate");
        assertDoesNotThrow(() -> new JParser(info1));
        assertDoesNotThrow(() -> new JParser(info2));
    }

    @Test
    public void missingFileTest() {
        MethodInfo info1 = new MethodInfo("src/test/java","", "testfiles.Y.snippet");
        FileNotFoundException fnfe = assertThrows(FileNotFoundException.class, () -> new JParser(info1));
        assertEquals("File not found at: src/test/java/testfiles/Y.java", fnfe.getMessage());
    }

    @Test
    public void missingMethodTest() {
        MethodInfo info1 = new MethodInfo("src/test/java","", "testfiles.A.snippet");
        MethodNotFoundException mnfe = assertThrows(MethodNotFoundException.class, () -> new JParser(info1));
        assertEquals("Method not found: snippet", mnfe.getMessage());
    }

}
