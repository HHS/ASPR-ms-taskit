package gov.hhs.aspr.ms.taskit.protobuf.translation.specs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.google.protobuf.DoubleValue;

import gov.hhs.aspr.ms.taskit.core.translation.TranslationSpecContext;
import gov.hhs.aspr.ms.taskit.protobuf.engine.ProtobufJsonTaskitEngine;
import gov.hhs.aspr.ms.taskit.protobuf.engine.ProtobufTaskitEngine;
import gov.hhs.aspr.ms.util.annotations.UnitTestConstructor;
import gov.hhs.aspr.ms.util.annotations.UnitTestForCoverage;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;

public class AT_DoubleTranslationSpec {

    @Test
    @UnitTestConstructor(target = DoubleTranslationSpec.class, args = {})
    public void testConstructor() {
        assertNotNull(new DoubleTranslationSpec());
    }

    @Test
    @UnitTestForCoverage
    public void testConvertInputObject() {
        ProtobufJsonTaskitEngine protobufTaskitEngine = ProtobufJsonTaskitEngine.builder().build();

        TranslationSpecContext<ProtobufTaskitEngine> translationSpecContext = new TranslationSpecContext<>(
                protobufTaskitEngine);

        DoubleTranslationSpec doubleTranslationSpec = new DoubleTranslationSpec();
        doubleTranslationSpec.init(translationSpecContext);

        Double expectedValue = 100.0;
        DoubleValue inputValue = DoubleValue.of(expectedValue);

        Double actualValue = doubleTranslationSpec.translateInputObject(inputValue);

        assertEquals(expectedValue, actualValue);
    }

    @Test
    @UnitTestForCoverage
    public void testConvertAppObject() {
        ProtobufJsonTaskitEngine protobufTaskitEngine = ProtobufJsonTaskitEngine.builder().build();

        TranslationSpecContext<ProtobufTaskitEngine> translationSpecContext = new TranslationSpecContext<>(
                protobufTaskitEngine);

        DoubleTranslationSpec doubleTranslationSpec = new DoubleTranslationSpec();
        doubleTranslationSpec.init(translationSpecContext);

        Double appValue = 100.0;
        DoubleValue expectedValue = DoubleValue.of(appValue);

        DoubleValue actualValue = doubleTranslationSpec.translateAppObject(appValue);

        assertEquals(expectedValue, actualValue);
    }

    @Test
    @UnitTestMethod(target = DoubleTranslationSpec.class, name = "getAppObjectClass", args = {})
    public void testGetAppObjectClass() {
        DoubleTranslationSpec doubleTranslationSpec = new DoubleTranslationSpec();

        assertEquals(Double.class, doubleTranslationSpec.getAppObjectClass());
    }

    @Test
    @UnitTestMethod(target = DoubleTranslationSpec.class, name = "getInputObjectClass", args = {})
    public void testGetInputObjectClass() {
        DoubleTranslationSpec doubleTranslationSpec = new DoubleTranslationSpec();

        assertEquals(DoubleValue.class, doubleTranslationSpec.getInputObjectClass());
    }
}
