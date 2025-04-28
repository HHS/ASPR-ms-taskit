package gov.hhs.aspr.ms.taskit.core.translation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.math3.random.RandomGenerator;
import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngine;
import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngineData;
import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngineId;
import gov.hhs.aspr.ms.taskit.core.engine.TaskitError;
import gov.hhs.aspr.ms.taskit.core.testsupport.TestObjectUtil;
import gov.hhs.aspr.ms.taskit.core.testsupport.engine.TestTaskitEngine;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestAppChildObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestAppObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestComplexInputObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestInputChildObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestInputObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestObjectWrapper;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.TestClassPair;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.TestTranslationSpec;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.complexobject.specs.TestComplexObjectTranslationSpec;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.object.specs.TestObjectTranslationSpec;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.testenum.specs.TestEnumTranslationSpec;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;
import gov.hhs.aspr.ms.util.errors.ContractException;
import gov.hhs.aspr.ms.util.random.RandomGeneratorProvider;

public class AT_TranslationSpec {

    @Test
    @UnitTestMethod(target = TranslationSpec.class, name = "init", args = { TaskitEngine.class })
    public void testInit() {
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestTaskitEngine testTaskitEngine = TestTaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec).build();

        assertTrue(testObjectTranslationSpec.isInitialized());

        // preconditions
        // calling init more than once
        ContractException contractException = assertThrows(ContractException.class, () -> {
            testObjectTranslationSpec.init(testTaskitEngine);
        });

        assertEquals(TaskitError.DOUBLE_TRANSLATION_SPEC_INIT, contractException.getErrorType());

        // given taskit engine type does not match type parameter
        contractException = assertThrows(ContractException.class, () -> {
            TaskitEngine taskitEngine = new TaskitEngine(
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

            new TestObjectTranslationSpec().init(taskitEngine);
        });

        assertEquals(TaskitError.INVALID_TASKIT_ENGINE, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TranslationSpec.class, name = "isInitialized", args = {})
    public void testIsInitialized() {
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestTaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec).build();

        assertTrue(testObjectTranslationSpec.isInitialized());

        assertFalse(new TestObjectTranslationSpec().isInitialized());
    }

    @Test
    @UnitTestMethod(target = TranslationSpec.class, name = "translate", args = { Object.class })
    public void testTranslate() {
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec complexObjectTranslationSpec = new TestComplexObjectTranslationSpec();
        TestEnumTranslationSpec testEnumTranslationSpec = new TestEnumTranslationSpec();
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

        TestTranslationSpec<Object, TestObjectWrapper> wrapperTranslationSpec2 = new TestTranslationSpec<Object, TestObjectWrapper>() {

            @Override
            protected TestObjectWrapper translateInputObject(Object inputObject) {
                TestObjectWrapper testObjectWrapper = new TestObjectWrapper();
                testObjectWrapper.setWrappedObject(inputObject);
                return testObjectWrapper;
            }

            @Override
            protected Object translateAppObject(TestObjectWrapper appObject) {
                return appObject.getWrappedObject();
            }

            @Override
            public Class<TestObjectWrapper> getAppObjectClass() {
                return TestObjectWrapper.class;
            }

            @Override
            public Class<Object> getInputObjectClass() {
                return Object.class;
            }
        };

        TestTaskitEngine.builder()
                .addTranslationSpec(testObjectTranslationSpec)
                .addTranslationSpec(complexObjectTranslationSpec)
                .addTranslationSpec(testEnumTranslationSpec)
                .addTranslationSpec(wrapperTranslationSpec)
                .addTranslationSpec(wrapperTranslationSpec2)
                .build();

        TestAppObject expectedAppObject = TestObjectUtil.generateTestAppObject();
        TestInputObject expectedInputObject = TestObjectUtil.getInputFromApp(expectedAppObject);

        // shouldTranslateAsApp
        TestInputObject actualInputObject = testObjectTranslationSpec.translate(expectedAppObject);
        assertEquals(expectedInputObject, actualInputObject);

        // shouldTranslateAsIn
        TestAppObject actualAppObject = testObjectTranslationSpec.translate(expectedInputObject);
        assertEquals(expectedAppObject, actualAppObject);

        TestAppChildObject expectedAppChildObject = TestObjectUtil.getChildAppFromApp(expectedAppObject);
        TestInputChildObject expectedInputChildObject = TestObjectUtil.getChildInputFromInput(expectedInputObject);

        // shouldTranslateAsApp
        TestInputObject actualInputChildObject = testObjectTranslationSpec.translate(expectedAppChildObject);
        assertEquals(expectedInputChildObject, TestObjectUtil.getChildInputFromInput(actualInputChildObject));

        // shouldTranslateAsIn
        TestAppObject actualAppChildObject = testObjectTranslationSpec.translate(expectedInputChildObject);
        assertEquals(expectedAppChildObject, TestObjectUtil.getChildAppFromApp(actualAppChildObject));

        // for code coverage
        Object wrappedObj = wrapperTranslationSpec.translate(new TestObjectWrapper());
        assertNull(wrappedObj);

        wrappedObj = wrapperTranslationSpec2.translate(new TestObjectWrapper());
        assertNull(wrappedObj);

        // precondition
        // TranslationSpec not initialized
        ContractException contractException = assertThrows(ContractException.class, () -> {
            TestObjectTranslationSpec testObjectTranslationSpec2 = new TestObjectTranslationSpec();
            testObjectTranslationSpec2.translate(new TestAppObject());
        });

        assertEquals(TaskitError.UNINITIALIZED_TRANSLATION_SPEC, contractException.getErrorType());

        // object is null
        contractException = assertThrows(ContractException.class, () -> {
            testObjectTranslationSpec.translate(null);
        });

        assertEquals(TaskitError.NULL_OBJECT_FOR_TRANSLATION, contractException.getErrorType());

        // unknown object
        contractException = assertThrows(ContractException.class, () -> {
            TestObjectTranslationSpec testObjectTranslationSpec2 = new TestObjectTranslationSpec();
            TestTaskitEngine.builder()
                    .addTranslationSpec(testObjectTranslationSpec2)
                    .build();

            testObjectTranslationSpec2.translate(new Object());
        });

        assertEquals(TaskitError.UNKNOWN_OBJECT, contractException.getErrorType());

        // unknown object 2
        contractException = assertThrows(ContractException.class, () -> {
            TestObjectTranslationSpec testObjectTranslationSpec2 = new TestObjectTranslationSpec();
            TestTaskitEngine.builder()
                    .addTranslationSpec(testObjectTranslationSpec2)
                    .build();

            testObjectTranslationSpec2.translate(new TestComplexInputObject());
        });

        assertEquals(TaskitError.UNKNOWN_OBJECT, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TranslationSpec.class, name = "getTranslationSpecClassMapping", args = {})
    public void testGetTranslationSpecClassMapping() {
        Map<Class<?>, ITranslationSpec> expectedMapping = new LinkedHashMap<>();
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();

        expectedMapping.put(testObjectTranslationSpec.getAppObjectClass(), testObjectTranslationSpec);
        expectedMapping.put(testObjectTranslationSpec.getInputObjectClass(), testObjectTranslationSpec);

        assertEquals(expectedMapping, testObjectTranslationSpec.getTranslationSpecClassMapping());
    }

    @Test
    @UnitTestMethod(target = TranslationSpec.class, name = "hashCode", args = {})
    public void testHashCode() {
		RandomGenerator randomGenerator = RandomGeneratorProvider.getRandomGenerator(2655488674438883354L);

		// equal objects have equal hash codes
		for (int i = 0; i < 30; i++) {
			long seed = randomGenerator.nextLong();

            TranslationSpec<?, ?, TestTaskitEngine> translationSpec1 = getRandomTranslationSpec(seed);
            TranslationSpec<?, ?, TestTaskitEngine> translationSpec2 = getRandomTranslationSpec(seed);

			assertEquals(translationSpec1, translationSpec2);
			assertEquals(translationSpec1.hashCode(), translationSpec2.hashCode());

            // initialize both translationSpecs and show they are still equal with equal hash codes
            TestTaskitEngine testTaskitEngine = TestTaskitEngine.builder()
                    .addTranslationSpec(new TestComplexObjectTranslationSpec()).build();

            translationSpec1.init(testTaskitEngine);
            translationSpec2.init(testTaskitEngine);
            assertEquals(translationSpec1, translationSpec2);
			assertEquals(translationSpec1.hashCode(), translationSpec2.hashCode());
		}

		// hash codes are reasonably distributed
		Set<Integer> hashCodes = new LinkedHashSet<>();
		for (TestClassPair testClassPair : TestClassPair.values()) {
            TranslationSpec<?, ?, TestTaskitEngine> translationSpec = testClassPair.createTranslationSpec();
			hashCodes.add(translationSpec.hashCode());
		}

		assertEquals(100, hashCodes.size());
    }

    @Test
    @UnitTestMethod(target = TranslationSpec.class, name = "equals", args = { Object.class })
    public void testEquals() {
		RandomGenerator randomGenerator = RandomGeneratorProvider.getRandomGenerator(8999922418377306870L);

		// never equal to another type
		for (int i = 0; i < 30; i++) {
            TranslationSpec<?, ?, TestTaskitEngine> translationSpec = getRandomTranslationSpec(randomGenerator.nextLong());
			assertFalse(translationSpec.equals(new Object()));
		}

		// never equal to null
		for (int i = 0; i < 30; i++) {
            TranslationSpec<?, ?, TestTaskitEngine> translationSpec = getRandomTranslationSpec(randomGenerator.nextLong());
			assertFalse(translationSpec.equals(null));
		}

		// reflexive
		for (int i = 0; i < 30; i++) {
            TranslationSpec<?, ?, TestTaskitEngine> translationSpec = getRandomTranslationSpec(randomGenerator.nextLong());
			assertTrue(translationSpec.equals(translationSpec));
		}

		// symmetric, transitive, consistent
		for (int i = 0; i < 30; i++) {
			long seed = randomGenerator.nextLong();
            TranslationSpec<?, ?, TestTaskitEngine> translationSpec1 = getRandomTranslationSpec(seed);
            TranslationSpec<?, ?, TestTaskitEngine> translationSpec2 = getRandomTranslationSpec(seed);

			assertFalse(translationSpec1 == translationSpec2);
			for (int j = 0; j < 10; j++) {
				assertTrue(translationSpec1.equals(translationSpec2));
				assertTrue(translationSpec2.equals(translationSpec1));
			}

            // initialize both translationSpecs and show they are still equal
            TestTaskitEngine testTaskitEngine = TestTaskitEngine.builder()
                    .addTranslationSpec(new TestComplexObjectTranslationSpec()).build();

            translationSpec1.init(testTaskitEngine);
            translationSpec2.init(testTaskitEngine);
			for (int j = 0; j < 10; j++) {
				assertTrue(translationSpec1.equals(translationSpec2));
				assertTrue(translationSpec2.equals(translationSpec1));
			}
		}

		// different inputs yield unequal translationSpecs
		Set<TranslationSpec<?, ?, TestTaskitEngine>> set = new LinkedHashSet<>();
        for (TestClassPair testClassPair : TestClassPair.values()) {
            TranslationSpec<?, ?, TestTaskitEngine> translationSpec = testClassPair.createTranslationSpec();
			set.add(translationSpec);
		}

		assertEquals(100, set.size());
    }

    private TranslationSpec<?, ?, TestTaskitEngine> getRandomTranslationSpec(long seed) {
        RandomGenerator randomGenerator = RandomGeneratorProvider.getRandomGenerator(seed);

        TestClassPair testClassPair = TestClassPair.getRandomTestClassPair(randomGenerator);
        return testClassPair.createTranslationSpec();
    }
}
