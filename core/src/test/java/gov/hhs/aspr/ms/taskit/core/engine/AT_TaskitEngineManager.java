package gov.hhs.aspr.ms.taskit.core.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.taskit.core.testsupport.TestObjectUtil;
import gov.hhs.aspr.ms.taskit.core.testsupport.engine.TestTaskitEngine;
import gov.hhs.aspr.ms.taskit.core.testsupport.engine.TestTaskitEngineId;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestAppChildObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestAppObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestInputObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.complexobject.TestComplexObjectTranslator;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.object.TestObjectTranslator;
import gov.hhs.aspr.ms.util.annotations.UnitTestForCoverage;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;
import gov.hhs.aspr.ms.util.errors.ContractException;
import gov.hhs.aspr.ms.util.resourcehelper.ResourceError;
import gov.hhs.aspr.ms.util.resourcehelper.ResourceHelper;

public class AT_TaskitEngineManager {

    Path basePath = ResourceHelper.getResourceDir(this.getClass());
    Path filePath = ResourceHelper.createDirectory(basePath, "test-output");

    @Test
    @UnitTestMethod(target = TaskitEngineManager.class, name = "read", args = { Path.class, Class.class,
            TaskitEngineId.class })
    public void testRead() {
        String fileName = "readInput-testOutput.json";

        ResourceHelper.createFile(filePath, fileName);

        TestTaskitEngine testTaskitEngine = TestTaskitEngine.builder()
                .addTranslator(TestObjectTranslator.getTranslator())
                .addTranslator(TestComplexObjectTranslator.getTranslator()).build();

        TaskitEngineManager taskitEngineManager = TaskitEngineManager.builder()
                .addTaskitEngine(testTaskitEngine).build();

        TestAppObject appObject = TestObjectUtil.generateTestAppObject();
        TestInputObject expectedInputObject = TestObjectUtil.getInputFromApp(appObject);

        taskitEngineManager.translateAndWrite(filePath.resolve(fileName), appObject,
                TestTaskitEngineId.TEST_ENGINE_ID);

        TestInputObject actualInputObject = taskitEngineManager.read(filePath.resolve(fileName),
                TestInputObject.class, TestTaskitEngineId.TEST_ENGINE_ID);

        assertEquals(expectedInputObject, actualInputObject);

        // preconditions
        // null path
        ContractException contractException = assertThrows(ContractException.class, () -> {
            taskitEngineManager.read(null, TestInputObject.class, TestTaskitEngineId.TEST_ENGINE_ID);
        });

        assertEquals(TaskitError.NULL_PATH, contractException.getErrorType());

        // filepath is a directory
        contractException = assertThrows(ContractException.class, () -> {
            taskitEngineManager.read(filePath, TestInputObject.class, TestTaskitEngineId.TEST_ENGINE_ID);
        });

        assertEquals(ResourceError.FILE_PATH_IS_DIRECTORY, contractException.getErrorType());

        // null classRef
        contractException = assertThrows(ContractException.class, () -> {
            taskitEngineManager.read(filePath.resolve(fileName), null, TestTaskitEngineId.TEST_ENGINE_ID);
        });

        assertEquals(TaskitError.NULL_CLASS_REF, contractException.getErrorType());

        // null taskit engine id
        contractException = assertThrows(ContractException.class, () -> {
            taskitEngineManager.read(filePath.resolve(fileName), TestInputObject.class, null);
        });

        assertEquals(TaskitError.NULL_TASKIT_ENGINE_ID, contractException.getErrorType());

        // null taskit engine
        contractException = assertThrows(ContractException.class, () -> {
            taskitEngineManager.read(filePath.resolve(fileName), TestInputObject.class, new TaskitEngineId() {

            });
        });

        assertEquals(TaskitError.NULL_TASKIT_ENGINE, contractException.getErrorType());

        // issue reading file
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            taskitEngineManager.read(filePath.resolve("badPath"), TestInputObject.class,
                    TestTaskitEngineId.TEST_ENGINE_ID);
        });

        assertTrue(runtimeException.getCause() instanceof IOException);
    }

    @Test
    @UnitTestMethod(target = TaskitEngineManager.class, name = "readAndTranslate", args = { Path.class, Class.class,
            TaskitEngineId.class })
    public void testReadAndTranslate() {
        String fileName = "readInput-testOutput.json";

        ResourceHelper.createFile(filePath, fileName);

        TestTaskitEngine testTaskitEngine = TestTaskitEngine.builder()
                .addTranslator(TestObjectTranslator.getTranslator())
                .addTranslator(TestComplexObjectTranslator.getTranslator()).build();

        TaskitEngineManager taskitEngineManager = TaskitEngineManager.builder()
                .addTaskitEngine(testTaskitEngine).build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();

        taskitEngineManager.translateAndWrite(filePath.resolve(fileName), expectedAppObject,
                TestTaskitEngineId.TEST_ENGINE_ID);

        TestAppObject actualAppObject = taskitEngineManager.readAndTranslate(filePath.resolve(fileName),
                TestInputObject.class, TestTaskitEngineId.TEST_ENGINE_ID);

        assertEquals(expectedAppObject, actualAppObject);

        // preconditions
        // null path
        ContractException contractException = assertThrows(ContractException.class, () -> {
            taskitEngineManager.readAndTranslate(null, TestInputObject.class, TestTaskitEngineId.TEST_ENGINE_ID);
        });

        assertEquals(TaskitError.NULL_PATH, contractException.getErrorType());

        // filepath is a directory
        contractException = assertThrows(ContractException.class, () -> {
            taskitEngineManager.readAndTranslate(filePath, TestInputObject.class, TestTaskitEngineId.TEST_ENGINE_ID);
        });

        assertEquals(ResourceError.FILE_PATH_IS_DIRECTORY, contractException.getErrorType());

        // null classRef
        contractException = assertThrows(ContractException.class, () -> {
            taskitEngineManager.readAndTranslate(filePath.resolve(fileName), null, TestTaskitEngineId.TEST_ENGINE_ID);
        });

        assertEquals(TaskitError.NULL_CLASS_REF, contractException.getErrorType());

        // null taskit engine id
        contractException = assertThrows(ContractException.class, () -> {
            taskitEngineManager.readAndTranslate(filePath.resolve(fileName), TestInputObject.class, null);
        });

        assertEquals(TaskitError.NULL_TASKIT_ENGINE_ID, contractException.getErrorType());

        // null taskit engine
        contractException = assertThrows(ContractException.class, () -> {
            taskitEngineManager.readAndTranslate(filePath.resolve(fileName), TestInputObject.class, new TaskitEngineId() {

            });
        });

        assertEquals(TaskitError.NULL_TASKIT_ENGINE, contractException.getErrorType());

        // issue reading file
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            taskitEngineManager.readAndTranslate(filePath.resolve("badPath2"), TestInputObject.class,
                    TestTaskitEngineId.TEST_ENGINE_ID);
        });

        assertTrue(runtimeException.getCause() instanceof IOException);
    }

    @Test
    @UnitTestMethod(target = TaskitEngineManager.class, name = "write", args = { Path.class, Object.class,
            TaskitEngineId.class })
    public void testWrite() {
        String fileName = "write-testOutput.json";

        ResourceHelper.createFile(filePath, fileName);

        TestTaskitEngine testTaskitEngine = TestTaskitEngine.builder()
                .addTranslator(TestObjectTranslator.getTranslator())
                .addTranslator(TestComplexObjectTranslator.getTranslator())
                .build();

        TaskitEngineManager taskitEngineManager = TaskitEngineManager.builder()
                .addTaskitEngine(testTaskitEngine).build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();
        TestInputObject inputObject = TestObjectUtil.getInputFromApp(expectedAppObject);

        taskitEngineManager.write(filePath.resolve(fileName), inputObject, TestTaskitEngineId.TEST_ENGINE_ID);

        // preconditions
        // TaskitCoreError#NULL_PATH is tested by testWrite_Base()
        // ResourceError#FILE_PATH_IS_DIRECTORY is tested by testWrite_Base()
        // TaskitCoreError#NULL_OBJECT_FOR_TRANSLATION is tested by testWrite_Base()
        // TaskitCoreError#NULL_TASKIT_ENGINE_ID is tested by testWrite_Base()
        // TaskitCoreError#NULL_TASKIT_ENGINE is tested by testWrite_Base()
        // RuntimeException is tested by testWrite_Base()
    }

    @Test
    @UnitTestMethod(target = TaskitEngineManager.class, name = "translateAndWrite", args = { Path.class, Object.class,
            TaskitEngineId.class })
    public void testTranslateAndWrite() {
        String fileName = "translateAndWrite-testOutput.json";

        ResourceHelper.createFile(filePath, fileName);

        TestTaskitEngine testTaskitEngine = TestTaskitEngine.builder()
                .addTranslator(TestObjectTranslator.getTranslator())
                .addTranslator(TestComplexObjectTranslator.getTranslator())
                .build();

        TaskitEngineManager taskitEngineManager = TaskitEngineManager.builder()
                .addTaskitEngine(testTaskitEngine).build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();

        taskitEngineManager.translateAndWrite(filePath.resolve(fileName), expectedAppObject,
                TestTaskitEngineId.TEST_ENGINE_ID);

        // preconditions
        // TaskitCoreError#NULL_PATH is tested by testWrite_Base()
        // ResourceError#FILE_PATH_IS_DIRECTORY is tested by testWrite_Base()
        // TaskitCoreError#NULL_OBJECT_FOR_TRANSLATION is tested by testWrite_Base()
        // TaskitCoreError#NULL_TASKIT_ENGINE_ID is tested by testWrite_Base()
        // TaskitCoreError#NULL_TASKIT_ENGINE is tested by testWrite_Base()
        // RuntimeException is tested by testWrite_Base()
    }

    @Test
    @UnitTestMethod(target = TaskitEngineManager.class, name = "translateAndWrite", args = { Path.class, Object.class,
            Class.class, TaskitEngineId.class })
    public void testTranslateAndWrite_Parent() {
        String fileName = "translateAndWrite-testOutput.json";

        ResourceHelper.createFile(filePath, fileName);

        TestTaskitEngine testTaskitEngine = TestTaskitEngine.builder()
                .addTranslator(TestObjectTranslator.getTranslator())
                .addTranslator(TestComplexObjectTranslator.getTranslator())
                .build();

        TaskitEngineManager taskitEngineManager = TaskitEngineManager.builder()
                .addTaskitEngine(testTaskitEngine).build();

        TestAppObject appObject = TestObjectUtil.generateTestAppObject();
        TestAppChildObject expectedAppObject = TestObjectUtil.getChildAppFromApp(appObject);

        taskitEngineManager.translateAndWrite(filePath.resolve(fileName), expectedAppObject, TestAppObject.class,
                TestTaskitEngineId.TEST_ENGINE_ID);

        // preconditions
        // outputClass is null
        ContractException contractException = assertThrows(ContractException.class, () -> {
            taskitEngineManager.translateAndWrite(filePath.resolve(fileName), expectedAppObject, null,
                    TestTaskitEngineId.TEST_ENGINE_ID);
        });

        assertEquals(TaskitError.NULL_CLASS_REF, contractException.getErrorType());

        // TaskitCoreError#NULL_PATH is tested by testWrite_Base()
        // ResourceError#FILE_PATH_IS_DIRECTORY is tested by testWrite_Base()
        // TaskitCoreError#NULL_OBJECT_FOR_TRANSLATION is tested by testWrite_Base()
        // TaskitCoreError#NULL_TASKIT_ENGINE_ID is tested by testWrite_Base()
        // TaskitCoreError#NULL_TASKIT_ENGINE is tested by testWrite_Base()
        // RuntimeException is tested by testWrite_Base()
    }

    @Test
    @UnitTestForCoverage
    public void testWrite_Base() {
        String fileName = "write_Base-testOutput.json";

        ResourceHelper.createFile(filePath, fileName);

        TestTaskitEngine testTaskitEngine = TestTaskitEngine.builder()
                .addTranslator(TestObjectTranslator.getTranslator())
                .addTranslator(TestComplexObjectTranslator.getTranslator())
                .build();

        TaskitEngineManager taskitEngineManager = TaskitEngineManager.builder()
                .addTaskitEngine(testTaskitEngine).build();

        TestAppObject appObject = TestObjectUtil.generateTestAppObject();

        // preconditions
        // path is null
        ContractException contractException = assertThrows(ContractException.class, () -> {
            taskitEngineManager.write(null, appObject, Optional.empty(),
                    TestTaskitEngineId.TEST_ENGINE_ID, false);
        });

        assertEquals(TaskitError.NULL_PATH, contractException.getErrorType());

        // path points at directory
        contractException = assertThrows(ContractException.class, () -> {
            taskitEngineManager.write(filePath, appObject, Optional.empty(),
                    TestTaskitEngineId.TEST_ENGINE_ID, false);
        });

        assertEquals(ResourceError.FILE_PATH_IS_DIRECTORY, contractException.getErrorType());

        // object is null
        contractException = assertThrows(ContractException.class, () -> {
            taskitEngineManager.write(filePath.resolve(fileName), null, Optional.empty(),
                    TestTaskitEngineId.TEST_ENGINE_ID, false);
        });

        assertEquals(TaskitError.NULL_OBJECT_FOR_TRANSLATION, contractException.getErrorType());

        // taskit engine id is null
        contractException = assertThrows(ContractException.class, () -> {
            taskitEngineManager.write(filePath.resolve(fileName), appObject, Optional.empty(),
                    null, false);
        });

        assertEquals(TaskitError.NULL_TASKIT_ENGINE_ID, contractException.getErrorType());

        // taskit engine is null
        contractException = assertThrows(ContractException.class, () -> {
            taskitEngineManager.write(filePath.resolve(fileName), appObject, Optional.empty(),
                    new TaskitEngineId() {
                    }, false);
        });

        assertEquals(TaskitError.NULL_TASKIT_ENGINE, contractException.getErrorType());

        // writing the file encounters a IOException
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            File file = filePath.resolve(fileName).toFile();

            file.setReadOnly();
            taskitEngineManager.write(filePath.resolve(fileName), appObject, Optional.empty(),
                    TestTaskitEngineId.TEST_ENGINE_ID, true);
        });

        assertTrue(IOException.class.isAssignableFrom(runtimeException.getCause().getClass()));
        filePath.resolve(fileName).toFile().setWritable(true);
    }

    @Test
    @UnitTestMethod(target = TaskitEngineManager.class, name = "builder", args = {})
    public void testBuilder() {
        // nothing to test
    }

    @Test
    @UnitTestMethod(target = TaskitEngineManager.Builder.class, name = "build", args = {})
    public void testBuild() {
        // preconditions
        // no taskit engines were added
        ContractException contractException = assertThrows(ContractException.class, () -> {
            TaskitEngineManager.builder().build();
        });

        assertEquals(TaskitError.NO_TASKIT_ENGINES, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TaskitEngineManager.Builder.class, name = "addTaskitEngine", args = {
            TaskitEngine.class })
    public void testAddTaskitEngine() {
        TestTaskitEngine taskitEngine = TestTaskitEngine.builder()
                .addTranslator(TestObjectTranslator.getTranslator())
                .addTranslator(TestComplexObjectTranslator.getTranslator())
                .build();

        TaskitEngineManager.builder().addTaskitEngine(taskitEngine).build();

        // preconditions
        // taskit engine is null
        ContractException contractException = assertThrows(ContractException.class, () -> {
            TaskitEngineManager.builder().addTaskitEngine(null);
        });

        assertEquals(TaskitError.NULL_TASKIT_ENGINE, contractException.getErrorType());

        // taskit engine is not initialized
        contractException = assertThrows(ContractException.class, () -> {
            TestTaskitEngine taskitEngine2 = TestTaskitEngine.builder()
                    .addTranslator(TestObjectTranslator.getTranslator())
                    .addTranslator(TestComplexObjectTranslator.getTranslator())
                    .buildWithoutInit();

            TaskitEngineManager.builder()
                    .addTaskitEngine(taskitEngine2);
        });

        assertEquals(TaskitError.UNINITIALIZED_TASKIT_ENGINE, contractException.getErrorType());
    }
}
