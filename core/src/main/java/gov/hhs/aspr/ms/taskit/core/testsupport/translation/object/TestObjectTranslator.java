package gov.hhs.aspr.ms.taskit.core.testsupport.translation.object;

import gov.hhs.aspr.ms.taskit.core.testsupport.engine.TestTaskitEngine;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.complexobject.TestComplexObjectTranslatorId;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.object.specs.TestEnumTranslationSpec;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.object.specs.TestObjectTranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.Translator;

/**
 * Translator for the TestObject
 */
public class TestObjectTranslator {
    private TestObjectTranslator() {
    }

    /**
     * @return the Translator for the TestObject
     */
    public static Translator getTranslator() {
        return Translator.builder().setTranslatorId(TestObjectTranslatorId.TRANSLATOR_ID)
                .addDependency(TestComplexObjectTranslatorId.TRANSLATOR_ID)
                .setInitializer(translatorContext -> {
                    translatorContext.getTaskitEngineBuilder(TestTaskitEngine.Builder.class)
                            .addTranslationSpec(new TestObjectTranslationSpec())
                            .addTranslationSpec(new TestEnumTranslationSpec());
                }).build();
    }
}
