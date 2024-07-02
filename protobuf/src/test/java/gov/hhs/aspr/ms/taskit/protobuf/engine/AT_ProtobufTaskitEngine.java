package gov.hhs.aspr.ms.taskit.protobuf.engine;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.protobuf.Any;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Int32Value;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.ProtocolMessageEnum;

import gov.hhs.aspr.ms.taskit.core.engine.TaskitCoreError;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestAppChildObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestAppObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.specs.TestObjectTranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.TranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.Translator;
import gov.hhs.aspr.ms.taskit.protobuf.ProtobufTaskitEngineTestHelper;
import gov.hhs.aspr.ms.taskit.protobuf.engine.ProtobufJsonTaskitEngine;
import gov.hhs.aspr.ms.taskit.protobuf.engine.ProtobufTaskitError;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.TestObjectUtil;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.objects.BadMessageBadArguments;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.objects.BadMessageIllegalAccess;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.objects.BadMessageNoMethod;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.objects.BadMessageNonStaticMethod;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.testcomplexobject.input.TestComplexInputObject;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.testobject.input.TestInputEnum;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.testobject.input.TestInputObject;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.translation.specs.TestProtobufComplexObjectTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.translation.specs.TestProtobufObjectTranslationSpec;
import gov.hhs.aspr.ms.util.annotations.UnitTestForCoverage;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;
import gov.hhs.aspr.ms.util.errors.ContractException;
import gov.hhs.aspr.ms.util.resourcehelper.ResourceHelper;

public class AT_ProtobufTaskitEngine {
    Path basePath = ResourceHelper.getResourceDir(this.getClass());
    Path filePath = ResourceHelper.createDirectory(basePath, "test-output");

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

    @Test
    @UnitTestForCoverage
    public void testDebugPrint() throws IOException {
        String fileName = "debugPrintFromEngine_1-testOutput.json";

        ResourceHelper.createFile(filePath, fileName);

        ProtobufJsonTaskitEngine protobufTaskitEngine = ProtobufJsonTaskitEngine.builder()
                .addTranslationSpec(new TestProtobufObjectTranslationSpec())
                .addTranslationSpec(new TestProtobufComplexObjectTranslationSpec()).build();

        protobufTaskitEngine.setDebug(true);

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();

        protobufTaskitEngine.writeOutput(filePath.resolve(fileName), expectedAppObject, Optional.empty());
        TestAppObject actualAppObject = protobufTaskitEngine.readInput(filePath.resolve(fileName),
                TestInputObject.class);
        assertEquals(expectedAppObject, actualAppObject);
    }

    @Test
    @UnitTestForCoverage
    public void testParseJson() {
        ProtobufJsonTaskitEngine protobufTaskitEngine = ProtobufJsonTaskitEngine.builder()
                .addTranslationSpec(new TestProtobufObjectTranslationSpec())
                .addTranslationSpec(new TestProtobufComplexObjectTranslationSpec()).setIgnoringUnknownFields(false)
                .build();

        // preconditions
        // json has unknown property and the ignoringUnknownFields property is set to
        // false
        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("unknownProperty", "unknownValue");

        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            protobufTaskitEngine.parseJson(new StringReader(jsonObject.toString()), TestInputObject.class);
        });

        assertEquals(InvalidProtocolBufferException.class, runtimeException.getCause().getClass());
    }

    @Test
    @UnitTestForCoverage
    public void testGetBuilderForMessage() {

        ProtobufJsonTaskitEngine protobufTaskitEngine = ProtobufJsonTaskitEngine.builder()
                .addTranslationSpec(new TestProtobufObjectTranslationSpec())
                .addTranslationSpec(new TestProtobufComplexObjectTranslationSpec()).build();

        // preconditions
        /*
         * Note on these preconditions: Because of the type enforced on readInput()
         * ensuring that the passed in classRef is a child of Message.class, these
         * preconditions should never be encountered. But for coverage purposes, are
         * included here.
         */
        // class ref does not contain a newBuilder method
        assertThrows(RuntimeException.class, () -> {
            protobufTaskitEngine.getBuilderForMessage(BadMessageNoMethod.class);
        });

        // class has a newBuilder method but it is not static
        assertThrows(RuntimeException.class, () -> {
            protobufTaskitEngine.getBuilderForMessage(BadMessageNonStaticMethod.class);
        });

        // class has a static newBuilder method but it takes arguments
        assertThrows(RuntimeException.class, () -> {
            protobufTaskitEngine.getBuilderForMessage(BadMessageBadArguments.class);
        });

        // class has a newBuilder method but it is not accessible
        assertThrows(RuntimeException.class, () -> {
            protobufTaskitEngine.getBuilderForMessage(BadMessageIllegalAccess.class);
        });

    }

    @Test
    @UnitTestForCoverage
    public void testReadInput() throws IOException {
        String fileName = "readInputFromEngine_1-testOutput.json";
        String fileName2 = "readInputFromEngine_2-testOutput.json";

        ResourceHelper.createFile(filePath, fileName);
        ResourceHelper.createFile(filePath, fileName2);

        ProtobufJsonTaskitEngine protobufTaskitEngine = ProtobufJsonTaskitEngine.builder()
                .addTranslationSpec(new TestProtobufObjectTranslationSpec())
                .addTranslationSpec(new TestProtobufComplexObjectTranslationSpec()).build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();

        protobufTaskitEngine.writeOutput(filePath.resolve(fileName), expectedAppObject, Optional.empty());
        TestAppObject actualAppObject = protobufTaskitEngine.readInput(filePath.resolve(fileName),
                TestInputObject.class);
        assertEquals(expectedAppObject, actualAppObject);

        protobufTaskitEngine.writeOutput(filePath.resolve(fileName2),
                TestObjectUtil.getChildAppFromApp(expectedAppObject),
                Optional.of(TestAppObject.class));
        TestAppObject actualAppChildObject = protobufTaskitEngine.readInput(filePath.resolve(fileName2),
                TestInputObject.class);
        assertEquals(expectedAppObject, actualAppChildObject);

        // preconditions
        // input class is not a Message class
        ContractException contractException = assertThrows(ContractException.class, () -> {
            protobufTaskitEngine.readInput(filePath.resolve(fileName2), TestAppObject.class);
        });

        assertEquals(ProtobufTaskitError.INVALID_INPUT_CLASS, contractException.getErrorType());

        // precondition for the Runtime exceptions are covered by the tests:
        // testGetBuilderForMessage() and testParseJson()
    }

    @Test
    @UnitTestForCoverage
    public void testWriteOutput() throws IOException {
        String fileName = "writeOutputFromEngine_1-testOutput.json";
        String fileName2 = "writeOutputFromEngine_2-testOutput.json";

        ResourceHelper.createFile(filePath, fileName);
        ResourceHelper.createFile(filePath, fileName2);

        ProtobufJsonTaskitEngine protobufTaskitEngine = ProtobufJsonTaskitEngine.builder()
                .addTranslationSpec(new TestProtobufObjectTranslationSpec())
                .addTranslationSpec(new TestProtobufComplexObjectTranslationSpec()).build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();

        protobufTaskitEngine.writeOutput(filePath.resolve(fileName), expectedAppObject, Optional.empty());
        TestAppObject actualAppObject = protobufTaskitEngine.readInput(filePath.resolve(fileName),
                TestInputObject.class);
        assertEquals(expectedAppObject, actualAppObject);

        protobufTaskitEngine.writeOutput(filePath.resolve(fileName2),
                TestObjectUtil.getChildAppFromApp(expectedAppObject),
                Optional.of(TestAppObject.class));
        TestAppObject actualAppChildObject = protobufTaskitEngine.readInput(filePath.resolve(fileName2),
                TestInputObject.class);
        assertEquals(expectedAppObject, actualAppChildObject);

        // this test is just for coverage, but this method should never be directly
        // called
        TestInputObject inputObject = TestObjectUtil.generateTestInputObject();
        protobufTaskitEngine.writeOutput(filePath.resolve(fileName2), inputObject, Optional.empty());
        actualAppObject = protobufTaskitEngine.readInput(filePath.resolve(fileName2), TestInputObject.class);
        assertEquals(TestObjectUtil.getAppFromInput(inputObject), actualAppObject);

        // preconditions
        // IO error occurs
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            protobufTaskitEngine.writeOutput(filePath.resolve("/foo"), expectedAppObject, Optional.empty());
        });

        assertTrue(runtimeException.getCause() instanceof IOException);
    }

    @Test
    @UnitTestMethod(target = ProtobufJsonTaskitEngine.class, name = "builder", args = {})
    public void testBuilder() {
        assertNotNull(ProtobufJsonTaskitEngine.builder());
    }

    @Test
    @UnitTestMethod(target = ProtobufJsonTaskitEngine.Builder.class, name = "build", args = {})
    public void testBuild() {

        ProtobufJsonTaskitEngine protobufTaskitEngine = ProtobufJsonTaskitEngine.builder().build();

        assertNotNull(protobufTaskitEngine);

        // parser and printer do not have equals contracts, so no way to check for
        // equality
        // the use cases for them are adequately tested in: testReadInput and
        // testWriteOutput
    }

    @Test
    @UnitTestForCoverage
    public void testGetDefaultMessage() {
        ProtobufJsonTaskitEngine.Builder pBuilder = ProtobufJsonTaskitEngine.builder();

        /*
         * Note: because this method is only ever called if the classRef is an instance
         * of Message.class, this method should never throw an exception. This test is
         * here exclusively for test coverage.
         */
        assertThrows(RuntimeException.class, () -> {
            pBuilder.getDefaultMessage(Message.class);
        });
    }

    @Test
    @UnitTestForCoverage
    public void testGetDefaultEnum() {
        ProtobufJsonTaskitEngine.Builder pBuilder = ProtobufJsonTaskitEngine.builder();

        /*
         * Note: because this method is only ever called if the classRef is an instance
         * of ProtocolMessageEnum.class, this method should never throw an exception.
         * This test is here exclusively for test coverage.
         */
        assertThrows(RuntimeException.class, () -> {
            pBuilder.getDefaultEnum(ProtocolMessageEnum.class);
        });
    }

    @Test
    @UnitTestForCoverage
    public void testPopulate() {
        ProtobufJsonTaskitEngine.Builder pBuilder = ProtobufJsonTaskitEngine.builder();

        // Protobuf Message
        pBuilder.populate(TestInputObject.class);

        // Protobuf Enum
        pBuilder.populate(TestInputEnum.class);

        // precondition
        // if class is neither a Message nor a ProtocolMessageEnum
        ContractException contractException = assertThrows(ContractException.class, () -> {
            pBuilder.populate(TestAppObject.class);
        });

        assertEquals(ProtobufTaskitError.INVALID_TRANSLATION_SPEC_INPUT_CLASS, contractException.getErrorType());

        // the class is exactly a Message.class
        contractException = assertThrows(ContractException.class, () -> {
            pBuilder.populate(Message.class);
        });

        assertEquals(ProtobufTaskitError.INVALID_TRANSLATION_SPEC_INPUT_CLASS, contractException.getErrorType());

        // the class is exactly a ProtocolMessageEnum.class
        contractException = assertThrows(ContractException.class, () -> {
            pBuilder.populate(ProtocolMessageEnum.class);
        });

        assertEquals(ProtobufTaskitError.INVALID_TRANSLATION_SPEC_INPUT_CLASS, contractException.getErrorType());

    }

    @Test
    @UnitTestMethod(target = ProtobufJsonTaskitEngine.Builder.class, name = "addFieldToIncludeDefaultValue", args = {
            FieldDescriptor.class })
    public void testAddFieldToIncludeDefaultValue() throws InvalidProtocolBufferException {

        ProtobufJsonTaskitEngine protobufTaskitEngine = ProtobufJsonTaskitEngine.builder()
                .addFieldToIncludeDefaultValue(TestInputObject.getDescriptor().findFieldByName("integer")).build();

        TestInputObject expectedInputObject = TestObjectUtil.generateTestInputObject().toBuilder().setInteger(0)
                .setBool(false).setString("").build();

        String message = protobufTaskitEngine.getJsonPrinter().print(expectedInputObject);

        JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();

        assertTrue(jsonObject.has("integer"));
        assertFalse(jsonObject.has("string"));
        assertFalse(jsonObject.has("bool"));

        assertTrue(jsonObject.get("integer").isJsonPrimitive());
        assertEquals(0, jsonObject.get("integer").getAsInt());
    }

    @Test
    @UnitTestMethod(target = ProtobufJsonTaskitEngine.Builder.class, name = "addTranslationSpec", args = {
            TranslationSpec.class })
    public void testAddTranslationSpec() {
        ProtobufTaskitEngineTestHelper.testAddTranslationSpec(ProtobufJsonTaskitEngine.builder());

        ProtobufJsonTaskitEngine protobufTaskitEngine = ProtobufJsonTaskitEngine.builder()
                .addTranslationSpec(new TestProtobufObjectTranslationSpec())
                .addTranslationSpec(new TestProtobufComplexObjectTranslationSpec()).build();

        assertDoesNotThrow(() -> {
            protobufTaskitEngine.getClassFromTypeUrl(TestInputObject.getDescriptor().getFullName());
            protobufTaskitEngine.getClassFromTypeUrl(TestComplexInputObject.getDescriptor().getFullName());
        });

        // precondition
        // translation spec is not a ProtobufTranslationSpec

        ContractException contractException = assertThrows(ContractException.class, () -> {
            ProtobufJsonTaskitEngine.builder().addTranslationSpec(new TestObjectTranslationSpec());
        });

        assertEquals(ProtobufTaskitError.INVALID_TRANSLATION_SPEC, contractException.getErrorType());
        // that the inputClass is not a Message nor a
        // ProtocolMessageEnum, and is tested in the testPopulate() test
    }

    @Test
    @UnitTestMethod(target = ProtobufJsonTaskitEngine.Builder.class, name = "addTranslator", args = {
            Translator.class })
    public void testAddTranslator() {
        ProtobufTaskitEngineTestHelper.testAddTranslator(ProtobufJsonTaskitEngine.builder());
    }

    @Test
    @UnitTestMethod(target = ProtobufJsonTaskitEngine.Builder.class, name = "addParentChildClassRelationship", args = {
            Class.class, Class.class })
    public void testAddParentChildClassRelationship() {
        ProtobufTaskitEngineTestHelper.testAddParentChildClassRelationship(ProtobufJsonTaskitEngine.builder());
    }
    
    @Test
    @UnitTestMethod(target = ProtobufJsonTaskitEngine.Builder.class, name = "setIgnoringUnknownFields", args = {
            boolean.class })
    public void testSetIgnoringUnknownFields() {
        ProtobufJsonTaskitEngine protobufTaskitEngine = ProtobufJsonTaskitEngine.builder()
                .setIgnoringUnknownFields(true).build();

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("bool", false);
        jsonObject.addProperty("unknownField", "unknownField");

        assertDoesNotThrow(() -> {
            protobufTaskitEngine.getJsonParser().merge(jsonObject.toString(), TestInputObject.newBuilder());
        });

        ProtobufJsonTaskitEngine protobufTaskitEngine2 = ProtobufJsonTaskitEngine.builder()
                .setIgnoringUnknownFields(false).build();

        assertThrows(InvalidProtocolBufferException.class, () -> {
            protobufTaskitEngine2.getJsonParser().merge(jsonObject.toString(), TestInputObject.newBuilder());
        });

        assertDoesNotThrow(() -> {
            jsonObject.remove("unknownField");
            protobufTaskitEngine.getJsonParser().merge(jsonObject.toString(), TestInputObject.newBuilder());
        });
    }

    @Test
    @UnitTestMethod(target = ProtobufJsonTaskitEngine.Builder.class, name = "setIncludingDefaultValueFields", args = {
            boolean.class })
    public void testSetIncludingDefaultValueFields() throws InvalidProtocolBufferException {

        ProtobufJsonTaskitEngine protobufTaskitEngine = ProtobufJsonTaskitEngine.builder()
                .setIncludingDefaultValueFields(true).build();

        TestInputObject expectedInputObject = TestObjectUtil.generateTestInputObject().toBuilder().setInteger(0)
                .setBool(false).setString("").build();

        String message = protobufTaskitEngine.getJsonPrinter().print(expectedInputObject);

        JsonObject jsonObject = JsonParser.parseString(message).getAsJsonObject();

        assertTrue(jsonObject.has("integer"));
        assertTrue(jsonObject.has("string"));
        assertTrue(jsonObject.has("bool"));

        assertTrue(jsonObject.get("integer").isJsonPrimitive());
        assertTrue(jsonObject.get("string").isJsonPrimitive());
        assertTrue(jsonObject.get("bool").isJsonPrimitive());

        assertEquals(0, jsonObject.get("integer").getAsInt());
        assertEquals(false, jsonObject.get("bool").getAsBoolean());
        assertEquals("", jsonObject.get("string").getAsString());

        protobufTaskitEngine = ProtobufJsonTaskitEngine.builder().setIncludingDefaultValueFields(false).build();

        message = protobufTaskitEngine.getJsonPrinter().print(expectedInputObject);

        jsonObject = JsonParser.parseString(message).getAsJsonObject();
        assertFalse(jsonObject.has("integer"));
        assertFalse(jsonObject.has("string"));
        assertFalse(jsonObject.has("bool"));
    }
}
