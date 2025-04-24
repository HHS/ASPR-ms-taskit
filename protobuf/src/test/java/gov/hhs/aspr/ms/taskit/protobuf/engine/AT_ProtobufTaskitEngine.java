package gov.hhs.aspr.ms.taskit.protobuf.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import com.google.protobuf.Any;
import com.google.protobuf.Int32Value;

import gov.hhs.aspr.ms.taskit.core.engine.TaskitError;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestAppChildObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestAppObject;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.TestObjectUtil;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.objects.BadMessageBadArguments;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.objects.BadMessageIllegalAccess;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.objects.BadMessageNoMethod;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.objects.BadMessageNonStaticMethod;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.objects.TestComplexInputObject;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.objects.TestInputObject;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.translation.specs.TestProtobufComplexObjectTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.translation.specs.TestProtobufEnumTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.translation.specs.TestProtobufObjectTranslationSpec;
import gov.hhs.aspr.ms.util.annotations.UnitTestForCoverage;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;
import gov.hhs.aspr.ms.util.errors.ContractException;
import gov.hhs.aspr.ms.util.resourcehelper.ResourceHelper;

public class AT_ProtobufTaskitEngine {
    Path basePath = ResourceHelper.getResourceDir(this.getClass());
    Path filePath = ResourceHelper.createDirectory(basePath, "test-output");

    @Test
    @UnitTestForCoverage
    public void testReadFile() {
        String fileName = "readProtoEngine_1-testOutput.json";

        ResourceHelper.createFile(filePath, fileName);

        ProtobufJsonTaskitEngine protobufTaskitEngine = ProtobufJsonTaskitEngine.builder()
                .addTranslationSpec(new TestProtobufObjectTranslationSpec())
                .addTranslationSpec(new TestProtobufComplexObjectTranslationSpec()).build();

        // preconditions
        // input class is not a Message class
        ContractException contractException = assertThrows(ContractException.class, () -> {
            protobufTaskitEngine.read(filePath.resolve(fileName), TestAppObject.class);
        });

        assertEquals(TaskitError.INVALID_INPUT_CLASS, contractException.getErrorType());
        
        /*
         * Note on these preconditions: Because of the type enforced on readInput()
         * ensuring that the passed in classRef is a child of Message.class, these
         * preconditions should never be encountered. But for coverage purposes, are
         * included here.
         */
        // class ref does not contain a newBuilder method
        assertThrows(RuntimeException.class, () -> {
            ProtobufTaskitEngineHelper.getBuilderForMessage(BadMessageNoMethod.class);
        });

        // class has a newBuilder method but it is not static
        assertThrows(RuntimeException.class, () -> {
            ProtobufTaskitEngineHelper.getBuilderForMessage(BadMessageNonStaticMethod.class);
        });

        // class has a static newBuilder method but it takes arguments
        assertThrows(RuntimeException.class, () -> {
            ProtobufTaskitEngineHelper.getBuilderForMessage(BadMessageBadArguments.class);
        });

        // class has a newBuilder method but it is not accessible
        assertThrows(RuntimeException.class, () -> {
            ProtobufTaskitEngineHelper.getBuilderForMessage(BadMessageIllegalAccess.class);
        });
    }

    @Test
    @UnitTestForCoverage
    public void testWriteFile() {
        String fileName = "writeProtoEngine_1-testOutput.json";

        ResourceHelper.createFile(filePath, fileName);

        ProtobufTaskitEngine protobufTaskitEngine = ProtobufJsonTaskitEngine.builder()
                .addTranslationSpec(new TestProtobufObjectTranslationSpec())
                .addTranslationSpec(new TestProtobufComplexObjectTranslationSpec()).build();

        // preconditions
        // the object to write isn't assignable from Message.class
        ContractException contractException = assertThrows(ContractException.class, () -> {
            protobufTaskitEngine.write(filePath.resolve(fileName), TestObjectUtil.generateTestAppObject());
        });

        assertEquals(TaskitError.INVALID_OUTPUT_CLASS, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = ProtobufTaskitEngine.class, name = "getAnyFromObject", args = { Object.class })
    public void testGetAnyFromObject() {
        /*
         * Test Note: because this method internally calls translateObject(), it
         * necessarily throws the same exceptions as that method. Because those
         * exceptions are tested in Taskit, we won't additionally test them here.
         */
        ProtobufJsonTaskitEngine protobufTaskitEngine = ProtobufJsonTaskitEngine.builder().build();

        Integer integer = 1500;
        Int32Value int32Value = Int32Value.of(integer);
        Any expectedAny = Any.pack(int32Value);

        Any actualAny = protobufTaskitEngine.getAnyFromObject(integer);

        assertEquals(expectedAny, actualAny);
    }

    @Test
    @UnitTestMethod(target = ProtobufTaskitEngine.class, name = "getAnyFromObjectAsClassSafe", args = {
            Object.class, Class.class })
    public void testGetAnyFromObjectAsClassSafe() {
        /*
         * Test Note: because this method internally calls translateObjectAsClassSafe()
         * and translateObjectAsClassUnsafe, it
         * necessarily throws the same exceptions as those methods. Because those
         * exceptions are tested in Taskit, we won't additionally test them here.
         */
        ProtobufJsonTaskitEngine protobufTaskitEngine = ProtobufJsonTaskitEngine.builder()
                .addTranslationSpec(new TestProtobufObjectTranslationSpec())
                .addTranslationSpec(new TestProtobufComplexObjectTranslationSpec())
                .addTranslationSpec(new TestProtobufEnumTranslationSpec()).build();

        TestAppObject testAppObject = TestObjectUtil.generateTestAppObject();
        TestAppChildObject testAppChildObject = TestObjectUtil.getChildAppFromApp(testAppObject);

        TestInputObject expectedInputObject = TestObjectUtil.getInputFromApp(testAppChildObject);
        Any expectedAny = Any.pack(expectedInputObject);

        Any actualAny = protobufTaskitEngine.getAnyFromObjectAsClassSafe(testAppChildObject,
                TestAppObject.class);

        assertEquals(expectedAny, actualAny);
    }

    @Test
    @UnitTestMethod(target = ProtobufTaskitEngine.class, name = "getObjectFromAny", args = { Any.class })
    public void testGetObjectFromAny() {
        /*
         * Test Note: because this method internally calls
         * translateObjectAsClassUnsafe(), it
         * necessarily throws the same exceptions as that method. Because those
         * exceptions are tested in Taskit, we won't additionally test them here.
         */
        ProtobufJsonTaskitEngine protobufTaskitEngine = ProtobufJsonTaskitEngine.builder()
                .addTranslationSpec(new TestProtobufObjectTranslationSpec())
                .addTranslationSpec(new TestProtobufComplexObjectTranslationSpec())
                .addTranslationSpec(new TestProtobufEnumTranslationSpec()).build();

        TestAppObject expectedObject = TestObjectUtil.generateTestAppObject();

        TestInputObject expectedInputObject = TestObjectUtil.getInputFromApp(expectedObject);
        Any any = Any.pack(expectedInputObject);

        Object actualObject = protobufTaskitEngine.getObjectFromAny(any);

        assertTrue(actualObject.getClass() == TestAppObject.class);
        assertEquals(expectedObject, actualObject);
    }

    @Test
    @UnitTestMethod(target = ProtobufTaskitEngine.class, name = "getClassFromTypeUrl", args = { String.class })
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

    @Test
    @UnitTestMethod(target = ProtobufTaskitEngine.class, name = "hashCode", args = {})
    public void testHashCode() {
        // TODO: update test
        ProtobufTaskitEngine protobufTaskitEngine1 = ProtobufJsonTaskitEngine.builder()
                .build();
        ProtobufTaskitEngine protobufTaskitEngine2 = ProtobufJsonTaskitEngine.builder()
                .addTranslationSpec(new TestProtobufObjectTranslationSpec())
                .build();
        ProtobufTaskitEngine protobufTaskitEngine3 = ProtobufJsonTaskitEngine.builder()
                .addTranslationSpec(new TestProtobufComplexObjectTranslationSpec())
                .build();
        ProtobufTaskitEngine protobufTaskitEngine4 = ProtobufJsonTaskitEngine.builder()
                .build();

        // exact same
        assertEquals(protobufTaskitEngine1.hashCode(), protobufTaskitEngine1.hashCode());
        assertEquals(protobufTaskitEngine2.hashCode(), protobufTaskitEngine2.hashCode());
        assertEquals(protobufTaskitEngine3.hashCode(), protobufTaskitEngine3.hashCode());
        assertEquals(protobufTaskitEngine4.hashCode(), protobufTaskitEngine4.hashCode());

        // super not equals
        assertNotEquals(protobufTaskitEngine1.hashCode(), protobufTaskitEngine2.hashCode());
        assertNotEquals(protobufTaskitEngine1.hashCode(), protobufTaskitEngine3.hashCode());
        assertNotEquals(protobufTaskitEngine2.hashCode(), protobufTaskitEngine1.hashCode());
        assertNotEquals(protobufTaskitEngine2.hashCode(), protobufTaskitEngine3.hashCode());
        assertNotEquals(protobufTaskitEngine2.hashCode(), protobufTaskitEngine4.hashCode());
        assertNotEquals(protobufTaskitEngine3.hashCode(), protobufTaskitEngine1.hashCode());
        assertNotEquals(protobufTaskitEngine3.hashCode(), protobufTaskitEngine2.hashCode());
        assertNotEquals(protobufTaskitEngine3.hashCode(), protobufTaskitEngine4.hashCode());
        assertNotEquals(protobufTaskitEngine4.hashCode(), protobufTaskitEngine2.hashCode());
        assertNotEquals(protobufTaskitEngine4.hashCode(), protobufTaskitEngine3.hashCode());

        // same specs
        assertEquals(protobufTaskitEngine1.hashCode(), protobufTaskitEngine4.hashCode());
        assertEquals(protobufTaskitEngine4.hashCode(), protobufTaskitEngine1.hashCode());
    }

    @Test
    @UnitTestMethod(target = ProtobufTaskitEngine.class, name = "equals", args = { Object.class })
    public void testEquals() {
        // TODO: update test
        ProtobufTaskitEngine protobufTaskitEngine1 = ProtobufJsonTaskitEngine.builder()
                .build();
        ProtobufTaskitEngine protobufTaskitEngine2 = ProtobufJsonTaskitEngine.builder()
                .addTranslationSpec(new TestProtobufObjectTranslationSpec())
                .build();
        ProtobufTaskitEngine protobufTaskitEngine3 = ProtobufJsonTaskitEngine.builder()
                .addTranslationSpec(new TestProtobufComplexObjectTranslationSpec())
                .build();
        ProtobufTaskitEngine protobufTaskitEngine4 = ProtobufJsonTaskitEngine.builder()
                .build();

        // exact same
        assertEquals(protobufTaskitEngine1, protobufTaskitEngine1);
        assertEquals(protobufTaskitEngine2, protobufTaskitEngine2);
        assertEquals(protobufTaskitEngine3, protobufTaskitEngine3);
        assertEquals(protobufTaskitEngine4, protobufTaskitEngine4);

        // null
        assertNotEquals(protobufTaskitEngine1, null);

        // not instance
        assertNotEquals(protobufTaskitEngine1, new Object());

        // super not equals
        assertNotEquals(protobufTaskitEngine1, protobufTaskitEngine2);
        assertNotEquals(protobufTaskitEngine1, protobufTaskitEngine3);
        assertNotEquals(protobufTaskitEngine2, protobufTaskitEngine1);
        assertNotEquals(protobufTaskitEngine2, protobufTaskitEngine3);
        assertNotEquals(protobufTaskitEngine2, protobufTaskitEngine4);
        assertNotEquals(protobufTaskitEngine3, protobufTaskitEngine1);
        assertNotEquals(protobufTaskitEngine3, protobufTaskitEngine2);
        assertNotEquals(protobufTaskitEngine3, protobufTaskitEngine4);
        assertNotEquals(protobufTaskitEngine4, protobufTaskitEngine2);
        assertNotEquals(protobufTaskitEngine4, protobufTaskitEngine3);

        // same specs
        assertEquals(protobufTaskitEngine1, protobufTaskitEngine4);
        assertEquals(protobufTaskitEngine4, protobufTaskitEngine1);
    }
}
