package gov.hhs.aspr.ms.taskit.protobuf.translation.specs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.google.protobuf.StringValue;

import gov.hhs.aspr.ms.taskit.protobuf.engine.ProtobufJsonTaskitEngine;
import gov.hhs.aspr.ms.util.annotations.UnitTestConstructor;
import gov.hhs.aspr.ms.util.annotations.UnitTestForCoverage;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;

public class AT_StringTranslationSpec {

    @Test
    @UnitTestConstructor(target = StringTranslationSpec.class, args = {})
    public void testConstructor() {
        assertNotNull(new StringTranslationSpec());
    }

    @Test
    @UnitTestForCoverage
    public void testConvertInputObject() {
        ProtobufJsonTaskitEngine protobufTaskitEngine = ProtobufJsonTaskitEngine.builder().build();

        StringTranslationSpec stringTranslationSpec = new StringTranslationSpec();
        stringTranslationSpec.init(protobufTaskitEngine);

        String expectedValue = "testString";
        StringValue inputValue = StringValue.of(expectedValue);

        String actualValue = stringTranslationSpec.translateInputObject(inputValue);

        assertEquals(expectedValue, actualValue);
    }

    @Test
    @UnitTestForCoverage
    public void testConvertAppObject() {
        ProtobufJsonTaskitEngine protobufTaskitEngine = ProtobufJsonTaskitEngine.builder().build();

        StringTranslationSpec stringTranslationSpec = new StringTranslationSpec();
        stringTranslationSpec.init(protobufTaskitEngine);

        String appValue = "testString";
        StringValue expectedValue = StringValue.of(appValue);

        StringValue actualValue = stringTranslationSpec.translateAppObject(appValue);

        assertEquals(expectedValue, actualValue);
    }

    @Test
    @UnitTestMethod(target = StringTranslationSpec.class, name = "getAppObjectClass", args = {})
    public void testGetAppObjectClass() {
        StringTranslationSpec stringTranslationSpec = new StringTranslationSpec();

        assertEquals(String.class, stringTranslationSpec.getAppObjectClass());
    }

    @Test
    @UnitTestMethod(target = StringTranslationSpec.class, name = "getInputObjectClass", args = {})
    public void testGetInputObjectClass() {
        StringTranslationSpec stringTranslationSpec = new StringTranslationSpec();

        assertEquals(StringValue.class, stringTranslationSpec.getInputObjectClass());
    }
}
