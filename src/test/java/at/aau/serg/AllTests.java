package at.aau.serg;

import at.aau.serg.javaparser.JParserTests;
import at.aau.serg.soot.SootAnalysisTests;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("All tests")
@SelectClasses({ConfigurationsTest.class, JParserTests.class, SootAnalysisTests.class})
public class AllTests {
}
