package gov.hhs.aspr.ms.taskit.core.testsupport.translation.testenum;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.taskit.core.translation.Translator;
import gov.hhs.aspr.ms.taskit.core.translation.TranslatorId;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;

public class AT_TestEnumTranslator {
    @Test
    @UnitTestMethod(target = TestEnumTranslator.class, name = "getTranslator", args = {})
    public void testGetTranslator() {
        Translator testEnumTranslator = TestEnumTranslator.getTranslator();

        assertEquals(TestEnumTranslatorId.TRANSLATOR_ID, testEnumTranslator.getTranslatorId());

        Set<TranslatorId> expectedDependencies = new LinkedHashSet<>();
        assertEquals(expectedDependencies, testEnumTranslator.getTranslatorDependencies());
    }
}
