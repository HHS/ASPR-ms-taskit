package gov.hhs.aspr.ms.taskit.core.testsupport.translation.complexobject;

import gov.hhs.aspr.ms.taskit.core.testsupport.engine.TestTaskitEngine;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.complexobject.specs.TestComplexObjectTranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.Translator;

public class TestComplexObjectTranslator {
    private TestComplexObjectTranslator() {
    }

    public static Translator getTranslator() {
        return Translator.builder().setTranslatorId(TestComplexObjectTranslatorId.TRANSLATOR_ID)
                .setInitializer(translatorContext -> {
                    translatorContext.getTaskitEngineBuilder(TestTaskitEngine.Builder.class)
                            .addTranslationSpec(new TestComplexObjectTranslationSpec());
                }).build();
    }
}
