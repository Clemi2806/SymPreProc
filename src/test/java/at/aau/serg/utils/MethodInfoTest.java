package at.aau.serg.utils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class MethodInfoTest {

    @ParameterizedTest
    @MethodSource("getValidFullySpecifiedMethodIdentifiers")
    public void validFullySpecifiedMethodIdentifier(String className, String methodName, String packageName) {
        MethodInfo methodInfo = new MethodInfo("", "", packageName + "." + className + "." + methodName);

        assertEquals(className, methodInfo.getClassName());
        assertEquals(methodName, methodInfo.getMethodName());
        assertEquals(packageName, methodInfo.getPackageName());
    }

    @ParameterizedTest
    @MethodSource("getValidFullySpecifiedMethodIdentifiersWithoutPackage")
    public void validFullySpecifiedMethodIdentifierWithoutPackage(String className, String methodName) {
        MethodInfo methodInfo = new MethodInfo("","", className + "." + methodName);

        assertEquals(className, methodInfo.getClassName());
        assertEquals(methodName, methodInfo.getMethodName());
    }

    static Stream<Arguments> getValidFullySpecifiedMethodIdentifiers() {
        return Stream.of(
                Arguments.of("MethodInfo", "snippet", "at.aau.serg.utils"),
                Arguments.of("String", "substring", "java.lang"),
                Arguments.of("ArrayList", "add", "java.util"),
                Arguments.of("HashMap", "put", "java.util"),
                Arguments.of("File", "createNewFile", "java.io"),
                Arguments.of("BufferedReader", "readLine", "java.io"),
                Arguments.of("Thread", "start", "java.lang"),
                Arguments.of("Math", "sqrt", "java.lang"),
                Arguments.of("Scanner", "nextInt", "java.util"),
                Arguments.of("Calendar", "getInstance", "java.util"),
                Arguments.of("Files", "write", "java.nio.file")

        );
    }

    static Stream<Arguments> getValidFullySpecifiedMethodIdentifiersWithoutPackage() {
        return Stream.of(
                Arguments.of("MethodInfo", "snippet"),
                Arguments.of("String", "substring"),
                Arguments.of("ArrayList", "add"),
                Arguments.of("HashMap", "put"),
                Arguments.of("File", "createNewFile"),
                Arguments.of("BufferedReader", "readLine"),
                Arguments.of("Thread", "start"),
                Arguments.of("Math", "sqrt"),
                Arguments.of("Scanner", "nextInt"),
                Arguments.of("Calendar", "getInstance"),
                Arguments.of("Files", "write")

        );
    }
}
