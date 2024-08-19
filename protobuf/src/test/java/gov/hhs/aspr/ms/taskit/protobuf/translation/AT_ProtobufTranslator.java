package gov.hhs.aspr.ms.taskit.protobuf.translation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.taskit.core.testsupport.TranslatorTestSupport;
import gov.hhs.aspr.ms.taskit.core.translation.Translator;
import gov.hhs.aspr.ms.util.annotations.UnitTestForCoverage;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;

public class AT_ProtobufTranslator {

    @Test
    @UnitTestForCoverage
    public void testGetTranslationSpecs() throws ClassNotFoundException {
        Set<String> missing = TranslatorTestSupport.testGetTranslationSpecs(ProtobufTranslator.class,
                ProtobufTranslator.getTranslationSpecs());

        assertTrue(missing.isEmpty(), missing.toString());
    }

    @Test
    @UnitTestMethod(target = ProtobufTranslator.class, name = "getTranslator", args = {})
    public void testGetTranslator() {
        Translator expectedTranslator = Translator.builder()
                .setTranslatorId(ProtobufTranslatorId.TRANSLATOR_ID)
                .setInitializer((translatorContext) -> {
                })
                .build();

        assertEquals(expectedTranslator, ProtobufTranslator.getTranslator());
    }
}
