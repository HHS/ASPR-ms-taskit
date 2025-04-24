package gov.hhs.aspr.ms.taskit.core.testsupport.translation.testenum;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.util.annotations.UnitTestField;

public class AT_TestEnumTranslatorId {
    @Test
    @UnitTestField(target = TestEnumTranslatorId.class, name = "TRANSLATOR_ID")
    public void testTranslatorId() {
        assertNotNull(TestEnumTranslatorId.TRANSLATOR_ID);
    }
}
