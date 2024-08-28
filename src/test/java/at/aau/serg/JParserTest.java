package at.aau.serg;

import at.aau.serg.javaparser.JParser;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class JParserTest {

    @Test
    public void createJParserTest() {
        assertDoesNotThrow(() -> new JParser("src/test/java", "testfiles.A", "calculate"));
        assertDoesNotThrow(() -> new JParser("src/test/java/", "testfiles.A", "calculate"));
    }

}
