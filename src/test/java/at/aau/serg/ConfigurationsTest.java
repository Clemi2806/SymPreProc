package at.aau.serg;

import at.aau.serg.cli.Configurations;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigurationsTest {

    @BeforeEach
    void reset(){
        Configurations.reset();
    }

    @Test
    public void uninitializedConfigurationsTest() {
        IllegalStateException exception = assertThrows(IllegalStateException.class, Configurations::getInstance);

        assertEquals("Configurations not initialized yet", exception.getMessage());
    }

    @Test
    public void testExample1() {
        Configurations configurations = assertDoesNotThrow(() -> Configurations.getInstance(Paths.get("src/test/resources/example1.cfg")));

        assertEquals(2,configurations.getPropertyAsStringArray("methods").length);
        assertEquals("java.io.PrintStream.printf", configurations.getPropertyAsStringArray("methods")[0]);
        assertEquals("testfiles.markedMethods.ObjectMethod.setX", configurations.getPropertyAsStringArray("methods")[1]);
    }

    @Test
    public void testExample2() {
        Configurations configurations = assertDoesNotThrow(() -> Configurations.getInstance(Paths.get("src/test/resources/example2.cfg")));

        assertEquals(0,configurations.getPropertyAsStringArray("methods").length);
    }
}
