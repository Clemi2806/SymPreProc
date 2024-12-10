package at.aau.serg;

import at.aau.serg.soot.Analysis;
import at.aau.serg.soot.AnalysisBuilder;
import org.apache.commons.cli.CommandLine;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class DisableAnalysisTest {
    private static final String METHOD_CALL = "1";
    private static final String VAR_READ = "2";
    private static final String VAR_WRITE = "3";
    private static final String OBJ_WRITE = "4";
    private static final String OBJ_READ = "5";
    private static final String MARKED_METHOD = "6";

    @Mock
    Analysis analysis;

    @Spy
    AnalysisBuilder analysisBuilder = new AnalysisBuilder(analysis);

    @Mock
    CommandLine cmd;

    @BeforeEach
    void reset() {
        Mockito.reset(analysisBuilder, cmd, analysis);
    }

    private void setupMocks(List<String> options) {
        when(cmd.hasOption(anyString())).then(invocationOnMock -> options.contains(invocationOnMock.getArgument(0).toString()));
    }

    @Test
    public void allEnabled() {
        setupMocks(new ArrayList<>()); // disable nothing

        Main.configureAnalysis(cmd, analysisBuilder);

        verify(analysisBuilder).markedMethodCall();
        verify(analysisBuilder).staticVariableRef();
        verify(analysisBuilder).staticVariableWrite();
        verify(analysisBuilder).objectFieldRead();
        verify(analysisBuilder).staticMethodCall();
        verify(analysisBuilder).objectFieldWrite();
    }

    @Test
    public void disableMarkedMethodCall() {
        List<String> options = new ArrayList<>();
        options.add(MARKED_METHOD);
        setupMocks(options);

        Main.configureAnalysis(cmd, analysisBuilder);

        verify(analysisBuilder, times(0)).markedMethodCall();
        verify(analysisBuilder).staticVariableRef();
        verify(analysisBuilder).staticVariableWrite();
        verify(analysisBuilder).objectFieldRead();
        verify(analysisBuilder).staticMethodCall();
        verify(analysisBuilder).objectFieldWrite();
    }

    @Test
    public void disableStaticVariableRef() {
        List<String> options = new ArrayList<>();
        options.add(VAR_READ);
        setupMocks(options);

        Main.configureAnalysis(cmd, analysisBuilder);

        verify(analysisBuilder).markedMethodCall();
        verify(analysisBuilder, times(0)).staticVariableRef();
        verify(analysisBuilder).staticVariableWrite();
        verify(analysisBuilder).objectFieldRead();
        verify(analysisBuilder).staticMethodCall();
        verify(analysisBuilder).objectFieldWrite();
    }

    @Test
    public void disableStaticVariableWrite() {
        List<String> options = new ArrayList<>();
        options.add(VAR_WRITE);
        setupMocks(options);

        Main.configureAnalysis(cmd, analysisBuilder);

        verify(analysisBuilder).markedMethodCall();
        verify(analysisBuilder).staticVariableRef();
        verify(analysisBuilder, times(0)).staticVariableWrite();
        verify(analysisBuilder).objectFieldRead();
        verify(analysisBuilder).staticMethodCall();
        verify(analysisBuilder).objectFieldWrite();
    }

    @Test
    public void disableObjectFieldRead() {
        List<String> options = new ArrayList<>();
        options.add(OBJ_READ);
        setupMocks(options);

        Main.configureAnalysis(cmd, analysisBuilder);

        verify(analysisBuilder).markedMethodCall();
        verify(analysisBuilder).staticVariableRef();
        verify(analysisBuilder).staticVariableWrite();
        verify(analysisBuilder, times(0)).objectFieldRead();
        verify(analysisBuilder).staticMethodCall();
        verify(analysisBuilder).objectFieldWrite();
    }

    @Test
    public void disableObjectFieldWrite() {
        List<String> options = new ArrayList<>();
        options.add(OBJ_WRITE);
        setupMocks(options);

        Main.configureAnalysis(cmd, analysisBuilder);

        verify(analysisBuilder).markedMethodCall();
        verify(analysisBuilder).staticVariableRef();
        verify(analysisBuilder).staticVariableWrite();
        verify(analysisBuilder).objectFieldRead();
        verify(analysisBuilder).staticMethodCall();
        verify(analysisBuilder, times(0)).objectFieldWrite();
    }

    @Test
    public void disableStaticMethodCall() {
        List<String> options = new ArrayList<>();
        options.add(METHOD_CALL);
        setupMocks(options);

        Main.configureAnalysis(cmd, analysisBuilder);

        verify(analysisBuilder).markedMethodCall();
        verify(analysisBuilder).staticVariableRef();
        verify(analysisBuilder).staticVariableWrite();
        verify(analysisBuilder).objectFieldRead();
        verify(analysisBuilder, times(0)).staticMethodCall();
        verify(analysisBuilder).objectFieldWrite();
    }
}
