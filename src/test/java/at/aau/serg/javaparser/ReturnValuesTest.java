package at.aau.serg.javaparser;

import org.junit.jupiter.api.Test;

import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

public class ReturnValuesTest {

    @Test
    public void singleReturnValueTest() {
        ReturnValues rv = new ReturnValues("Hello there!");

        assertEquals(1, rv.size());
        Object object = rv.iterator().next();
        assertInstanceOf(String.class, object);
        assertEquals("Hello there!", object);
    }

    @Test
    public void multiReturnValueTest() {
        ReturnValues rv = new ReturnValues("Hello there!", 1);

        assertEquals(2, rv.size());
        Iterator<?> iterator = rv.iterator();
        assertEquals("Hello there!", iterator.next());
        assertEquals(1, iterator.next());
    }

    @Test
    public void getValuesTest() {
        ReturnValues rv = new ReturnValues("Hello there!");

        assertEquals(1, rv.getValues().size());
        assertEquals("Hello there!", rv.getValues().get(0));
        assertThrows(UnsupportedOperationException.class, () -> rv.getValues().remove(0));
        assertThrows(UnsupportedOperationException.class, () -> rv.getValues().add(0));
    }
}
