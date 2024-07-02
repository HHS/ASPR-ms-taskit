package gov.hhs.aspr.ms.taskit.core.testsupport.engine;

import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngineId;

public final class TestTaskitEngineId implements TaskitEngineId {
    public static final TaskitEngineId TEST_ENGINE_ID = new TestTaskitEngineId();

    private TestTaskitEngineId() {
    }
}
