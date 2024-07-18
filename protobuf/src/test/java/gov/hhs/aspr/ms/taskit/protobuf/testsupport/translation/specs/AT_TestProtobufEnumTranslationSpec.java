package gov.hhs.aspr.ms.taskit.protobuf.testsupport.translation.specs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestAppEnum;
import gov.hhs.aspr.ms.taskit.protobuf.engine.ProtobufJsonTaskitEngine;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.objects.TestInputEnum;
import gov.hhs.aspr.ms.util.annotations.UnitTestConstructor;
import gov.hhs.aspr.ms.util.annotations.UnitTestForCoverage;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;

public class AT_TestProtobufEnumTranslationSpec {

    @Test
    @UnitTestConstructor(target = TestProtobufEnumTranslationSpec.class, args = {})
    public void testConstructor() {
        assertNotNull(new TestProtobufEnumTranslationSpec());
    }

    @Test
    @UnitTestForCoverage
    public void testConvertInputObject() {
        TestProtobufEnumTranslationSpec enumTranslationSpec = new TestProtobufEnumTranslationSpec();

        ProtobufJsonTaskitEngine.builder()
                .addTranslationSpec(enumTranslationSpec)
                .build();

        TestAppEnum expectedValue = TestAppEnum.TEST1;
        TestInputEnum inputValue = TestInputEnum.TEST1;

        TestAppEnum actualValue = enumTranslationSpec.translateInputObject(inputValue);

        assertEquals(expectedValue, actualValue);
    }

    @Test
    @UnitTestForCoverage
    public void testConvertAppObject() {
        TestProtobufEnumTranslationSpec enumTranslationSpec = new TestProtobufEnumTranslationSpec();

        ProtobufJsonTaskitEngine.builder()
                .addTranslationSpec(enumTranslationSpec)
                .build();

        TestAppEnum appValue = TestAppEnum.TEST2;
        TestInputEnum expectedValue = TestInputEnum.TEST2;

        TestInputEnum actualValue = enumTranslationSpec.translateAppObject(appValue);

        assertEquals(expectedValue, actualValue);
    }

    @Test
    @UnitTestMethod(target = TestProtobufEnumTranslationSpec.class, name = "getAppObjectClass", args = {})
    public void testGetAppObjectClass() {
        TestProtobufEnumTranslationSpec enumTranslationSpec = new TestProtobufEnumTranslationSpec();

        assertEquals(TestAppEnum.class, enumTranslationSpec.getAppObjectClass());
    }

    @Test
    @UnitTestMethod(target = TestProtobufEnumTranslationSpec.class, name = "getInputObjectClass", args = {})
    public void testGetInputObjectClass() {
        TestProtobufEnumTranslationSpec enumTranslationSpec = new TestProtobufEnumTranslationSpec();

        assertEquals(TestInputEnum.class, enumTranslationSpec.getInputObjectClass());
    }
}
