package gov.hhs.aspr.ms.taskit.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.hhs.aspr.ms.taskit.core.testsupport.testcomplexobject.translationSpecs.TestComplexObjectTranslationSpec;
import gov.hhs.aspr.ms.taskit.core.testsupport.testobject.TestAppObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.testobject.TestObjectTranslator;
import gov.hhs.aspr.ms.taskit.core.testsupport.testobject.TestObjectWrapper;
import gov.hhs.aspr.ms.taskit.core.testsupport.testobject.translationSpecs.TestObjectTranslationSpec;
import gov.hhs.aspr.ms.util.errors.ContractException;

public final class TranslationEngineTestHelper {
    private TranslationEngineTestHelper() {
    }

    public static void testAddTranslationSpec(TranslationEngine.Builder builder) {
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec testComplexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        TranslationEngine testTranslationEngine = builder.addTranslationSpec(testObjectTranslationSpec)
                .addTranslationSpec(testComplexObjectTranslationSpec).build();

        // show that the translation specs are retrievable by their own app and input
        // classes
        assertEquals(testObjectTranslationSpec,
                testTranslationEngine.getTranslationSpecForClass(testObjectTranslationSpec.getAppObjectClass()));
        assertEquals(testObjectTranslationSpec,
                testTranslationEngine.getTranslationSpecForClass(testObjectTranslationSpec.getInputObjectClass()));

        assertEquals(testComplexObjectTranslationSpec,
                testTranslationEngine.getTranslationSpecForClass(testComplexObjectTranslationSpec.getAppObjectClass()));
        assertEquals(testComplexObjectTranslationSpec, testTranslationEngine
                .getTranslationSpecForClass(testComplexObjectTranslationSpec.getInputObjectClass()));

        builder.clearBuilder();
        // preconditions
        // translationSpec is null
        ContractException contractException = assertThrows(ContractException.class, () -> {
            builder.addTranslationSpec(null);
        });

        assertEquals(CoreTranslationError.NULL_TRANSLATION_SPEC, contractException.getErrorType());

        builder.clearBuilder();
        // the translation spec getAppClass method returns null
        contractException = assertThrows(ContractException.class, () -> {
            TranslationSpec<TestObjectWrapper, Object> wrapperTranslationSpec = new TranslationSpec<TestObjectWrapper, Object>() {

                @Override
                protected Object convertInputObject(TestObjectWrapper inputObject) {
                    return inputObject.getWrappedObject();
                }

                @Override
                protected TestObjectWrapper convertAppObject(Object appObject) {
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

        assertEquals(CoreTranslationError.NULL_TRANSLATION_SPEC_APP_CLASS, contractException.getErrorType());

        builder.clearBuilder();
        // the translation spec getInputClass method returns null
        contractException = assertThrows(ContractException.class, () -> {
            TranslationSpec<TestObjectWrapper, Object> wrapperTranslationSpec = new TranslationSpec<TestObjectWrapper, Object>() {

                @Override
                protected Object convertInputObject(TestObjectWrapper inputObject) {
                    return inputObject.getWrappedObject();
                }

                @Override
                protected TestObjectWrapper convertAppObject(Object appObject) {
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

        assertEquals(CoreTranslationError.NULL_TRANSLATION_SPEC_INPUT_CLASS, contractException.getErrorType());

        builder.clearBuilder();
        // if the translation spec has already been added (same, but different
        // instances)
        contractException = assertThrows(ContractException.class, () -> {
            TestObjectTranslationSpec testObjectTranslationSpec1 = new TestObjectTranslationSpec();
            TestObjectTranslationSpec testObjectTranslationSpec2 = new TestObjectTranslationSpec();

            builder.addTranslationSpec(testObjectTranslationSpec1).addTranslationSpec(testObjectTranslationSpec2);
        });

        assertEquals(CoreTranslationError.DUPLICATE_TRANSLATION_SPEC, contractException.getErrorType());

        builder.clearBuilder();
        // if the translation spec has already been added (exact same instance)
        contractException = assertThrows(ContractException.class, () -> {
            TestObjectTranslationSpec testObjectTranslationSpec1 = new TestObjectTranslationSpec();

            builder.addTranslationSpec(testObjectTranslationSpec1).addTranslationSpec(testObjectTranslationSpec1);
        });

        assertEquals(CoreTranslationError.DUPLICATE_TRANSLATION_SPEC, contractException.getErrorType());
    }

    public static void testAddTranslator(TranslationEngine.Builder builder) {
        builder.addTranslator(TestObjectTranslator.getTranslator());

        // preconditions
        ContractException contractException = assertThrows(ContractException.class, () -> {
            builder.addTranslator(null);
        });

        assertEquals(CoreTranslationError.NULL_TRANSLATOR, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            builder.addTranslator(TestObjectTranslator.getTranslator())
                    .addTranslator(TestObjectTranslator.getTranslator());
        });

        assertEquals(CoreTranslationError.DUPLICATE_TRANSLATOR, contractException.getErrorType());
    }

    public static void testAddParentChildClassRelationship(TranslationEngine.Builder builder) {
        builder.addParentChildClassRelationship(TestAppObject.class, Object.class);

        // preconditions
        ContractException contractException = assertThrows(ContractException.class, () -> {
            builder.addParentChildClassRelationship(null, Object.class);
        });

        assertEquals(CoreTranslationError.NULL_CLASS_REF, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            builder.addParentChildClassRelationship(TestAppObject.class, null);
        });

        assertEquals(CoreTranslationError.NULL_CLASS_REF, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            builder.addParentChildClassRelationship(TestAppObject.class, Object.class)
                    .addParentChildClassRelationship(TestAppObject.class, Object.class);
        });

        assertEquals(CoreTranslationError.DUPLICATE_CLASSREF, contractException.getErrorType());
    }
}
