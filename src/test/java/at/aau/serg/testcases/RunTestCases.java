package at.aau.serg.testcases;

import at.aau.serg.Main;
import at.aau.serg.cli.Configurations;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class RunTestCases {

    @ParameterizedTest
    @MethodSource("configurationSupplier")
    public void noExceptionTest(String config) {
        assertDoesNotThrow(() -> Main.main(config.split(" ")));
    }

    public static Stream<Arguments> configurationSupplier() {
        return Stream.of(
    Arguments.of("--sourcepath src/test/java/ --classpath target/test-classes/ --output target/PF.java --method testfiles.markedMethods.PrintfMethod.snippet --config src/test/resources/printfConfig.cfg"),
            Arguments.of("--sourcepath src/test/java/ --classpath target/test-classes/ --output target/Object.java --method testfiles.objects.A.snippet"),
            Arguments.of("--sourcepath src/test/java/ --classpath target/test-classes/ --output target/MM_MA.java --method testfiles.markedMethods.MultipleArgs.snippet --config src/test/resources/markedMethodsExample.cfg"),
            Arguments.of("--sourcepath src/test/java/ --classpath target/test-classes/ --output target/UserMain.java --method testfiles.objects.UserMain.snippet --config src/test/resources/exampleUser.cfg"),
            Arguments.of("--sourcepath src/test/java/ --classpath target/test-classes/ --output target/Write.java --method testfiles.staticVars.A.snippet"),
            Arguments.of("--sourcepath src/test/java/ --classpath target/test-classes/ --output target/MM.java --method testfiles.markedMethods.MM.snippet --config src/test/resources/markedMethodsExample.cfg"),
            Arguments.of("--sourcepath src/test/java/ --classpath target/test-classes/ --output target/MMRW.java --method testfiles.markedMethods.ReadWriteMarkedMethod.snippet --config src/test/resources/markedMethodsExample.cfg")
        );
    }
}
