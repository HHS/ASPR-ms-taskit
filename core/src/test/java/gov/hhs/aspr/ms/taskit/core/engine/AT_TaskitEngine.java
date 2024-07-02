package gov.hhs.aspr.ms.taskit.core.engine;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngine;
import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngineType;
import gov.hhs.aspr.ms.taskit.core.engine.TaskitCoreError;
import gov.hhs.aspr.ms.taskit.core.testsupport.TestObjectUtil;
import gov.hhs.aspr.ms.taskit.core.testsupport.engine.TestTaskitEngine;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestAppChildObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestAppObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestComplexAppObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestComplexInputObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestInputChildObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestInputObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestObjectWrapper;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.TestComplexObjectTranslator;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.TestComplexObjectTranslatorId;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.TestObjectTranslator;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.TestObjectTranslatorId;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.specs.TestComplexObjectTranslationSpec;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.specs.TestObjectTranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.ITranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.TranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.Translator;
import gov.hhs.aspr.ms.taskit.core.translation.TranslatorId;
import gov.hhs.aspr.ms.util.annotations.UnitTestForCoverage;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;
import gov.hhs.aspr.ms.util.errors.ContractException;
import gov.hhs.aspr.ms.util.graph.MutableGraph;

public class AT_TaskitEngine {

    @Test
    @UnitTestMethod(target = TaskitEngine.class, name = "isInitialized", args = {})
    public void testIsInitialized() {
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec testComplexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        TestTaskitEngine testTaskitEngine = TestTaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec).addTranslationSpec(testComplexObjectTranslationSpec)
                .buildWithoutSpecInit();

        assertFalse(testTaskitEngine.isInitialized);

        testTaskitEngine.initTranslationSpecs();
        assertTrue(testTaskitEngine.isInitialized());
    }

    @Test
    @UnitTestForCoverage
    public void testValidateTaskitEngineType() {
        // preconditions
        // TaskitEngineType is set to UNKNOWN
        ContractException contractException = assertThrows(ContractException.class, () -> {
            TaskitEngine engine = TestTaskitEngine.builder().buildWithUnknownType();

            engine.validateInit();
        });

        assertEquals(TaskitCoreError.UNKNOWN_TASKIT_ENGINE_ID, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TaskitEngine.class, name = "getTaskitEngineType", args = {})
    public void testGetTaskitEngineType() {
        TaskitEngine taskitEngine = TestTaskitEngine.builder().build();

        assertEquals(TaskitEngineType.CUSTOM, taskitEngine.getTaskitEngineId());

        taskitEngine = TestTaskitEngine.builder().buildWithUnknownType();

        assertEquals(TaskitEngineType.UNKNOWN, taskitEngine.getTaskitEngineId());
    }

    @Test
    @UnitTestForCoverage
    public void testTranslationSpecsAreInitialized() {
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec testComplexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        TestTaskitEngine testTaskitEngine = TestTaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec).addTranslationSpec(testComplexObjectTranslationSpec)
                .build();

        assertDoesNotThrow(() -> testTaskitEngine.translationSpecsAreInitialized());

        // preconditions
        // one or more Translation Specs are not properly initialized
        assertThrows(RuntimeException.class, () -> {
            TestTaskitEngine testTaskitEngine2 = TestTaskitEngine.builder()
                    .addTranslationSpec(new TestObjectTranslationSpec())
                    .addTranslationSpec(testComplexObjectTranslationSpec).buildWithoutSpecInit();

            testTaskitEngine2.translationSpecsAreInitialized();
        });
    }

    @Test
    @UnitTestMethod(target = TaskitEngine.class, name = "getTranslationSpecs", args = {})
    public void testGetTranslationSpecs() {
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec testComplexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        TestTaskitEngine testTaskitEngine = TestTaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec).addTranslationSpec(testComplexObjectTranslationSpec)
                .build();

        Set<ITranslationSpec> actualTranslationSpecs = testTaskitEngine.getTranslationSpecs();

        assertTrue(actualTranslationSpecs.contains(testObjectTranslationSpec));
        assertTrue(actualTranslationSpecs.contains(testComplexObjectTranslationSpec));
    }

    @Test
    @UnitTestForCoverage
    public void testValidateTranslatorsInitialized() {

        assertDoesNotThrow(() -> {
            TestTaskitEngine.builder().addTranslator(TestObjectTranslator.getTranslator())
                    .addTranslator(TestComplexObjectTranslator.getTranslator()).build();

        });

        // preconditions
        ContractException contractException = assertThrows(ContractException.class, () -> {
            TaskitEngine engine = TestTaskitEngine.builder()
                    .addTranslator(TestObjectTranslator.getTranslator())
                    .addTranslator(TestComplexObjectTranslator.getTranslator()).buildWithNoTranslatorInit();
            engine.validateInit();
        });
        assertEquals(TaskitCoreError.UNINITIALIZED_TRANSLATORS, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TaskitEngine.class, name = "translateObject", args = { Object.class })
    public void testConvertObject() {
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec testComplexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        TestTaskitEngine testTaskitEngine = TestTaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec).addTranslationSpec(testComplexObjectTranslationSpec)
                .build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();
        TestInputObject expectedInputObject = TestObjectUtil.getInputFromApp(expectedAppObject);

        TestInputObject actualInputObject = testTaskitEngine.translateObject(expectedAppObject);
        assertEquals(expectedInputObject, actualInputObject);

        TestAppObject actualAppObject = testTaskitEngine.translateObject(expectedInputObject);
        assertEquals(expectedAppObject, actualAppObject);

        // preconditions
        // the contract exception for CoreTranslationError.UNKNOWN_TRANSLATION_SPEC is
        // covered by the test - testGetTranslationSpecForClass

        // the passed in object is null
        ContractException contractException = assertThrows(ContractException.class, () -> {
            testTaskitEngine.translateObject(null);
        });

        assertEquals(TaskitCoreError.NULL_OBJECT_FOR_TRANSLATION, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TaskitEngine.class, name = "translateObjectAsSafeClass", args = { Object.class,
            Class.class })
    public void testConvertObjectAsSafeClass() {
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec testComplexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        TestTaskitEngine testTaskitEngine = TestTaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec).addTranslationSpec(testComplexObjectTranslationSpec)
                .build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();
        TestInputObject expectedInputObject = TestObjectUtil.getInputFromApp(expectedAppObject);

        TestAppChildObject expectedAppChildObject = TestObjectUtil.getChildAppFromApp(expectedAppObject);
        TestInputChildObject expectedInputChildObject = TestObjectUtil.getChildInputFromInput(expectedInputObject);

        TestInputObject actualInputChildObject = testTaskitEngine.translateObjectAsClassSafe(expectedAppChildObject,
                TestAppObject.class);
        assertEquals(expectedInputChildObject, TestObjectUtil.getChildInputFromInput(actualInputChildObject));

        TestAppObject actualAppChildObject = testTaskitEngine.translateObjectAsClassSafe(expectedInputChildObject,
                TestInputObject.class);
        assertEquals(expectedAppChildObject, TestObjectUtil.getChildAppFromApp(actualAppChildObject));

        // preconditions
        // the contract exception for CoreTranslationError.UNKNOWN_TRANSLATION_SPEC is
        // covered by the test - testGetTranslationSpecForClass

        // the passed in object is null
        ContractException contractException = assertThrows(ContractException.class, () -> {
            testTaskitEngine.translateObjectAsClassSafe(null, Object.class);
        });

        assertEquals(TaskitCoreError.NULL_OBJECT_FOR_TRANSLATION, contractException.getErrorType());

        // the passed in parentClassRef is null
        contractException = assertThrows(ContractException.class, () -> {
            testTaskitEngine.translateObjectAsClassSafe(expectedAppChildObject, null);
        });

        assertEquals(TaskitCoreError.NULL_CLASS_REF, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TaskitEngine.class, name = "translateObjectAsUnsafeClass", args = { Object.class,
            Class.class })
    public void testConvertObjectAsUnsafeClass() {
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

        TestTaskitEngine testTaskitEngine = TestTaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec).addTranslationSpec(testComplexObjectTranslationSpec)
                .addTranslationSpec(wrapperTranslationSpec).build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();

        TestObjectWrapper expectedWrapper = new TestObjectWrapper();
        expectedWrapper.setWrappedObject(expectedAppObject);

        TestObjectWrapper actualWrapper = testTaskitEngine.translateObjectAsClassUnsafe(expectedAppObject,
                TestObjectWrapper.class);

        assertEquals(expectedWrapper, actualWrapper);

        Object actualAppObject = testTaskitEngine.translateObject(actualWrapper);

        assertEquals(expectedAppObject, actualAppObject);

        // preconditions
        // the contract exception for CoreTranslationError.UNKNOWN_TRANSLATION_SPEC is
        // covered by the test - testGetTranslationSpecForClass

        // the passed in object is null
        ContractException contractException = assertThrows(ContractException.class, () -> {
            testTaskitEngine.translateObjectAsClassUnsafe(null, Object.class);
        });

        assertEquals(TaskitCoreError.NULL_OBJECT_FOR_TRANSLATION, contractException.getErrorType());

        // the passed in parentClassRef is null
        contractException = assertThrows(ContractException.class, () -> {
            testTaskitEngine.translateObjectAsClassUnsafe(expectedAppObject, null);
        });

        assertEquals(TaskitCoreError.NULL_CLASS_REF, contractException.getErrorType());
    }

    @Test
    @UnitTestForCoverage
    public void testGetTranslationSpecForClass() {
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec testComplexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        TestTaskitEngine testTaskitEngine = TestTaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec).addTranslationSpec(testComplexObjectTranslationSpec)
                .build();

        assertEquals(testObjectTranslationSpec, testTaskitEngine.getTranslationSpecForClass(TestAppObject.class));
        assertEquals(testObjectTranslationSpec,
                testTaskitEngine.getTranslationSpecForClass(TestInputObject.class));

        assertNotEquals(testComplexObjectTranslationSpec,
                testTaskitEngine.getTranslationSpecForClass(TestAppObject.class));
        assertNotEquals(testComplexObjectTranslationSpec,
                testTaskitEngine.getTranslationSpecForClass(TestInputObject.class));

        assertEquals(testComplexObjectTranslationSpec,
                testTaskitEngine.getTranslationSpecForClass(TestComplexAppObject.class));
        assertEquals(testComplexObjectTranslationSpec,
                testTaskitEngine.getTranslationSpecForClass(TestComplexInputObject.class));

        assertNotEquals(testObjectTranslationSpec,
                testTaskitEngine.getTranslationSpecForClass(TestComplexAppObject.class));
        assertNotEquals(testObjectTranslationSpec,
                testTaskitEngine.getTranslationSpecForClass(TestComplexInputObject.class));

        // preconditions
        // no Translation Spec exists for the given class
        ContractException contractException = assertThrows(ContractException.class, () -> {
            testTaskitEngine.getTranslationSpecForClass(Object.class);
        });

        assertEquals(TaskitCoreError.UNKNOWN_TRANSLATION_SPEC, contractException.getErrorType());
    }

    @Test
    @UnitTestForCoverage
    public void testGetOrderedTranslators() {

        TaskitEngine.Builder taskitEngineBuilder = TestTaskitEngine.builder()
                .addTranslator(TestObjectTranslator.getTranslator())
                .addTranslator(TestComplexObjectTranslator.getTranslator());

        List<Translator> expectedList = new ArrayList<>();
        expectedList.add(TestComplexObjectTranslator.getTranslator());
        expectedList.add(TestObjectTranslator.getTranslator());

        List<Translator> actualList = taskitEngineBuilder.getOrderedTranslators();

        assertEquals(expectedList, actualList);

        // preconditions

        // duplicate translator in the graph

        ContractException contractException = assertThrows(ContractException.class, () -> {
            MutableGraph<TranslatorId, Object> mutableGraph = new MutableGraph<>();
            Map<TranslatorId, Translator> translatorMap = new LinkedHashMap<>();
            mutableGraph.addNode(TestObjectTranslatorId.TRANSLATOR_ID);
            taskitEngineBuilder.addNodes(mutableGraph, translatorMap);
        });

        assertEquals(TaskitCoreError.DUPLICATE_TRANSLATOR, contractException.getErrorType());

        // missing translator
        contractException = assertThrows(ContractException.class, () -> {
            MutableGraph<TranslatorId, Object> mutableGraph = new MutableGraph<>();
            Map<TranslatorId, Translator> translatorMap = new LinkedHashMap<>();

            // call normally
            taskitEngineBuilder.getOrderedTranslators(mutableGraph, translatorMap);
            // remove a mapping
            translatorMap.remove(TestComplexObjectTranslatorId.TRANSLATOR_ID);
            TranslatorId thirdId = new TranslatorId() {
            };
            mutableGraph.addNode(thirdId);
            mutableGraph.addEdge(new Object(), thirdId, TestComplexObjectTranslatorId.TRANSLATOR_ID);
            taskitEngineBuilder.checkForMissingTranslators(mutableGraph, translatorMap);
        });

        assertEquals(TaskitCoreError.MISSING_TRANSLATOR, contractException.getErrorType());

        // cyclic graph
        contractException = assertThrows(ContractException.class, () -> {
            MutableGraph<TranslatorId, Object> mutableGraph = new MutableGraph<>();
            Map<TranslatorId, Translator> translatorMap = new LinkedHashMap<>();

            // call normally
            taskitEngineBuilder.getOrderedTranslators(mutableGraph, translatorMap);
            mutableGraph.addEdge(new Object(), TestComplexObjectTranslatorId.TRANSLATOR_ID,
                    TestObjectTranslatorId.TRANSLATOR_ID);
            TranslatorId thirdId = new TranslatorId() {
            };
            TranslatorId fourthId = new TranslatorId() {
            };
            mutableGraph.addNode(thirdId);
            mutableGraph.addNode(fourthId);
            mutableGraph.addEdge(new Object(), thirdId, fourthId);
            mutableGraph.addEdge(new Object(), fourthId, thirdId);
            taskitEngineBuilder.checkForCyclicGraph(mutableGraph);
        });

        assertEquals(TaskitCoreError.CIRCULAR_TRANSLATOR_DEPENDENCIES, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TaskitEngine.class, name = "hashCode", args = {})
    public void testHashCode() {
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec testComplexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        TestTaskitEngine testTaskitEngine1 = TestTaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec).addTranslationSpec(testComplexObjectTranslationSpec)
                .build();

        TestTaskitEngine testTaskitEngine2 = TestTaskitEngine.builder()
                .addTranslationSpec(testComplexObjectTranslationSpec).build();

        TestTaskitEngine testTaskitEngine3 = TestTaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec).build();

        TestTaskitEngine testTaskitEngine4 = TestTaskitEngine.builder().build();

        TestTaskitEngine testTaskitEngine5 = TestTaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec).addTranslationSpec(testComplexObjectTranslationSpec)
                .build();

        // exact same, same hash code
        assertEquals(testTaskitEngine1.hashCode(), testTaskitEngine1.hashCode());

        // different translation specs
        assertNotEquals(testTaskitEngine1.hashCode(), testTaskitEngine2.hashCode());
        assertNotEquals(testTaskitEngine1.hashCode(), testTaskitEngine3.hashCode());
        assertNotEquals(testTaskitEngine1.hashCode(), testTaskitEngine4.hashCode());
        assertNotEquals(testTaskitEngine2.hashCode(), testTaskitEngine3.hashCode());
        assertNotEquals(testTaskitEngine2.hashCode(), testTaskitEngine4.hashCode());
        assertNotEquals(testTaskitEngine3.hashCode(), testTaskitEngine4.hashCode());

        // same translation specs
        assertEquals(testTaskitEngine1.hashCode(), testTaskitEngine5.hashCode());
    }

    @Test
    @UnitTestMethod(target = TaskitEngine.class, name = "equals", args = { Object.class })
    public void testEquals() {
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec testComplexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        TestTaskitEngine testTaskitEngine1 = TestTaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec).addTranslationSpec(testComplexObjectTranslationSpec)
                .build();

        TestTaskitEngine testTaskitEngine2 = TestTaskitEngine.builder()
                .addTranslationSpec(testComplexObjectTranslationSpec).build();

        TestTaskitEngine testTaskitEngine3 = TestTaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec).build();

        TestTaskitEngine testTaskitEngine4 = TestTaskitEngine.builder().build();

        TestTaskitEngine testTaskitEngine5 = TestTaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec).addTranslationSpec(testComplexObjectTranslationSpec)
                .build();

        TestObjectTranslationSpec testObjectTranslationSpec2 = new TestObjectTranslationSpec();
        TestObjectTranslationSpec testObjectTranslationSpec3 = new TestObjectTranslationSpec();

        TestTaskitEngine testTaskitEngine6 = TestTaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec2).buildWithoutSpecInit();

        TestTaskitEngine testTaskitEngine7 = TestTaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec3).buildWithoutSpecInit();

        // exact same
        assertEquals(testTaskitEngine1, testTaskitEngine1);

        assertNotEquals(testTaskitEngine1, null);

        assertNotEquals(testTaskitEngine1, new Object());

        // different translation specs
        assertNotEquals(testTaskitEngine1, testTaskitEngine2);
        assertNotEquals(testTaskitEngine1, testTaskitEngine3);
        assertNotEquals(testTaskitEngine1, testTaskitEngine4);
        assertNotEquals(testTaskitEngine2, testTaskitEngine3);
        assertNotEquals(testTaskitEngine2, testTaskitEngine4);
        assertNotEquals(testTaskitEngine3, testTaskitEngine4);

        testObjectTranslationSpec2.init(testTaskitEngine1);
        testObjectTranslationSpec3.init(testTaskitEngine5);
        assertNotEquals(testTaskitEngine6, testTaskitEngine7);

        // init vs not init
        assertNotEquals(testTaskitEngine1, testTaskitEngine6);

        // same translation specs
        assertEquals(testTaskitEngine1, testTaskitEngine5);

        TaskitEngine.Data data = new TaskitEngine.Data();
        assertEquals(data, data);
        assertNotEquals(data, null);
        assertNotEquals(data, new Object());
    }
}
