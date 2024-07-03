package gov.hhs.aspr.ms.taskit.protobuf.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.google.protobuf.Any;
import com.google.protobuf.Int32Value;

import gov.hhs.aspr.ms.taskit.core.engine.TaskitCoreError;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestAppChildObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestAppObject;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.TestObjectUtil;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.objects.TestComplexInputObject;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.objects.TestInputObject;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.translation.specs.TestProtobufComplexObjectTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.translation.specs.TestProtobufObjectTranslationSpec;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;
import gov.hhs.aspr.ms.util.errors.ContractException;

public class AT_ProtobufTaskitEngine {
    @Test
    @UnitTestMethod(target = ProtobufJsonTaskitEngine.class, name = "getAnyFromObject", args = { Object.class })
    public void testGetAnyFromObject() {
        ProtobufJsonTaskitEngine protobufTaskitEngine = ProtobufJsonTaskitEngine.builder().build();

        Integer integer = 1500;
        Int32Value int32Value = Int32Value.of(integer);
        Any expectedAny = Any.pack(int32Value);

        Any actualAny = protobufTaskitEngine.getAnyFromObject(integer);

        assertEquals(expectedAny, actualAny);
    }

    @Test
    @UnitTestMethod(target = ProtobufJsonTaskitEngine.class, name = "getAnyFromObjectAsSafeClass", args = {
            Object.class, Class.class })
    public void testGetAnyFromObjectAsSafeClass() {
        ProtobufJsonTaskitEngine protobufTaskitEngine = ProtobufJsonTaskitEngine.builder()
                .addTranslationSpec(new TestProtobufObjectTranslationSpec())
                .addTranslationSpec(new TestProtobufComplexObjectTranslationSpec()).build();

        TestAppObject testAppObject = TestObjectUtil.generateTestAppObject();
        TestAppChildObject testAppChildObject = TestObjectUtil.getChildAppFromApp(testAppObject);

        TestInputObject expectedInputObject = TestObjectUtil.getInputFromApp(testAppChildObject);
        Any expectedAny = Any.pack(expectedInputObject);

        Any actualAny = protobufTaskitEngine.getAnyFromObjectAsClassSafe(testAppChildObject, TestAppObject.class);

        assertEquals(expectedAny, actualAny);

        // preconditions

        // no translationSpec was provided for the parent class
        ContractException contractException = assertThrows(ContractException.class, () -> {
            ProtobufJsonTaskitEngine protobufTaskitEngine2 = ProtobufJsonTaskitEngine.builder()
                    .addTranslationSpec(new TestProtobufComplexObjectTranslationSpec()).build();

            TestAppObject testAppObject2 = TestObjectUtil.generateTestAppObject();
            TestAppChildObject testAppChildObject2 = TestObjectUtil.getChildAppFromApp(testAppObject2);

            protobufTaskitEngine2.getAnyFromObjectAsClassSafe(testAppChildObject2, TestAppObject.class);
        });

        assertEquals(TaskitCoreError.UNKNOWN_TRANSLATION_SPEC, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = ProtobufJsonTaskitEngine.class, name = "getObjectFromAny", args = { Any.class })
    public void testGetObjectFromAny() {
        ProtobufJsonTaskitEngine protobufTaskitEngine = ProtobufJsonTaskitEngine.builder()
                .addTranslationSpec(new TestProtobufObjectTranslationSpec())
                .addTranslationSpec(new TestProtobufComplexObjectTranslationSpec()).build();

        TestAppObject expectedObject = TestObjectUtil.generateTestAppObject();

        TestInputObject expectedInputObject = TestObjectUtil.getInputFromApp(expectedObject);
        Any any = Any.pack(expectedInputObject);

        Object actualObject = protobufTaskitEngine.getObjectFromAny(any);

        assertTrue(actualObject.getClass() == TestAppObject.class);
        assertEquals(expectedObject, actualObject);
    }

    @Test
    @UnitTestMethod(target = ProtobufJsonTaskitEngine.class, name = "getClassFromTypeUrl", args = { String.class })
    public void testGetClassFromTypeUrl() {
        ProtobufJsonTaskitEngine protobufTaskitEngine = ProtobufJsonTaskitEngine.builder()
                .addTranslationSpec(new TestProtobufObjectTranslationSpec())
                .addTranslationSpec(new TestProtobufComplexObjectTranslationSpec()).build();

        Class<TestInputObject> testInputObjectClass = TestInputObject.class;
        Class<TestComplexInputObject> testComplexInputObjectClass = TestComplexInputObject.class;

        String testInputObjectTypeUrl = TestInputObject.getDescriptor().getFullName();
        String testComplexInputObjectTypeUrl = TestComplexInputObject.getDescriptor().getFullName();

        assertEquals(testInputObjectClass, protobufTaskitEngine.getClassFromTypeUrl(testInputObjectTypeUrl));
        assertEquals(testComplexInputObjectClass,
                protobufTaskitEngine.getClassFromTypeUrl(testComplexInputObjectTypeUrl));

        // preconditions
        // no typeUrl was provided and/or malformed typeUrl
        ContractException contractException = assertThrows(ContractException.class, () -> {
            ProtobufJsonTaskitEngine protobufTaskitEngine2 = ProtobufJsonTaskitEngine.builder()
                    .addTranslationSpec(new TestProtobufComplexObjectTranslationSpec()).build();

            protobufTaskitEngine2.getClassFromTypeUrl("badUrl");
        });

        assertEquals(ProtobufTaskitError.UNKNOWN_TYPE_URL, contractException.getErrorType());
    }
}
