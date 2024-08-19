package gov.hhs.aspr.ms.taskit.protobuf.engine;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.util.annotations.UnitTestField;

public class AT_ProtobufTaskitEngineId {

    @Test
    @UnitTestField(target = ProtobufTaskitEngineId.class, name = "JSON_ENGINE_ID")
    public void testJsonTaskitEngineId() {
        assertNotNull(ProtobufTaskitEngineId.JSON_ENGINE_ID);
    }

    @Test
    @UnitTestField(target = ProtobufTaskitEngineId.class, name = "BINARY_ENGINE_ID")
    public void testBinaryTaskitEngineId() {
        assertNotNull(ProtobufTaskitEngineId.BINARY_ENGINE_ID);
    }
}
