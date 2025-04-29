package gov.hhs.aspr.ms.taskit.core.translation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

import org.apache.commons.math3.random.RandomGenerator;
import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.taskit.core.engine.TaskitError;
import gov.hhs.aspr.ms.taskit.core.testsupport.engine.TestTaskitEngine;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.complexobject.TestComplexObjectTranslatorId;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.object.TestObjectTranslatorId;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.object.specs.TestObjectTranslationSpec;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;
import gov.hhs.aspr.ms.util.errors.ContractException;
import gov.hhs.aspr.ms.util.random.RandomGeneratorProvider;
import gov.hhs.aspr.ms.util.wrappers.MutableBoolean;

public class AT_Translator {

    @Test
    @UnitTestMethod(target = Translator.class, name = "getTranslatorId", args = {})
    public void testGetTranslatorId() {
        TranslatorId expectedTranslatorId = new TranslatorId() {
        };
        Translator testTranslator = Translator.builder().setInitializer((translatorContext) -> {
        }).setTranslatorId(expectedTranslatorId).build();

        assertEquals(expectedTranslatorId, testTranslator.getTranslatorId());
    }

    @Test
    @UnitTestMethod(target = Translator.class, name = "getTranslatorDependencies", args = {})
    public void testGetTranslatorDependencies() {
        TranslatorId expectedTranslatorId = new TranslatorId() {
        };
        Translator testTranslator = Translator.builder().setInitializer((translatorContext) -> {
        }).setTranslatorId(expectedTranslatorId).addDependency(TestObjectTranslatorId.TRANSLATOR_ID)
                .addDependency(TestComplexObjectTranslatorId.TRANSLATOR_ID).build();

        Set<TranslatorId> expectedDependencies = new LinkedHashSet<>();
        expectedDependencies.add(TestObjectTranslatorId.TRANSLATOR_ID);
        expectedDependencies.add(TestComplexObjectTranslatorId.TRANSLATOR_ID);

        Set<TranslatorId> actualDependencies = testTranslator.getTranslatorDependencies();

        assertEquals(expectedDependencies, actualDependencies);
    }

    @Test
    @UnitTestMethod(target = Translator.class, name = "initialize", args = { TranslatorContext.class })
    public void testInitialize() {
        TranslatorId expectedTranslatorId = new TranslatorId() {
        };

        MutableBoolean initBool = new MutableBoolean(false);

        Translator testTranslator = Translator.builder()
                .setInitializer((c) -> {
                    initBool.setValue(true);
                })
                .setTranslatorId(expectedTranslatorId)
                .build();

        testTranslator.initialize(new TranslatorContext(TestTaskitEngine.builder()
                .addTranslationSpec(new TestObjectTranslationSpec())));

        assertTrue(initBool.getValue());
        assertTrue(testTranslator.isInitialized());
    }

    @Test
    @UnitTestMethod(target = Translator.class, name = "isInitialized", args = {})
    public void testIsInitialized() {
        TranslatorId expectedTranslatorId = new TranslatorId() {
        };

        Translator testTranslator = Translator.builder()
                .setInitializer((c) -> {
                })
                .setTranslatorId(expectedTranslatorId)
                .build();

        assertFalse(testTranslator.isInitialized());

        // initialize
        testTranslator.initialize(new TranslatorContext(TestTaskitEngine.builder()
                .addTranslationSpec(new TestObjectTranslationSpec())));

        assertTrue(testTranslator.isInitialized());
    }

    private static enum TestTranslatorId implements TranslatorId {
		TRANSLATOR_ID_1, TRANSLATOR_ID_2, TRANSLATOR_ID_3, TRANSLATOR_ID_4, TRANSLATOR_ID_5, TRANSLATOR_ID_6, 
        TRANSLATOR_ID_7, TRANSLATOR_ID_8, TRANSLATOR_ID_9, TRANSLATOR_ID_10, TRANSLATOR_ID_11, TRANSLATOR_ID_12;

		private static TestTranslatorId getRandomTranslatorId(RandomGenerator randomGenerator) {
			int index = randomGenerator.nextInt(TestTranslatorId.values().length);
			return TestTranslatorId.values()[index];
		}

		private static Set<TestTranslatorId> getRandomTranslatorIds(RandomGenerator randomGenerator) {
			Set<TestTranslatorId> result = new LinkedHashSet<>();
			for (TestTranslatorId testTranslatorId : TestTranslatorId.values()) {
				if (randomGenerator.nextBoolean()) {
					result.add(testTranslatorId);
				}
			}
			return result;
		}
	}

    private Translator getRandomTranslator(long seed) {
        RandomGenerator randomGenerator = RandomGeneratorProvider.getRandomGenerator(seed);

        Translator.Builder builder = Translator.builder();

        TestTranslatorId translatorId = TestTranslatorId.getRandomTranslatorId(randomGenerator);
		builder.setTranslatorId(translatorId);

		Set<TestTranslatorId> randomTestTranslatorIds = TestTranslatorId.getRandomTranslatorIds(randomGenerator);
		for (TestTranslatorId testTranslatorId : randomTestTranslatorIds) {
			if (testTranslatorId != translatorId) {
				builder.addDependency(testTranslatorId);
			}
		}

        builder.setInitializer((c) -> {});

        return builder.build();
    }

    @Test
    @UnitTestMethod(target = Translator.class, name = "hashCode", args = {})
    public void testHashCode() {
		RandomGenerator randomGenerator = RandomGeneratorProvider.getRandomGenerator(1717890677700007680L);

		// equal objects have equal hash codes
		for (int i = 0; i < 30; i++) {
			long seed = randomGenerator.nextLong();
			Translator translator1 = getRandomTranslator(seed);
			Translator translator2 = getRandomTranslator(seed);

			assertEquals(translator1, translator2);
			assertEquals(translator1.hashCode(), translator2.hashCode());
		}

		// hash codes are reasonably distributed
		Set<Integer> hashCodes = new LinkedHashSet<>();
		for (int i = 0; i < 100; i++) {
			Translator translator = getRandomTranslator(randomGenerator.nextLong());
			hashCodes.add(translator.hashCode());
		}

		assertEquals(100, hashCodes.size());
    }

    @Test
    @UnitTestMethod(target = Translator.class, name = "equals", args = { Object.class })
    public void testEquals() {
		RandomGenerator randomGenerator = RandomGeneratorProvider.getRandomGenerator(8964621488877306870L);

		// never equal to another type
		for (int i = 0; i < 30; i++) {
			Translator translator = getRandomTranslator(randomGenerator.nextLong());
			assertFalse(translator.equals(new Object()));
		}

		// never equal to null
		for (int i = 0; i < 30; i++) {
			Translator translator = getRandomTranslator(randomGenerator.nextLong());
			assertFalse(translator.equals(null));
		}

		// reflexive
		for (int i = 0; i < 30; i++) {
			Translator translator = getRandomTranslator(randomGenerator.nextLong());
			assertTrue(translator.equals(translator));
		}

		// symmetric, transitive, consistent
		for (int i = 0; i < 30; i++) {
			long seed = randomGenerator.nextLong();
			Translator translator1 = getRandomTranslator(seed);
			Translator translator2 = getRandomTranslator(seed);
			assertFalse(translator1 == translator2);
			for (int j = 0; j < 10; j++) {
				assertTrue(translator1.equals(translator2));
				assertTrue(translator2.equals(translator1));
			}
		}

		// different inputs yield unequal translators
		Set<Translator> set = new LinkedHashSet<>();
		for (int i = 0; i < 100; i++) {
			Translator translator = getRandomTranslator(randomGenerator.nextLong());
			set.add(translator);
		}
		assertEquals(100, set.size());
    }

    @Test
    @UnitTestMethod(target = Translator.class, name = "builder", args = {})
    public void testBuilder() {
        assertNotNull(Translator.builder());
    }

    @Test
    @UnitTestMethod(target = Translator.Builder.class, name = "build", args = {})
    public void testBuild() {
        TranslatorId translatorIdA = new TranslatorId() {
        };
        Translator translatorA = Translator.builder().setInitializer((translatorContext) -> {
        }).setTranslatorId(translatorIdA).build();

        assertNotNull(translatorA);

        // preconditions
        // null initializer
        ContractException contractException = assertThrows(ContractException.class, () -> {
            Translator.builder().setTranslatorId(translatorIdA).build();
        });

        assertEquals(TaskitError.NULL_INIT_CONSUMER, contractException.getErrorType());

        // null translatorId
        contractException = assertThrows(ContractException.class, () -> {
            Translator.builder().setInitializer((translatorContext) -> {
            }).build();
        });

        assertEquals(TaskitError.NULL_TRANSLATOR_ID, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = Translator.Builder.class, name = "setTranslatorId", args = { TranslatorId.class })
    public void testSetTranslatorId() {
        TranslatorId translatorIdA = new TranslatorId() {
        };
        Translator translatorA = Translator.builder().setInitializer((translatorContext) -> {
        }).setTranslatorId(translatorIdA).build();

        assertEquals(translatorIdA, translatorA.getTranslatorId());

        // preconditions
        // null translatorId
        ContractException contractException = assertThrows(ContractException.class, () -> {
            Translator.builder().setTranslatorId(null);
        });

        assertEquals(TaskitError.NULL_TRANSLATOR_ID, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = Translator.Builder.class, name = "setInitializer", args = { Consumer.class })
    public void testSetInitializer() {
        Consumer<TranslatorContext> expectedInitializer = (translatorContext) -> {
        };
        Translator testTranslator = Translator.builder().setInitializer(expectedInitializer)
                .setTranslatorId(new TranslatorId() {
                }).build();

        assertEquals(expectedInitializer, testTranslator.getInitializer());

        // preconditions
        // null initializer
        ContractException contractException = assertThrows(ContractException.class, () -> {
            Translator.builder().setInitializer(null);
        });

        assertEquals(TaskitError.NULL_INIT_CONSUMER, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = Translator.Builder.class, name = "addDependency", args = { TranslatorId.class })
    public void testAddDependency() {
        TranslatorId expectedTranslatorId = new TranslatorId() {
        };
        Translator testTranslator = Translator.builder().setInitializer((translatorContext) -> {
        }).setTranslatorId(expectedTranslatorId).addDependency(TestObjectTranslatorId.TRANSLATOR_ID)
                .addDependency(TestComplexObjectTranslatorId.TRANSLATOR_ID).build();

        Set<TranslatorId> expectedDependencies = new LinkedHashSet<>();
        expectedDependencies.add(TestObjectTranslatorId.TRANSLATOR_ID);
        expectedDependencies.add(TestComplexObjectTranslatorId.TRANSLATOR_ID);

        Set<TranslatorId> actualDependencies = testTranslator.getTranslatorDependencies();

        assertEquals(expectedDependencies, actualDependencies);

        // preconditions
        // null dependency
        ContractException contractException = assertThrows(ContractException.class, () -> {
            Translator.builder().addDependency(null);
        });

        assertEquals(TaskitError.NULL_DEPENDENCY, contractException.getErrorType());

        // duplicate dependency
        contractException = assertThrows(ContractException.class, () -> {
            Translator.builder().addDependency(TestObjectTranslatorId.TRANSLATOR_ID)
                    .addDependency(TestObjectTranslatorId.TRANSLATOR_ID);
        });

        assertEquals(TaskitError.DUPLICATE_DEPENDENCY, contractException.getErrorType());
    }
}
