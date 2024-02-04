package gov.hhs.aspr.ms.taskit.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.taskit.core.testsupport.TestTranslationEngine;
import gov.hhs.aspr.ms.util.annotations.UnitTestConstructor;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;
import gov.hhs.aspr.ms.util.errors.ContractException;

public class AT_TranslatorContext {

    @Test
    @UnitTestConstructor(target = TranslatorContext.class, args = { TranslationEngine.Builder.class })
    public void testConstructor() {
        TranslatorContext translatorContext = new TranslatorContext(TestTranslationEngine.builder());

        assertNotNull(translatorContext);
    }

    @Test
    @UnitTestMethod(target = TranslatorContext.class, name = "getTranslationEngineBuilder", args = { Class.class })
    public void testGetTranslationEngineBuilder() {
        TestTranslationEngine.Builder expectedBuilder = TestTranslationEngine.builder();
        
        TranslatorContext translatorContext = new TranslatorContext(expectedBuilder);

        TestTranslationEngine.Builder actualBuilder = translatorContext
                .getTranslationEngineBuilder(TestTranslationEngine.Builder.class);
        assertTrue(expectedBuilder == actualBuilder);

        // preconditions

        // invalid class ref
        ContractException contractException = assertThrows(ContractException.class, () -> {
            translatorContext.getTranslationEngineBuilder(TranslationEngine.Builder.class);
        });

        assertEquals(CoreTranslationError.INVALID_TRANSLATION_ENGINE_BUILDER_CLASS_REF, contractException.getErrorType());
    }

}
