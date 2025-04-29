package gov.hhs.aspr.ms.taskit.core.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.taskit.core.testsupport.TestObjectUtil;
import gov.hhs.aspr.ms.taskit.core.testsupport.engine.TestTaskitEngine;
import gov.hhs.aspr.ms.taskit.core.testsupport.engine.TestTaskitEngineBuilderBridge;
import gov.hhs.aspr.ms.taskit.core.testsupport.engine.TestTaskitEngineId;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestAppChildObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestAppObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestInputChildObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestInputObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestObjectWrapper;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.TestTranslationSpec;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.complexobject.TestComplexObjectTranslator;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.object.TestObjectTranslator;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;
import gov.hhs.aspr.ms.util.errors.ContractException;
import gov.hhs.aspr.ms.util.resourcehelper.ResourceHelper;

public class AT_TaskitEngineManager {

    Path basePath = ResourceHelper.getResourceDir(this.getClass());
    Path filePath = ResourceHelper.createDirectory(basePath, "test-output");

    @Test
    @UnitTestMethod(target = TaskitEngineManager.class, name = "translateObject", args = { Object.class,
            TaskitEngineId.class })
    public void testTranslateObject() {
        TestTaskitEngine testTaskitEngine = TestTaskitEngine.builder()
                .addTranslator(TestObjectTranslator.getTranslator())
                .addTranslator(TestComplexObjectTranslator.getTranslator())
                .build();

        TaskitEngineManager taskitEngineManager = TaskitEngineManager.builder()
                .addTaskitEngine(testTaskitEngine).build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();
        TestInputObject expectedInputObject = TestObjectUtil.getInputFromApp(expectedAppObject);

        TestAppObject actualAppObject = taskitEngineManager.translateObject(expectedInputObject,
                TestTaskitEngineId.TEST_ENGINE_ID);

        assertEquals(expectedAppObject, actualAppObject);

        TestInputObject actualInputObject = taskitEngineManager.translateObject(expectedAppObject,
                TestTaskitEngineId.TEST_ENGINE_ID);

        assertEquals(expectedInputObject, actualInputObject);
        // preconditions
        // null taskit engine id
        ContractException contractException = assertThrows(ContractException.class, () -> {
            taskitEngineManager.translateObject(actualInputObject, null);
        });

        assertEquals(TaskitError.NULL_TASKIT_ENGINE_ID, contractException.getErrorType());

        // null taskit engine
        contractException = assertThrows(ContractException.class, () -> {
            taskitEngineManager.translateObject(actualInputObject, new TaskitEngineId() {

            });
        });

        assertEquals(TaskitError.NULL_TASKIT_ENGINE, contractException.getErrorType());

        // null object for translation
        contractException = assertThrows(ContractException.class, () -> {
            taskitEngineManager.translateObject(null, TestTaskitEngineId.TEST_ENGINE_ID);
        });

        assertEquals(TaskitError.NULL_OBJECT_FOR_TRANSLATION, contractException.getErrorType());

        // unknownTranslation spec
        contractException = assertThrows(ContractException.class, () -> {
            taskitEngineManager.translateObject(new Object(), TestTaskitEngineId.TEST_ENGINE_ID);
        });

        assertEquals(TaskitError.UNKNOWN_TRANSLATION_SPEC, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TaskitEngineManager.class, name = "translateObjectAsClassSafe", args = { Object.class,
            Class.class, TaskitEngineId.class })
    public void testTranslateObjectAsClassSafe() {
        TestTaskitEngine testTaskitEngine = TestTaskitEngine.builder()
                .addTranslator(TestObjectTranslator.getTranslator())
                .addTranslator(TestComplexObjectTranslator.getTranslator())
                .build();

        TaskitEngineManager taskitEngineManager = TaskitEngineManager.builder()
                .addTaskitEngine(testTaskitEngine).build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();
        TestAppChildObject testAppChildObject = TestObjectUtil.getChildAppFromApp(expectedAppObject);

        TestInputObject expectedInputObject = TestObjectUtil.getInputFromApp(expectedAppObject);
        TestInputChildObject testInputChildObject = TestObjectUtil.getChildInputFromInput(expectedInputObject);

        TestInputObject actualInputObject = taskitEngineManager.translateObjectAsClassSafe(testAppChildObject,
                TestAppObject.class, TestTaskitEngineId.TEST_ENGINE_ID);

        assertEquals(expectedInputObject, actualInputObject);

        TestAppObject actualAppObject = taskitEngineManager.translateObjectAsClassSafe(testInputChildObject,
                TestInputObject.class, TestTaskitEngineId.TEST_ENGINE_ID);

        assertEquals(expectedAppObject, actualAppObject);

        // preconditions
        // null taskit engine id
        ContractException contractException = assertThrows(ContractException.class, () -> {
            taskitEngineManager.translateObjectAsClassSafe(expectedAppObject, TestAppObject.class, null);
        });

        assertEquals(TaskitError.NULL_TASKIT_ENGINE_ID, contractException.getErrorType());

        // null taskit engine
        contractException = assertThrows(ContractException.class, () -> {
            taskitEngineManager.translateObjectAsClassSafe(expectedAppObject, TestAppObject.class,
                    new TaskitEngineId() {

                    });
        });

        assertEquals(TaskitError.NULL_TASKIT_ENGINE, contractException.getErrorType());

        // null object for translation
        contractException = assertThrows(ContractException.class, () -> {
            taskitEngineManager.translateObjectAsClassSafe(null,
                    TestInputObject.class, TestTaskitEngineId.TEST_ENGINE_ID);
        });

        assertEquals(TaskitError.NULL_OBJECT_FOR_TRANSLATION, contractException.getErrorType());

        // null class ref
        contractException = assertThrows(ContractException.class, () -> {
            taskitEngineManager.translateObjectAsClassSafe(expectedAppObject,
                    null, TestTaskitEngineId.TEST_ENGINE_ID);
        });

        assertEquals(TaskitError.NULL_CLASS_REF, contractException.getErrorType());

        // unknownTranslation spec
        contractException = assertThrows(ContractException.class, () -> {
            taskitEngineManager.translateObjectAsClassSafe(expectedAppObject,
                    Object.class, TestTaskitEngineId.TEST_ENGINE_ID);
        });

        assertEquals(TaskitError.UNKNOWN_TRANSLATION_SPEC, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TaskitEngineManager.class, name = "translateObjectAsClassUnsafe", args = {
            Object.class,
            Class.class, TaskitEngineId.class })
    public void testTranslateObjectAsClassUnsafe() {

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

        TestTaskitEngine testTaskitEngine = TestTaskitEngine.builder()
                .addTranslator(TestObjectTranslator.getTranslator())
                .addTranslator(TestComplexObjectTranslator.getTranslator())
                .addTranslationSpec(wrapperTranslationSpec)
                .build();

        TaskitEngineManager taskitEngineManager = TaskitEngineManager.builder()
                .addTaskitEngine(testTaskitEngine).build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();

        TestObjectWrapper expectedWrapper = new TestObjectWrapper();
        expectedWrapper.setWrappedObject(expectedAppObject);

        TestObjectWrapper actualWrapper = taskitEngineManager.translateObjectAsClassUnsafe(expectedAppObject,
                TestObjectWrapper.class, TestTaskitEngineId.TEST_ENGINE_ID);

        assertEquals(expectedWrapper, actualWrapper);

        // preconditions
        // null taskit engine id
        ContractException contractException = assertThrows(ContractException.class, () -> {
            taskitEngineManager.translateObjectAsClassUnsafe(expectedAppObject, TestAppObject.class, null);
        });

        assertEquals(TaskitError.NULL_TASKIT_ENGINE_ID, contractException.getErrorType());

        // null taskit engine
        contractException = assertThrows(ContractException.class, () -> {
            taskitEngineManager.translateObjectAsClassUnsafe(expectedAppObject, TestAppObject.class,
                    new TaskitEngineId() {

                    });
        });

        assertEquals(TaskitError.NULL_TASKIT_ENGINE, contractException.getErrorType());

        // null object for translation
        contractException = assertThrows(ContractException.class, () -> {
            taskitEngineManager.translateObjectAsClassUnsafe(null,
                    TestInputObject.class, TestTaskitEngineId.TEST_ENGINE_ID);
        });

        assertEquals(TaskitError.NULL_OBJECT_FOR_TRANSLATION, contractException.getErrorType());

        // null class ref
        contractException = assertThrows(ContractException.class, () -> {
            taskitEngineManager.translateObjectAsClassUnsafe(expectedAppObject,
                    null, TestTaskitEngineId.TEST_ENGINE_ID);
        });

        assertEquals(TaskitError.NULL_CLASS_REF, contractException.getErrorType());

        // unknownTranslation spec
        contractException = assertThrows(ContractException.class, () -> {
            taskitEngineManager.translateObjectAsClassUnsafe(expectedAppObject,
                    TaskitEngine.class, TestTaskitEngineId.TEST_ENGINE_ID);
        });

        assertEquals(TaskitError.UNKNOWN_TRANSLATION_SPEC, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TaskitEngineManager.class, name = "read", args = { Path.class, Class.class,
            TaskitEngineId.class })
    public void testRead() {
        String fileName = "readInput-testOutput.json";

        ResourceHelper.createFile(filePath, fileName);

        TestTaskitEngine testTaskitEngine = TestTaskitEngine.builder()
                .addTranslator(TestObjectTranslator.getTranslator())
                .addTranslator(TestComplexObjectTranslator.getTranslator())
                .build();

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
        // only the preconditions not tested byt AT_TaskitEngine are tested here

        // null taskit engine id
        ContractException contractException = assertThrows(ContractException.class, () -> {
            taskitEngineManager.read(filePath.resolve(fileName), TestInputObject.class, null);
        });

        assertEquals(TaskitError.NULL_TASKIT_ENGINE_ID, contractException.getErrorType());

        // null taskit engine
        contractException = assertThrows(ContractException.class, () -> {
            taskitEngineManager.read(filePath.resolve(fileName), TestInputObject.class,
                    new TaskitEngineId() {

                    });
        });

        assertEquals(TaskitError.NULL_TASKIT_ENGINE, contractException.getErrorType());

        // issue reading file
        assertThrows(RuntimeException.class, () -> {
            ResourceHelper.createFile(filePath, "foo.json");

            taskitEngineManager.read(filePath.resolve("foo.json"), TestInputObject.class,
                    TestTaskitEngineId.TEST_ENGINE_ID);
        });

    }

    @Test
    @UnitTestMethod(target = TaskitEngineManager.class, name = "readAndTranslate", args = { Path.class, Class.class,
            TaskitEngineId.class })
    public void testReadAndTranslate() {
        String fileName = "readInput-testOutput.json";

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

        TestAppObject actualAppObject = taskitEngineManager.readAndTranslate(filePath.resolve(fileName),
                TestInputObject.class, TestTaskitEngineId.TEST_ENGINE_ID);

        assertEquals(expectedAppObject, actualAppObject);

        // preconditions
        // only the preconditions not tested byt AT_TaskitEngine are tested here
 
        // null taskit engine id
        ContractException contractException = assertThrows(ContractException.class, () -> {
            taskitEngineManager.readAndTranslate(filePath.resolve(fileName), TestInputObject.class, null);
        });

        assertEquals(TaskitError.NULL_TASKIT_ENGINE_ID, contractException.getErrorType());

        // null taskit engine
        contractException = assertThrows(ContractException.class, () -> {
            taskitEngineManager.readAndTranslate(filePath.resolve(fileName), TestInputObject.class,
                    new TaskitEngineId() {

                    });
        });

        assertEquals(TaskitError.NULL_TASKIT_ENGINE, contractException.getErrorType());

        // issue reading file
        assertThrows(RuntimeException.class, () -> {
            taskitEngineManager.readAndTranslate(filePath.resolve("badJson.json"), TestInputObject.class,
                    TestTaskitEngineId.TEST_ENGINE_ID);
        });
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
        // only the preconditions not tested byt AT_TaskitEngine are tested here

        // taskit engine id is null
        ContractException contractException = assertThrows(ContractException.class, () -> {
            taskitEngineManager.write(filePath.resolve(fileName), inputObject, null);
        });

        assertEquals(TaskitError.NULL_TASKIT_ENGINE_ID, contractException.getErrorType());

        // taskit engine is null
        contractException = assertThrows(ContractException.class, () -> {
            taskitEngineManager.write(filePath.resolve(fileName), inputObject, new TaskitEngineId() {
            });
        });

        assertEquals(TaskitError.NULL_TASKIT_ENGINE, contractException.getErrorType());

        // writing the file encounters a IOException
        File file = filePath.resolve(fileName).toFile();
        assertThrows(RuntimeException.class, () -> {
            file.setReadOnly();
            taskitEngineManager.write(filePath.resolve(fileName), inputObject, TestTaskitEngineId.TEST_ENGINE_ID);
        });
        file.setReadable(true);
        file.delete();
    }

    @Test
    @UnitTestMethod(target = TaskitEngineManager.class, name = "translateAndWrite", args = { Path.class,
            Object.class,
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
        // only the preconditions not tested byt AT_TaskitEngine are tested here

        // taskit engine id is null
        ContractException contractException = assertThrows(ContractException.class, () -> {
            taskitEngineManager.translateAndWrite(filePath.resolve(fileName), expectedAppObject, null);
        });

        assertEquals(TaskitError.NULL_TASKIT_ENGINE_ID, contractException.getErrorType());

        // taskit engine is null
        contractException = assertThrows(ContractException.class, () -> {
            taskitEngineManager.translateAndWrite(filePath.resolve(fileName), expectedAppObject, new TaskitEngineId() {
            });
        });

        assertEquals(TaskitError.NULL_TASKIT_ENGINE, contractException.getErrorType());

        // writing the file encounters a IOException
        File file = filePath.resolve(fileName).toFile();
        assertThrows(RuntimeException.class, () -> {
            file.setReadOnly();
            taskitEngineManager.translateAndWrite(filePath.resolve(fileName), expectedAppObject,
                    TestTaskitEngineId.TEST_ENGINE_ID);
        });
        file.setReadable(true);
        file.delete();
    }

    @Test
    @UnitTestMethod(target = TaskitEngineManager.class, name = "translateAndWrite", args = { Path.class,
            Object.class,
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

        taskitEngineManager.translateAndWrite(filePath.resolve(fileName), expectedAppObject,
                TestAppObject.class,
                TestTaskitEngineId.TEST_ENGINE_ID);

        // preconditions
        // only the preconditions not tested byt AT_TaskitEngine are tested here

        // taskit engine id is null
        ContractException contractException = assertThrows(ContractException.class, () -> {
            taskitEngineManager.translateAndWrite(filePath.resolve(fileName), expectedAppObject, TestAppObject.class,
                    null);
        });

        assertEquals(TaskitError.NULL_TASKIT_ENGINE_ID, contractException.getErrorType());

        // taskit engine is null
        contractException = assertThrows(ContractException.class, () -> {
            taskitEngineManager.translateAndWrite(filePath.resolve(fileName), expectedAppObject, TestAppObject.class,
                    new TaskitEngineId() {
                    });
        });

        assertEquals(TaskitError.NULL_TASKIT_ENGINE, contractException.getErrorType());

        // writing the file encounters a IOException
        File file = filePath.resolve(fileName).toFile();
        assertThrows(RuntimeException.class, () -> {
            file.setReadOnly();
            taskitEngineManager.translateAndWrite(filePath.resolve(fileName), expectedAppObject, TestAppObject.class,
                    TestTaskitEngineId.TEST_ENGINE_ID);
        });
        file.setReadable(true);
        file.delete();
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
            TestTaskitEngine taskitEngine2 = new TestTaskitEngineBuilderBridge(TestTaskitEngine.builder()
                    .addTranslator(TestObjectTranslator.getTranslator())
                    .addTranslator(TestComplexObjectTranslator.getTranslator()))
                    .buildWithoutInit();

            TaskitEngineManager.builder()
                    .addTaskitEngine(taskitEngine2);
        });

        assertEquals(TaskitError.UNINITIALIZED_TASKIT_ENGINE, contractException.getErrorType());
    }
}
