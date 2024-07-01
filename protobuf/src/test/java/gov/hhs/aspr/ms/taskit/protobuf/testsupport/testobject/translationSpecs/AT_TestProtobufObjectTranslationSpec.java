package gov.hhs.aspr.ms.taskit.protobuf.testsupport.testobject.translationSpecs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.taskit.core.testsupport.testobject.TestAppObject;
import gov.hhs.aspr.ms.taskit.protobuf.ProtobufTaskitEngine;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.TestObjectUtil;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.testcomplexobject.translationSpecs.TestProtobufComplexObjectTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.testobject.input.TestInputObject;
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
        ProtobufTaskitEngine protobufTaskitEngine = ProtobufTaskitEngine.builder()
                .addTranslationSpec(new TestProtobufComplexObjectTranslationSpec()).build();

        TestProtobufObjectTranslationSpec objectTranslationSpec = new TestProtobufObjectTranslationSpec();
        objectTranslationSpec.init(protobufTaskitEngine);

        TestAppObject expectedValue = TestObjectUtil.generateTestAppObject();
        TestInputObject inputValue = TestObjectUtil.getInputFromApp(expectedValue);

        TestAppObject actualValue = objectTranslationSpec.convertInputObject(inputValue);

        assertEquals(expectedValue, actualValue);
    }

    @Test
    @UnitTestForCoverage
    public void testConvertAppObject() {
        ProtobufTaskitEngine protobufTaskitEngine = ProtobufTaskitEngine.builder()
                .addTranslationSpec(new TestProtobufComplexObjectTranslationSpec()).build();

        TestProtobufObjectTranslationSpec objectTranslationSpec = new TestProtobufObjectTranslationSpec();
        objectTranslationSpec.init(protobufTaskitEngine);

        TestAppObject appValue = TestObjectUtil.generateTestAppObject();
        TestInputObject expectedValue = TestObjectUtil.getInputFromApp(appValue);

        TestInputObject actualValue = objectTranslationSpec.convertAppObject(appValue);

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
