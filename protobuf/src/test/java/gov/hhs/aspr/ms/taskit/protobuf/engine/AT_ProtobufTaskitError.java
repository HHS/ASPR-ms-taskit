package gov.hhs.aspr.ms.taskit.protobuf.engine;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;

public class AT_ProtobufTaskitError {

    @Test
    @UnitTestMethod(target = ProtobufTaskitError.class, name = "getDescription", args = {})
    public void testGetDescription() {
        // show that each ErrorType has a non-null, non-empty description
        for (ProtobufTaskitError protobufTaskitError : ProtobufTaskitError.values()) {
            assertNotNull(protobufTaskitError.getDescription());
            assertTrue(protobufTaskitError.getDescription().length() > 0);
        }

        // show that each description is unique (ignoring case as well)
        Set<String> descriptions = new LinkedHashSet<>();
        for (ProtobufTaskitError protobufTaskitError : ProtobufTaskitError.values()) {
            boolean isUnique = descriptions.add(protobufTaskitError.getDescription().toLowerCase());
            assertTrue(isUnique, protobufTaskitError + " duplicates the description of another member");
        }
    }
}
