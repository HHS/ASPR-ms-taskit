package gov.hhs.aspr.ms.taskit.protobuf.translationSpecs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.google.protobuf.Int32Value;

import gov.hhs.aspr.ms.taskit.protobuf.engine.ProtobufTaskitEngine;
import gov.hhs.aspr.ms.taskit.protobuf.translation.specs.IntegerTranslationSpec;
import gov.hhs.aspr.ms.util.annotations.UnitTestConstructor;
import gov.hhs.aspr.ms.util.annotations.UnitTestForCoverage;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;

public class AT_IntegerTranslationSpec {

    @Test
    @UnitTestConstructor(target = IntegerTranslationSpec.class, args = {})
    public void testConstructor() {
        assertNotNull(new IntegerTranslationSpec());
    }

    @Test
    @UnitTestForCoverage
    public void testConvertInputObject() {
        ProtobufTaskitEngine protobufTaskitEngine = ProtobufTaskitEngine.builder().build();

        IntegerTranslationSpec integerTranslationSpec = new IntegerTranslationSpec();
        integerTranslationSpec.init(protobufTaskitEngine);

        Integer expectedValue = 10;
        Int32Value inputValue = Int32Value.of(expectedValue);

        Integer actualValue = integerTranslationSpec.translateInputObject(inputValue);

        assertEquals(expectedValue, actualValue);
    }

    @Test
    @UnitTestForCoverage
    public void testConvertAppObject() {
        ProtobufTaskitEngine protobufTaskitEngine = ProtobufTaskitEngine.builder().build();

        IntegerTranslationSpec integerTranslationSpec = new IntegerTranslationSpec();
        integerTranslationSpec.init(protobufTaskitEngine);

        Integer appValue = 10;
        Int32Value expectedValue = Int32Value.of(appValue);

        Int32Value actualValue = integerTranslationSpec.translateAppObject(appValue);

        assertEquals(expectedValue, actualValue);
    }

    @Test
    @UnitTestMethod(target = IntegerTranslationSpec.class, name = "getAppObjectClass", args = {})
    public void testGetAppObjectClass() {
        IntegerTranslationSpec integerTranslationSpec = new IntegerTranslationSpec();

        assertEquals(Integer.class, integerTranslationSpec.getAppObjectClass());
    }

    @Test
    @UnitTestMethod(target = IntegerTranslationSpec.class, name = "getInputObjectClass", args = {})
    public void testGetInputObjectClass() {
        IntegerTranslationSpec integerTranslationSpec = new IntegerTranslationSpec();

        assertEquals(Int32Value.class, integerTranslationSpec.getInputObjectClass());
    }
}
