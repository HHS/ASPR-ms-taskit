package gov.hhs.aspr.ms.taskit.core.testsupport.translation.complexobject;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.util.annotations.UnitTestField;

public class AT_TestComplexTranslatorId {

    @Test
    @UnitTestField(target = TestComplexObjectTranslatorId.class, name = "TRANSLATOR_ID")
    public void testTranslatorId() {
        assertNotNull(TestComplexObjectTranslatorId.TRANSLATOR_ID);
    }
}
