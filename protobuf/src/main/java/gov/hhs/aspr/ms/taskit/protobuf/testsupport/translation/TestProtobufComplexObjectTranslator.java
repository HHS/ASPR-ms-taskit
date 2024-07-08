package gov.hhs.aspr.ms.taskit.protobuf.testsupport.translation;

import gov.hhs.aspr.ms.taskit.core.testsupport.translation.complexobject.TestComplexObjectTranslatorId;
import gov.hhs.aspr.ms.taskit.core.translation.Translator;
import gov.hhs.aspr.ms.taskit.protobuf.engine.IProtobufTaskitEngineBuilder;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.translation.specs.TestProtobufComplexObjectTranslationSpec;

public class TestProtobufComplexObjectTranslator {
    private TestProtobufComplexObjectTranslator() {
    }

    public static Translator getTranslator() {
        return Translator.builder().setTranslatorId(TestComplexObjectTranslatorId.TRANSLATOR_ID)
                .setInitializer(translatorContext -> {
                    translatorContext.getTaskitEngineBuilder(IProtobufTaskitEngineBuilder.class)
                            .addTranslationSpec(new TestProtobufComplexObjectTranslationSpec());
                }).build();
    }
}
