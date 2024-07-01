package gov.hhs.aspr.ms.taskit.core.testsupport.testobject;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.taskit.core.testsupport.translation.TestObjectTranslatorId;
import gov.hhs.aspr.ms.util.annotations.UnitTestField;

public class AT_TestObjectTranslatorId {

    @Test
    @UnitTestField(target = TestObjectTranslatorId.class, name = "TRANSLATOR_ID")
    public void testTranslatorId() {
        assertNotNull(TestObjectTranslatorId.TRANSLATOR_ID);
    }
}
