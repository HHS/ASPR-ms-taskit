package gov.hhs.aspr.ms.taskit.core.testsupport.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngine;
import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngineTestHelper;
import gov.hhs.aspr.ms.taskit.core.testsupport.TestObjectUtil;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestAppChildObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestAppObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestInputChildObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestInputObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestObjectWrapper;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.complexobject.specs.TestComplexObjectTranslationSpec;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.object.specs.TestObjectTranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.TranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.Translator;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;
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
    @UnitTestMethod(target = TestTaskitEngine.Builder.class, name = "buildWithoutInit", args = {})
    public void testBuildWithoutInit() {
        TestTaskitEngine testTaskitEngine = TestTaskitEngine.builder()
                .addTranslationSpec(new TestObjectTranslationSpec()).buildWithoutInit();

        assertEquals(TestTaskitEngineId.TEST_ENGINE_ID, testTaskitEngine.getTaskitEngineId());
        assertFalse(testTaskitEngine.getTaskitEngine().isInitialized());
    }

    @Test
    @UnitTestMethod(target = TestTaskitEngine.Builder.class, name = "addTranslationSpec", args = {
            TranslationSpec.class })
    public void testAddTranslationSpec() {
        // see AT_TaskitEngine.testAddTranslationSpec()
        // code here is strictly for coverage, and coverage alone
        TaskitEngineTestHelper.testAddTranslationSpec((c) -> TestTaskitEngine.builder());
    }

    @Test
    @UnitTestMethod(target = TestTaskitEngine.Builder.class, name = "addTranslator", args = { Translator.class })
    public void testAddTranslator() {
        // see AT_TaskitEngine.testAddTranslator()
        // code here is strictly for coverage, and coverage alone
        TaskitEngineTestHelper.testAddTranslator(TestTaskitEngine.builder());
    }

    @Test
    @UnitTestMethod(target = TestTaskitEngine.class, name = "builder", args = {})
    public void testBuilder() {
        // nothing to test
    }

    @Test
    @UnitTestMethod(target = TestTaskitEngine.class, name = "write", args = { Path.class, Object.class })
    public void testWrite() throws IOException {
        String fileName = "testEngineWrite_1-testOutput.json";
        String fileName2 = "testEngineWrite_2-testOutput.json";

        ResourceHelper.createFile(filePath, fileName);
        ResourceHelper.createFile(filePath, fileName2);

        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec complexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        TestTaskitEngine testTaskitEngine = TestTaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec)
                .addTranslationSpec(complexObjectTranslationSpec).build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();
        TestInputObject inputObject = TestObjectUtil.getInputFromApp(expectedAppObject);

        testTaskitEngine.write(filePath.resolve(fileName), inputObject);
        TestAppObject actualAppObject = testTaskitEngine.readAndTranslate(filePath.resolve(fileName),
                TestInputObject.class);
        assertEquals(expectedAppObject, actualAppObject);
    }

    @Test
    @UnitTestMethod(target = TestTaskitEngine.class, name = "translateAndWrite", args = { Path.class,
            Object.class })
    public void testTranslateAndWrite() throws IOException {
        String fileName = "testEngineTranslateAndWrite_1-testOutput.json";
        String fileName2 = "testEngineTranslateAndWrite_2-testOutput.json";

        ResourceHelper.createFile(filePath, fileName);
        ResourceHelper.createFile(filePath, fileName2);

        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec complexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        TestTaskitEngine testTaskitEngine = TestTaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec)
                .addTranslationSpec(complexObjectTranslationSpec).build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();

        testTaskitEngine.translateAndWrite(filePath.resolve(fileName), expectedAppObject);
        TestAppObject actualAppObject = testTaskitEngine.readAndTranslate(filePath.resolve(fileName),
                TestInputObject.class);
        assertEquals(expectedAppObject, actualAppObject);

        testTaskitEngine.translateAndWrite(filePath.resolve(fileName2),
                TestObjectUtil.getChildAppFromApp(expectedAppObject), TestAppObject.class);
        TestAppObject actualAppChildObject = testTaskitEngine.readAndTranslate(filePath.resolve(fileName2),
                TestInputObject.class);
        assertEquals(expectedAppObject, actualAppChildObject);
    }

    @Test
    @UnitTestMethod(target = TestTaskitEngine.class, name = "translateAndWrite", args = { Path.class, Object.class,
            Class.class })
    public void testTranslateAndWrite_Class() throws IOException {
        String fileName = "testEngineTranslateAndWrite_class_1-testOutput.json";
        String fileName2 = "testEngineTranslateAndWrite_class_2-testOutput.json";

        ResourceHelper.createFile(filePath, fileName);
        ResourceHelper.createFile(filePath, fileName2);

        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec complexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        TestTaskitEngine testTaskitEngine = TestTaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec)
                .addTranslationSpec(complexObjectTranslationSpec).build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();

        testTaskitEngine.translateAndWrite(filePath.resolve(fileName2),
                TestObjectUtil.getChildAppFromApp(expectedAppObject), TestAppObject.class);
        TestAppObject actualAppChildObject = testTaskitEngine.readAndTranslate(filePath.resolve(fileName2),
                TestInputObject.class);
        assertEquals(expectedAppObject, actualAppChildObject);
    }

    @Test
    @UnitTestMethod(target = TestTaskitEngine.class, name = "read", args = { Path.class, Class.class })
    public void testRead() throws IOException {
        String fileName = "testEngineRead_1-testOutput.json";
        String fileName2 = "testEngineRead_2-testOutput.json";

        ResourceHelper.createFile(filePath, fileName);
        ResourceHelper.createFile(filePath, fileName2);

        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec complexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        TestTaskitEngine testTaskitEngine = TestTaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec)
                .addTranslationSpec(complexObjectTranslationSpec).build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();
        TestInputObject expectedInputObject = TestObjectUtil.getInputFromApp(expectedAppObject);

        testTaskitEngine.translateAndWrite(filePath.resolve(fileName), expectedAppObject);
        TestInputObject actualInputObject = testTaskitEngine.read(filePath.resolve(fileName),
                TestInputObject.class);
        assertEquals(expectedInputObject, actualInputObject);
    }

    @Test
    @UnitTestMethod(target = TestTaskitEngine.class, name = "readAndTranslate", args = { Path.class, Class.class })
    public void testReadAndTranslate() throws IOException {
        String fileName = "testEngineReadAndTranslate_1-testOutput.json";
        String fileName2 = "testEngineReadAndTranslate_2-testOutput.json";

        ResourceHelper.createFile(filePath, fileName);
        ResourceHelper.createFile(filePath, fileName2);

        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec complexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        TestTaskitEngine testTaskitEngine = TestTaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec)
                .addTranslationSpec(complexObjectTranslationSpec).build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();

        testTaskitEngine.translateAndWrite(filePath.resolve(fileName), expectedAppObject);
        TestAppObject actualAppObject = testTaskitEngine.readAndTranslate(filePath.resolve(fileName),
                TestInputObject.class);
        assertEquals(expectedAppObject, actualAppObject);
    }

    @Test
    @UnitTestMethod(target = TestTaskitEngine.class, name = "getTaskitEngine", args = {})
    public void testGetTaskitEngine() {
        TaskitEngine taskitEngine = TaskitEngine.builder().addTranslationSpec(new TestObjectTranslationSpec())
                .setTaskitEngineId(TestTaskitEngineId.TEST_ENGINE_ID).build();

        TestTaskitEngine testTaskitEngine = TestTaskitEngine.builder()
                .addTranslationSpec(new TestObjectTranslationSpec()).build();

        taskitEngine.init(testTaskitEngine);

        assertEquals(taskitEngine, testTaskitEngine.getTaskitEngine());
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
        TranslationSpec<TestObjectWrapper, Object> wrapperTranslationSpec = new TranslationSpec<TestObjectWrapper, Object>() {

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
        TestTaskitEngine taskitEngine1 = TestTaskitEngine.builder()
                .addTranslationSpec(new TestObjectTranslationSpec())
                .addTranslationSpec(new TestComplexObjectTranslationSpec())
                .build();

        TestTaskitEngine taskitEngine2 = TestTaskitEngine.builder()
                .addTranslationSpec(new TestComplexObjectTranslationSpec())
                .build();

        TestTaskitEngine taskitEngine3 = TestTaskitEngine.builder()
                .addTranslationSpec(new TestObjectTranslationSpec())
                .build();

        TestTaskitEngine taskitEngine4 = TestTaskitEngine.builder()
                .addTranslationSpec(new TestObjectTranslationSpec())
                .addTranslationSpec(new TestComplexObjectTranslationSpec())
                .build();

        TestObjectTranslationSpec testObjectTranslationSpec2 = new TestObjectTranslationSpec();
        TestObjectTranslationSpec testObjectTranslationSpec3 = new TestObjectTranslationSpec();

        TestTaskitEngine taskitEngine5 = TestTaskitEngine.builder()
                .addTranslationSpec(new TestObjectTranslationSpec())
                .buildWithoutInit();

        TestTaskitEngine taskitEngine6 = TestTaskitEngine.builder()
                .addTranslationSpec(new TestObjectTranslationSpec())
                .build();

        // exact same
        assertEquals(taskitEngine1, taskitEngine1);

        assertNotEquals(taskitEngine1, null);

        assertNotEquals(taskitEngine1, new Object());

        // different translation specs
        assertNotEquals(taskitEngine1, taskitEngine2);
        assertNotEquals(taskitEngine1, taskitEngine3);
        assertNotEquals(taskitEngine2, taskitEngine3);
        assertNotEquals(taskitEngine2, taskitEngine4);
        assertNotEquals(taskitEngine3, taskitEngine4);

        testObjectTranslationSpec2.init(taskitEngine1);
        testObjectTranslationSpec3.init(taskitEngine4);

        // init vs not init
        assertNotEquals(taskitEngine5, taskitEngine6);

        
        // same translation specs
        assertEquals(taskitEngine1, taskitEngine4);
    }
}
