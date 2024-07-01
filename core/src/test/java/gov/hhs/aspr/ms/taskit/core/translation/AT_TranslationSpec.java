package gov.hhs.aspr.ms.taskit.core.translation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngine;
import gov.hhs.aspr.ms.taskit.core.engine.TaskitError;
import gov.hhs.aspr.ms.taskit.core.testsupport.TestObjectUtil;
import gov.hhs.aspr.ms.taskit.core.testsupport.engine.TestTaskitEngine;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestAppChildObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestAppObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestComplexInputObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestInputChildObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestInputObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.specs.TestComplexObjectTranslationSpec;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.specs.TestObjectTranslationSpec;
import gov.hhs.aspr.ms.util.annotations.UnitTestConstructor;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;
import gov.hhs.aspr.ms.util.errors.ContractException;

public class AT_TranslationSpec {

    @Test
    @UnitTestConstructor(target = TranslationSpec.class, args = {})
    public void testConstructor() {
        TranslationSpec<TestInputObject, TestAppObject> translationSpec = new TranslationSpec<>() {

            @Override
            protected TestAppObject translateInputObject(TestInputObject inputObject) {
                throw new UnsupportedOperationException("Unimplemented method 'translateInputObject'");
            }

            @Override
            protected TestInputObject translateAppObject(TestAppObject appObject) {
                throw new UnsupportedOperationException("Unimplemented method 'translateAppObject'");
            }

            @Override
            public Class<TestAppObject> getAppObjectClass() {
                return TestAppObject.class;
            }

            @Override
            public Class<TestInputObject> getInputObjectClass() {
                return TestInputObject.class;
            }

        };

        assertNotNull(translationSpec);
    }

    @Test
    @UnitTestMethod(target = TranslationSpec.class, name = "init", args = { TaskitEngine.class })
    public void testInit() {
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestTaskitEngine testTaskitEngine = TestTaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec).build();

        testObjectTranslationSpec.init(testTaskitEngine);

        assertTrue(testObjectTranslationSpec.isInitialized());

    }

    @Test
    @UnitTestMethod(target = TranslationSpec.class, name = "isInitialized", args = {})
    public void testIsInitialized() {
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestTaskitEngine testTaskitEngine = TestTaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec).build();

        testObjectTranslationSpec.init(testTaskitEngine);

        assertTrue(testObjectTranslationSpec.isInitialized());

        assertFalse(new TestObjectTranslationSpec().isInitialized());
    }

    @Test
    @UnitTestMethod(target = TranslationSpec.class, name = "translate", args = { Object.class })
    public void testConvert() {
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec complexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        TestTaskitEngine testTaskitEngine = TestTaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec).addTranslationSpec(complexObjectTranslationSpec).build();

        testObjectTranslationSpec.init(testTaskitEngine);
        complexObjectTranslationSpec.init(testTaskitEngine);

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();
        TestInputObject expectedInputObject = TestObjectUtil.getInputFromApp(expectedAppObject);

        TestInputObject actualInputObject = testObjectTranslationSpec.translate(expectedAppObject);
        assertEquals(expectedInputObject, actualInputObject);

        TestAppObject actualAppObject = testObjectTranslationSpec.translate(expectedInputObject);
        assertEquals(expectedAppObject, actualAppObject);

        TestAppChildObject expectedAppChildObject = TestObjectUtil.getChildAppFromApp(expectedAppObject);
        TestInputChildObject expectedInputChildObject = TestObjectUtil.getChildInputFromInput(expectedInputObject);

        TestInputObject actualInputChildObject = testObjectTranslationSpec.translate(expectedAppChildObject);
        assertEquals(expectedInputChildObject, TestObjectUtil.getChildInputFromInput(actualInputChildObject));

        TestAppObject actualAppChildObject = testObjectTranslationSpec.translate(expectedInputChildObject);
        assertEquals(expectedAppChildObject, TestObjectUtil.getChildAppFromApp(actualAppChildObject));

        // precondition
        // TranslationSpec not initialized
        ContractException contractException = assertThrows(ContractException.class, () -> {
            TestObjectTranslationSpec testObjectTranslationSpec2 = new TestObjectTranslationSpec();
            testObjectTranslationSpec2.translate(new TestAppObject());
        });

        assertEquals(TaskitError.UNINITIALIZED_TRANSLATION_SPEC, contractException.getErrorType());

        // unknown object
        contractException = assertThrows(ContractException.class, () -> {
            TestObjectTranslationSpec testObjectTranslationSpec2 = new TestObjectTranslationSpec();
            testObjectTranslationSpec2.init(testTaskitEngine);
            testObjectTranslationSpec2.translate(new TestComplexInputObject());
        });

        assertEquals(TaskitError.UNKNOWN_OBJECT, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TranslationSpec.class, name = "hashCode", args = {})
    public void testHashCode() {
        TestTaskitEngine testTaskitEngine = TestTaskitEngine.builder().build();
        // base
        TranslationSpec<TestInputObject, TestAppObject> translationSpecA = new TranslationSpec<>() {

            @Override
            protected TestAppObject translateInputObject(TestInputObject inputObject) {
                throw new UnsupportedOperationException("Unimplemented method 'translateInputObject'");
            }

            @Override
            protected TestInputObject translateAppObject(TestAppObject appObject) {
                throw new UnsupportedOperationException("Unimplemented method 'translateAppObject'");
            }

            @Override
            public Class<TestAppObject> getAppObjectClass() {
                return TestAppObject.class;
            }

            @Override
            public Class<TestInputObject> getInputObjectClass() {
                return TestInputObject.class;
            }

        };

        // same input class, different app class
        TranslationSpec<TestInputObject, TestAppChildObject> translationSpecB = new TranslationSpec<>() {

            @Override
            protected TestAppChildObject translateInputObject(TestInputObject inputObject) {
                throw new UnsupportedOperationException("Unimplemented method 'translateInputObject'");
            }

            @Override
            protected TestInputObject translateAppObject(TestAppChildObject appObject) {
                throw new UnsupportedOperationException("Unimplemented method 'translateAppObject'");
            }

            @Override
            public Class<TestAppChildObject> getAppObjectClass() {
                return TestAppChildObject.class;
            }

            @Override
            public Class<TestInputObject> getInputObjectClass() {
                return TestInputObject.class;
            }

        };

        // same app class, different input class
        TranslationSpec<TestInputChildObject, TestAppObject> translationSpecC = new TranslationSpec<>() {

            @Override
            protected TestAppObject translateInputObject(TestInputChildObject inputObject) {
                throw new UnsupportedOperationException("Unimplemented method 'translateInputObject'");
            }

            @Override
            protected TestInputChildObject translateAppObject(TestAppObject appObject) {
                throw new UnsupportedOperationException("Unimplemented method 'translateAppObject'");
            }

            @Override
            public Class<TestAppObject> getAppObjectClass() {
                return TestAppObject.class;
            }

            @Override
            public Class<TestInputChildObject> getInputObjectClass() {
                return TestInputChildObject.class;
            }

        };

        // different app and different input class
        TranslationSpec<TestInputChildObject, TestAppChildObject> translationSpecD = new TranslationSpec<>() {

            @Override
            protected TestAppChildObject translateInputObject(TestInputChildObject inputObject) {
                throw new UnsupportedOperationException("Unimplemented method 'translateInputObject'");
            }

            @Override
            protected TestInputChildObject translateAppObject(TestAppChildObject appObject) {
                throw new UnsupportedOperationException("Unimplemented method 'translateAppObject'");
            }

            @Override
            public Class<TestAppChildObject> getAppObjectClass() {
                return TestAppChildObject.class;
            }

            @Override
            public Class<TestInputChildObject> getInputObjectClass() {
                return TestInputChildObject.class;
            }

        };

        // duplicate of the base
        TranslationSpec<TestInputObject, TestAppObject> translationSpecE = new TranslationSpec<>() {

            @Override
            protected TestAppObject translateInputObject(TestInputObject inputObject) {
                throw new UnsupportedOperationException("Unimplemented method 'translateInputObject'");
            }

            @Override
            protected TestInputObject translateAppObject(TestAppObject appObject) {
                throw new UnsupportedOperationException("Unimplemented method 'translateAppObject'");
            }

            @Override
            public Class<TestAppObject> getAppObjectClass() {
                return TestAppObject.class;
            }

            @Override
            public Class<TestInputObject> getInputObjectClass() {
                return TestInputObject.class;
            }

        };

        // init the duplicate base
        translationSpecE.init(testTaskitEngine);

        // same exact object should be equal
        assertEquals(translationSpecA.hashCode(), translationSpecA.hashCode());

        // different types of objects should not be equal
        assertNotEquals(translationSpecA.hashCode(), new Object().hashCode());

        // different app class should not be equal
        assertNotEquals(translationSpecA.hashCode(), translationSpecB.hashCode());

        // different input class should not be equal
        assertNotEquals(translationSpecA.hashCode(), translationSpecC.hashCode());

        // different input and different app class should not be equal
        assertNotEquals(translationSpecA.hashCode(), translationSpecD.hashCode());

        // if one is initialized and the other is not, they should not be equal
        assertNotEquals(translationSpecA.hashCode(), translationSpecE.hashCode());

        // init base
        translationSpecA.init(testTaskitEngine);

        // if all above are equal, then the two specs are equal
        assertEquals(translationSpecA.hashCode(), translationSpecE.hashCode());
    }

    @Test
    @UnitTestMethod(target = TranslationSpec.class, name = "equals", args = { Object.class })
    public void testEquals() {
        TestTaskitEngine testTaskitEngine = TestTaskitEngine.builder().build();
        // base
        TranslationSpec<TestInputObject, TestAppObject> translationSpecA = new TranslationSpec<>() {

            @Override
            protected TestAppObject translateInputObject(TestInputObject inputObject) {
                throw new UnsupportedOperationException("Unimplemented method 'translateInputObject'");
            }

            @Override
            protected TestInputObject translateAppObject(TestAppObject appObject) {
                throw new UnsupportedOperationException("Unimplemented method 'translateAppObject'");
            }

            @Override
            public Class<TestAppObject> getAppObjectClass() {
                return TestAppObject.class;
            }

            @Override
            public Class<TestInputObject> getInputObjectClass() {
                return TestInputObject.class;
            }

        };

        // same input class, different app class
        TranslationSpec<TestInputObject, TestAppChildObject> translationSpecB = new TranslationSpec<>() {

            @Override
            protected TestAppChildObject translateInputObject(TestInputObject inputObject) {
                throw new UnsupportedOperationException("Unimplemented method 'translateInputObject'");
            }

            @Override
            protected TestInputObject translateAppObject(TestAppChildObject appObject) {
                throw new UnsupportedOperationException("Unimplemented method 'translateAppObject'");
            }

            @Override
            public Class<TestAppChildObject> getAppObjectClass() {
                return TestAppChildObject.class;
            }

            @Override
            public Class<TestInputObject> getInputObjectClass() {
                return TestInputObject.class;
            }

        };

        // same app class, different input class
        TranslationSpec<TestInputChildObject, TestAppObject> translationSpecC = new TranslationSpec<>() {

            @Override
            protected TestAppObject translateInputObject(TestInputChildObject inputObject) {
                throw new UnsupportedOperationException("Unimplemented method 'translateInputObject'");
            }

            @Override
            protected TestInputChildObject translateAppObject(TestAppObject appObject) {
                throw new UnsupportedOperationException("Unimplemented method 'translateAppObject'");
            }

            @Override
            public Class<TestAppObject> getAppObjectClass() {
                return TestAppObject.class;
            }

            @Override
            public Class<TestInputChildObject> getInputObjectClass() {
                return TestInputChildObject.class;
            }

        };

        // different app and different input class
        TranslationSpec<TestInputChildObject, TestAppChildObject> translationSpecD = new TranslationSpec<>() {

            @Override
            protected TestAppChildObject translateInputObject(TestInputChildObject inputObject) {
                throw new UnsupportedOperationException("Unimplemented method 'translateInputObject'");
            }

            @Override
            protected TestInputChildObject translateAppObject(TestAppChildObject appObject) {
                throw new UnsupportedOperationException("Unimplemented method 'translateAppObject'");
            }

            @Override
            public Class<TestAppChildObject> getAppObjectClass() {
                return TestAppChildObject.class;
            }

            @Override
            public Class<TestInputChildObject> getInputObjectClass() {
                return TestInputChildObject.class;
            }

        };

        // duplicate of the base
        TranslationSpec<TestInputObject, TestAppObject> translationSpecE = new TranslationSpec<>() {

            @Override
            protected TestAppObject translateInputObject(TestInputObject inputObject) {
                throw new UnsupportedOperationException("Unimplemented method 'translateInputObject'");
            }

            @Override
            protected TestInputObject translateAppObject(TestAppObject appObject) {
                throw new UnsupportedOperationException("Unimplemented method 'translateAppObject'");
            }

            @Override
            public Class<TestAppObject> getAppObjectClass() {
                return TestAppObject.class;
            }

            @Override
            public Class<TestInputObject> getInputObjectClass() {
                return TestInputObject.class;
            }

        };

        // init the duplicate base
        translationSpecE.init(testTaskitEngine);

        // same exact object should be equal
        assertEquals(translationSpecA, translationSpecA);

        // null object should not be equal
        assertNotEquals(translationSpecA, null);

        // different types of objects should not be equal
        assertNotEquals(translationSpecA, new Object());

        // different app class should not be equal
        assertNotEquals(translationSpecA, translationSpecB);

        // different input class should not be equal
        assertNotEquals(translationSpecA, translationSpecC);

        // different input and different app class should not be equal
        assertNotEquals(translationSpecA, translationSpecD);

        // if one is initialized and the other is not, they should not be equal
        assertNotEquals(translationSpecA, translationSpecE);

        // init base
        translationSpecA.init(testTaskitEngine);

        // if all above are equal, then the two specs are equal
        assertEquals(translationSpecA, translationSpecE);
    }
}