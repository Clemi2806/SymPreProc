package at.aau.serg.javaparser;

import com.github.javaparser.ast.body.MethodDeclaration;
import fj.data.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class JParserParseTest {

    @Test
    public void noMethodFoundTest() {
        JParser parser = new JParser(null, new MethodDeclaration().setBody(null));
        IllegalStateException ise = assertThrows(IllegalStateException.class, () -> parser.parse(Collections.EMPTY_SET));
        assertEquals("Unable to load method body", ise.getMessage());
    }
}
