package gov.hhs.aspr.ms.taskit.core.testsupport.translation;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestAppObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestInputObject;
import gov.hhs.aspr.ms.util.annotations.UnitTestConstructor;

public class AT_TestTranslationSpec {

    @Test
    @UnitTestConstructor(target = TestTranslationSpec.class, args = {})
    public void testConstructor() {
        assertNotNull(new TestTranslationSpec<TestInputObject, TestAppObject>() {

            @Override
            protected TestAppObject translateInputObject(TestInputObject inputObject) {
                return new TestAppObject();
            }

            @Override
            protected TestInputObject translateAppObject(TestAppObject appObject) {
                return new TestInputObject();
            }

            @Override
            public Class<TestAppObject> getAppObjectClass() {
                return TestAppObject.class;
            }

            @Override
            public Class<TestInputObject> getInputObjectClass() {
                return TestInputObject.class;
            }

        });
    }
}
