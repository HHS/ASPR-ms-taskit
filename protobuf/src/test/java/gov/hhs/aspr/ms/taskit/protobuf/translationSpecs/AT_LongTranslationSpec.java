package gov.hhs.aspr.ms.taskit.protobuf.translationSpecs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.google.protobuf.Int64Value;

import gov.hhs.aspr.ms.taskit.protobuf.engine.ProtobufTaskitEngine;
import gov.hhs.aspr.ms.taskit.protobuf.translation.specs.LongTranslationSpec;
import gov.hhs.aspr.ms.util.annotations.UnitTestConstructor;
import gov.hhs.aspr.ms.util.annotations.UnitTestForCoverage;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;

public class AT_LongTranslationSpec {

    @Test
    @UnitTestConstructor(target = LongTranslationSpec.class, args = {})
    public void testConstructor() {
        assertNotNull(new LongTranslationSpec());
    }

    @Test
    @UnitTestForCoverage
    public void testConvertInputObject() {
        ProtobufTaskitEngine protobufTaskitEngine = ProtobufTaskitEngine.builder().build();

        LongTranslationSpec longTranslationSpec = new LongTranslationSpec();
        longTranslationSpec.init(protobufTaskitEngine);

        Long expectedValue = 100L;
        Int64Value inputValue = Int64Value.of(expectedValue);

        Long actualValue = longTranslationSpec.translateInputObject(inputValue);

        assertEquals(expectedValue, actualValue);
    }

    @Test
    @UnitTestForCoverage
    public void testConvertAppObject() {
        ProtobufTaskitEngine protobufTaskitEngine = ProtobufTaskitEngine.builder().build();

        LongTranslationSpec longTranslationSpec = new LongTranslationSpec();
        longTranslationSpec.init(protobufTaskitEngine);

        Long appValue = 1000L;
        Int64Value expectedValue = Int64Value.of(appValue);

        Int64Value actualValue = longTranslationSpec.translateAppObject(appValue);

        assertEquals(expectedValue, actualValue);
    }

    @Test
    @UnitTestMethod(target = LongTranslationSpec.class, name = "getAppObjectClass", args = {})
    public void testGetAppObjectClass() {
        LongTranslationSpec longTranslationSpec = new LongTranslationSpec();

        assertEquals(Long.class, longTranslationSpec.getAppObjectClass());
    }

    @Test
    @UnitTestMethod(target = LongTranslationSpec.class, name = "getInputObjectClass", args = {})
    public void testGetInputObjectClass() {
        LongTranslationSpec longTranslationSpec = new LongTranslationSpec();

        assertEquals(Int64Value.class, longTranslationSpec.getInputObjectClass());
    }
}
