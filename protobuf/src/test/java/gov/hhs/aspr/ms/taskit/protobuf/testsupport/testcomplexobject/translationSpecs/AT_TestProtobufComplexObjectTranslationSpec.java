package gov.hhs.aspr.ms.taskit.protobuf.testsupport.testcomplexobject.translationSpecs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.taskit.core.testsupport.testcomplexobject.TestComplexAppObject;
import gov.hhs.aspr.ms.taskit.protobuf.engine.ProtobufTaskitEngine;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.TestObjectUtil;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.testcomplexobject.input.TestComplexInputObject;
import gov.hhs.aspr.ms.util.annotations.UnitTestConstructor;
import gov.hhs.aspr.ms.util.annotations.UnitTestForCoverage;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;

public class AT_TestProtobufComplexObjectTranslationSpec {

    @Test
    @UnitTestConstructor(target = TestProtobufComplexObjectTranslationSpec.class, args = {})
    public void testConstructor() {
        assertNotNull(new TestProtobufComplexObjectTranslationSpec());
    }

    @Test
    @UnitTestForCoverage
    public void testConvertInputObject() {
        ProtobufTaskitEngine protobufTaskitEngine = ProtobufTaskitEngine.builder()
                .addTranslationSpec(new TestProtobufComplexObjectTranslationSpec()).build();

        TestProtobufComplexObjectTranslationSpec complexObjectTranslationSpec = new TestProtobufComplexObjectTranslationSpec();
        complexObjectTranslationSpec.init(protobufTaskitEngine);

        TestComplexAppObject expectedValue = TestObjectUtil.generateTestComplexAppObject();
        TestComplexInputObject inputValue = TestObjectUtil.getComplexInputFromComplexApp(expectedValue);

        TestComplexAppObject actualValue = complexObjectTranslationSpec.translateInputObject(inputValue);

        assertEquals(expectedValue, actualValue);
    }

    @Test
    @UnitTestForCoverage
    public void testConvertAppObject() {
        ProtobufTaskitEngine protobufTaskitEngine = ProtobufTaskitEngine.builder()
                .addTranslationSpec(new TestProtobufComplexObjectTranslationSpec()).build();

        TestProtobufComplexObjectTranslationSpec complexObjectTranslationSpec = new TestProtobufComplexObjectTranslationSpec();
        complexObjectTranslationSpec.init(protobufTaskitEngine);

        TestComplexAppObject appValue = TestObjectUtil.generateTestComplexAppObject();
        TestComplexInputObject expectedValue = TestObjectUtil.getComplexInputFromComplexApp(appValue);

        TestComplexInputObject actualValue = complexObjectTranslationSpec.translateAppObject(appValue);

        assertEquals(expectedValue, actualValue);
    }

    @Test
    @UnitTestMethod(target = TestProtobufComplexObjectTranslationSpec.class, name = "getAppObjectClass", args = {})
    public void testGetAppObjectClass() {
        TestProtobufComplexObjectTranslationSpec complexObjectTranslationSpec = new TestProtobufComplexObjectTranslationSpec();

        assertEquals(TestComplexAppObject.class, complexObjectTranslationSpec.getAppObjectClass());
    }

    @Test
    @UnitTestMethod(target = TestProtobufComplexObjectTranslationSpec.class, name = "getInputObjectClass", args = {})
    public void testGetInputObjectClass() {
        TestProtobufComplexObjectTranslationSpec complexObjectTranslationSpec = new TestProtobufComplexObjectTranslationSpec();

        assertEquals(TestComplexInputObject.class, complexObjectTranslationSpec.getInputObjectClass());
    }
}
