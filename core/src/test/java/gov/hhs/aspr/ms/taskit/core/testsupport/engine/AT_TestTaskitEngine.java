package gov.hhs.aspr.ms.taskit.core.testsupport.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngine;
import gov.hhs.aspr.ms.taskit.core.testsupport.TestObjectUtil;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestAppObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestInputObject;
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
        TestTaskitEngine testTaskitEngine = TestTaskitEngine.builder().addTranslationSpec(new TestObjectTranslationSpec()).buildWithoutInit();

        assertEquals(TestTaskitEngineId.TEST_ENGINE_ID, testTaskitEngine.getTaskitEngineId());
        assertFalse(testTaskitEngine.getTaskitEngine().isInitialized());
    }

    @Test
    @UnitTestMethod(target = TestTaskitEngine.Builder.class, name = "addTranslationSpec", args = {
            TranslationSpec.class })
    public void testAddTranslationSpec() {
        // nothing to test, see AT_TaskitEngine.testAddTranslationSpec()
    }

    @Test
    @UnitTestMethod(target = TestTaskitEngine.Builder.class, name = "addTranslator", args = { Translator.class })
    public void testAddTranslator() {
        // Nothing to test, see AT_TaskitEngine.testAddTranslator()
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
                .addTranslationSpec(testObjectTranslationSpec).addTranslationSpec(complexObjectTranslationSpec).build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();
        TestInputObject inputObject = TestObjectUtil.getInputFromApp(expectedAppObject);

        testTaskitEngine.write(filePath.resolve(fileName), inputObject);
        TestAppObject actualAppObject = testTaskitEngine.readAndTranslate(filePath.resolve(fileName),
                TestInputObject.class);
        assertEquals(expectedAppObject, actualAppObject);
    }

    @Test
    @UnitTestMethod(target = TestTaskitEngine.class, name = "translateAndWrite", args = { Path.class, Object.class })
    public void testTranslateAndWrite() throws IOException {
        String fileName = "testEngineTranslateAndWrite_1-testOutput.json";
        String fileName2 = "testEngineTranslateAndWrite_2-testOutput.json";

        ResourceHelper.createFile(filePath, fileName);
        ResourceHelper.createFile(filePath, fileName2);

        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec complexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        TestTaskitEngine testTaskitEngine = TestTaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec).addTranslationSpec(complexObjectTranslationSpec).build();

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
                .addTranslationSpec(testObjectTranslationSpec).addTranslationSpec(complexObjectTranslationSpec).build();

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
                .addTranslationSpec(testObjectTranslationSpec).addTranslationSpec(complexObjectTranslationSpec).build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();
        TestInputObject expectedInputObject = TestObjectUtil.getInputFromApp(expectedAppObject);

        testTaskitEngine.translateAndWrite(filePath.resolve(fileName), expectedAppObject);
        TestInputObject actualInputObject = testTaskitEngine.read(filePath.resolve(fileName), TestInputObject.class);
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
                .addTranslationSpec(testObjectTranslationSpec).addTranslationSpec(complexObjectTranslationSpec).build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();

        testTaskitEngine.translateAndWrite(filePath.resolve(fileName), expectedAppObject);
        TestAppObject actualAppObject = testTaskitEngine.readAndTranslate(filePath.resolve(fileName),
                TestInputObject.class);
        assertEquals(expectedAppObject, actualAppObject);
    }

    @Test
    @UnitTestMethod(target = TestTaskitEngine.Builder.class, name = "getTaskitEngine", args = {})
    public void testGetTaskitEngine() {
        TaskitEngine taskitEngine = TaskitEngine.builder().addTranslationSpec(new TestObjectTranslationSpec())
                .setTaskitEngineId(TestTaskitEngineId.TEST_ENGINE_ID).build();

        TestTaskitEngine testTaskitEngine = TestTaskitEngine.builder()
                .addTranslationSpec(new TestObjectTranslationSpec()).build();

        taskitEngine.init(testTaskitEngine);

        assertEquals(taskitEngine, testTaskitEngine.getTaskitEngine());
    }

    @Test
    @UnitTestMethod(target = TestTaskitEngine.Builder.class, name = "getTaskitEngineId", args = {})
    public void testGetTaskitEngineId() {
        assertEquals(TestTaskitEngineId.TEST_ENGINE_ID, TestTaskitEngine.builder()
                .addTranslationSpec(new TestObjectTranslationSpec()).build().getTaskitEngineId());
    }

    @Test
    @UnitTestMethod(target = TestTaskitEngine.Builder.class, name = "translateObject", args = { Object.class })
    public void testTranslateObject() {
        // Nothing to test, see AT_TaskitEngine.testTranslateObject()
    }

    @Test
    @UnitTestMethod(target = TestTaskitEngine.Builder.class, name = "translateObjectAsClassSafe", args = { Object.class,
            Class.class })
    public void testTranslateObjectAsClassSafe() {
        // Nothing to test, see AT_TaskitEngine.testTranslateObjectAsClassSafe()
    }

    @Test
    @UnitTestMethod(target = TestTaskitEngine.Builder.class, name = "translateObjectAsClassUnsafe", args = {
            Object.class, Class.class })
    public void testTranslateObjectAsClassUnsafe() {
        // Nothing to test, see AT_TaskitEngine.testTranslateObjectAsClassUnsafe()
    }

    @Test
    @UnitTestMethod(target = TestTaskitEngine.class, name = "hashCode", args = {})
    public void testHashCode() {
        // Nothing to test, see AT_TaskitEngine.testHashCode()
    }

    @Test
    @UnitTestMethod(target = TestTaskitEngine.class, name = "equals", args = { Object.class })
    public void testEquals() {
        // Nothing to test, see AT_TaskitEngine.testEquals()
    }
}
