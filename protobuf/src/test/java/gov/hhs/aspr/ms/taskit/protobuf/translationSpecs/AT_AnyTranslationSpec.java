package gov.hhs.aspr.ms.taskit.protobuf.translationSpecs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.google.protobuf.Any;
import com.google.protobuf.Int32Value;
import com.google.protobuf.InvalidProtocolBufferException;

import gov.hhs.aspr.ms.taskit.core.testsupport.testobject.TestAppEnum;
import gov.hhs.aspr.ms.taskit.protobuf.engine.ProtobufTaskitEngine;
import gov.hhs.aspr.ms.taskit.protobuf.input.WrapperEnumValue;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.testcomplexobject.translationSpecs.TestProtobufComplexObjectTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.testobject.input.TestInputEnum;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.testobject.input.TestInputObject;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.testobject.translationSpecs.TestProtobufEnumTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.testobject.translationSpecs.TestProtobufObjectTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.translation.translationSpecs.AnyTranslationSpec;
import gov.hhs.aspr.ms.util.annotations.UnitTestConstructor;
import gov.hhs.aspr.ms.util.annotations.UnitTestForCoverage;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;

/**
 * TranslationSpec that defines how to translate from any Java Object to a
 * Protobuf {@link Any} type and vice versa
 */
public class AT_AnyTranslationSpec {

    @Test
    @UnitTestConstructor(target = AnyTranslationSpec.class, args = {})
    public void testConstructor() {
        assertNotNull(new AnyTranslationSpec());
    }

    @Test
    @UnitTestForCoverage
    public void testConvertInputObject() {
        ProtobufTaskitEngine protobufTaskitEngine = ProtobufTaskitEngine.builder()
                .addTranslationSpec(new TestProtobufEnumTranslationSpec())
                .addTranslationSpec(new TestProtobufObjectTranslationSpec())
                .addTranslationSpec(new TestProtobufComplexObjectTranslationSpec()).build();

        AnyTranslationSpec anyTranslationSpec = new AnyTranslationSpec();
        anyTranslationSpec.init(protobufTaskitEngine);

        Integer expectedValue = 100;
        Int32Value int32Value = Int32Value.of(expectedValue);

        Any any = Any.pack(int32Value);

        Object obj = anyTranslationSpec.translateInputObject(any);

        assertEquals(expectedValue, obj);

        // preconditions
        // the type url of the any is malformed
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            Any badAny = Any.newBuilder().setTypeUrl("badTypeUrl").build();
            anyTranslationSpec.translateInputObject(badAny);
        });

        assertEquals("Malformed type url", runtimeException.getMessage());

        // the type url is set to a value that doesn't correspond to a Message Type
        runtimeException = assertThrows(RuntimeException.class, () -> {
            Any badAny = Any.newBuilder().setTypeUrl("/" + TestInputEnum.TEST1.getDescriptorForType().getFullName())
                    .build();
            anyTranslationSpec.translateInputObject(badAny);
        });

        assertEquals("Message is not assignable from " + TestInputEnum.class.getName(), runtimeException.getMessage());

        // the type url doesn't match the class of the packed message
        // this is tested in the test: testUnpackMessage
    }

    @Test
    @UnitTestForCoverage
    public void testUnpackMessage() {
        ProtobufTaskitEngine protobufTaskitEngine = ProtobufTaskitEngine.builder()
                .addTranslationSpec(new TestProtobufEnumTranslationSpec())
                .addTranslationSpec(new TestProtobufObjectTranslationSpec())
                .addTranslationSpec(new TestProtobufComplexObjectTranslationSpec()).build();

        AnyTranslationSpec anyTranslationSpec = new AnyTranslationSpec();
        anyTranslationSpec.init(protobufTaskitEngine);

        Integer expectedValue = 100;
        Int32Value int32Value = Int32Value.of(expectedValue);

        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            Any badAny = Any.pack(int32Value);
            anyTranslationSpec.unpackMessage(badAny, TestInputObject.class);
        });

        assertEquals("Unable To unpack any type to given class: " + TestInputObject.class.getName(),
                runtimeException.getMessage());
        assertEquals(InvalidProtocolBufferException.class, runtimeException.getCause().getClass());
    }

    @Test
    @UnitTestForCoverage
    public void testConvertAppObject() {
        ProtobufTaskitEngine protobufTaskitEngine = ProtobufTaskitEngine.builder()
                .addTranslationSpec(new TestProtobufEnumTranslationSpec()).build();

        AnyTranslationSpec anyTranslationSpec = new AnyTranslationSpec();
        anyTranslationSpec.init(protobufTaskitEngine);

        // app object translateed into any
        Integer value = 100;
        Int32Value expectedValue = Int32Value.of(value);

        Any expectedAny = Any.pack(expectedValue);

        Any actualAny = anyTranslationSpec.translateAppObject(value);

        assertEquals(expectedAny, actualAny);

        // app enum translateed into any by wrapping it in a WrapperEnumValue
        TestAppEnum appValue = TestAppEnum.TEST1;
        TestInputEnum expectedValueEnum = TestInputEnum.TEST1;

        WrapperEnumValue wrapperEnumValue = WrapperEnumValue.newBuilder()
                .setEnumTypeUrl(TestInputEnum.getDescriptor().getFullName()).setValue(expectedValueEnum.name()).build();

        expectedAny = Any.pack(wrapperEnumValue);

        actualAny = anyTranslationSpec.translateAppObject(appValue);

        assertEquals(expectedAny, actualAny);

        // by calling covert on an object that was already translateed
        // this case is specifically used for
        // ProtobufTaskitEngine.testGetAnyFromObjectAsSafeClass
        actualAny = anyTranslationSpec.translateAppObject(wrapperEnumValue);

        assertEquals(expectedAny, actualAny);
    }

    @Test
    @UnitTestMethod(target = AnyTranslationSpec.class, name = "getAppObjectClass", args = {})
    public void testGetAppObjectClass() {
        AnyTranslationSpec anyTranslationSpec = new AnyTranslationSpec();

        assertEquals(Object.class, anyTranslationSpec.getAppObjectClass());
    }

    @Test
    @UnitTestMethod(target = AnyTranslationSpec.class, name = "getInputObjectClass", args = {})
    public void testGetInputObjectClass() {
        AnyTranslationSpec anyTranslationSpec = new AnyTranslationSpec();

        assertEquals(Any.class, anyTranslationSpec.getInputObjectClass());
    }
}
