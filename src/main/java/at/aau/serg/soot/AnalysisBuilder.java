package at.aau.serg.soot;

import at.aau.serg.soot.decorators.*;

public class AnalysisBuilder {
    private Analysis analysis;

    public AnalysisBuilder(Analysis analysis) {
        this.analysis = analysis;
    }

    public Analysis build() {
        return analysis;
    }

    public AnalysisBuilder staticMethodCall() {
        this.analysis = new StaticMethodCallAnalysis(analysis);
        return this;
    }


    public AnalysisBuilder staticVariableRef() {
        this.analysis = new StaticVariableReferenceAnalysis(analysis);
        return this;
    }


    public AnalysisBuilder staticVariableWrite() {
        this.analysis = new StaticVariableWriteAnalysis(analysis);
        return this;
    }

    public AnalysisBuilder objectFieldWrite() {
        this.analysis = new ObjectFieldWriteAnalysis(analysis);
        return this;
    }

    public AnalysisBuilder objectFieldRead() {
        this.analysis = new ObjectFieldReadAnalysis(analysis);
        return this;
    }

    public AnalysisBuilder markedMethodCall() {
        this.analysis = new MarkedMethodCallAnalysis(analysis);
        return this;
    }

    public Analysis fullAnalysis() {
        return this.staticMethodCall().staticVariableRef().staticVariableWrite().objectFieldWrite().objectFieldRead().markedMethodCall().build();
    }
}
