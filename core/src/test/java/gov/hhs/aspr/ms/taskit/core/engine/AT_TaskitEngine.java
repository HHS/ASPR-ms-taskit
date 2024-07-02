package gov.hhs.aspr.ms.taskit.core.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.Set;

import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.taskit.core.testsupport.TestObjectUtil;
import gov.hhs.aspr.ms.taskit.core.testsupport.engine.TestTaskitEngineId;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestAppChildObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestAppObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestComplexAppObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestComplexInputObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestInputChildObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestInputObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestObjectWrapper;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.specs.TestComplexObjectTranslationSpec;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.specs.TestObjectTranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.ITranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.TranslationSpec;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;
import gov.hhs.aspr.ms.util.errors.ContractException;

public class AT_TaskitEngine {

    private abstract class BadTranslationSpec<I, A> extends TranslationSpec<I, A> {

        @Override
        public void init(ITaskitEngine taskitEngine) {
        }
    }

    @Test
    @UnitTestMethod(target = TaskitEngine.class, name = "getTaskitEngineId", args = {})
    public void testGetTaskitEngineId() {
        TaskitEngine taskitEngine = TaskitEngine.builder()
                .addTranslationSpec(new TestObjectTranslationSpec())
                .setTaskitEngineId(TestTaskitEngineId.TEST_ENGINE_ID).build();

        assertEquals(TestTaskitEngineId.TEST_ENGINE_ID, taskitEngine.getTaskitEngineId());
    }

    @Test
    @UnitTestMethod(target = TaskitEngine.class, name = "init", args = { ITaskitEngine.class })
    public void testInit() {
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec testComplexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        TaskitEngine taskitEngine = TaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec)
                .addTranslationSpec(testComplexObjectTranslationSpec)
                .setTaskitEngineId(TestTaskitEngineId.TEST_ENGINE_ID)
                .build();

        assertFalse(taskitEngine.isInitialized());
        assertFalse(testObjectTranslationSpec.isInitialized());
        assertFalse(testComplexObjectTranslationSpec.isInitialized());

        taskitEngine.init(taskitEngine);

        assertTrue(taskitEngine.isInitialized());
        assertTrue(testObjectTranslationSpec.isInitialized());
        assertTrue(testComplexObjectTranslationSpec.isInitialized());

        // precondition
        // a translation spec was not properly initialized
        ContractException contractException = assertThrows(ContractException.class, () -> {
            BadTranslationSpec<TestInputObject, TestAppObject> badTranslationSpec = new BadTranslationSpec<TestInputObject, TestAppObject>() {

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

            TaskitEngine taskitEngine2 = TaskitEngine.builder()
                    .addTranslationSpec(badTranslationSpec)
                    .setTaskitEngineId(TestTaskitEngineId.TEST_ENGINE_ID)
                    .build();

            taskitEngine2.init(taskitEngine2);
        });

        assertEquals(TaskitCoreError.UNINITIALIZED_TRANSLATION_SPEC, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TaskitEngine.class, name = "isInitialized", args = {})
    public void testIsInitialized() {
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec testComplexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        TaskitEngine taskitEngine = TaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec)
                .addTranslationSpec(testComplexObjectTranslationSpec)
                .setTaskitEngineId(TestTaskitEngineId.TEST_ENGINE_ID)
                .build();

        assertFalse(taskitEngine.isInitialized());

        taskitEngine.init(taskitEngine);
        assertTrue(taskitEngine.isInitialized());
    }

    @Test
    @UnitTestMethod(target = TaskitEngine.class, name = "getTaskitEngine", args = {})
    public void testGetTaskitEngine() {
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec testComplexObjectTranslationSpec = new TestComplexObjectTranslationSpec();

        TaskitEngine taskitEngine = TaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec)
                .addTranslationSpec(testComplexObjectTranslationSpec)
                .setTaskitEngineId(TestTaskitEngineId.TEST_ENGINE_ID)
                .build();

        assertEquals(taskitEngine, taskitEngine.getTaskitEngine());
        assertTrue(taskitEngine == taskitEngine.getTaskitEngine());
    }

    @Test
    @UnitTestMethod(target = TaskitEngine.class, name = "getTranslationSpecs", args = {})
    public void testGetTranslationSpecs() {
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec testComplexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        TaskitEngine taskitEngine = TaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec)
                .addTranslationSpec(testComplexObjectTranslationSpec)
                .setTaskitEngineId(TestTaskitEngineId.TEST_ENGINE_ID)
                .build();

        Set<ITranslationSpec> actualTranslationSpecs = taskitEngine.getTranslationSpecs();

        assertTrue(actualTranslationSpecs.contains(testObjectTranslationSpec));
        assertTrue(actualTranslationSpecs.contains(testComplexObjectTranslationSpec));
    }

    @Test
    @UnitTestMethod(target = TaskitEngine.class, name = "write", args = { Path.class, Object.class })
    public void testWrite() {
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec testComplexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        TaskitEngine taskitEngine = TaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec)
                .addTranslationSpec(testComplexObjectTranslationSpec)
                .setTaskitEngineId(TestTaskitEngineId.TEST_ENGINE_ID)
                .build();

        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () -> {
            taskitEngine.write(null, null);
        });

        assertEquals("Called 'write' on TaskitEngine", exception.getMessage());
    }

    @Test
    @UnitTestMethod(target = TaskitEngine.class, name = "translateAndWrite", args = { Path.class, Object.class })
    public void testTranslateAndWrite() {
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec testComplexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        TaskitEngine taskitEngine = TaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec)
                .addTranslationSpec(testComplexObjectTranslationSpec)
                .setTaskitEngineId(TestTaskitEngineId.TEST_ENGINE_ID)
                .build();

        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () -> {
            taskitEngine.translateAndWrite(null, null);
        });

        assertEquals("Called 'translateAndWrite' on TaskitEngine", exception.getMessage());
    }

    @Test
    @UnitTestMethod(target = TaskitEngine.class, name = "translateAndWrite", args = { Path.class, Object.class,
            Class.class })
    public void testTranslateAndWrite_Class() {
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec testComplexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        TaskitEngine taskitEngine = TaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec)
                .addTranslationSpec(testComplexObjectTranslationSpec)
                .setTaskitEngineId(TestTaskitEngineId.TEST_ENGINE_ID)
                .build();

        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () -> {
            taskitEngine.translateAndWrite(null, null, null);
        });

        assertEquals("Called 'translateAndWrite' on TaskitEngine", exception.getMessage());
    }

    @Test
    @UnitTestMethod(target = TaskitEngine.class, name = "read", args = { Path.class, Class.class })
    public void testRead() {
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec testComplexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        TaskitEngine taskitEngine = TaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec)
                .addTranslationSpec(testComplexObjectTranslationSpec)
                .setTaskitEngineId(TestTaskitEngineId.TEST_ENGINE_ID)
                .build();

        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () -> {
            taskitEngine.read(null, null);
        });

        assertEquals("Called 'read' on TaskitEngine", exception.getMessage());
    }

    @Test
    @UnitTestMethod(target = TaskitEngine.class, name = "readAndTranslate", args = { Path.class, Class.class })
    public void testReadAndTranslate() {
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec testComplexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        TaskitEngine taskitEngine = TaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec)
                .addTranslationSpec(testComplexObjectTranslationSpec)
                .setTaskitEngineId(TestTaskitEngineId.TEST_ENGINE_ID)
                .build();

        UnsupportedOperationException exception = assertThrows(UnsupportedOperationException.class, () -> {
            taskitEngine.readAndTranslate(null, null);
        });

        assertEquals("Called 'readAndTranslate' on TaskitEngine", exception.getMessage());
    }

    @Test
    @UnitTestMethod(target = TaskitEngine.class, name = "translateObject", args = { Object.class })
    public void testTranslateObject() {
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec testComplexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        TaskitEngine taskitEngine = TaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec)
                .addTranslationSpec(testComplexObjectTranslationSpec)
                .setTaskitEngineId(TestTaskitEngineId.TEST_ENGINE_ID)
                .build();

        taskitEngine.init(taskitEngine);

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

        assertEquals(TaskitCoreError.NULL_OBJECT_FOR_TRANSLATION, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TaskitEngine.class, name = "translateObjectAsSafeClass", args = { Object.class,
            Class.class })
    public void testTranslateObjectAsSafeClass() {
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec testComplexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        TaskitEngine taskitEngine = TaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec)
                .addTranslationSpec(testComplexObjectTranslationSpec)
                .setTaskitEngineId(TestTaskitEngineId.TEST_ENGINE_ID)
                .build();

        taskitEngine.init(taskitEngine);

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

        assertEquals(TaskitCoreError.NULL_OBJECT_FOR_TRANSLATION, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TaskitEngine.class, name = "translateObjectAsClassUnsafe", args = { Object.class,
            Class.class })
    public void testTranslateObjectAsClassUnsafe() {
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

        TaskitEngine taskitEngine = TaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec)
                .addTranslationSpec(testComplexObjectTranslationSpec)
                .addTranslationSpec(wrapperTranslationSpec)
                .setTaskitEngineId(TestTaskitEngineId.TEST_ENGINE_ID)
                .build();

        taskitEngine.init(taskitEngine);

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

        assertEquals(TaskitCoreError.NULL_OBJECT_FOR_TRANSLATION, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TaskitEngine.class, name = "getTranslationSpecForClass", args = { Class.class })
    public void testGetTranslationSpecForClass() {
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec testComplexObjectTranslationSpec = new TestComplexObjectTranslationSpec();

        TaskitEngine taskitEngine = TaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec)
                .addTranslationSpec(testComplexObjectTranslationSpec)
                .setTaskitEngineId(TestTaskitEngineId.TEST_ENGINE_ID)
                .build();

        taskitEngine.init(taskitEngine);

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

        assertEquals(TaskitCoreError.NULL_CLASS_REF, contractException.getErrorType());

        // no Translation Spec exists for the given class
        contractException = assertThrows(ContractException.class, () -> {
            taskitEngine.getTranslationSpecForClass(Object.class);
        });

        assertEquals(TaskitCoreError.UNKNOWN_TRANSLATION_SPEC, contractException.getErrorType());
    }

    // @Test
    // @UnitTestForCoverage
    // public void testGetOrderedTranslators() {

    // TaskitEngine.Builder taskitEngineBuilder = TaskitEngine.builder()
    // .addTranslator(TestObjectTranslator.getTranslator())
    // .addTranslator(TestComplexObjectTranslator.getTranslator());

    // List<Translator> expectedList = new ArrayList<>();
    // expectedList.add(TestComplexObjectTranslator.getTranslator());
    // expectedList.add(TestObjectTranslator.getTranslator());

    // List<Translator> actualList = taskitEngineBuilder.getOrderedTranslators();

    // assertEquals(expectedList, actualList);

    // // preconditions

    // // duplicate translator in the graph

    // ContractException contractException = assertThrows(ContractException.class,
    // () -> {
    // MutableGraph<TranslatorId, Object> mutableGraph = new MutableGraph<>();
    // Map<TranslatorId, Translator> translatorMap = new LinkedHashMap<>();
    // mutableGraph.addNode(TestObjectTranslatorId.TRANSLATOR_ID);
    // taskitEngineBuilder.addNodes(mutableGraph, translatorMap);
    // });

    // assertEquals(TaskitCoreError.DUPLICATE_TRANSLATOR,
    // contractException.getErrorType());

    // // missing translator
    // contractException = assertThrows(ContractException.class, () -> {
    // MutableGraph<TranslatorId, Object> mutableGraph = new MutableGraph<>();
    // Map<TranslatorId, Translator> translatorMap = new LinkedHashMap<>();

    // // call normally
    // taskitEngineBuilder.getOrderedTranslators(mutableGraph, translatorMap);
    // // remove a mapping
    // translatorMap.remove(TestComplexObjectTranslatorId.TRANSLATOR_ID);
    // TranslatorId thirdId = new TranslatorId() {
    // };
    // mutableGraph.addNode(thirdId);
    // mutableGraph.addEdge(new Object(), thirdId,
    // TestComplexObjectTranslatorId.TRANSLATOR_ID);
    // taskitEngineBuilder.checkForMissingTranslators(mutableGraph, translatorMap);
    // });

    // assertEquals(TaskitCoreError.MISSING_TRANSLATOR,
    // contractException.getErrorType());

    // // cyclic graph
    // contractException = assertThrows(ContractException.class, () -> {
    // MutableGraph<TranslatorId, Object> mutableGraph = new MutableGraph<>();
    // Map<TranslatorId, Translator> translatorMap = new LinkedHashMap<>();

    // // call normally
    // taskitEngineBuilder.getOrderedTranslators(mutableGraph, translatorMap);
    // mutableGraph.addEdge(new Object(),
    // TestComplexObjectTranslatorId.TRANSLATOR_ID,
    // TestObjectTranslatorId.TRANSLATOR_ID);
    // TranslatorId thirdId = new TranslatorId() {
    // };
    // TranslatorId fourthId = new TranslatorId() {
    // };
    // mutableGraph.addNode(thirdId);
    // mutableGraph.addNode(fourthId);
    // mutableGraph.addEdge(new Object(), thirdId, fourthId);
    // mutableGraph.addEdge(new Object(), fourthId, thirdId);
    // taskitEngineBuilder.checkForCyclicGraph(mutableGraph);
    // });

    // assertEquals(TaskitCoreError.CIRCULAR_TRANSLATOR_DEPENDENCIES,
    // contractException.getErrorType());
    // }

    // TODO: update test
    @Test
    @UnitTestMethod(target = TaskitEngine.class, name = "hashCode", args = {})
    public void testHashCode() {
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec testComplexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        TaskitEngine taskitEngine1 = TaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec)
                .addTranslationSpec(testComplexObjectTranslationSpec)
                .setTaskitEngineId(TestTaskitEngineId.TEST_ENGINE_ID)
                .build();

        TaskitEngine taskitEngine2 = TaskitEngine.builder()
                .addTranslationSpec(testComplexObjectTranslationSpec)
                .setTaskitEngineId(TestTaskitEngineId.TEST_ENGINE_ID)
                .build();

        TaskitEngine taskitEngine3 = TaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec)
                .setTaskitEngineId(TestTaskitEngineId.TEST_ENGINE_ID)
                .build();

    
        TaskitEngine taskitEngine4 = TaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec)
                .addTranslationSpec(testComplexObjectTranslationSpec)
                .setTaskitEngineId(TestTaskitEngineId.TEST_ENGINE_ID)
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
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec testComplexObjectTranslationSpec = new TestComplexObjectTranslationSpec();

        TaskitEngine taskitEngine1 = TaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec)
                .addTranslationSpec(testComplexObjectTranslationSpec)
                .setTaskitEngineId(TestTaskitEngineId.TEST_ENGINE_ID)
                .build();

        TaskitEngine taskitEngine2 = TaskitEngine.builder()
                .addTranslationSpec(testComplexObjectTranslationSpec)
                .setTaskitEngineId(TestTaskitEngineId.TEST_ENGINE_ID)
                .build();

        TaskitEngine taskitEngine3 = TaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec)
                .setTaskitEngineId(TestTaskitEngineId.TEST_ENGINE_ID)
                .build();

        TaskitEngine taskitEngine4 = TaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec)
                .addTranslationSpec(testComplexObjectTranslationSpec)
                .setTaskitEngineId(TestTaskitEngineId.TEST_ENGINE_ID)
                .build();

        TestObjectTranslationSpec testObjectTranslationSpec2 = new TestObjectTranslationSpec();
        TestObjectTranslationSpec testObjectTranslationSpec3 = new TestObjectTranslationSpec();

        TaskitEngine taskitEngine5 = TaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec2)
                .setTaskitEngineId(TestTaskitEngineId.TEST_ENGINE_ID)
                .build();

        TaskitEngine taskitEngine6 = TaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec3)
                .setTaskitEngineId(TestTaskitEngineId.TEST_ENGINE_ID)
                .build();

        TaskitEngine taskitEngine7 = TaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec)
                .addTranslationSpec(testComplexObjectTranslationSpec)
                .setTaskitEngineId(TestTaskitEngineId.TEST_ENGINE_ID)
                .build();

        taskitEngine1.init(taskitEngine1);
        taskitEngine2.init(taskitEngine2);
        taskitEngine3.init(taskitEngine3);
        taskitEngine4.init(taskitEngine4);

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
        // init with different engines should not be equal
        assertNotEquals(taskitEngine5, taskitEngine6);

        // init vs not init
        assertNotEquals(taskitEngine1, taskitEngine7);

        // same translation specs
        assertEquals(taskitEngine1, taskitEngine4);
    }
}
