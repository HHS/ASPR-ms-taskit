package gov.hhs.aspr.ms.taskit.core.testsupport.translation.testenum.specs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.taskit.core.testsupport.TestObjectUtil;
import gov.hhs.aspr.ms.taskit.core.testsupport.engine.TestTaskitEngine;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestAppEnum;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestInputEnum;
import gov.hhs.aspr.ms.util.annotations.UnitTestConstructor;
import gov.hhs.aspr.ms.util.annotations.UnitTestForCoverage;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;

public class AT_TestEnumTranslationSpec {
    @Test
    @UnitTestConstructor(target = TestEnumTranslationSpec.class, args = {})
    public void testConstructor() {
        assertNotNull(new TestEnumTranslationSpec());
    }

    @Test
    @UnitTestForCoverage
    public void testConvertInputObject() {
        TestInputEnum testInputEnum = TestInputEnum.TEST1;
        TestAppEnum expectedAppEnum = TestObjectUtil.getAppEnumFromInputEnum(testInputEnum);

        TestEnumTranslationSpec testEnumTranslationSpec = new TestEnumTranslationSpec();

        TestTaskitEngine.builder()//
                .addTranslationSpec(testEnumTranslationSpec)//
                .build();

        TestAppEnum actualAppEnum = testEnumTranslationSpec.translateInputObject(testInputEnum);

        assertEquals(expectedAppEnum, actualAppEnum);
    }

    @Test
    @UnitTestForCoverage
    public void testConvertAppObject() {
        TestAppEnum testAppEnum = TestAppEnum.TEST2;
        TestInputEnum expectedInputEnum = TestObjectUtil.getInputEnumFromAppEnum(testAppEnum);

        TestEnumTranslationSpec testEnumTranslationSpec = new TestEnumTranslationSpec();

        TestTaskitEngine.builder()//
                .addTranslationSpec(testEnumTranslationSpec)//
                .build();

        TestInputEnum actualInputEnum = testEnumTranslationSpec.translateAppObject(testAppEnum);

        assertEquals(expectedInputEnum, actualInputEnum);
    }

    @Test
    @UnitTestMethod(target = TestEnumTranslationSpec.class, name = "getAppObjectClass", args = {})
    public void testGetAppObjectClass() {
        TestEnumTranslationSpec testEnumTranslationSpec = new TestEnumTranslationSpec();

        assertEquals(TestAppEnum.class, testEnumTranslationSpec.getAppObjectClass());
    }

    @Test
    @UnitTestMethod(target = TestEnumTranslationSpec.class, name = "getInputObjectClass", args = {})
    public void testGetInputObjectClass() {
        TestEnumTranslationSpec testEnumTranslationSpec = new TestEnumTranslationSpec();

        assertEquals(TestInputEnum.class, testEnumTranslationSpec.getInputObjectClass());
    }
}
