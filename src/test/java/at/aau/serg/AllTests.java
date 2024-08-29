package at.aau.serg;

import at.aau.serg.javaparser.JParserCreationTest;
import at.aau.serg.soot.SootAnalysisTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("All tests")
@SelectClasses({ConfigurationsTest.class, JParserCreationTest.class, SootAnalysisTest.class})
public class AllTests {
}
