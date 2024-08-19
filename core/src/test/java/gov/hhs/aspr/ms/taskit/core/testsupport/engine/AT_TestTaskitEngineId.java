package gov.hhs.aspr.ms.taskit.core.testsupport.engine;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.util.annotations.UnitTestField;

public class AT_TestTaskitEngineId {

    @Test
    @UnitTestField(target = TestTaskitEngineId.class, name = "TEST_ENGINE_ID")
    public void testTestTaskitEngineId() {
        assertNotNull(TestTaskitEngineId.TEST_ENGINE_ID);
    }
}
