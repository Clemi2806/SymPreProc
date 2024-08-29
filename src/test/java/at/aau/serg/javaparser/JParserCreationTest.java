package at.aau.serg.javaparser;

import org.junit.jupiter.api.Test;

import java.io.FileNotFoundException;

import static org.junit.jupiter.api.Assertions.*;

public class JParserCreationTest {

    @Test
    public void createJParserTest() {
        assertDoesNotThrow(() -> new JParser("src/test/java", "testfiles.A", "calculate"));
        assertDoesNotThrow(() -> new JParser("src/test/java/", "testfiles.A", "calculate"));
    }

    @Test
    public void missingFileTest() {
        FileNotFoundException fnfe = assertThrows(FileNotFoundException.class, () -> new JParser("src/test/java", "testfiles.Y", "snippet"));
        assertEquals("File not found at: src/test/java/testfiles/Y.java", fnfe.getMessage());
    }

    @Test
    public void missingMethodTest() {
        MethodNotFoundException mnfe = assertThrows(MethodNotFoundException.class, () -> new JParser("src/test/java", "testfiles.A", "snippet"));
        assertEquals("Method not found: snippet", mnfe.getMessage());
    }

}
