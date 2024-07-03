package gov.hhs.aspr.ms.taskit.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.hhs.aspr.ms.taskit.core.engine.TaskitCoreError;
import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngine;
import gov.hhs.aspr.ms.taskit.core.testsupport.engine.TestTaskitEngine;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestObjectWrapper;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.complexobject.specs.TestComplexObjectTranslationSpec;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.object.TestObjectTranslator;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.object.specs.TestObjectTranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.TranslationSpec;
import gov.hhs.aspr.ms.util.errors.ContractException;

public final class TaskitEngineTestHelper {
    private TaskitEngineTestHelper() {
    }

    public static void testAddTranslationSpec(TestTaskitEngine.Builder builder) {
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec testComplexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        TestTaskitEngine testTaskitEngine = (TestTaskitEngine) builder.addTranslationSpec(testObjectTranslationSpec)
                .addTranslationSpec(testComplexObjectTranslationSpec).build();

        // show that the translation specs are retrievable by their own app and input
        // classes
        assertEquals(testObjectTranslationSpec,
                testTaskitEngine.getTaskitEngine().getTranslationSpecForClass(testObjectTranslationSpec.getAppObjectClass()));
        assertEquals(testObjectTranslationSpec,
                testTaskitEngine.getTaskitEngine().getTranslationSpecForClass(testObjectTranslationSpec.getInputObjectClass()));

        assertEquals(testComplexObjectTranslationSpec,
                testTaskitEngine.getTaskitEngine().getTranslationSpecForClass(testComplexObjectTranslationSpec.getAppObjectClass()));
        assertEquals(testComplexObjectTranslationSpec, testTaskitEngine
                .getTaskitEngine().getTranslationSpecForClass(testComplexObjectTranslationSpec.getInputObjectClass()));

        builder.clearBuilder();
        // preconditions
        // translationSpec is null
        ContractException contractException = assertThrows(ContractException.class, () -> {
            builder.addTranslationSpec(null);
        });

        assertEquals(TaskitCoreError.NULL_TRANSLATION_SPEC, contractException.getErrorType());

        builder.clearBuilder();
        // the translation spec getAppClass method returns null
        contractException = assertThrows(ContractException.class, () -> {
            TranslationSpec<TestObjectWrapper, Object> wrapperTranslationSpec = new TranslationSpec<TestObjectWrapper, Object>() {

                @Override
                protected Object translateInputObject(TestObjectWrapper inputObject) {
                    return inputObject.getWrappedObject();
                }

                @Override
                protected TestObjectWrapper translateAppObject(Object appObject) {
                    TestObjectWrapper objectWrapper = new TestObjectWrapper();

                    objectWrapper.setWrappedObject(appObject);

                    return objectWrapper;
                }

                @Override
                public Class<Object> getAppObjectClass() {
                    return null;
                }

                @Override
                public Class<TestObjectWrapper> getInputObjectClass() {
                    return TestObjectWrapper.class;
                }
            };
            builder.addTranslationSpec(wrapperTranslationSpec);
        });

        assertEquals(TaskitCoreError.NULL_TRANSLATION_SPEC_APP_CLASS, contractException.getErrorType());

        builder.clearBuilder();
        // the translation spec getInputClass method returns null
        contractException = assertThrows(ContractException.class, () -> {
            TranslationSpec<TestObjectWrapper, Object> wrapperTranslationSpec = new TranslationSpec<TestObjectWrapper, Object>() {

                @Override
                protected Object translateInputObject(TestObjectWrapper inputObject) {
                    return inputObject.getWrappedObject();
                }

                @Override
                protected TestObjectWrapper translateAppObject(Object appObject) {
                    TestObjectWrapper objectWrapper = new TestObjectWrapper();

                    objectWrapper.setWrappedObject(appObject);

                    return objectWrapper;
                }

                @Override
                public Class<Object> getAppObjectClass() {
                    return Object.class;
                }

                @Override
                public Class<TestObjectWrapper> getInputObjectClass() {
                    return null;
                }
            };
            builder.addTranslationSpec(wrapperTranslationSpec);
        });

        assertEquals(TaskitCoreError.NULL_TRANSLATION_SPEC_INPUT_CLASS, contractException.getErrorType());

        builder.clearBuilder();
        // if the translation spec has already been added (same, but different
        // instances)
        contractException = assertThrows(ContractException.class, () -> {
            TestObjectTranslationSpec testObjectTranslationSpec1 = new TestObjectTranslationSpec();
            TestObjectTranslationSpec testObjectTranslationSpec2 = new TestObjectTranslationSpec();

            builder.addTranslationSpec(testObjectTranslationSpec1).addTranslationSpec(testObjectTranslationSpec2);
        });

        assertEquals(TaskitCoreError.DUPLICATE_TRANSLATION_SPEC, contractException.getErrorType());

        builder.clearBuilder();
        // if the translation spec has already been added (exact same instance)
        contractException = assertThrows(ContractException.class, () -> {
            TestObjectTranslationSpec testObjectTranslationSpec1 = new TestObjectTranslationSpec();

            builder.addTranslationSpec(testObjectTranslationSpec1).addTranslationSpec(testObjectTranslationSpec1);
        });

        assertEquals(TaskitCoreError.DUPLICATE_TRANSLATION_SPEC, contractException.getErrorType());
    }

    public static void testAddTranslator(TaskitEngine.Builder builder) {
        builder.addTranslator(TestObjectTranslator.getTranslator());

        // preconditions
        ContractException contractException = assertThrows(ContractException.class, () -> {
            builder.addTranslator(null);
        });

        assertEquals(TaskitCoreError.NULL_TRANSLATOR, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            builder.addTranslator(TestObjectTranslator.getTranslator())
                    .addTranslator(TestObjectTranslator.getTranslator());
        });

        assertEquals(TaskitCoreError.DUPLICATE_TRANSLATOR, contractException.getErrorType());
    }
}
