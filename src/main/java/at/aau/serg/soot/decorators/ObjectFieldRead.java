package at.aau.serg.soot.decorators;

import at.aau.serg.soot.Analysis;
import at.aau.serg.soot.analysisTypes.AnalysisResult;

import java.util.Set;

public class ObjectFieldRead extends AnalysisDecorator{
    public ObjectFieldRead(Analysis analysis) {
        super(analysis);
    }

    @Override
    public Set<AnalysisResult> analyse() {
        return super.analyse();
    }
}
