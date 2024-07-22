package gov.hhs.aspr.ms.taskit.core.testsupport.translation.complexobject;

import gov.hhs.aspr.ms.taskit.core.testsupport.engine.TestTaskitEngine;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.complexobject.specs.TestComplexObjectTranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.Translator;

/**
 * Test translator for the TestComplexObject
 */
public class TestComplexObjectTranslator {
    private TestComplexObjectTranslator() {
    }

    /**
     * @return the translator for the TestComplexObject
     */
    public static Translator getTranslator() {
        return Translator.builder().setTranslatorId(TestComplexObjectTranslatorId.TRANSLATOR_ID)
                .setInitializer(translatorContext -> {
                    translatorContext.getTaskitEngineBuilder(TestTaskitEngine.Builder.class)
                            .addTranslationSpec(new TestComplexObjectTranslationSpec());
                }).build();
    }
}
