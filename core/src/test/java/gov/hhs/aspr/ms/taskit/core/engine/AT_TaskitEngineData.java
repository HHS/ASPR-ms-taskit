package gov.hhs.aspr.ms.taskit.core.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.taskit.core.testsupport.engine.TestTaskitEngine;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.bad.BadTranslationSpecEmptyMap;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.bad.BadTranslationSpecNullMap;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.complexobject.specs.TestComplexObjectTranslationSpec;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.object.TestObjectTranslator;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.object.specs.TestObjectTranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.TranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.Translator;
import gov.hhs.aspr.ms.taskit.core.translation.TranslatorContext;
import gov.hhs.aspr.ms.taskit.core.translation.TranslatorId;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;
import gov.hhs.aspr.ms.util.errors.ContractException;

public class AT_TaskitEngineData {
    @Test
    @UnitTestMethod(target = TaskitEngineData.Builder.class, name = "build", args = {})
    public void testBuild() {
        // preconditions:
        // uninitialized translator
        ContractException contractException = assertThrows(ContractException.class, () -> {
            TaskitEngineData.builder().addTranslator(TestObjectTranslator.getTranslator()).build();
        });

        assertEquals(TaskitError.UNINITIALIZED_TRANSLATORS, contractException.getErrorType());
        // duplicate translator
        contractException = assertThrows(ContractException.class, () -> {
            TaskitEngineData.builder()
                    .addTranslator(TestObjectTranslator.getTranslator())
                    .addTranslator(TestObjectTranslator.getTranslator()).buildWithoutInit();
        });

        assertEquals(TaskitError.DUPLICATE_TRANSLATOR, contractException.getErrorType());

        // missing translator
        contractException = assertThrows(ContractException.class, () -> {
            TaskitEngineData.builder()
                    .addTranslator(TestObjectTranslator.getTranslator()).buildWithoutInit();
        });

        assertEquals(TaskitError.MISSING_TRANSLATOR, contractException.getErrorType());

        // circular translator dependencies
        contractException = assertThrows(ContractException.class, () -> {
            TranslatorId translatorId1 = new TranslatorId() {
            };
            TranslatorId translatorId2 = new TranslatorId() {
            };
            Translator translator1 = Translator.builder().setTranslatorId(translatorId1).addDependency(translatorId2)
                    .setInitializer((c) -> {
                    }).build();
            Translator translator2 = Translator.builder().setTranslatorId(translatorId2).addDependency(translatorId1)
                    .setInitializer((c) -> {
                    }).build();
            TaskitEngineData.builder()
                    .addTranslator(translator1)
                    .addTranslator(translator2).buildWithoutInit();
        });

        assertEquals(TaskitError.CIRCULAR_TRANSLATOR_DEPENDENCIES, contractException.getErrorType());

        // no translation specs were added
        contractException = assertThrows(ContractException.class, () -> {
            TaskitEngineData.builder().build();
        });

        assertEquals(TaskitError.NO_TRANSLATION_SPECS, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TaskitEngineData.Builder.class, name = "addTranslationSpec", args = {
            TranslationSpec.class })
    public void testAddTranslationSpec() {
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec testComplexObjectTranslationSpec = new TestComplexObjectTranslationSpec();

        TaskitEngineData.Builder builder = TaskitEngineData.builder();

        TaskitEngineData taskitEngineData = builder
                .addTranslationSpec(testObjectTranslationSpec)
                .addTranslationSpec(testComplexObjectTranslationSpec).build();

        // show that the translation specs are retrievable by their own app and input
        // classes
        assertEquals(testObjectTranslationSpec,
                taskitEngineData.classToTranslationSpecMap.get(testObjectTranslationSpec.getAppObjectClass()));
        assertEquals(testObjectTranslationSpec,
                taskitEngineData.classToTranslationSpecMap.get(testObjectTranslationSpec.getInputObjectClass()));

        assertEquals(testComplexObjectTranslationSpec,
                taskitEngineData.classToTranslationSpecMap.get(testComplexObjectTranslationSpec.getAppObjectClass()));
        assertEquals(testComplexObjectTranslationSpec,
                taskitEngineData.classToTranslationSpecMap.get(testComplexObjectTranslationSpec.getInputObjectClass()));

        // preconditions
        // translationSpec is null
        ContractException contractException = assertThrows(ContractException.class, () -> {
            builder.addTranslationSpec(null);
        });

        assertEquals(TaskitError.NULL_TRANSLATION_SPEC, contractException.getErrorType());

        // null translationSpecToClassMap
        contractException = assertThrows(ContractException.class, () -> {
            BadTranslationSpecNullMap badTranslationSpecNullMap = new BadTranslationSpecNullMap();
            builder.addTranslationSpec(badTranslationSpecNullMap);
        });

        assertEquals(TaskitError.NULL_TRANSLATION_SPEC_CLASS_MAP,
                contractException.getErrorType());

        // empty translationSpecToClassMap
        contractException = assertThrows(ContractException.class, () -> {
            BadTranslationSpecEmptyMap badTranslationSpecEmptyMap = new BadTranslationSpecEmptyMap();
            builder.addTranslationSpec(badTranslationSpecEmptyMap);
        });

        assertEquals(TaskitError.EMPTY_TRANSLATION_SPEC_CLASS_MAP,
                contractException.getErrorType());

        // if the translation spec has already been added (same, but different
        // instances)
        contractException = assertThrows(ContractException.class, () -> {
            TestObjectTranslationSpec testObjectTranslationSpec1 = new TestObjectTranslationSpec();
            TestObjectTranslationSpec testObjectTranslationSpec2 = new TestObjectTranslationSpec();

            TaskitEngineData.builder()
                    .addTranslationSpec(testObjectTranslationSpec1)
                    .addTranslationSpec(testObjectTranslationSpec2);
        });

        assertEquals(TaskitError.DUPLICATE_TRANSLATION_SPEC, contractException.getErrorType());

        // if the translation spec has already been added (exact same instance)
        contractException = assertThrows(ContractException.class, () -> {
            TestObjectTranslationSpec testObjectTranslationSpec1 = new TestObjectTranslationSpec();

            TaskitEngineData.builder()
                    .addTranslationSpec(testObjectTranslationSpec1)
                    .addTranslationSpec(testObjectTranslationSpec1);
        });

        assertEquals(TaskitError.DUPLICATE_TRANSLATION_SPEC, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TaskitEngineData.Builder.class, name = "addTranslator", args = { Translator.class })
    public void testAddTranslator() {
        Translator translator = TestObjectTranslator.getTranslator();
        TranslatorContext translatorContext = new TranslatorContext(TestTaskitEngine.builder());
        translator.initialize(translatorContext);

        TaskitEngineData.builder().addTranslator(translator);

        // preconditions
        // null translator
        ContractException contractException = assertThrows(ContractException.class, () -> {
            TaskitEngineData.builder().addTranslator(null);
        });

        assertEquals(TaskitError.NULL_TRANSLATOR, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TaskitEngineData.class, name = "builder", args = {})
    public void testBuilder() {
        // nothing to test
    }
}
