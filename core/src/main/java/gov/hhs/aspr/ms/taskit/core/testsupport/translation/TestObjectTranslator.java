package gov.hhs.aspr.ms.taskit.core.testsupport.translation;

import gov.hhs.aspr.ms.taskit.core.testsupport.engine.TestTaskitEngine;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.specs.TestObjectTranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.Translator;

public class TestObjectTranslator {
    private TestObjectTranslator() {
    }

    public static Translator getTranslator() {
        return Translator.builder().setTranslatorId(TestObjectTranslatorId.TRANSLATOR_ID)
                .addDependency(TestComplexObjectTranslatorId.TRANSLATOR_ID).setInitializer(translatorContext -> {
                    translatorContext.getTaskitEngineBuilder(TestTaskitEngine.Builder.class)
                            .addTranslationSpec(new TestObjectTranslationSpec());
                }).build();
    }
}