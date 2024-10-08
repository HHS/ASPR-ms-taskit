package gov.hhs.aspr.ms.taskit.core.testsupport.translation.complexobject.specs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.taskit.core.testsupport.TestObjectUtil;
import gov.hhs.aspr.ms.taskit.core.testsupport.engine.TestTaskitEngine;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestComplexAppObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestComplexInputObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.object.specs.TestObjectTranslationSpec;
import gov.hhs.aspr.ms.util.annotations.UnitTestConstructor;
import gov.hhs.aspr.ms.util.annotations.UnitTestForCoverage;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;

public class AT_TestComplexObjectTranslationSpec {

    @Test
    @UnitTestConstructor(target = TestComplexObjectTranslationSpec.class, args = {})
    public void testConstructor() {
        assertNotNull(new TestComplexObjectTranslationSpec());
    }

    @Test
    @UnitTestForCoverage
    public void testConvertInputObject() {
        TestComplexInputObject testComplexInputObject = TestObjectUtil.generateTestComplexInputObject();
        TestComplexAppObject expectedComplexAppObject = TestObjectUtil
                .getComplexAppFromComplexInput(testComplexInputObject);

        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec complexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        TestTaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec).addTranslationSpec(complexObjectTranslationSpec).build();

        TestComplexAppObject actualComplexAppObject = complexObjectTranslationSpec
                .translateInputObject(testComplexInputObject);

        assertEquals(expectedComplexAppObject, actualComplexAppObject);
    }

    @Test
    @UnitTestForCoverage
    public void testConvertAppObject() {
        TestComplexAppObject testComplexAppObject = TestObjectUtil.generateTestComplexAppObject();
        TestComplexInputObject expectedComplexInputObject = TestObjectUtil
                .getComplexInputFromComplexApp(testComplexAppObject);

        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec complexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        TestTaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec).addTranslationSpec(complexObjectTranslationSpec).build();

        TestComplexInputObject actualComplexInputObject = complexObjectTranslationSpec
                .translateAppObject(testComplexAppObject);

        assertEquals(expectedComplexInputObject, actualComplexInputObject);
    }

    @Test
    @UnitTestMethod(target = TestComplexObjectTranslationSpec.class, name = "getAppObjectClass", args = {})
    public void testGetAppObjectClass() {
        TestComplexObjectTranslationSpec testComplexObjectTranslationSpec = new TestComplexObjectTranslationSpec();

        assertEquals(TestComplexAppObject.class, testComplexObjectTranslationSpec.getAppObjectClass());
    }

    @Test
    @UnitTestMethod(target = TestComplexObjectTranslationSpec.class, name = "getInputObjectClass", args = {})
    public void testGetInputObjectClass() {
        TestComplexObjectTranslationSpec testComplexObjectTranslationSpec = new TestComplexObjectTranslationSpec();

        assertEquals(TestComplexInputObject.class, testComplexObjectTranslationSpec.getInputObjectClass());
    }

}
