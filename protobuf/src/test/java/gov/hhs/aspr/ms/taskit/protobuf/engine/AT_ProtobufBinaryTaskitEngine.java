package gov.hhs.aspr.ms.taskit.protobuf.engine;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.google.protobuf.Message;
import com.google.protobuf.ProtocolMessageEnum;

import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestAppObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.complexobject.TestComplexObjectTranslatorId;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.object.specs.TestObjectTranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.ITranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.Translator;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.TestObjectUtil;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.objects.TestComplexInputObject;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.objects.TestInputEnum;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.objects.TestInputObject;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.translation.specs.TestProtobufComplexObjectTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.translation.specs.TestProtobufObjectTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.translation.ProtobufTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.translation.specs.AnyTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.translation.specs.BooleanTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.translation.specs.DateTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.translation.specs.DoubleTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.translation.specs.EnumTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.translation.specs.FloatTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.translation.specs.IntegerTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.translation.specs.LongTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.translation.specs.StringTranslationSpec;
import gov.hhs.aspr.ms.util.annotations.UnitTestForCoverage;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;
import gov.hhs.aspr.ms.util.errors.ContractException;
import gov.hhs.aspr.ms.util.resourcehelper.ResourceHelper;

public class AT_ProtobufBinaryTaskitEngine {
    Path basePath = ResourceHelper.getResourceDir(this.getClass());
    Path filePath = ResourceHelper.createDirectory(basePath, "test-output");

    @Test
    @UnitTestForCoverage
    public void testReadFile() throws IOException {
        String fileName = "readProtoJsonEngine_1-testOutput.bin";

        ResourceHelper.createFile(filePath, fileName);

        ProtobufBinaryTaskitEngine protobufTaskitEngine = ProtobufBinaryTaskitEngine.builder()
                .addTranslationSpec(new TestProtobufObjectTranslationSpec())
                .addTranslationSpec(new TestProtobufComplexObjectTranslationSpec()).build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();
        TestInputObject expectedObject = TestObjectUtil.getInputFromApp(expectedAppObject);

        protobufTaskitEngine.translateAndWrite(filePath.resolve(fileName), expectedAppObject);
        TestInputObject actualObject = protobufTaskitEngine.read(filePath.resolve(fileName),
                TestInputObject.class);
        assertEquals(expectedObject, actualObject);
    }

    @Test
    @UnitTestForCoverage
    public void testWriteFile() throws IOException {
        String fileName = "writeProtoJsonEngine_1-testOutput.bin";

        ResourceHelper.createFile(filePath, fileName);

        ProtobufBinaryTaskitEngine protobufTaskitEngine = ProtobufBinaryTaskitEngine.builder()
                .addTranslationSpec(new TestProtobufObjectTranslationSpec())
                .addTranslationSpec(new TestProtobufComplexObjectTranslationSpec()).build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();
        TestInputObject testInputObject = TestObjectUtil.getInputFromApp(expectedAppObject);

        protobufTaskitEngine.write(filePath.resolve(fileName), testInputObject);
        TestInputObject actualAppObject = protobufTaskitEngine.read(filePath.resolve(fileName),
                TestInputObject.class);
        assertEquals(testInputObject, actualAppObject);
    }

    @Test
    @UnitTestMethod(target = ProtobufBinaryTaskitEngine.class, name = "builder", args = {})
    public void testBuilder() {
        // Nothing to test
    }

    @Test
    @UnitTestMethod(target = ProtobufBinaryTaskitEngine.Builder.class, name = "build", args = {})
    public void testBuild() {

        /*
         * Test Note: build internally calls TaskitEngineData.build(). As such, the
         * build method will also throw the exceptions from that method. Because that is
         * already tested in Taskit, the precondition tests will not be tested here
         */
        ProtobufBinaryTaskitEngine protobufTaskitEngine = ProtobufBinaryTaskitEngine.builder()
                .addTranslationSpec(new TestProtobufComplexObjectTranslationSpec()).build();

        assertEquals(ProtobufTaskitEngineId.BINARY_ENGINE_ID, protobufTaskitEngine.getTaskitEngineId());
        assertTrue(protobufTaskitEngine.isInitialized());

        List<ProtobufTranslationSpec<?, ?>> list = new ArrayList<>();

        list.add(new BooleanTranslationSpec());
        list.add(new IntegerTranslationSpec());
        list.add(new LongTranslationSpec());
        list.add(new StringTranslationSpec());
        list.add(new FloatTranslationSpec());
        list.add(new DoubleTranslationSpec());
        list.add(new DateTranslationSpec());
        list.add(new EnumTranslationSpec());
        list.add(new AnyTranslationSpec());

        for (ProtobufTranslationSpec<?, ?> translationSpec : list) {
            translationSpec.init(protobufTaskitEngine);
        }

        assertTrue(protobufTaskitEngine.getTranslationSpecs().containsAll(list));

        // parser and printer do not have equals contracts, so no way to check for
        // equality
        // the use cases for them are adequately tested in: testReadInput and
        // testWriteOutput
    }

    @Test
    @UnitTestForCoverage
    public void testPopulate() {
        ProtobufBinaryTaskitEngine.Builder pBuilder = ProtobufBinaryTaskitEngine.builder();

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
    @UnitTestMethod(target = ProtobufBinaryTaskitEngine.Builder.class, name = "addTranslationSpec", args = {
            ITranslationSpec.class })
    public void testAddTranslationSpec() {
        // base functionality and preconditions tested by core.
        // This test will only test the things specifically and uniquely done by the
        // ProtobufBinaryTaskitEngine

        ProtobufBinaryTaskitEngine protobufTaskitEngine = ProtobufBinaryTaskitEngine.builder()
                .addTranslationSpec(new TestProtobufObjectTranslationSpec())
                .addTranslationSpec(new TestProtobufComplexObjectTranslationSpec()).build();

        assertDoesNotThrow(() -> {
            protobufTaskitEngine.getClassFromTypeUrl(TestInputObject.getDescriptor().getFullName());
            protobufTaskitEngine.getClassFromTypeUrl(TestComplexInputObject.getDescriptor().getFullName());
        });

        // precondition
        // that the inputClass is not a Message nor a
        // ProtocolMessageEnum, and is tested in the testPopulate() test
        // translation spec is not a protobuf translation spec
        ContractException contractException = assertThrows(ContractException.class, () -> {
            ProtobufBinaryTaskitEngine.builder()
                    .addTranslationSpec(new TestObjectTranslationSpec());
        });

        assertEquals(ProtobufTaskitError.INVALID_TRANSLATION_SPEC, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = ProtobufBinaryTaskitEngine.Builder.class, name = "addTranslator", args = {
            Translator.class })
    public void testAddTranslator() {
        Translator translator = Translator.builder()
                .setTranslatorId(TestComplexObjectTranslatorId.TRANSLATOR_ID)
                .setInitializer(translatorContext -> {
                    translatorContext.getTaskitEngineBuilder(ProtobufBinaryTaskitEngine.Builder.class)
                            .addTranslationSpec(new TestProtobufComplexObjectTranslationSpec());
                })
                .build();

        ProtobufBinaryTaskitEngine.builder()
                .addTranslator(translator).build();

        assertTrue(translator.isInitialized());

        // preconditions tested by core
    }
}
