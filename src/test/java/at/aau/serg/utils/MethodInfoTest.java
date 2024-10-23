package at.aau.serg.utils;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

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

    @ParameterizedTest
    @MethodSource("getJavaFilePathExamples")
    public void javaFilePathWithPackagesAndSourcePath(String expectedPath, String sourcePath, String methodReference) {
        MethodInfo methodInfo = new MethodInfo(sourcePath, "", methodReference);

        assertEquals(expectedPath, methodInfo.getJavaFilePath());
    }

    @ParameterizedTest
    @MethodSource("getValidFullySpecifiedMethodIdentifiersWithoutPackage")
    public void fullyQualifiedMethodNameWithoutPackage(String className, String methodName) {
        MethodInfo methodInfo = new MethodInfo("","", className + "." + methodName);
        String expected = className + "." + methodName;


        assertEquals(expected, methodInfo.getFullyQualifiedMethodName());
    }

    @ParameterizedTest
    @MethodSource("getValidFullySpecifiedMethodIdentifiersWithPackage")
    public void fullyQualifiedMethodNameWithPackage(String className, String methodName) {
        MethodInfo methodInfo = new MethodInfo("","", className + "." + methodName);
        String expected = className + "." + methodName;


        assertEquals(expected, methodInfo.getFullyQualifiedMethodName());
    }

    @ParameterizedTest
    @MethodSource("getValidFullySpecifiedMethodIdentifiersWithoutPackage")
    public void fullyQualifiedClassNameWithoutPackage(String className, String methodName) {
        MethodInfo methodInfo = new MethodInfo("","", className + "." + methodName);
        String expected = className;


        assertEquals(expected, methodInfo.getFullyQualifiedClassName());
    }

    @ParameterizedTest
    @MethodSource("getValidFullySpecifiedMethodIdentifiersWithPackage")
    public void fullyQualifiedClassNameWithPackage(String className, String methodName) {
        MethodInfo methodInfo = new MethodInfo("","", className + "." + methodName);
        String expected = className;


        assertEquals(expected, methodInfo.getFullyQualifiedClassName());
    }

    static Stream<Arguments> getJavaFilePathExamples() {
        return Stream.of(
                Arguments.of("/src/main/java/com/example/utils/StringUtils.java", "/src/main/java", "com.example.utils.StringUtils.capitalize"),
                Arguments.of("/src/main/java/com/example/service/UserService.java", "/src/main/java/", "com.example.service.UserService.getUserById"),
                Arguments.of("/src/main/java/com/example/controller/UserController.java", "/src/main/java", "com.example.controller.UserController.createUser"),
                Arguments.of("/src/main/java/com/example/repository/UserRepository.java", "/src/main/java/", "com.example.repository.UserRepository.findUserByEmail"),
                Arguments.of("/src/main/java/com/example/model/User.java", "/src/main/java", "com.example.model.User.getFullName"),
                Arguments.of("/src/main/java/com/example/security/AuthService.java", "/src/main/java", "com.example.security.AuthService.authenticate"),
                Arguments.of("/src/main/java/com/example/config/AppConfig.java", "/src/main/java", "com.example.config.AppConfig.loadConfiguration"),
                Arguments.of("/src/main/java/com/example/exception/CustomException.java", "/src/main/java", "com.example.exception.CustomException.getMessage"),
                Arguments.of("/src/main/java/com/example/utils/DateUtils.java", "/src/main/java", "com.example.utils.DateUtils.formatDate"),
                Arguments.of("/src/main/java/com/example/service/EmailService.java", "/src/main/java/", "com.example.service.EmailService.sendEmail")

        );
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

    static Stream<Arguments> getValidFullySpecifiedMethodIdentifiersWithPackage() {
        return Stream.of(
                Arguments.of("com.example.MethodInfo", "snippet"),
                Arguments.of("java.lang.String", "substring"),
                Arguments.of("java.lang.ArrayList", "add"),
                Arguments.of("java.lang.HashMap", "put"),
                Arguments.of("java.io.File", "createNewFile"),
                Arguments.of("java.io.BufferedReader", "readLine"),
                Arguments.of("java.lang.Thread", "start"),
                Arguments.of("java.lang.Math", "sqrt"),
                Arguments.of("java.util.Scanner", "nextInt"),
                Arguments.of("java.util.Calendar", "getInstance"),
                Arguments.of("java.nio.file.Files", "write")

        );
    }
}
