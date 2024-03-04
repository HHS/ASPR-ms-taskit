package gov.hhs.aspr.ms.taskit.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import gov.hhs.aspr.ms.taskit.core.testsupport.testobject.TestObjectTranslator;
import gov.hhs.aspr.ms.taskit.core.testsupport.testobject.TestObjectWrapper;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.testcomplexobject.translationSpecs.TestProtobufComplexObjectTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.testobject.translationSpecs.TestProtobufObjectTranslationSpec;
import gov.hhs.aspr.ms.util.errors.ContractException;

public final class ProtobufTranslationEngineTestHelper {
    private ProtobufTranslationEngineTestHelper() {
    }

    public static void testAddTranslationSpec(TranslationEngine.Builder builder) {
        TestProtobufObjectTranslationSpec testObjectTranslationSpec = new TestProtobufObjectTranslationSpec();
        TestProtobufComplexObjectTranslationSpec testComplexObjectTranslationSpec = new TestProtobufComplexObjectTranslationSpec();
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
            TestProtobufObjectTranslationSpec testObjectTranslationSpec1 = new TestProtobufObjectTranslationSpec();
            TestProtobufObjectTranslationSpec testObjectTranslationSpec2 = new TestProtobufObjectTranslationSpec();

            builder.addTranslationSpec(testObjectTranslationSpec1).addTranslationSpec(testObjectTranslationSpec2);
        });

        assertEquals(CoreTranslationError.DUPLICATE_TRANSLATION_SPEC, contractException.getErrorType());

        builder.clearBuilder();
        // if the translation spec has already been added (exact same instance)
        contractException = assertThrows(ContractException.class, () -> {
            TestProtobufObjectTranslationSpec testObjectTranslationSpec1 = new TestProtobufObjectTranslationSpec();

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

}
