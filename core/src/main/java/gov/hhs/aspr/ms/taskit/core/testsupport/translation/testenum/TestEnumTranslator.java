package gov.hhs.aspr.ms.taskit.core.testsupport.translation.testenum;

import gov.hhs.aspr.ms.taskit.core.testsupport.engine.TestTaskitEngine;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.testenum.specs.TestEnumTranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.Translator;

/**
 * Translator for the TestEnum
 */
public class TestEnumTranslator {
    private TestEnumTranslator() {
    }

    /**
     * @return the Translator for the TestEnum
     */
    public static Translator getTranslator() {
        return Translator.builder().setTranslatorId(TestEnumTranslatorId.TRANSLATOR_ID)
                .setInitializer(translatorContext -> {
                    translatorContext.getTaskitEngineBuilder(TestTaskitEngine.Builder.class)
                            .addTranslationSpec(new TestEnumTranslationSpec());
                }).build();
    }
}
