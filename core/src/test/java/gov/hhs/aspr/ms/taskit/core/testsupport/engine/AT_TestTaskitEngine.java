package gov.hhs.aspr.ms.taskit.core.testsupport.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngine;
import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngineData;
import gov.hhs.aspr.ms.taskit.core.engine.TaskitError;
import gov.hhs.aspr.ms.taskit.core.testsupport.TestObjectUtil;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestAppChildObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestAppObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestInputChildObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestInputObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestObjectWrapper;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.TestTranslationSpec;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.bad.BadTranslationSpecEmptyMap;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.bad.BadTranslationSpecNullMap;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.complexobject.specs.TestComplexObjectTranslationSpec;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.object.TestObjectTranslator;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.object.specs.TestObjectTranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.ITranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.Translator;
import gov.hhs.aspr.ms.taskit.core.translation.TranslatorContext;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;
import gov.hhs.aspr.ms.util.errors.ContractException;
import gov.hhs.aspr.ms.util.resourcehelper.ResourceHelper;

public class AT_TestTaskitEngine {
    Path basePath = ResourceHelper.getResourceDir(this.getClass());
    Path filePath = ResourceHelper.createDirectory(basePath, "test-output");

    @Test
    @UnitTestMethod(target = TestTaskitEngine.Builder.class, name = "build", args = {})
    public void testBuild() {
        assertEquals(TestTaskitEngineId.TEST_ENGINE_ID, TestTaskitEngine.builder()
                .addTranslationSpec(new TestObjectTranslationSpec()).build().getTaskitEngineId());
    }

    @Test
    @UnitTestMethod(target = TestTaskitEngine.Builder.class, name = "addTranslationSpec", args = {
            ITranslationSpec.class })
    public void testAddTranslationSpec() {
        // see AT_TaskitEngineData.testAddTranslationSpec()
        // code here is strictly for coverage, and coverage alone
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec testComplexObjectTranslationSpec = new TestComplexObjectTranslationSpec();

        TestTaskitEngine.Builder builder = TestTaskitEngine.builder();

        TestTaskitEngine taskitEngine = builder
                .addTranslationSpec(testObjectTranslationSpec)
                .addTranslationSpec(testComplexObjectTranslationSpec).build();

        // show that the translation specs are retrievable by their own app and input
        // classes
        assertEquals(testObjectTranslationSpec,
                taskitEngine.getTranslationSpecForClass(testObjectTranslationSpec.getAppObjectClass()));
        assertEquals(testObjectTranslationSpec,
                taskitEngine.getTranslationSpecForClass(
                        testObjectTranslationSpec.getInputObjectClass()));

        assertEquals(testComplexObjectTranslationSpec,
                taskitEngine.getTranslationSpecForClass(
                        testComplexObjectTranslationSpec.getAppObjectClass()));
        assertEquals(testComplexObjectTranslationSpec,
                taskitEngine.getTranslationSpecForClass(
                        testComplexObjectTranslationSpec.getInputObjectClass()));

        // preconditions
        // translationSpec is null
        ContractException contractException = assertThrows(ContractException.class, () -> {
            builder.addTranslationSpec(null);
        });

        assertEquals(TaskitError.NULL_TRANSLATION_SPEC, contractException.getErrorType());

        // null translationSpecToClassMap
        contractException = assertThrows(ContractException.class, () -> {
            BadTranslationSpecNullMap badTranslationSpecNullMap = new BadTranslationSpecNullMap();
            builder.addTranslationSpec(badTranslationSpecNullMap);
        });

        assertEquals(TaskitError.NULL_TRANSLATION_SPEC_CLASS_MAP,
                contractException.getErrorType());

        // empty translationSpecToClassMap
        contractException = assertThrows(ContractException.class, () -> {
            BadTranslationSpecEmptyMap badTranslationSpecEmptyMap = new BadTranslationSpecEmptyMap();
            builder.addTranslationSpec(badTranslationSpecEmptyMap);
        });

        // if the translation spec has already been added (same, but different
        // instances)
        contractException = assertThrows(ContractException.class, () -> {
            TestObjectTranslationSpec testObjectTranslationSpec1 = new TestObjectTranslationSpec();
            TestObjectTranslationSpec testObjectTranslationSpec2 = new TestObjectTranslationSpec();

            TestTaskitEngine.builder()
                    .addTranslationSpec(testObjectTranslationSpec1)
                    .addTranslationSpec(testObjectTranslationSpec2);
        });

        assertEquals(TaskitError.DUPLICATE_TRANSLATION_SPEC, contractException.getErrorType());

        // if the translation spec has already been added (exact same instance)
        contractException = assertThrows(ContractException.class, () -> {
            TestObjectTranslationSpec testObjectTranslationSpec1 = new TestObjectTranslationSpec();

            TestTaskitEngine.builder()
                    .addTranslationSpec(testObjectTranslationSpec1)
                    .addTranslationSpec(testObjectTranslationSpec1);
        });

        assertEquals(TaskitError.DUPLICATE_TRANSLATION_SPEC, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TestTaskitEngine.Builder.class, name = "addTranslator", args = { Translator.class })
    public void testAddTranslator() {
        // see AT_TaskitEngine.testAddTranslator()
        // code here is strictly for coverage, and coverage alone
        Translator translator = TestObjectTranslator.getTranslator();
        TranslatorContext translatorContext = new TranslatorContext(TestTaskitEngine.builder());
        translator.initialize(translatorContext);

        TaskitEngineData.builder().addTranslator(translator);

        // preconditions
        // null translator
        ContractException contractException = assertThrows(ContractException.class, () -> {
            TaskitEngineData.builder().addTranslator(null);
        });

        assertEquals(TaskitError.NULL_TRANSLATOR, contractException.getErrorType());

    }

    @Test
    @UnitTestMethod(target = TestTaskitEngine.class, name = "builder", args = {})
    public void testBuilder() {
        // nothing to test
    }

    

    @Test
    @UnitTestMethod(target = TestTaskitEngine.class, name = "getTaskitEngineId", args = {})
    public void testGetTaskitEngineId() {
        assertEquals(TestTaskitEngineId.TEST_ENGINE_ID, TestTaskitEngine.builder()
                .addTranslationSpec(new TestObjectTranslationSpec()).build().getTaskitEngineId());
    }

    @Test
    @UnitTestMethod(target = TestTaskitEngine.class, name = "translateObject", args = { Object.class })
    public void testTranslateObject() {
        // see AT_TaskitEngine.testTranslateObject()
        // code here is strictly for coverage, and coverage alone
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec testComplexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        TestTaskitEngine taskitEngine = TestTaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec)
                .addTranslationSpec(testComplexObjectTranslationSpec)
                .build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();
        TestInputObject expectedInputObject = TestObjectUtil.getInputFromApp(expectedAppObject);

        TestInputObject actualInputObject = taskitEngine.translateObject(expectedAppObject);
        assertEquals(expectedInputObject, actualInputObject);

        TestAppObject actualAppObject = taskitEngine.translateObject(expectedInputObject);
        assertEquals(expectedAppObject, actualAppObject);
    }

    @Test
    @UnitTestMethod(target = TestTaskitEngine.class, name = "translateObjectAsClassSafe", args = {
            Object.class,
            Class.class })
    public void testTranslateObjectAsClassSafe() {
        // see AT_TaskitEngine.testTranslateObjectAsClassSafe()
        // code here is strictly for coverage, and coverage alone
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec testComplexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        TestTaskitEngine taskitEngine = TestTaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec)
                .addTranslationSpec(testComplexObjectTranslationSpec)
                .build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();
        TestInputObject expectedInputObject = TestObjectUtil.getInputFromApp(expectedAppObject);

        TestAppChildObject expectedAppChildObject = TestObjectUtil.getChildAppFromApp(expectedAppObject);
        TestInputChildObject expectedInputChildObject = TestObjectUtil
                .getChildInputFromInput(expectedInputObject);

        TestInputObject actualInputChildObject = taskitEngine.translateObjectAsClassSafe(
                expectedAppChildObject,
                TestAppObject.class);
        assertEquals(expectedInputChildObject, TestObjectUtil.getChildInputFromInput(actualInputChildObject));

        TestAppObject actualAppChildObject = taskitEngine.translateObjectAsClassSafe(
                expectedInputChildObject,
                TestInputObject.class);
        assertEquals(expectedAppChildObject, TestObjectUtil.getChildAppFromApp(actualAppChildObject));
    }

    @Test
    @UnitTestMethod(target = TestTaskitEngine.class, name = "translateObjectAsClassUnsafe", args = {
            Object.class, Class.class })
    public void testTranslateObjectAsClassUnsafe() {
        // see AT_TaskitEngine.testTranslateObjectAsClassUnsafe()
        // code here is strictly for coverage, and coverage alone
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec testComplexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        // custom Translation Spec to simulate a Spec that might use a class to "wrap"
        // another class
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

        TestTaskitEngine taskitEngine = TestTaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec)
                .addTranslationSpec(testComplexObjectTranslationSpec)
                .addTranslationSpec(wrapperTranslationSpec)
                .build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();

        TestObjectWrapper expectedWrapper = new TestObjectWrapper();
        expectedWrapper.setWrappedObject(expectedAppObject);

        TestObjectWrapper actualWrapper = taskitEngine.translateObjectAsClassUnsafe(expectedAppObject,
                TestObjectWrapper.class);

        assertEquals(expectedWrapper, actualWrapper);

        Object actualAppObject = taskitEngine.translateObject(actualWrapper);

        assertEquals(expectedAppObject, actualAppObject);
    }

    // TODO: update test
    @Test
    @UnitTestMethod(target = TestTaskitEngine.class, name = "hashCode", args = {})
    public void testHashCode() {
        // see AT_TaskitEngine.testHashCode()
        // code here is strictly for coverage, and coverage alone
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec testComplexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        TestTaskitEngine taskitEngine1 = TestTaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec)
                .addTranslationSpec(testComplexObjectTranslationSpec)
                .buildWithoutInit();

        TestTaskitEngine taskitEngine2 = TestTaskitEngine.builder()
                .addTranslationSpec(testComplexObjectTranslationSpec)
                .build();

        TestTaskitEngine taskitEngine3 = TestTaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec)
                .build();

        TestTaskitEngine taskitEngine4 = TestTaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec)
                .addTranslationSpec(testComplexObjectTranslationSpec)
                .buildWithoutInit();

        // exact same, same hash code
        assertEquals(taskitEngine1.hashCode(), taskitEngine1.hashCode());

        // different translation specs
        assertNotEquals(taskitEngine1.hashCode(), taskitEngine2.hashCode());
        assertNotEquals(taskitEngine1.hashCode(), taskitEngine3.hashCode());
        assertNotEquals(taskitEngine2.hashCode(), taskitEngine3.hashCode());
        assertNotEquals(taskitEngine2.hashCode(), taskitEngine4.hashCode());
        assertNotEquals(taskitEngine3.hashCode(), taskitEngine4.hashCode());

        // same translation specs
        assertEquals(taskitEngine1.hashCode(), taskitEngine4.hashCode());
    }

    // TODO: update test
    @Test
    @UnitTestMethod(target = TestTaskitEngine.class, name = "equals", args = { Object.class })
    public void testEquals() {
        // see AT_TaskitEngine.testEquals()
        // code here is strictly for coverage, and coverage alone
        TaskitEngine taskitEngine1 = TestTaskitEngine.builder()
                .addTranslationSpec(new TestObjectTranslationSpec())
                .addTranslationSpec(new TestComplexObjectTranslationSpec())
                .buildWithoutInit();

        TaskitEngine taskitEngine2 = TestTaskitEngine.builder()
                .addTranslationSpec(new TestComplexObjectTranslationSpec())
                .buildWithoutInit();

        TaskitEngine taskitEngine3 = TestTaskitEngine.builder()
                .addTranslationSpec(new TestObjectTranslationSpec())
                .buildWithoutInit();

        TaskitEngine taskitEngine4 = TestTaskitEngine.builder()
                .addTranslationSpec(new TestObjectTranslationSpec())
                .addTranslationSpec(new TestComplexObjectTranslationSpec())
                .buildWithoutInit();

        TaskitEngine taskitEngine5 = TestTaskitEngine.builder()
                .addTranslationSpec(new TestObjectTranslationSpec())
                .addTranslationSpec(new TestComplexObjectTranslationSpec())
                .build();

        // exact same
        assertEquals(taskitEngine1, taskitEngine1);

        // null test
        assertNotEquals(taskitEngine1, null);

        // not an instance test
        assertNotEquals(taskitEngine1, new Object());

        // different translation specs
        assertNotEquals(taskitEngine1, taskitEngine2);
        assertNotEquals(taskitEngine1, taskitEngine3);
        assertNotEquals(taskitEngine2, taskitEngine3);
        assertNotEquals(taskitEngine2, taskitEngine4);
        assertNotEquals(taskitEngine3, taskitEngine4);

        // same translation specs
        assertEquals(taskitEngine1, taskitEngine4);

        // init vs not init
        assertNotEquals(taskitEngine1, taskitEngine5);

        taskitEngine1.init();
        taskitEngine2.init();
        taskitEngine3.init();
        taskitEngine4.init();

        // init and same translation specs
        assertEquals(taskitEngine1, taskitEngine4);

        // init and different translation specs
        assertNotEquals(taskitEngine1, taskitEngine2);
        assertNotEquals(taskitEngine1, taskitEngine3);
        assertNotEquals(taskitEngine2, taskitEngine3);
        assertNotEquals(taskitEngine2, taskitEngine4);
        assertNotEquals(taskitEngine3, taskitEngine4);
    }
}
