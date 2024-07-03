package gov.hhs.aspr.ms.taskit.protobuf.engine;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.ProtocolMessageEnum;

import gov.hhs.aspr.ms.taskit.core.engine.TaskitCoreError;
import gov.hhs.aspr.ms.taskit.core.testsupport.engine.TestTaskitEngine;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestAppObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.object.specs.TestObjectTranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.TranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.Translator;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.TestObjectUtil;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.objects.BadMessageBadArguments;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.objects.BadMessageIllegalAccess;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.objects.BadMessageNoMethod;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.objects.BadMessageNonStaticMethod;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.objects.TestComplexInputObject;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.objects.TestInputEnum;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.objects.TestInputObject;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.translation.specs.TestProtobufComplexObjectTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.translation.specs.TestProtobufObjectTranslationSpec;
import gov.hhs.aspr.ms.util.annotations.UnitTestForCoverage;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;
import gov.hhs.aspr.ms.util.errors.ContractException;
import gov.hhs.aspr.ms.util.resourcehelper.ResourceHelper;

public class AT_ProtobufJsonTaskitEngine {
    Path basePath = ResourceHelper.getResourceDir(this.getClass());
    Path filePath = ResourceHelper.createDirectory(basePath, "test-output");

    @Test
    @UnitTestMethod(target = ProtobufJsonTaskitEngine.class, name = "read", args = { Path.class, Class.class })
    public void testRead() throws IOException {
        String fileName = "readProtoJsonEngine_1-testOutput.json";

        ResourceHelper.createFile(filePath, fileName);

        ProtobufJsonTaskitEngine protobufTaskitEngine = ProtobufJsonTaskitEngine.builder()
                .addTranslationSpec(new TestProtobufObjectTranslationSpec())
                .addTranslationSpec(new TestProtobufComplexObjectTranslationSpec()).build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();
        TestInputObject expectedObject = TestObjectUtil.getInputFromApp(expectedAppObject);

        protobufTaskitEngine.translateAndWrite(filePath.resolve(fileName), expectedAppObject);
        TestInputObject actualObject = protobufTaskitEngine.read(filePath.resolve(fileName),
                TestInputObject.class);
        assertEquals(expectedObject, actualObject);

        // preconditions
        // input class is not a Message class
        ContractException contractException = assertThrows(ContractException.class, () -> {
            protobufTaskitEngine.read(filePath.resolve(fileName), TestAppObject.class);
        });

        assertEquals(TaskitCoreError.INVALID_INPUT_CLASS, contractException.getErrorType());

        // json has unknown property and the ignoringUnknownFields property is set to
        // false
        ProtobufJsonTaskitEngine protobufTaskitEngine2 = ProtobufJsonTaskitEngine.builder()
                .addTranslationSpec(new TestProtobufObjectTranslationSpec())
                .addTranslationSpec(new TestProtobufComplexObjectTranslationSpec())
                .setIgnoringUnknownFields(false)
                .build();

        // use test engine to output bad json object
        TestTaskitEngine testTaskitEngine = TestTaskitEngine.builder()
                .addTranslationSpec(new TestObjectTranslationSpec()).build();

        JsonObject jsonObject = new JsonObject();

        jsonObject.addProperty("unknownProperty", "unknownValue");

        testTaskitEngine.write(filePath.resolve("readProtoJsonEngine_3_bad"), jsonObject);

        assertThrows(InvalidProtocolBufferException.class, () -> {
            protobufTaskitEngine2.read(filePath.resolve("readProtoJsonEngine_3_bad"), TestInputObject.class);
        });

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
    @UnitTestMethod(target = ProtobufJsonTaskitEngine.class, name = "readAndTranslate", args = { Path.class,
            Class.class })
    public void testReadAndTranslate() throws IOException {
        String fileName = "readAndTranslateProtoJsonEngine_1-testOutput.json";

        ResourceHelper.createFile(filePath, fileName);

        ProtobufJsonTaskitEngine protobufTaskitEngine = ProtobufJsonTaskitEngine.builder()
                .addTranslationSpec(new TestProtobufObjectTranslationSpec())
                .addTranslationSpec(new TestProtobufComplexObjectTranslationSpec()).build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();

        protobufTaskitEngine.translateAndWrite(filePath.resolve(fileName), expectedAppObject);
        TestAppObject actualObject = protobufTaskitEngine.readAndTranslate(filePath.resolve(fileName),
                TestInputObject.class);
        assertEquals(expectedAppObject, actualObject);

        // preconditions
        // tested by testRead()
    }

    @Test
    @UnitTestMethod(target = ProtobufJsonTaskitEngine.class, name = "write", args = { Path.class,
            Object.class })
    public void testWriteOutput() throws IOException {
        String fileName = "writeProtoJsonEngine_1-testOutput.json";

        ResourceHelper.createFile(filePath, fileName);

        ProtobufJsonTaskitEngine protobufTaskitEngine = ProtobufJsonTaskitEngine.builder()
                .addTranslationSpec(new TestProtobufObjectTranslationSpec())
                .addTranslationSpec(new TestProtobufComplexObjectTranslationSpec()).build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();
        TestInputObject testInputObject = TestObjectUtil.getInputFromApp(expectedAppObject);

        protobufTaskitEngine.write(filePath.resolve(fileName), testInputObject);
        TestInputObject actualAppObject = protobufTaskitEngine.read(filePath.resolve(fileName),
                TestInputObject.class);
        assertEquals(testInputObject, actualAppObject);

        // preconditions
        // the object to write isn't assignable from Message.class
        ContractException contractException = assertThrows(ContractException.class, () -> {
            protobufTaskitEngine.write(filePath.resolve(fileName), expectedAppObject);
        });

        assertEquals(TaskitCoreError.INVALID_OUTPUT_CLASS, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = ProtobufJsonTaskitEngine.class, name = "translateAndWrite", args = { Path.class,
            Object.class })
    public void testTranslateAndWriteOutput() throws IOException {
        String fileName = "translateAndWriteProtoJsonEngine_1-testOutput.json";

        ResourceHelper.createFile(filePath, fileName);

        ProtobufJsonTaskitEngine protobufTaskitEngine = ProtobufJsonTaskitEngine.builder()
                .addTranslationSpec(new TestProtobufObjectTranslationSpec())
                .addTranslationSpec(new TestProtobufComplexObjectTranslationSpec()).build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();

        protobufTaskitEngine.translateAndWrite(filePath.resolve(fileName), expectedAppObject);
        TestAppObject actualAppObject = protobufTaskitEngine.readAndTranslate(filePath.resolve(fileName),
                TestInputObject.class);
        assertEquals(expectedAppObject, actualAppObject);

        // preconditions
        // tested by testWrite()
    }

    @Test
    @UnitTestMethod(target = ProtobufJsonTaskitEngine.class, name = "translateAndWrite", args = { Path.class,
            Object.class, Class.class })
    public void testTranslateAndWriteOutput_Class() throws IOException {
        String fileName = "translateAndWriteProtoJsonEngine_Class_1-testOutput.json";

        ResourceHelper.createFile(filePath, fileName);

        ProtobufJsonTaskitEngine protobufTaskitEngine = ProtobufJsonTaskitEngine.builder()
                .addTranslationSpec(new TestProtobufObjectTranslationSpec())
                .addTranslationSpec(new TestProtobufComplexObjectTranslationSpec()).build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();

        protobufTaskitEngine.translateAndWrite(filePath.resolve(fileName),
                TestObjectUtil.getChildAppFromApp(expectedAppObject),
                TestAppObject.class);
        TestAppObject actualAppChildObject = protobufTaskitEngine.readAndTranslate(filePath.resolve(fileName),
                TestInputObject.class);
        assertEquals(expectedAppObject, actualAppChildObject);

        // preconditions
        // tested by testWrite()
    }

    @Test
    @UnitTestMethod(target = ProtobufJsonTaskitEngine.class, name = "builder", args = {})
    public void testBuilder() {
        // Nothing to test
    }

    @Test
    @UnitTestMethod(target = ProtobufJsonTaskitEngine.Builder.class, name = "build", args = {})
    public void testBuild() {

        ProtobufJsonTaskitEngine protobufTaskitEngine = ProtobufJsonTaskitEngine.builder()
                .addTranslationSpec(new TestProtobufComplexObjectTranslationSpec()).build();

        assertEquals(ProtobufTaskitEngineId.JSON_ENGINE_ID, protobufTaskitEngine.getTaskitEngineId());
        assertTrue(protobufTaskitEngine.getTaskitEngine().isInitialized());

        // parser and printer do not have equals contracts, so no way to check for
        // equality
        // the use cases for them are adequately tested in: testReadInput and
        // testWriteOutput
    }

    @Test
    @UnitTestForCoverage
    public void testGetDefaultMessage() {
        /*
         * Note: because this method is only ever called if the classRef is an instance
         * of Message.class, this method should never throw an exception. This test is
         * here exclusively for test coverage.
         */
        assertThrows(RuntimeException.class, () -> {
            ProtobufTaskitEngineHelper.getDefaultMessage(Message.class);
        });
    }

    @Test
    @UnitTestForCoverage
    public void testGetDefaultEnum() {
        /*
         * Note: because this method is only ever called if the classRef is an instance
         * of ProtocolMessageEnum.class, this method should never throw an exception.
         * This test is here exclusively for test coverage.
         */
        assertThrows(RuntimeException.class, () -> {
            ProtobufTaskitEngineHelper.getDefaultEnum(ProtocolMessageEnum.class);
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
        // base functionality tested by AT_TaskitEngine.testAddTranslationSpec(). This
        // test will only test the things specifically and uniquely done by the
        // ProtobufJsonTaskitEngine

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
        // Nothing to test, tested by AT_TaskitEngine.testAddTranslator()
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
