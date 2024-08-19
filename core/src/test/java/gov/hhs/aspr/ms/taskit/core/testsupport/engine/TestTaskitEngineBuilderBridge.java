package gov.hhs.aspr.ms.taskit.core.testsupport.engine;

public class TestTaskitEngineBuilderBridge {
    private TestTaskitEngine.Builder taskitEngineBuilder;

    public TestTaskitEngineBuilderBridge(TestTaskitEngine.Builder taskitEngineBuilder) {
        this.taskitEngineBuilder = taskitEngineBuilder;
    }

    public TestTaskitEngine buildWithoutInit() {
        return taskitEngineBuilder.buildWithoutInit();
    }
}
