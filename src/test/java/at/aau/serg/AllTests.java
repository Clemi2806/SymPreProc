package at.aau.serg;

import at.aau.serg.javaparser.JParserTests;
import at.aau.serg.soot.SootAnalysisTests;
import at.aau.serg.utils.UtilsTests;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("All tests")
@SelectClasses({ConfigurationsTest.class, JParserTests.class, SootAnalysisTests.class, UtilsTests.class, DisableAnalysisTest.class})
public class AllTests {
}
