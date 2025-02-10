package gov.hhs.aspr.ms.taskit.protobuf.translation.specs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import com.google.protobuf.Any;

import gov.hhs.aspr.ms.util.annotations.UnitTestConstructor;
import gov.hhs.aspr.ms.util.annotations.UnitTestForCoverage;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;

public class AT_TaskitObjectTranslationSpec {

    @Test
    @UnitTestConstructor(target = TaskitObjectTranslationSpec.class, args = {})
    public void testConstructor() {
        assertNotNull(new TaskitObjectTranslationSpec());
    }

    @Test
    @UnitTestForCoverage
    public void testConvertInputObject() {
        // TODO: implement test
    }

    @Test
    @UnitTestForCoverage
    public void testConvertAppObject() {
        // TODO: implement test
    }

    @Test
    @UnitTestMethod(target = TaskitObjectTranslationSpec.class, name = "getAppObjectClass", args = {})
    public void testGetAppObjectClass() {
        TaskitObjectTranslationSpec TaskitObjectTranslationSpec = new TaskitObjectTranslationSpec();

        assertEquals(Object.class, TaskitObjectTranslationSpec.getAppObjectClass());
    }

    @Test
    @UnitTestMethod(target = TaskitObjectTranslationSpec.class, name = "getInputObjectClass", args = {})
    public void testGetInputObjectClass() {
        TaskitObjectTranslationSpec TaskitObjectTranslationSpec = new TaskitObjectTranslationSpec();

        assertEquals(Any.class, TaskitObjectTranslationSpec.getInputObjectClass());
    }
}
