package gov.hhs.aspr.ms.taskit.core.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.taskit.core.testsupport.TestObjectUtil;
import gov.hhs.aspr.ms.taskit.core.testsupport.engine.TestTaskitEngine;
import gov.hhs.aspr.ms.taskit.core.testsupport.engine.TestTaskitEngineBuilderBridge;
import gov.hhs.aspr.ms.taskit.core.testsupport.engine.TestTaskitEngineId;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestAppChildObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestAppObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestComplexAppObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestComplexInputObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestInputChildObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestInputObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestObjectWrapper;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.TestTranslationSpec;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.complexobject.specs.TestComplexObjectTranslationSpec;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.object.specs.TestObjectTranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.ITranslationSpec;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;
import gov.hhs.aspr.ms.util.errors.ContractException;
import gov.hhs.aspr.ms.util.resourcehelper.ResourceError;
import gov.hhs.aspr.ms.util.resourcehelper.ResourceHelper;

public class AT_TaskitEngine {
    Path basePath = ResourceHelper.getResourceDir(this.getClass());
    Path filePath = ResourceHelper.createDirectory(basePath, "test-output");

    @Test
    @UnitTestMethod(target = TaskitEngine.class, name = "getTaskitEngineId", args = {})
    public void testGetTaskitEngineId() {
        TaskitEngine taskitEngine = TestTaskitEngine.builder()
                .addTranslationSpec(new TestObjectTranslationSpec())
                .build();

        assertEquals(TestTaskitEngineId.TEST_ENGINE_ID, taskitEngine.getTaskitEngineId());
    }

    @Test
    @UnitTestMethod(target = TaskitEngine.class, name = "init", args = {})
    public void testInit() {
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec testComplexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        TaskitEngine taskitEngine = new TestTaskitEngineBuilderBridge(TestTaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec)
                .addTranslationSpec(testComplexObjectTranslationSpec))
                .buildWithoutInit();

        assertFalse(taskitEngine.isInitialized());
        assertFalse(testObjectTranslationSpec.isInitialized());
        assertFalse(testComplexObjectTranslationSpec.isInitialized());

        taskitEngine.init();

        assertTrue(taskitEngine.isInitialized());
        assertTrue(testObjectTranslationSpec.isInitialized());
        assertTrue(testComplexObjectTranslationSpec.isInitialized());
    }

    @Test
    @UnitTestMethod(target = TaskitEngine.class, name = "isInitialized", args = {})
    public void testIsInitialized() {
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec testComplexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        TaskitEngine taskitEngine = new TestTaskitEngineBuilderBridge(TestTaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec)
                .addTranslationSpec(testComplexObjectTranslationSpec))
                .buildWithoutInit();

        assertFalse(taskitEngine.isInitialized());

        taskitEngine.init();
        assertTrue(taskitEngine.isInitialized());
    }

    @Test
    @UnitTestMethod(target = TaskitEngine.class, name = "getTranslationSpecs", args = {})
    public void testGetTranslationSpecs() {
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec testComplexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        TaskitEngine taskitEngine = TestTaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec)
                .addTranslationSpec(testComplexObjectTranslationSpec)
                .build();

        Set<ITranslationSpec> actualTranslationSpecs = taskitEngine.getTranslationSpecs();

        assertTrue(actualTranslationSpecs.contains(testObjectTranslationSpec));
        assertTrue(actualTranslationSpecs.contains(testComplexObjectTranslationSpec));
    }

    @Test
    @UnitTestMethod(target = TaskitEngine.class, name = "write", args = { Path.class, Object.class })
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

        // preconditions
        // null path
        ContractException contractException = assertThrows(ContractException.class, () -> {
            testTaskitEngine.write(null, inputObject);
        });

        assertEquals(TaskitError.NULL_PATH, contractException.getErrorType());

        // file path is directory
        contractException = assertThrows(ContractException.class, () -> {
            testTaskitEngine.write(filePath, inputObject);
        });

        assertEquals(ResourceError.FILE_PATH_IS_DIRECTORY, contractException.getErrorType());

        // directory path is file
        contractException = assertThrows(ContractException.class, () -> {
            testTaskitEngine.write(filePath.resolve(fileName).resolve(fileName), inputObject);
        });

        assertEquals(ResourceError.DIRECTORY_PATH_IS_FILE, contractException.getErrorType());

        // null object
        contractException = assertThrows(ContractException.class, () -> {
            testTaskitEngine.write(filePath.resolve(fileName), null);
        });

        assertEquals(TaskitError.NULL_OBJECT_FOR_TRANSLATION, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TaskitEngine.class, name = "translateAndWrite", args = { Path.class,
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

        // preconditions
        // null path
        ContractException contractException = assertThrows(ContractException.class, () -> {
            testTaskitEngine.translateAndWrite(null, expectedAppObject);
        });

        assertEquals(TaskitError.NULL_PATH, contractException.getErrorType());

        // file path is directory
        contractException = assertThrows(ContractException.class, () -> {
            testTaskitEngine.translateAndWrite(filePath, expectedAppObject);
        });

        assertEquals(ResourceError.FILE_PATH_IS_DIRECTORY, contractException.getErrorType());

        // directory path is file
        contractException = assertThrows(ContractException.class, () -> {
            testTaskitEngine.translateAndWrite(filePath.resolve(fileName).resolve(fileName), expectedAppObject);
        });

        assertEquals(ResourceError.DIRECTORY_PATH_IS_FILE, contractException.getErrorType());

        // null object
        contractException = assertThrows(ContractException.class, () -> {
            testTaskitEngine.translateAndWrite(filePath.resolve(fileName), null);
        });

        assertEquals(TaskitError.NULL_OBJECT_FOR_TRANSLATION, contractException.getErrorType());

        // unknown translation spec
        contractException = assertThrows(ContractException.class, () -> {
            testTaskitEngine.translateAndWrite(filePath.resolve(fileName), new TestAppChildObject());
        });

        assertEquals(TaskitError.UNKNOWN_TRANSLATION_SPEC, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TaskitEngine.class, name = "translateAndWrite", args = { Path.class, Object.class,
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

        // preconditions
        // null path
        ContractException contractException = assertThrows(ContractException.class, () -> {
            testTaskitEngine.translateAndWrite(null, expectedAppObject, TestAppObject.class);
        });

        assertEquals(TaskitError.NULL_PATH, contractException.getErrorType());

        // file path is directory
        contractException = assertThrows(ContractException.class, () -> {
            testTaskitEngine.translateAndWrite(filePath, expectedAppObject, TestAppObject.class);
        });

        assertEquals(ResourceError.FILE_PATH_IS_DIRECTORY, contractException.getErrorType());

        // directory path is file
        contractException = assertThrows(ContractException.class, () -> {
            testTaskitEngine.translateAndWrite(filePath.resolve(fileName).resolve(fileName), expectedAppObject,
                    TestAppObject.class);
        });

        assertEquals(ResourceError.DIRECTORY_PATH_IS_FILE, contractException.getErrorType());

        // null object
        contractException = assertThrows(ContractException.class, () -> {
            testTaskitEngine.translateAndWrite(filePath.resolve(fileName), null, TestAppObject.class);
        });

        assertEquals(TaskitError.NULL_OBJECT_FOR_TRANSLATION, contractException.getErrorType());

        // null class
        contractException = assertThrows(ContractException.class, () -> {
            testTaskitEngine.translateAndWrite(filePath.resolve(fileName), expectedAppObject, null);
        });

        assertEquals(TaskitError.NULL_CLASS_REF, contractException.getErrorType());

        // unknown translation spec
        contractException = assertThrows(ContractException.class, () -> {
            testTaskitEngine.translateAndWrite(filePath.resolve(fileName), new TestAppChildObject(),
                    TestAppChildObject.class);
        });

        assertEquals(TaskitError.UNKNOWN_TRANSLATION_SPEC, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TaskitEngine.class, name = "read", args = { Path.class, Class.class })
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

        // preconditions
        // null path
        ContractException contractException = assertThrows(ContractException.class, () -> {
            testTaskitEngine.read(null, TestInputObject.class);
        });

        assertEquals(TaskitError.NULL_PATH, contractException.getErrorType());

        // file path is a directory
        contractException = assertThrows(ContractException.class, () -> {
            testTaskitEngine.read(filePath, TestInputObject.class);
        });

        assertEquals(ResourceError.FILE_PATH_IS_DIRECTORY, contractException.getErrorType());

        // unknown file
        contractException = assertThrows(ContractException.class, () -> {
            testTaskitEngine.read(filePath.resolve("unknownFile.json"), TestInputObject.class);
        });

        assertEquals(ResourceError.UNKNOWN_FILE, contractException.getErrorType());

        // null classref
        contractException = assertThrows(ContractException.class, () -> {
            testTaskitEngine.read(filePath.resolve(fileName), null);
        });

        assertEquals(TaskitError.NULL_CLASS_REF, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TaskitEngine.class, name = "readAndTranslate", args = { Path.class, Class.class })
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

        // preconditions
        // null path
        ContractException contractException = assertThrows(ContractException.class, () -> {
            testTaskitEngine.readAndTranslate(null, TestInputObject.class);
        });

        assertEquals(TaskitError.NULL_PATH, contractException.getErrorType());

        // file path is a directory
        contractException = assertThrows(ContractException.class, () -> {
            testTaskitEngine.readAndTranslate(filePath, TestInputObject.class);
        });

        assertEquals(ResourceError.FILE_PATH_IS_DIRECTORY, contractException.getErrorType());

        // unknown file
        contractException = assertThrows(ContractException.class, () -> {
            testTaskitEngine.readAndTranslate(filePath.resolve("unknownFile.json"), TestInputObject.class);
        });

        assertEquals(ResourceError.UNKNOWN_FILE, contractException.getErrorType());

        // null classref
        contractException = assertThrows(ContractException.class, () -> {
            testTaskitEngine.readAndTranslate(filePath.resolve(fileName), null);
        });

        assertEquals(TaskitError.NULL_CLASS_REF, contractException.getErrorType());

        // unknown translation spec
        contractException = assertThrows(ContractException.class, () -> {

            TestTaskitEngine.builder().addTranslationSpec(new TestComplexObjectTranslationSpec()).build()
                    .readAndTranslate(filePath.resolve(fileName), TestInputObject.class);
        });

        assertEquals(TaskitError.UNKNOWN_TRANSLATION_SPEC, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TaskitEngine.class, name = "translateObject", args = { Object.class })
    public void testTranslateObject() {
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec testComplexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        TaskitEngine taskitEngine = TestTaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec)
                .addTranslationSpec(testComplexObjectTranslationSpec)
                .build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();
        TestInputObject expectedInputObject = TestObjectUtil.getInputFromApp(expectedAppObject);

        TestInputObject actualInputObject = taskitEngine.translateObject(expectedAppObject);
        assertEquals(expectedInputObject, actualInputObject);

        TestAppObject actualAppObject = taskitEngine.translateObject(expectedInputObject);
        assertEquals(expectedAppObject, actualAppObject);

        // preconditions
        // the contract exception for TaskitCoreError#UNKNOWN_TRANSLATION_SPEC is
        // covered by the test - testGetTranslationSpecForClass

        // the passed in object is null
        ContractException contractException = assertThrows(ContractException.class, () -> {
            taskitEngine.translateObject(null);
        });

        assertEquals(TaskitError.NULL_OBJECT_FOR_TRANSLATION, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TaskitEngine.class, name = "translateObjectAsClassSafe", args = { Object.class,
            Class.class })
    public void testTranslateObjectAsSafeClass() {
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec testComplexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        TaskitEngine taskitEngine = TestTaskitEngine.builder()
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

        // preconditions
        // TaskitCoreError#NULL_CLASS_REF is covered by the test -
        // testGetTranslationSpecForClass
        // TaskitCoreError#UNKNOWN_TRANSLATION_SPEC is covered by the test -
        // testGetTranslationSpecForClass

        // the passed in object is null
        ContractException contractException = assertThrows(ContractException.class, () -> {
            taskitEngine.translateObjectAsClassSafe(null, Object.class);
        });

        assertEquals(TaskitError.NULL_OBJECT_FOR_TRANSLATION, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TaskitEngine.class, name = "translateObjectAsClassUnsafe", args = { Object.class,
            Class.class })
    public void testTranslateObjectAsClassUnsafe() {
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

        TaskitEngine taskitEngine = TestTaskitEngine.builder()
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

        // preconditions
        // TaskitCoreError#NULL_CLASS_REF is covered by the test -
        // testGetTranslationSpecForClass
        // TaskitCoreError#UNKNOWN_TRANSLATION_SPEC is covered by the test -
        // testGetTranslationSpecForClass

        // the passed in object is null
        ContractException contractException = assertThrows(ContractException.class, () -> {
            taskitEngine.translateObjectAsClassUnsafe(null, Object.class);
        });

        assertEquals(TaskitError.NULL_OBJECT_FOR_TRANSLATION, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TaskitEngine.class, name = "getTranslationSpecForClass", args = { Class.class })
    public void testGetTranslationSpecForClass() {
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec testComplexObjectTranslationSpec = new TestComplexObjectTranslationSpec();

        TaskitEngine taskitEngine = TestTaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec)
                .addTranslationSpec(testComplexObjectTranslationSpec)
                .build();

        assertEquals(testObjectTranslationSpec,
                taskitEngine.getTranslationSpecForClass(TestAppObject.class));
        assertEquals(testObjectTranslationSpec,
                taskitEngine.getTranslationSpecForClass(TestInputObject.class));

        assertEquals(testComplexObjectTranslationSpec,
                taskitEngine.getTranslationSpecForClass(TestComplexAppObject.class));
        assertEquals(testComplexObjectTranslationSpec,
                taskitEngine.getTranslationSpecForClass(TestComplexInputObject.class));

        // preconditions
        // the classRef is null
        ContractException contractException = assertThrows(ContractException.class, () -> {
            taskitEngine.getTranslationSpecForClass(null);
        });

        assertEquals(TaskitError.NULL_CLASS_REF, contractException.getErrorType());

        // no Translation Spec exists for the given class
        contractException = assertThrows(ContractException.class, () -> {
            taskitEngine.getTranslationSpecForClass(Object.class);
        });

        assertEquals(TaskitError.UNKNOWN_TRANSLATION_SPEC, contractException.getErrorType());
    }

    // TODO: update test
    @Test
    @UnitTestMethod(target = TaskitEngine.class, name = "hashCode", args = {})
    public void testHashCode() {
        TaskitEngine taskitEngine1 = TestTaskitEngine.builder()
                .addTranslationSpec(new TestObjectTranslationSpec())
                .addTranslationSpec(new TestComplexObjectTranslationSpec())
                .build();

        TaskitEngine taskitEngine2 = TestTaskitEngine.builder()
                .addTranslationSpec(new TestComplexObjectTranslationSpec())
                .build();

        TaskitEngine taskitEngine3 = TestTaskitEngine.builder()
                .addTranslationSpec(new TestObjectTranslationSpec())
                .build();

        TaskitEngine taskitEngine4 = TestTaskitEngine.builder()
                .addTranslationSpec(new TestObjectTranslationSpec())
                .addTranslationSpec(new TestComplexObjectTranslationSpec())
                .build();

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
    @UnitTestMethod(target = TaskitEngine.class, name = "equals", args = { Object.class })
    public void testEquals() {
        TaskitEngine taskitEngine1 = new TestTaskitEngineBuilderBridge(TestTaskitEngine.builder()
                .addTranslationSpec(new TestObjectTranslationSpec())
                .addTranslationSpec(new TestComplexObjectTranslationSpec()))
                .buildWithoutInit();

        TaskitEngine taskitEngine2 = new TestTaskitEngineBuilderBridge(TestTaskitEngine.builder()
                .addTranslationSpec(new TestComplexObjectTranslationSpec()))
                .buildWithoutInit();

        TaskitEngine taskitEngine3 = new TestTaskitEngineBuilderBridge(TestTaskitEngine.builder()
                .addTranslationSpec(new TestObjectTranslationSpec()))
                .buildWithoutInit();

        TaskitEngine taskitEngine4 = new TestTaskitEngineBuilderBridge(TestTaskitEngine.builder()
                .addTranslationSpec(new TestObjectTranslationSpec())
                .addTranslationSpec(new TestComplexObjectTranslationSpec()))
                .buildWithoutInit();

        TaskitEngine taskitEngine5 = TestTaskitEngine.builder()
                .addTranslationSpec(new TestObjectTranslationSpec())
                .addTranslationSpec(new TestComplexObjectTranslationSpec())
                .build();

        TaskitEngine taskitEngine6 = new TaskitEngine(
                TaskitEngineData.builder().addTranslationSpec(new TestObjectTranslationSpec()).build(),
                new TaskitEngineId() {

                }) {

            @Override
            protected <O> void writeToFile(File file, O outputObject) throws IOException {
            }

            @Override
            protected <I> I readFile(File file, Class<I> inputClassRef) throws IOException {
                return null;
            }

        };

        // exact same
        assertEquals(taskitEngine1, taskitEngine1);

        // null test
        assertNotEquals(taskitEngine1, null);

        // not an instance test
        assertNotEquals(taskitEngine1, new Object());

        // different id
        assertNotEquals(taskitEngine1, taskitEngine6);

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
