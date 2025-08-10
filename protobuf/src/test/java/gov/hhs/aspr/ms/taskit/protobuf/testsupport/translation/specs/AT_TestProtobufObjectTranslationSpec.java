package gov.hhs.aspr.ms.taskit.protobuf.testsupport.translation.specs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestAppObject;
import gov.hhs.aspr.ms.taskit.protobuf.engine.ProtobufJsonTaskitEngine;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.TestObjectUtil;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.objects.TestInputObject;
import gov.hhs.aspr.ms.util.annotations.UnitTestConstructor;
import gov.hhs.aspr.ms.util.annotations.UnitTestForCoverage;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;

public class AT_TestProtobufObjectTranslationSpec {

    @Test
    @UnitTestConstructor(target = TestProtobufObjectTranslationSpec.class, args = {})
    public void testConstructor() {
        assertNotNull(new TestProtobufObjectTranslationSpec());
    }

    @Test
    @UnitTestForCoverage
    public void testConvertInputObject() {
        TestProtobufObjectTranslationSpec objectTranslationSpec = new TestProtobufObjectTranslationSpec();

        ProtobufJsonTaskitEngine.builder()
                .addTranslationSpec(objectTranslationSpec)
                .addTranslationSpec(new TestProtobufComplexObjectTranslationSpec())
                .addTranslationSpec(new TestProtobufEnumTranslationSpec())
                .build();

        TestAppObject expectedValue = TestObjectUtil.generateTestAppObject();
        TestInputObject inputValue = TestObjectUtil.getInputFromApp(expectedValue);

        TestAppObject actualValue = objectTranslationSpec.translateInputObject(inputValue);

        assertEquals(expectedValue, actualValue);
    }

    @Test
    @UnitTestForCoverage
    public void testConvertAppObject() {
        TestProtobufObjectTranslationSpec objectTranslationSpec = new TestProtobufObjectTranslationSpec();

        ProtobufJsonTaskitEngine.builder()
                .addTranslationSpec(objectTranslationSpec)
                .addTranslationSpec(new TestProtobufComplexObjectTranslationSpec())
                .addTranslationSpec(new TestProtobufEnumTranslationSpec())
                .build();

        TestAppObject appValue = TestObjectUtil.generateTestAppObject();
        TestInputObject expectedValue = TestObjectUtil.getInputFromApp(appValue);

        TestInputObject actualValue = objectTranslationSpec.translateAppObject(appValue);

        assertEquals(expectedValue, actualValue);
    }

    @Test
    @UnitTestMethod(target = TestProtobufObjectTranslationSpec.class, name = "getAppObjectClass", args = {})
    public void testGetAppObjectClass() {
        TestProtobufObjectTranslationSpec objectTranslationSpec = new TestProtobufObjectTranslationSpec();

        assertEquals(TestAppObject.class, objectTranslationSpec.getAppObjectClass());
    }

    @Test
    @UnitTestMethod(target = TestProtobufObjectTranslationSpec.class, name = "getInputObjectClass", args = {})
    public void testGetInputObjectClass() {
        TestProtobufObjectTranslationSpec objectTranslationSpec = new TestProtobufObjectTranslationSpec();

        assertEquals(TestInputObject.class, objectTranslationSpec.getInputObjectClass());
    }
}
