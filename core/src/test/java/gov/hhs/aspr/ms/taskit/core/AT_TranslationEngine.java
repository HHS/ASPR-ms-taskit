package gov.hhs.aspr.ms.taskit.core;

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

import gov.hhs.aspr.ms.taskit.core.testsupport.TestObjectUtil;
import gov.hhs.aspr.ms.taskit.core.testsupport.TestTranslationEngine;
import gov.hhs.aspr.ms.taskit.core.testsupport.testcomplexobject.TestComplexAppObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.testcomplexobject.TestComplexObjectTranslator;
import gov.hhs.aspr.ms.taskit.core.testsupport.testcomplexobject.TestComplexObjectTranslatorId;
import gov.hhs.aspr.ms.taskit.core.testsupport.testcomplexobject.input.TestComplexInputObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.testcomplexobject.translationSpecs.TestComplexObjectTranslationSpec;
import gov.hhs.aspr.ms.taskit.core.testsupport.testobject.TestAppChildObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.testobject.TestAppObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.testobject.TestObjectTranslator;
import gov.hhs.aspr.ms.taskit.core.testsupport.testobject.TestObjectTranslatorId;
import gov.hhs.aspr.ms.taskit.core.testsupport.testobject.TestObjectWrapper;
import gov.hhs.aspr.ms.taskit.core.testsupport.testobject.input.TestInputChildObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.testobject.input.TestInputObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.testobject.translationSpecs.TestObjectTranslationSpec;
import gov.hhs.aspr.ms.util.annotations.UnitTestForCoverage;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;
import gov.hhs.aspr.ms.util.errors.ContractException;
import gov.hhs.aspr.ms.util.graph.MutableGraph;

public class AT_TranslationEngine {

    @Test
    @UnitTestMethod(target = TranslationEngine.class, name = "isInitialized", args = {})
    public void testIsInitialized() {
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec testComplexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        TestTranslationEngine testTranslationEngine = TestTranslationEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec).addTranslationSpec(testComplexObjectTranslationSpec)
                .buildWithoutSpecInit();

        assertFalse(testTranslationEngine.isInitialized);

        testTranslationEngine.initTranslationSpecs();
        assertTrue(testTranslationEngine.isInitialized());
    }

    @Test
    @UnitTestForCoverage
    public void testValidateTranslationEngineType() {
        // preconditions
        // TranslationEngineType is set to UNKNOWN
        ContractException contractException = assertThrows(ContractException.class, () -> {
            TranslationEngine engine = TestTranslationEngine.builder().buildWithUnknownType();

            engine.validateInit();
        });

        assertEquals(CoreTranslationError.UNKNOWN_TRANSLATION_ENGINE_TYPE, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TranslationEngine.class, name = "getTranslationEngineType", args = {})
    public void testGetTranslationEngineType() {
        TranslationEngine translationEngine = TestTranslationEngine.builder().build();

        assertEquals(TranslationEngineType.CUSTOM, translationEngine.getTranslationEngineType());

        translationEngine = TestTranslationEngine.builder().buildWithUnknownType();

        assertEquals(TranslationEngineType.UNKNOWN, translationEngine.getTranslationEngineType());
    }

    @Test
    @UnitTestForCoverage
    public void testTranslationSpecsAreInitialized() {
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec testComplexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        TestTranslationEngine testTranslationEngine = TestTranslationEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec).addTranslationSpec(testComplexObjectTranslationSpec)
                .build();

        assertDoesNotThrow(() -> testTranslationEngine.translationSpecsAreInitialized());

        // preconditions
        // one or more Translation Specs are not properly initialized
        assertThrows(RuntimeException.class, () -> {
            TestTranslationEngine testTranslationEngine2 = TestTranslationEngine.builder()
                    .addTranslationSpec(new TestObjectTranslationSpec())
                    .addTranslationSpec(testComplexObjectTranslationSpec).buildWithoutSpecInit();

            testTranslationEngine2.translationSpecsAreInitialized();
        });
    }

    @Test
    @UnitTestMethod(target = TranslationEngine.class, name = "getTranslationSpecs", args = {})
    public void testGetTranslationSpecs() {
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec testComplexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        TestTranslationEngine testTranslationEngine = TestTranslationEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec).addTranslationSpec(testComplexObjectTranslationSpec)
                .build();

        Set<BaseTranslationSpec> actualTranslationSpecs = testTranslationEngine.getTranslationSpecs();

        assertTrue(actualTranslationSpecs.contains(testObjectTranslationSpec));
        assertTrue(actualTranslationSpecs.contains(testComplexObjectTranslationSpec));
    }

    @Test
    @UnitTestForCoverage
    public void testValidateTranslatorsInitialized() {

        assertDoesNotThrow(() -> {
            TestTranslationEngine.builder().addTranslator(TestObjectTranslator.getTranslator())
                    .addTranslator(TestComplexObjectTranslator.getTranslator()).build();

        });

        // preconditions
        ContractException contractException = assertThrows(ContractException.class, () -> {
            TranslationEngine engine = TestTranslationEngine.builder()
                    .addTranslator(TestObjectTranslator.getTranslator())
                    .addTranslator(TestComplexObjectTranslator.getTranslator()).buildWithNoTranslatorInit();
            engine.validateInit();
        });
        assertEquals(CoreTranslationError.UNINITIALIZED_TRANSLATORS, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TranslationEngine.class, name = "convertObject", args = { Object.class })
    public void testConvertObject() {
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec testComplexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        TestTranslationEngine testTranslationEngine = TestTranslationEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec).addTranslationSpec(testComplexObjectTranslationSpec)
                .build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();
        TestInputObject expectedInputObject = TestObjectUtil.getInputFromApp(expectedAppObject);

        TestInputObject actualInputObject = testTranslationEngine.convertObject(expectedAppObject);
        assertEquals(expectedInputObject, actualInputObject);

        TestAppObject actualAppObject = testTranslationEngine.convertObject(expectedInputObject);
        assertEquals(expectedAppObject, actualAppObject);

        // preconditions
        // the contract exception for CoreTranslationError.UNKNOWN_TRANSLATION_SPEC is
        // covered by the test - testGetTranslationSpecForClass

        // the passed in object is null
        ContractException contractException = assertThrows(ContractException.class, () -> {
            testTranslationEngine.convertObject(null);
        });

        assertEquals(CoreTranslationError.NULL_OBJECT_FOR_TRANSLATION, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TranslationEngine.class, name = "convertObjectAsSafeClass", args = { Object.class,
            Class.class })
    public void testConvertObjectAsSafeClass() {
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec testComplexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        TestTranslationEngine testTranslationEngine = TestTranslationEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec).addTranslationSpec(testComplexObjectTranslationSpec)
                .build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();
        TestInputObject expectedInputObject = TestObjectUtil.getInputFromApp(expectedAppObject);

        TestAppChildObject expectedAppChildObject = TestObjectUtil.getChildAppFromApp(expectedAppObject);
        TestInputChildObject expectedInputChildObject = TestObjectUtil.getChildInputFromInput(expectedInputObject);

        TestInputObject actualInputChildObject = testTranslationEngine.convertObjectAsSafeClass(expectedAppChildObject,
                TestAppObject.class);
        assertEquals(expectedInputChildObject, TestObjectUtil.getChildInputFromInput(actualInputChildObject));

        TestAppObject actualAppChildObject = testTranslationEngine.convertObjectAsSafeClass(expectedInputChildObject,
                TestInputObject.class);
        assertEquals(expectedAppChildObject, TestObjectUtil.getChildAppFromApp(actualAppChildObject));

        // preconditions
        // the contract exception for CoreTranslationError.UNKNOWN_TRANSLATION_SPEC is
        // covered by the test - testGetTranslationSpecForClass

        // the passed in object is null
        ContractException contractException = assertThrows(ContractException.class, () -> {
            testTranslationEngine.convertObjectAsSafeClass(null, Object.class);
        });

        assertEquals(CoreTranslationError.NULL_OBJECT_FOR_TRANSLATION, contractException.getErrorType());

        // the passed in parentClassRef is null
        contractException = assertThrows(ContractException.class, () -> {
            testTranslationEngine.convertObjectAsSafeClass(expectedAppChildObject, null);
        });

        assertEquals(CoreTranslationError.NULL_CLASS_REF, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TranslationEngine.class, name = "convertObjectAsUnsafeClass", args = { Object.class,
            Class.class })
    public void testConvertObjectAsUnsafeClass() {
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec testComplexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        // custom Translation Spec to simulate a Spec that might use a class to "wrap"
        // another class
        TranslationSpec<TestObjectWrapper, Object> wrapperTranslationSpec = new TranslationSpec<TestObjectWrapper, Object>() {

            @Override
            protected Object convertInputObject(TestObjectWrapper inputObject) {
                return inputObject.getWrappedObject();
            }

            @Override
            protected TestObjectWrapper convertAppObject(Object appObject) {
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

        TestTranslationEngine testTranslationEngine = TestTranslationEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec).addTranslationSpec(testComplexObjectTranslationSpec)
                .addTranslationSpec(wrapperTranslationSpec).build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();

        TestObjectWrapper expectedWrapper = new TestObjectWrapper();
        expectedWrapper.setWrappedObject(expectedAppObject);

        TestObjectWrapper actualWrapper = testTranslationEngine.convertObjectAsUnsafeClass(expectedAppObject,
                TestObjectWrapper.class);

        assertEquals(expectedWrapper, actualWrapper);

        Object actualAppObject = testTranslationEngine.convertObject(actualWrapper);

        assertEquals(expectedAppObject, actualAppObject);

        // preconditions
        // the contract exception for CoreTranslationError.UNKNOWN_TRANSLATION_SPEC is
        // covered by the test - testGetTranslationSpecForClass

        // the passed in object is null
        ContractException contractException = assertThrows(ContractException.class, () -> {
            testTranslationEngine.convertObjectAsUnsafeClass(null, Object.class);
        });

        assertEquals(CoreTranslationError.NULL_OBJECT_FOR_TRANSLATION, contractException.getErrorType());

        // the passed in parentClassRef is null
        contractException = assertThrows(ContractException.class, () -> {
            testTranslationEngine.convertObjectAsUnsafeClass(expectedAppObject, null);
        });

        assertEquals(CoreTranslationError.NULL_CLASS_REF, contractException.getErrorType());
    }

    @Test
    @UnitTestForCoverage
    public void testGetTranslationSpecForClass() {
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec testComplexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        TestTranslationEngine testTranslationEngine = TestTranslationEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec).addTranslationSpec(testComplexObjectTranslationSpec)
                .build();

        assertEquals(testObjectTranslationSpec, testTranslationEngine.getTranslationSpecForClass(TestAppObject.class));
        assertEquals(testObjectTranslationSpec,
                testTranslationEngine.getTranslationSpecForClass(TestInputObject.class));

        assertNotEquals(testComplexObjectTranslationSpec,
                testTranslationEngine.getTranslationSpecForClass(TestAppObject.class));
        assertNotEquals(testComplexObjectTranslationSpec,
                testTranslationEngine.getTranslationSpecForClass(TestInputObject.class));

        assertEquals(testComplexObjectTranslationSpec,
                testTranslationEngine.getTranslationSpecForClass(TestComplexAppObject.class));
        assertEquals(testComplexObjectTranslationSpec,
                testTranslationEngine.getTranslationSpecForClass(TestComplexInputObject.class));

        assertNotEquals(testObjectTranslationSpec,
                testTranslationEngine.getTranslationSpecForClass(TestComplexAppObject.class));
        assertNotEquals(testObjectTranslationSpec,
                testTranslationEngine.getTranslationSpecForClass(TestComplexInputObject.class));

        // preconditions
        // no Translation Spec exists for the given class
        ContractException contractException = assertThrows(ContractException.class, () -> {
            testTranslationEngine.getTranslationSpecForClass(Object.class);
        });

        assertEquals(CoreTranslationError.UNKNOWN_TRANSLATION_SPEC, contractException.getErrorType());
    }

    @Test
    @UnitTestForCoverage
    public void testGetOrderedTranslators() {

        TranslationEngine.Builder translationEngineBuilder = TestTranslationEngine.builder()
                .addTranslator(TestObjectTranslator.getTranslator())
                .addTranslator(TestComplexObjectTranslator.getTranslator());

        List<Translator> expectedList = new ArrayList<>();
        expectedList.add(TestComplexObjectTranslator.getTranslator());
        expectedList.add(TestObjectTranslator.getTranslator());

        List<Translator> actualList = translationEngineBuilder.getOrderedTranslators();

        assertEquals(expectedList, actualList);

        // preconditions

        // duplicate translator in the graph

        ContractException contractException = assertThrows(ContractException.class, () -> {
            MutableGraph<TranslatorId, Object> mutableGraph = new MutableGraph<>();
            Map<TranslatorId, Translator> translatorMap = new LinkedHashMap<>();
            mutableGraph.addNode(TestObjectTranslatorId.TRANSLATOR_ID);
            translationEngineBuilder.addNodes(mutableGraph, translatorMap);
        });

        assertEquals(CoreTranslationError.DUPLICATE_TRANSLATOR, contractException.getErrorType());

        // missing translator
        contractException = assertThrows(ContractException.class, () -> {
            MutableGraph<TranslatorId, Object> mutableGraph = new MutableGraph<>();
            Map<TranslatorId, Translator> translatorMap = new LinkedHashMap<>();

            // call normally
            translationEngineBuilder.getOrderedTranslators(mutableGraph, translatorMap);
            // remove a mapping
            translatorMap.remove(TestComplexObjectTranslatorId.TRANSLATOR_ID);
            TranslatorId thirdId = new TranslatorId() {
            };
            mutableGraph.addNode(thirdId);
            mutableGraph.addEdge(new Object(), thirdId, TestComplexObjectTranslatorId.TRANSLATOR_ID);
            translationEngineBuilder.checkForMissingTranslators(mutableGraph, translatorMap);
        });

        assertEquals(CoreTranslationError.MISSING_TRANSLATOR, contractException.getErrorType());

        // cyclic graph
        contractException = assertThrows(ContractException.class, () -> {
            MutableGraph<TranslatorId, Object> mutableGraph = new MutableGraph<>();
            Map<TranslatorId, Translator> translatorMap = new LinkedHashMap<>();

            // call normally
            translationEngineBuilder.getOrderedTranslators(mutableGraph, translatorMap);
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
            translationEngineBuilder.checkForCyclicGraph(mutableGraph);
        });

        assertEquals(CoreTranslationError.CIRCULAR_TRANSLATOR_DEPENDENCIES, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TranslationEngine.class, name = "hashCode", args = {})
    public void testHashCode() {
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec testComplexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        TestTranslationEngine testTranslationEngine1 = TestTranslationEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec).addTranslationSpec(testComplexObjectTranslationSpec)
                .build();

        TestTranslationEngine testTranslationEngine2 = TestTranslationEngine.builder()
                .addTranslationSpec(testComplexObjectTranslationSpec).build();

        TestTranslationEngine testTranslationEngine3 = TestTranslationEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec).build();

        TestTranslationEngine testTranslationEngine4 = TestTranslationEngine.builder().build();

        TestTranslationEngine testTranslationEngine5 = TestTranslationEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec).addTranslationSpec(testComplexObjectTranslationSpec)
                .build();

        // exact same, same hash code
        assertEquals(testTranslationEngine1.hashCode(), testTranslationEngine1.hashCode());

        // different translation specs
        assertNotEquals(testTranslationEngine1.hashCode(), testTranslationEngine2.hashCode());
        assertNotEquals(testTranslationEngine1.hashCode(), testTranslationEngine3.hashCode());
        assertNotEquals(testTranslationEngine1.hashCode(), testTranslationEngine4.hashCode());
        assertNotEquals(testTranslationEngine2.hashCode(), testTranslationEngine3.hashCode());
        assertNotEquals(testTranslationEngine2.hashCode(), testTranslationEngine4.hashCode());
        assertNotEquals(testTranslationEngine3.hashCode(), testTranslationEngine4.hashCode());

        // same translation specs
        assertEquals(testTranslationEngine1.hashCode(), testTranslationEngine5.hashCode());
    }

    @Test
    @UnitTestMethod(target = TranslationEngine.class, name = "equals", args = { Object.class })
    public void testEquals() {
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec testComplexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        TestTranslationEngine testTranslationEngine1 = TestTranslationEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec).addTranslationSpec(testComplexObjectTranslationSpec)
                .build();

        TestTranslationEngine testTranslationEngine2 = TestTranslationEngine.builder()
                .addTranslationSpec(testComplexObjectTranslationSpec).build();

        TestTranslationEngine testTranslationEngine3 = TestTranslationEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec).build();

        TestTranslationEngine testTranslationEngine4 = TestTranslationEngine.builder().build();

        TestTranslationEngine testTranslationEngine5 = TestTranslationEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec).addTranslationSpec(testComplexObjectTranslationSpec)
                .build();

        TestObjectTranslationSpec testObjectTranslationSpec2 = new TestObjectTranslationSpec();
        TestObjectTranslationSpec testObjectTranslationSpec3 = new TestObjectTranslationSpec();

        TestTranslationEngine testTranslationEngine6 = TestTranslationEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec2).buildWithoutSpecInit();

        TestTranslationEngine testTranslationEngine7 = TestTranslationEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec3).buildWithoutSpecInit();

        // exact same
        assertEquals(testTranslationEngine1, testTranslationEngine1);

        assertNotEquals(testTranslationEngine1, null);

        assertNotEquals(testTranslationEngine1, new Object());

        // different translation specs
        assertNotEquals(testTranslationEngine1, testTranslationEngine2);
        assertNotEquals(testTranslationEngine1, testTranslationEngine3);
        assertNotEquals(testTranslationEngine1, testTranslationEngine4);
        assertNotEquals(testTranslationEngine2, testTranslationEngine3);
        assertNotEquals(testTranslationEngine2, testTranslationEngine4);
        assertNotEquals(testTranslationEngine3, testTranslationEngine4);

        testObjectTranslationSpec2.init(testTranslationEngine1);
        testObjectTranslationSpec3.init(testTranslationEngine5);
        assertNotEquals(testTranslationEngine6, testTranslationEngine7);

        // init vs not init
        assertNotEquals(testTranslationEngine1, testTranslationEngine6);

        // same translation specs
        assertEquals(testTranslationEngine1, testTranslationEngine5);

        TranslationEngine.Data data = new TranslationEngine.Data();
        assertEquals(data, data);
        assertNotEquals(data, null);
        assertNotEquals(data, new Object());
    }
}
