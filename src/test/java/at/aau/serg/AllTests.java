package at.aau.serg;

import at.aau.serg.soot.SootAnalysisTest;
import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.Suite;
import org.junit.platform.suite.api.SuiteDisplayName;

@Suite
@SuiteDisplayName("All tests")
@SelectClasses({ConfigurationsTest.class, JParserTest.class, SootAnalysisTest.class})
public class AllTests {
}
