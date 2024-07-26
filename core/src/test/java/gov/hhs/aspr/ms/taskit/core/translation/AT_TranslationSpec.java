package gov.hhs.aspr.ms.taskit.core.translation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngine;
import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngineData;
import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngineId;
import gov.hhs.aspr.ms.taskit.core.engine.TaskitError;
import gov.hhs.aspr.ms.taskit.core.testsupport.TestObjectUtil;
import gov.hhs.aspr.ms.taskit.core.testsupport.engine.TestTaskitEngine;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestAppChildObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestAppObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestComplexInputObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestInputChildObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestInputObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestObjectWrapper;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.TestTranslationSpec;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.complexobject.specs.TestComplexObjectTranslationSpec;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.object.specs.TestObjectTranslationSpec;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;
import gov.hhs.aspr.ms.util.errors.ContractException;

public class AT_TranslationSpec {

    @Test
    @UnitTestMethod(target = TranslationSpec.class, name = "init", args = { TaskitEngine.class })
    public void testInit() {
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestTaskitEngine testTaskitEngine = TestTaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec).build();

        assertTrue(testObjectTranslationSpec.isInitialized());

        // preconditions
        // calling init more than once
        ContractException contractException = assertThrows(ContractException.class, () -> {
            testObjectTranslationSpec.init(testTaskitEngine);
        });

        assertEquals(TaskitError.DOUBLE_TRANSLATION_SPEC_INIT, contractException.getErrorType());

        // given taskit engine type does not match type parameter
        contractException = assertThrows(ContractException.class, () -> {
            TaskitEngine taskitEngine = new TaskitEngine(
                    TaskitEngineData.builder().addTranslationSpec(new TestObjectTranslationSpec()).build(),
                    new TaskitEngineId() {

                    }) {

                @Override
                protected <O> void writeToFile(FileWriter fileWriter, O outputObject) throws IOException {

                }

                @Override
                protected <I> I readFile(FileReader reader, Class<I> inputClassRef) throws IOException {
                    return null;
                }

            };

            new TestObjectTranslationSpec().init(taskitEngine);
        });

        assertEquals(TaskitError.INVALID_TASKIT_ENGINE, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TranslationSpec.class, name = "isInitialized", args = {})
    public void testIsInitialized() {
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestTaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec).build();

        assertTrue(testObjectTranslationSpec.isInitialized());

        assertFalse(new TestObjectTranslationSpec().isInitialized());
    }

    @Test
    @UnitTestMethod(target = TranslationSpec.class, name = "translate", args = { Object.class })
    public void testTranslate() {
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec complexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        TestTranslationSpec<TestObjectWrapper, Object> wrapperTranslationSpec = new TestTranslationSpec<TestObjectWrapper, Object>() {

            @Override
            protected Object translateInputObject(TestObjectWrapper inputObject) {
                return inputObject.getWrappedObject();
            }

            @Override
            protected TestObjectWrapper translateAppObject(Object appObject) {
                TestObjectWrapper objectWrapper = new TestObjectWrapper();

                objectWrapper.setWrappedObject(appObject);

                return objectWrapper;
            }

            @Override
            public Class<Object> getAppObjectClass() {
                return Object.class;
            }

            @Override
            public Class<TestObjectWrapper> getInputObjectClass() {
                return TestObjectWrapper.class;
            }
        };

        TestTranslationSpec<Object, TestObjectWrapper> wrapperTranslationSpec2 = new TestTranslationSpec<Object, TestObjectWrapper>() {

            @Override
            protected TestObjectWrapper translateInputObject(Object inputObject) {
                TestObjectWrapper testObjectWrapper = new TestObjectWrapper();
                testObjectWrapper.setWrappedObject(inputObject);
                return testObjectWrapper;
            }

            @Override
            protected Object translateAppObject(TestObjectWrapper appObject) {
                return appObject.getWrappedObject();
            }

            @Override
            public Class<TestObjectWrapper> getAppObjectClass() {
                return TestObjectWrapper.class;
            }

            @Override
            public Class<Object> getInputObjectClass() {
                return Object.class;
            }
        };

        TestTaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec)
                .addTranslationSpec(complexObjectTranslationSpec)
                .addTranslationSpec(wrapperTranslationSpec)
                .addTranslationSpec(wrapperTranslationSpec2)
                .build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();
        TestInputObject expectedInputObject = TestObjectUtil.getInputFromApp(expectedAppObject);

        // shouldTranslateAsApp
        TestInputObject actualInputObject = testObjectTranslationSpec.translate(expectedAppObject);
        assertEquals(expectedInputObject, actualInputObject);

        // shouldTranslateAsIn
        TestAppObject actualAppObject = testObjectTranslationSpec.translate(expectedInputObject);
        assertEquals(expectedAppObject, actualAppObject);

        TestAppChildObject expectedAppChildObject = TestObjectUtil.getChildAppFromApp(expectedAppObject);
        TestInputChildObject expectedInputChildObject = TestObjectUtil.getChildInputFromInput(expectedInputObject);

        // shouldTranslateAsApp
        TestInputObject actualInputChildObject = testObjectTranslationSpec.translate(expectedAppChildObject);
        assertEquals(expectedInputChildObject, TestObjectUtil.getChildInputFromInput(actualInputChildObject));

        // shouldTranslateAsIn
        TestAppObject actualAppChildObject = testObjectTranslationSpec.translate(expectedInputChildObject);
        assertEquals(expectedAppChildObject, TestObjectUtil.getChildAppFromApp(actualAppChildObject));

        // for code coverage
        Object wrappedObj = wrapperTranslationSpec.translate(new TestObjectWrapper());
        assertNull(wrappedObj);

        wrappedObj = wrapperTranslationSpec2.translate(new TestObjectWrapper());
        assertNull(wrappedObj);

        // precondition
        // TranslationSpec not initialized
        ContractException contractException = assertThrows(ContractException.class, () -> {
            TestObjectTranslationSpec testObjectTranslationSpec2 = new TestObjectTranslationSpec();
            testObjectTranslationSpec2.translate(new TestAppObject());
        });

        assertEquals(TaskitError.UNINITIALIZED_TRANSLATION_SPEC, contractException.getErrorType());

        // object is null
        contractException = assertThrows(ContractException.class, () -> {
            testObjectTranslationSpec.translate(null);
        });

        assertEquals(TaskitError.NULL_OBJECT_FOR_TRANSLATION, contractException.getErrorType());

        // unknown object
        contractException = assertThrows(ContractException.class, () -> {
            TestObjectTranslationSpec testObjectTranslationSpec2 = new TestObjectTranslationSpec();
            TestTaskitEngine.builder()
                    .addTranslationSpec(testObjectTranslationSpec2)
                    .build();

            testObjectTranslationSpec2.translate(new Object());
        });

        assertEquals(TaskitError.UNKNOWN_OBJECT, contractException.getErrorType());

        // unknown object 2
        contractException = assertThrows(ContractException.class, () -> {
            TestObjectTranslationSpec testObjectTranslationSpec2 = new TestObjectTranslationSpec();
            TestTaskitEngine.builder()
                    .addTranslationSpec(testObjectTranslationSpec2)
                    .build();

            testObjectTranslationSpec2.translate(new TestComplexInputObject());
        });

        assertEquals(TaskitError.UNKNOWN_OBJECT, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TranslationSpec.class, name = "getTranslationSpecClassMapping", args = {})
    public void testGetTranslationSpecClassMapping() {
        Map<Class<?>, ITranslationSpec> expectedMapping = new LinkedHashMap<>();
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();

        expectedMapping.put(testObjectTranslationSpec.getAppObjectClass(), testObjectTranslationSpec);
        expectedMapping.put(testObjectTranslationSpec.getInputObjectClass(), testObjectTranslationSpec);

        assertEquals(expectedMapping, testObjectTranslationSpec.getTranslationSpecClassMapping());
    }

    @Test
    @UnitTestMethod(target = TranslationSpec.class, name = "hashCode", args = {})
    public void testHashCode() {
        TestTaskitEngine testTaskitEngine = TestTaskitEngine.builder()
                .addTranslationSpec(new TestComplexObjectTranslationSpec()).build();
        // base
        TranslationSpec<TestInputObject, TestAppObject, TestTaskitEngine> translationSpecA = new TranslationSpec<>(
                TestTaskitEngine.class) {

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
        TranslationSpec<TestInputObject, TestAppChildObject, TestTaskitEngine> translationSpecB = new TranslationSpec<>(
                TestTaskitEngine.class) {

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
        TranslationSpec<TestInputChildObject, TestAppObject, TestTaskitEngine> translationSpecC = new TranslationSpec<>(
                TestTaskitEngine.class) {

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
        TranslationSpec<TestInputChildObject, TestAppChildObject, TestTaskitEngine> translationSpecD = new TranslationSpec<>(
                TestTaskitEngine.class) {

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
        TranslationSpec<TestInputObject, TestAppObject, TestTaskitEngine> translationSpecE = new TranslationSpec<>(
                TestTaskitEngine.class) {

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
        TestTaskitEngine testTaskitEngine = TestTaskitEngine.builder()
                .addTranslationSpec(new TestComplexObjectTranslationSpec()).build();
        // base
        TranslationSpec<TestInputObject, TestAppObject, TestTaskitEngine> translationSpecA = new TranslationSpec<>(
                TestTaskitEngine.class) {

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
        TranslationSpec<TestInputObject, TestAppChildObject, TestTaskitEngine> translationSpecB = new TranslationSpec<>(
                TestTaskitEngine.class) {

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
        TranslationSpec<TestInputChildObject, TestAppObject, TestTaskitEngine> translationSpecC = new TranslationSpec<>(
                TestTaskitEngine.class) {

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
        TranslationSpec<TestInputChildObject, TestAppChildObject, TestTaskitEngine> translationSpecD = new TranslationSpec<>(
                TestTaskitEngine.class) {

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
        TranslationSpec<TestInputObject, TestAppObject, TestTaskitEngine> translationSpecE = new TranslationSpec<>(
                TestTaskitEngine.class) {

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
