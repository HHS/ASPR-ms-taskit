package gov.hhs.aspr.ms.taskit.core.engine;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngineManager;
import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngine;
import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngineType;
import gov.hhs.aspr.ms.taskit.core.engine.TaskitCoreError;
import gov.hhs.aspr.ms.taskit.core.testsupport.TestObjectUtil;
import gov.hhs.aspr.ms.taskit.core.testsupport.engine.TestTaskitEngine;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestAppChildObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestAppObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestComplexAppObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestComplexInputObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestInputObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.TestComplexObjectTranslator;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.TestObjectTranslator;
import gov.hhs.aspr.ms.util.annotations.UnitTestForCoverage;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;
import gov.hhs.aspr.ms.util.errors.ContractException;
import gov.hhs.aspr.ms.util.resourcehelper.ResourceHelper;

public class AT_TaskitController {

    Path basePath = ResourceHelper.getResourceDir(this.getClass());
    Path filePath = ResourceHelper.createDirectory(basePath, "test-output");

    @Test
    @UnitTestForCoverage
    public void testValidateTaskitEngine() {
        TaskitEngineManager translationController = TaskitEngineManager.builder()
                .addTaskitEngine(TestTaskitEngine.builder().build())
                .buildWithoutInitAndChecks();

        // preconditions
        ContractException contractException = assertThrows(ContractException.class, () -> {
            translationController.validateTaskitEngines();
        });

        assertEquals(TaskitCoreError.NO_TASKIT_ENGINES, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            for (TaskitEngine taskitEngine : translationController.data.taskitEngines) {
                translationController.taskitEngineIdToEngineMap
                        .put(taskitEngine.getTaskitEngineId(), null);
            }
            translationController.validateTaskitEngines();
        });

        assertEquals(TaskitCoreError.NULL_TASKIT_ENGINE, contractException.getErrorType());

        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            TestTaskitEngine taskitEngine2 = TestTaskitEngine.builder()
                    .buildWithoutSpecInit();

            translationController.taskitEngineIdToEngineMap.put(taskitEngine2.getTaskitEngineId(),
                    taskitEngine2);

            translationController.validateTaskitEngines();
        });

        assertEquals("TaskitEngine has been built but has not been initialized.",
                runtimeException.getMessage());

        runtimeException = assertThrows(RuntimeException.class, () -> {
            for (TaskitEngine taskitEngine : translationController.data.taskitEngines) {
                translationController.taskitEngineIdToEngineMap.put(
                        taskitEngine.getTaskitEngineId(),
                        taskitEngine);
                translationController.taskitEngineClassToIdMap.put(taskitEngine.getClass(),
                        null);
            }
            translationController.validateTaskitEngines();
        });

        assertEquals(
                "Not all Taskit Engines have an associated Class -> Type -> Engine Mapping. Something went very wrong.",
                runtimeException.getMessage());

        runtimeException = assertThrows(RuntimeException.class, () -> {
            for (TaskitEngine taskitEngine : translationController.data.taskitEngines) {
                translationController.taskitEngineIdToEngineMap.put(
                        taskitEngine.getTaskitEngineId(),
                        taskitEngine);
            }
            translationController.validateTaskitEngines();
        });

        assertEquals(
                "Not all Taskit Engines have an associated Class -> Type -> Engine Mapping. Something went very wrong.",
                runtimeException.getMessage());
    }

    @Test
    @UnitTestForCoverage
    /*
     * purpose of this test is to show that if there isn't a valid TaskitEngine
     * class -> Taskit Engine Type -> Taskit Engine mapping, an exception
     * is thrown
     */
    public void testValidateTaskitEngines() {
        TaskitEngineManager translationController = TaskitEngineManager.builder()
                .buildWithoutInitAndChecks();

        // preconditions
        ContractException contractException = assertThrows(ContractException.class, () -> {
            translationController.validateTaskitEngines();
        });

        assertEquals(TaskitCoreError.NO_TASKIT_ENGINES, contractException.getErrorType());

        // class to type map not populated
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            TaskitEngineManager translationController2 = TaskitEngineManager.builder()
                    .addTaskitEngine(TestTaskitEngine.builder().build())
                    .buildWithoutInitAndChecks();
            for (TaskitEngine taskitEngine : translationController2.data.taskitEngines) {
                translationController2.taskitEngineIdToEngineMap.put(
                        taskitEngine.getTaskitEngineId(),
                        taskitEngine);
            }
            translationController2.validateTaskitEngines();
        });

        assertEquals(
                "Not all Taskit Engines have an associated Class -> Type -> Engine Mapping. Something went very wrong.",
                runtimeException.getMessage());

        // class to type map not fully mapped
        runtimeException = assertThrows(RuntimeException.class, () -> {
            TaskitEngineManager translationController2 = TaskitEngineManager.builder()
                    .addTaskitEngine(TestTaskitEngine.builder().build())
                    .buildWithoutInitAndChecks();
            for (TaskitEngine taskitEngine : translationController2.data.taskitEngines) {
                translationController2.taskitEngineIdToEngineMap.put(
                        taskitEngine.getTaskitEngineId(),
                        taskitEngine);
                translationController2.taskitEngineClassToIdMap.put(taskitEngine.getClass(),
                        null);
            }
            translationController2.validateTaskitEngines();
        });

        assertEquals(
                "Not all Taskit Engines have an associated Class -> Type -> Engine Mapping. Something went very wrong.",
                runtimeException.getMessage());

    }

    @Test
    @UnitTestMethod(target = TaskitEngineManager.class, name = "readInput", args = {})
    public void testReadInput() {
        String fileName = "readInput-testOutput.json";

        ResourceHelper.createFile(filePath, fileName);

        TestTaskitEngine testTaskitEngine = TestTaskitEngine.builder()
                .addTranslator(TestObjectTranslator.getTranslator())
                .addTranslator(TestComplexObjectTranslator.getTranslator()).build();

        TaskitEngineManager translationController = TaskitEngineManager.builder()
                .addInputFilePath(filePath.resolve(fileName), TestInputObject.class,
                        TaskitEngineType.CUSTOM)
                .addTaskitEngine(testTaskitEngine).build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();

        translationController.writeOutput(expectedAppObject, filePath.resolve(fileName),
                TaskitEngineType.CUSTOM);

        translationController.readInput();

        assertEquals(1, translationController.getNumObjects());

        TestAppObject actualTestAppObject = translationController.getFirstObject(TestAppObject.class);

        assertEquals(expectedAppObject, actualTestAppObject);

        // preconditions

        // invalid file path
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            translationController.readInput(filePath.resolve("badPath"), TestInputObject.class,
                    TestTaskitEngine.builder().build());
        });

        assertTrue(runtimeException.getCause() instanceof IOException);
    }

    @Test
    @UnitTestForCoverage
    public void testWriteOutput_Engine() {
        String fileName = "badFilePath-testOutput.json";

        ResourceHelper.createFile(filePath, fileName);
        TestTaskitEngine engine = TestTaskitEngine.builder()
                .addTranslator(TestObjectTranslator.getTranslator())
                .addTranslator(TestComplexObjectTranslator.getTranslator()).build();
        TaskitEngineManager translationController = TaskitEngineManager.builder()
                .addTaskitEngine(engine)
                .build();

        // preconditions

        // if the filePath is invalid
        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            translationController.writeOutput(filePath, TestObjectUtil.generateTestAppObject(),
                    Optional.empty(),
                    engine);
        });

        assertTrue(runtimeException.getCause() instanceof IOException);
    }

    @Test
    @UnitTestMethod(target = TaskitEngineManager.class, name = "writeOutput", args = { Object.class,
            Path.class, TaskitEngineType.class })
    public void testWriteOutput() {
        String fileName = "writeOutput-testOutput.json";

        ResourceHelper.createFile(filePath, fileName);

        TestTaskitEngine testTaskitEngine = TestTaskitEngine.builder()
                .addTranslator(TestObjectTranslator.getTranslator())
                .addTranslator(TestComplexObjectTranslator.getTranslator())
                .addParentChildClassRelationship(TestAppChildObject.class, TestAppObject.class)
                .build();

        TaskitEngineManager translationController = TaskitEngineManager.builder()
                .addTaskitEngine(testTaskitEngine).build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();

        translationController.writeOutput(expectedAppObject, filePath.resolve(fileName),
                TaskitEngineType.CUSTOM);

        TestAppChildObject testAppChildObject = TestObjectUtil.getChildAppFromApp(expectedAppObject);

        translationController.writeOutput(testAppChildObject, filePath.resolve(fileName),
                TaskitEngineType.CUSTOM);
        // preconditions
        // CoreTranslationError.NULL_OBJECT_FOR_TRANSLATION is tested by
        // testWriteOutput_Base()
        // CoreTranslationError#NULL_PATH is tested by testWriteOutput_Base()
        // CoreTranslationError#INVALID_OUTPUT_PATH is tested by testWriteOutput_Base()
        // CoreTranslationError#NULL_TRANSLATION_ENGINE is tested by
        // testWriteOutput_Base()
    }

    @Test
    @UnitTestMethod(target = TaskitEngineManager.class, name = "writeOutput", args = { Object.class, Class.class,
            Path.class, TaskitEngineType.class })
    public void testWriteOutput_ParentClass() {
        String fileName = "writeOutput_ParentClass-testOutput.json";

        ResourceHelper.createFile(filePath, fileName);

        TestTaskitEngine testTaskitEngine = TestTaskitEngine.builder()
                .addTranslator(TestObjectTranslator.getTranslator())
                .addTranslator(TestComplexObjectTranslator.getTranslator()).build();

        TaskitEngineManager translationController = TaskitEngineManager.builder()
                .addTaskitEngine(testTaskitEngine).build();

        TestAppObject appObject = TestObjectUtil.generateTestAppObject();
        TestAppChildObject expectedAppObject = TestObjectUtil.getChildAppFromApp(appObject);

        translationController.writeOutput(expectedAppObject, TestAppObject.class, filePath.resolve(fileName),
                TaskitEngineType.CUSTOM);

        // preconditions
        // the given parent classref is null
        ContractException contractException = assertThrows(ContractException.class, () -> {
            Class<TestAppObject> badClassRef = null;
            translationController.writeOutput(expectedAppObject, badClassRef, filePath.resolve(fileName),
                    TaskitEngineType.CUSTOM);
        });

        assertEquals(TaskitCoreError.NULL_CLASS_REF, contractException.getErrorType());

        // preconditions
        // CoreTranslationError.NULL_OBJECT_FOR_TRANSLATION is tested by
        // testWriteOutput_Base()
        // CoreTranslationError#NULL_PATH is tested by testWriteOutput_Base()
        // CoreTranslationError#INVALID_OUTPUT_PATH is tested by testWriteOutput_Base()
        // CoreTranslationError#NULL_TRANSLATION_ENGINE is tested by
        // testWriteOutput_Base()
    }

    @Test
    @UnitTestForCoverage
    public void testWriteOutput_Base() {
        String fileName = "writeOutput_Base-testOutput.json";

        ResourceHelper.createFile(filePath, fileName);

        TestTaskitEngine testTaskitEngine = TestTaskitEngine.builder()
                .addTranslator(TestObjectTranslator.getTranslator())
                .addTranslator(TestComplexObjectTranslator.getTranslator()).build();

        TaskitEngineManager translationController = TaskitEngineManager.builder()
                .addTaskitEngine(testTaskitEngine).build();

        TestAppObject appObject = TestObjectUtil.generateTestAppObject();
        TestAppChildObject expectedAppObject = TestObjectUtil.getChildAppFromApp(appObject);

        // preconditions
        // the given object is null
        ContractException contractException = assertThrows(ContractException.class, () -> {
            translationController.writeOutput(null, Optional.empty(), filePath.resolve(fileName),
                    TaskitEngineType.CUSTOM);
        });

        assertEquals(TaskitCoreError.NULL_OBJECT_FOR_TRANSLATION, contractException.getErrorType());

        // the path is null
        contractException = assertThrows(ContractException.class, () -> {
            translationController.writeOutput(expectedAppObject, Optional.empty(), null,
                    TaskitEngineType.CUSTOM);
        });

        assertEquals(TaskitCoreError.NULL_PATH, contractException.getErrorType());

        // if the path is invalid
        contractException = assertThrows(ContractException.class, () -> {
            translationController.writeOutput(expectedAppObject, Optional.empty(),
                    filePath.resolve("badPath").resolve(fileName),
                    TaskitEngineType.CUSTOM);
        });

        assertEquals(TaskitCoreError.INVALID_OUTPUT_PATH, contractException.getErrorType());

        // if the translation engine is null
        contractException = assertThrows(ContractException.class, () -> {
            translationController.writeOutput(expectedAppObject, Optional.empty(),
                    filePath.resolve(fileName),
                    TaskitEngineType.UNKNOWN);
        });

        assertEquals(TaskitCoreError.NULL_TASKIT_ENGINE, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TaskitEngineManager.class, name = "getFirstObject", args = { Class.class })
    public void testGetFirstObject() throws IOException {
        String fileName = "getFirstObject-testOutput.json";
        String fileName2 = "getFirstObject-testOutput2.json";

        ResourceHelper.createFile(filePath, fileName);
        ResourceHelper.createFile(filePath, fileName2);

        TestTaskitEngine testTaskitEngine = TestTaskitEngine.builder()
                .addTranslator(TestObjectTranslator.getTranslator())
                .addTranslator(TestComplexObjectTranslator.getTranslator()).build();

        TaskitEngineManager translationController = TaskitEngineManager.builder()
                .addInputFilePath(filePath.resolve(fileName), TestInputObject.class,
                        TaskitEngineType.CUSTOM)
                .addInputFilePath(filePath.resolve(fileName2), TestComplexInputObject.class,
                        TaskitEngineType.CUSTOM)
                .addTaskitEngine(testTaskitEngine).build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();
        TestComplexAppObject expectedComplexAppObject = TestObjectUtil.generateTestComplexAppObject();

        translationController.writeOutput(expectedAppObject, filePath.resolve(fileName),
                TaskitEngineType.CUSTOM);

        translationController.writeOutput(expectedComplexAppObject, filePath.resolve(fileName2),
                TaskitEngineType.CUSTOM);

        translationController.readInput();

        assertEquals(2, translationController.getNumObjects());
        TestAppObject actualTestAppObject = translationController.getFirstObject(TestAppObject.class);
        assertEquals(1, translationController.getNumObjects());

        assertNotNull(actualTestAppObject);

        // preconditions
        ContractException contractException = assertThrows(ContractException.class, () -> {

            translationController.getFirstObject(TestInputObject.class);
        });

        assertEquals(TaskitCoreError.UNKNOWN_CLASSREF, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TaskitEngineManager.class, name = "getObjects", args = { Class.class })
    public void testGetObjects_OfClass() throws IOException {
        String fileName = "GetObjects_OfClass_1-testOutput.json";
        String fileName2 = "GetObjects_OfClass_2-testOutput.json";
        String fileName3 = "GetObjects_OfClass_3-testOutput.json";

        ResourceHelper.createFile(filePath, fileName);
        ResourceHelper.createFile(filePath, fileName2);
        ResourceHelper.createFile(filePath, fileName3);

        TestTaskitEngine testTaskitEngine = TestTaskitEngine.builder()
                .addTranslator(TestObjectTranslator.getTranslator())
                .addTranslator(TestComplexObjectTranslator.getTranslator()).build();

        TaskitEngineManager translationController = TaskitEngineManager.builder()
                .addInputFilePath(filePath.resolve(fileName), TestInputObject.class,
                        TaskitEngineType.CUSTOM)
                .addInputFilePath(filePath.resolve(fileName2), TestInputObject.class,
                        TaskitEngineType.CUSTOM)
                .addInputFilePath(filePath.resolve(fileName3), TestComplexInputObject.class,
                        TaskitEngineType.CUSTOM)
                .addTaskitEngine(testTaskitEngine).build();

        List<TestAppObject> expectedObjects = TestObjectUtil.getListOfAppObjects(2);
        TestComplexAppObject expectedComplexAppObject = TestObjectUtil.generateTestComplexAppObject();

        translationController.writeOutput(expectedObjects.get(0), filePath.resolve(fileName),
                TaskitEngineType.CUSTOM);
        translationController.writeOutput(expectedObjects.get(1), filePath.resolve(fileName2),
                TaskitEngineType.CUSTOM);
        translationController.writeOutput(expectedComplexAppObject, filePath.resolve(fileName3),
                TaskitEngineType.CUSTOM);

        translationController.readInput();

        assertEquals(3, translationController.getNumObjects());

        List<TestAppObject> actualObjects = translationController.getObjects(TestAppObject.class);
        assertEquals(1, translationController.getNumObjects());

        assertEquals(2, actualObjects.size());

        assertTrue(actualObjects.containsAll(expectedObjects));

        List<TestAppObject> actualObjects2 = translationController
                .getObjects(TestAppObject.class);
        assertTrue(actualObjects2.isEmpty());
    }

    @Test
    @UnitTestMethod(target = TaskitEngineManager.class, name = "getObjects", args = {})
    public void testGetObjects() throws IOException {
        String fileName = "GetObjects_1-testOutput.json";
        String fileName2 = "GetObjects_2-testOutput.json";

        ResourceHelper.createFile(filePath, fileName);
        ResourceHelper.createFile(filePath, fileName2);

        TestTaskitEngine testTaskitEngine = TestTaskitEngine.builder()
                .addTranslator(TestObjectTranslator.getTranslator())
                .addTranslator(TestComplexObjectTranslator.getTranslator()).build();

        TaskitEngineManager translationController = TaskitEngineManager.builder()
                .addInputFilePath(filePath.resolve(fileName), TestInputObject.class,
                        TaskitEngineType.CUSTOM)
                .addInputFilePath(filePath.resolve(fileName2), TestInputObject.class,
                        TaskitEngineType.CUSTOM)
                .addTaskitEngine(testTaskitEngine).build();

        List<TestAppObject> expectedObjects = TestObjectUtil.getListOfAppObjects(2);

        translationController.writeOutput(expectedObjects.get(0), filePath.resolve(fileName),
                TaskitEngineType.CUSTOM);
        translationController.writeOutput(expectedObjects.get(1), filePath.resolve(fileName2),
                TaskitEngineType.CUSTOM);

        translationController.readInput();

        assertEquals(2, translationController.getNumObjects());

        List<Object> actualObjects = translationController.getObjects();
        assertEquals(0, translationController.getNumObjects());
        assertEquals(2, actualObjects.size());

        assertTrue(actualObjects.containsAll(expectedObjects));
    }

    @Test
    @UnitTestMethod(target = TaskitEngineManager.class, name = "builder", args = {})
    public void testBuilder() {
        assertNotNull(TaskitEngineManager.builder());
    }

    @Test
    @UnitTestMethod(target = TaskitEngineManager.Builder.class, name = "build", args = {})
    public void testBuild() {
        TaskitEngineManager translationController = TaskitEngineManager.builder()
                .addTaskitEngine(TestTaskitEngine.builder().build()).build();

        assertNotNull(translationController);

        // preconditions
        ContractException contractException = assertThrows(ContractException.class, () -> {
            TaskitEngineManager.builder().build();
        });

        assertEquals(TaskitCoreError.NULL_TASKIT_ENGINE, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TaskitEngineManager.Builder.class, name = "addInputFilePath", args = { Path.class,
            Class.class, TaskitEngineType.class })
    public void testAddInputFilePath() {
        String fileName = "addInputFilePath-testOutput.json";

        ResourceHelper.createFile(filePath, fileName);

        assertDoesNotThrow(() -> TaskitEngineManager.builder()
                .addInputFilePath(filePath.resolve(fileName), TestInputObject.class,
                        TaskitEngineType.CUSTOM)
                .addTaskitEngine(TestTaskitEngine.builder().build()).build());

        // preconditions
        ContractException contractException = assertThrows(ContractException.class, () -> {
            TaskitEngineManager.builder().addInputFilePath(null, TestInputObject.class,
                    TaskitEngineType.CUSTOM);
        });

        assertEquals(TaskitCoreError.NULL_PATH, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            TaskitEngineManager.builder().addInputFilePath(filePath.resolve(fileName), null,
                    TaskitEngineType.CUSTOM);
        });

        assertEquals(TaskitCoreError.NULL_CLASS_REF, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            TaskitEngineManager.builder()
                    .addInputFilePath(filePath.resolve(fileName), TestInputObject.class,
                            TaskitEngineType.CUSTOM)
                    .addInputFilePath(filePath.resolve(fileName), TestInputObject.class,
                            TaskitEngineType.CUSTOM);
        });

        assertEquals(TaskitCoreError.DUPLICATE_INPUT_PATH, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            TaskitEngineManager.builder().addInputFilePath(filePath.resolve("badPath"),
                    TestInputObject.class,
                    TaskitEngineType.CUSTOM);
        });

        assertEquals(TaskitCoreError.INVALID_INPUT_PATH, contractException.getErrorType());

    }

    @Test
    @UnitTestMethod(target = TaskitEngineManager.Builder.class, name = "addParentChildClassRelationship", args = {
            Class.class, Class.class })
    public void testAddParentChildClassRelationship() {
        TaskitEngineManager.builder().addParentChildClassRelationship(TestAppObject.class, Object.class);

        // preconditions
        ContractException contractException = assertThrows(ContractException.class, () -> {
            TaskitEngineManager.builder().addParentChildClassRelationship(null, Object.class);
        });

        assertEquals(TaskitCoreError.NULL_CLASS_REF, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            TaskitEngineManager.builder().addParentChildClassRelationship(TestAppObject.class, null);
        });

        assertEquals(TaskitCoreError.NULL_CLASS_REF, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            TaskitEngineManager.builder()
                    .addParentChildClassRelationship(TestAppObject.class, Object.class)
                    .addParentChildClassRelationship(TestAppObject.class, Object.class);
        });

        assertEquals(TaskitCoreError.DUPLICATE_CLASSREF, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TaskitEngineManager.Builder.class, name = "addTaskitEngine", args = {
            TaskitEngine.class })
    public void testAddTaskitEngine() {
        TestTaskitEngine taskitEngine = TestTaskitEngine.builder()
                .addTranslator(TestObjectTranslator.getTranslator())
                .addTranslator(TestComplexObjectTranslator.getTranslator())
                .addParentChildClassRelationship(TestAppObject.class, Object.class).build();

        TaskitEngineManager.builder().addTaskitEngine(taskitEngine).build();

        // preconditions
        ContractException contractException = assertThrows(ContractException.class, () -> {
            TaskitEngineManager.builder().addTaskitEngine(null);
        });

        assertEquals(TaskitCoreError.NULL_TASKIT_ENGINE, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            TestTaskitEngine taskitEngine2 = TestTaskitEngine.builder()
                    .addTranslator(TestObjectTranslator.getTranslator())
                    .addTranslator(TestComplexObjectTranslator.getTranslator())
                    .addParentChildClassRelationship(TestAppObject.class, Object.class).build();

            TaskitEngineManager.builder()
                    .addParentChildClassRelationship(TestAppObject.class, Object.class)
                    .addTaskitEngine(taskitEngine2);
        });

        assertEquals(TaskitCoreError.DUPLICATE_CLASSREF, contractException.getErrorType());
    }
}
