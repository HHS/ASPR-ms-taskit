package gov.hhs.aspr.ms.taskit.protobuf.translationSpecs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.google.protobuf.FloatValue;

import gov.hhs.aspr.ms.taskit.protobuf.engine.ProtobufTaskitEngine;
import gov.hhs.aspr.ms.taskit.protobuf.translation.specs.FloatTranslationSpec;
import gov.hhs.aspr.ms.util.annotations.UnitTestConstructor;
import gov.hhs.aspr.ms.util.annotations.UnitTestForCoverage;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;

public class AT_FloatTranslationSpec {

    @Test
    @UnitTestConstructor(target = FloatTranslationSpec.class, args = {})
    public void testConstructor() {
        assertNotNull(new FloatTranslationSpec());
    }

    @Test
    @UnitTestForCoverage
    public void testConvertInputObject() {
        ProtobufTaskitEngine protobufTaskitEngine = ProtobufTaskitEngine.builder().build();

        FloatTranslationSpec floatTranslationSpec = new FloatTranslationSpec();
        floatTranslationSpec.init(protobufTaskitEngine);

        Float expectedValue = 10.0f;
        FloatValue inputValue = FloatValue.of(expectedValue);

        Float actualValue = floatTranslationSpec.translateInputObject(inputValue);

        assertEquals(expectedValue, actualValue);
    }

    @Test
    @UnitTestForCoverage
    public void testConvertAppObject() {
        ProtobufTaskitEngine protobufTaskitEngine = ProtobufTaskitEngine.builder().build();

        FloatTranslationSpec floatTranslationSpec = new FloatTranslationSpec();
        floatTranslationSpec.init(protobufTaskitEngine);

        Float appValue = 10.01f;
        FloatValue expectedValue = FloatValue.of(appValue);

        FloatValue actualValue = floatTranslationSpec.translateAppObject(appValue);

        assertEquals(expectedValue, actualValue);
    }

    @Test
    @UnitTestMethod(target = FloatTranslationSpec.class, name = "getAppObjectClass", args = {})
    public void testGetAppObjectClass() {
        FloatTranslationSpec floatTranslationSpec = new FloatTranslationSpec();

        assertEquals(Float.class, floatTranslationSpec.getAppObjectClass());
    }

    @Test
    @UnitTestMethod(target = FloatTranslationSpec.class, name = "getInputObjectClass", args = {})
    public void testGetInputObjectClass() {
        FloatTranslationSpec floatTranslationSpec = new FloatTranslationSpec();

        assertEquals(FloatValue.class, floatTranslationSpec.getInputObjectClass());
    }
}