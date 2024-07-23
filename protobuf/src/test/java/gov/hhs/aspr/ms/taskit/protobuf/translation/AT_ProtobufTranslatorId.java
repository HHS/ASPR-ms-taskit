package gov.hhs.aspr.ms.taskit.protobuf.translation;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.util.annotations.UnitTestField;

public class AT_ProtobufTranslatorId {
    @Test
    @UnitTestField(target = ProtobufTranslatorId.class, name = "TRANSLATOR_ID")
    public void testTranslatorId() {
        assertNotNull(ProtobufTranslatorId.TRANSLATOR_ID);
    }
}
