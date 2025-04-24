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
import java.util.List;
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
		for (int i = 0; i < 100; i++) {
			TranslationSpec<?, ?, TestTaskitEngine> translationSpec = getRandomTranslationSpec(randomGenerator.nextLong());
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
		for (int i = 0; i < 100; i++) {
			TranslationSpec<?, ?, TestTaskitEngine> translationSpec = getRandomTranslationSpec(randomGenerator.nextLong());
			set.add(translationSpec);
		}
		assertEquals(100, set.size());
    }

    private static final List<Class<?>> TYPES = List.of(T1.class, T2.class, T3.class, T4.class,
            T5.class, T6.class, T7.class, T8.class, T9.class, T10.class,
            T11.class, T12.class, T13.class, T14.class, T15.class, T16.class,
            T17.class, T18.class, T19.class, T20.class, T21.class, T22.class,
            T23.class, T24.class, T25.class, T26.class, T27.class, T28.class,
            T29.class, T30.class, T31.class, T32.class, T33.class, T34.class,
            T35.class, T36.class, T37.class, T38.class, T39.class, T40.class,
            T41.class, T42.class, T43.class, T44.class, T45.class, T46.class,
            T47.class, T48.class, T49.class, T50.class, TestAppObject.class,
            TestInputObject.class, TestAppChildObject.class, TestInputChildObject.class);

    private static final class DynamicTranslationSpec<I, A> extends TranslationSpec<I, A, TestTaskitEngine> {
        private final Class<I> typeI;
        private final Class<A> typeA;

        public DynamicTranslationSpec(Class<I> typeI, Class<A> typeA) {
            super(TestTaskitEngine.class);
            this.typeI = typeI;
            this.typeA = typeA;
        }

        @Override
        protected A translateInputObject(I inputObject) {
            throw new UnsupportedOperationException("Unimplemented method 'translateInputObject'");
        }

        @Override
        protected I translateAppObject(A appObject) {
            throw new UnsupportedOperationException("Unimplemented method 'translateAppObject'");
        }

        @Override
        public Class<A> getAppObjectClass() {
            return typeA;
        }

        @Override
        public Class<I> getInputObjectClass() {
            return typeI;
        }
    }

    private TranslationSpec<?, ?, TestTaskitEngine> getRandomTranslationSpec(long seed) {
        RandomGenerator randomGenerator = RandomGeneratorProvider.getRandomGenerator(seed);
        Class<?> t1 = TYPES.get(randomGenerator.nextInt(TYPES.size()));
        Class<?> t2 = TYPES.get(randomGenerator.nextInt(TYPES.size()));
        return new DynamicTranslationSpec<>(t1, t2);
    }

    private static class T1 {}
    private static class T2 {}
    private static class T3 {}
    private static class T4 {}
    private static class T5 {}
    private static class T6 {}
    private static class T7 {}
    private static class T8 {}
    private static class T9 {}
    private static class T10 {}
    private static class T11 {}
    private static class T12 {}
    private static class T13 {}
    private static class T14 {}
    private static class T15 {}
    private static class T16 {}
    private static class T17 {}
    private static class T18 {}
    private static class T19 {}
    private static class T20 {}
    private static class T21 {}
    private static class T22 {}
    private static class T23 {}
    private static class T24 {}
    private static class T25 {}
    private static class T26 {}
    private static class T27 {}
    private static class T28 {}
    private static class T29 {}
    private static class T30 {}
    private static class T31 {}
    private static class T32 {}
    private static class T33 {}
    private static class T34 {}
    private static class T35 {}
    private static class T36 {}
    private static class T37 {}
    private static class T38 {}
    private static class T39 {}
    private static class T40 {}
    private static class T41 {}
    private static class T42 {}
    private static class T43 {}
    private static class T44 {}
    private static class T45 {}
    private static class T46 {}
    private static class T47 {}
    private static class T48 {}
    private static class T49 {}
    private static class T50 {}
}
