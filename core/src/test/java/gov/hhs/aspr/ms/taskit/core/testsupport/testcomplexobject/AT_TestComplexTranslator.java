package gov.hhs.aspr.ms.taskit.core.testsupport.testcomplexobject;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.taskit.core.translation.Translator;
import gov.hhs.aspr.ms.taskit.core.translation.TranslatorId;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;

public class AT_TestComplexTranslator {

    @Test
    @UnitTestMethod(target = TestComplexObjectTranslator.class, name = "getTranslator", args = {})
    public void testGetTranslator() {
        Translator testComplexTranslator = TestComplexObjectTranslator.getTranslator();

        assertEquals(TestComplexObjectTranslatorId.TRANSLATOR_ID, testComplexTranslator.getTranslatorId());
        Set<TranslatorId> expectedDependencies = new LinkedHashSet<>();

        assertEquals(expectedDependencies, testComplexTranslator.getTranslatorDependencies());
    }
}
