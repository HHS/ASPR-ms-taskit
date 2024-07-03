package gov.hhs.aspr.ms.taskit.protobuf.translation.specs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.google.protobuf.UInt32Value;

import gov.hhs.aspr.ms.taskit.protobuf.engine.ProtobufJsonTaskitEngine;
import gov.hhs.aspr.ms.util.annotations.UnitTestConstructor;
import gov.hhs.aspr.ms.util.annotations.UnitTestForCoverage;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;

public class AT_UIntegerTranslationSpec {

    @Test
    @UnitTestConstructor(target = UIntegerTranslationSpec.class, args = {})
    public void testConstructor() {
        assertNotNull(new UIntegerTranslationSpec());
    }

    @Test
    @UnitTestForCoverage
    public void testConvertInputObject() {
        ProtobufJsonTaskitEngine protobufTaskitEngine = ProtobufJsonTaskitEngine.builder().build();

        UIntegerTranslationSpec uIntegerTranslationSpec = new UIntegerTranslationSpec();
        uIntegerTranslationSpec.init(protobufTaskitEngine);

        Integer expectedValue = 10;
        UInt32Value inputValue = UInt32Value.of(expectedValue);

        Integer actualValue = uIntegerTranslationSpec.translateInputObject(inputValue);

        assertEquals(expectedValue, actualValue);
    }

    @Test
    @UnitTestForCoverage
    public void testConvertAppObject() {
        ProtobufJsonTaskitEngine protobufTaskitEngine = ProtobufJsonTaskitEngine.builder().build();

        UIntegerTranslationSpec uIntegerTranslationSpec = new UIntegerTranslationSpec();
        uIntegerTranslationSpec.init(protobufTaskitEngine);

        Integer appValue = 100;
        UInt32Value expectedValue = UInt32Value.of(appValue);

        UInt32Value actualValue = uIntegerTranslationSpec.translateAppObject(appValue);

        assertEquals(expectedValue, actualValue);
    }

    @Test
    @UnitTestMethod(target = UIntegerTranslationSpec.class, name = "getAppObjectClass", args = {})
    public void testGetAppObjectClass() {
        UIntegerTranslationSpec uIntegerTranslationSpec = new UIntegerTranslationSpec();

        assertEquals(Integer.class, uIntegerTranslationSpec.getAppObjectClass());
    }

    @Test
    @UnitTestMethod(target = UIntegerTranslationSpec.class, name = "getInputObjectClass", args = {})
    public void testGetInputObjectClass() {
        UIntegerTranslationSpec uIntegerTranslationSpec = new UIntegerTranslationSpec();

        assertEquals(UInt32Value.class, uIntegerTranslationSpec.getInputObjectClass());
    }
}