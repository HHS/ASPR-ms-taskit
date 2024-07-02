package gov.hhs.aspr.ms.taskit.core.testsupport.engine;

import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngineId;

/** 
 * Test Engine Id for the TestTaskitEngine
 * 
 * package access due to not allowing outside access.
 */
final class TestTaskitEngineId implements TaskitEngineId {
    public static final TaskitEngineId TEST_ENGINE_ID = new TestTaskitEngineId();

    private TestTaskitEngineId() {
    }
}
