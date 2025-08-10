package gov.hhs.aspr.ms.taskit.core.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.math3.random.RandomGenerator;
import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.taskit.core.testsupport.engine.TestTaskitEngine;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.DynamicTestTranslationSpec;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.bad.BadTranslationSpecEmptyMap;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.bad.BadTranslationSpecNullMap;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.complexobject.TestComplexObjectTranslatorId;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.complexobject.specs.TestComplexObjectTranslationSpec;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.object.TestObjectTranslator;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.object.specs.TestObjectTranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.ITranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.Translator;
import gov.hhs.aspr.ms.taskit.core.translation.TranslatorContext;
import gov.hhs.aspr.ms.taskit.core.translation.TranslatorId;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;
import gov.hhs.aspr.ms.util.errors.ContractException;
import gov.hhs.aspr.ms.util.random.RandomGeneratorProvider;

public class AT_TaskitEngineData {
    @Test
    @UnitTestMethod(target = TaskitEngineData.Builder.class, name = "build", args = {})
    public void testBuild() {
        // preconditions:
        // uninitialized translator
        ContractException contractException = assertThrows(ContractException.class, () -> {
            TaskitEngineData.builder().addTranslator(TestObjectTranslator.getTranslator()).build();
        });

        assertEquals(TaskitError.UNINITIALIZED_TRANSLATORS, contractException.getErrorType());
        // duplicate translator
        contractException = assertThrows(ContractException.class, () -> {
            TaskitEngineData.builder()
                    .addTranslator(TestObjectTranslator.getTranslator())
                    .addTranslator(TestObjectTranslator.getTranslator())
                    .checkTranslatorGraph(false);
        });

        assertEquals(TaskitError.DUPLICATE_TRANSLATOR, contractException.getErrorType());

        // missing translator
        contractException = assertThrows(ContractException.class, () -> {
            TaskitEngineData.builder()
                    .addTranslator(TestObjectTranslator.getTranslator())
                    .addTranslator(Translator.builder()
                            .setTranslatorId(new TranslatorId() {
                            })
                            .addDependency(TestComplexObjectTranslatorId.TRANSLATOR_ID)
                            .setInitializer((c) -> {
                            }).build())
                    .checkTranslatorGraph(false);
        });

        assertEquals(TaskitError.MISSING_TRANSLATOR, contractException.getErrorType());

        // circular translator dependencies
        contractException = assertThrows(ContractException.class, () -> {
            TranslatorId translatorId1 = new TranslatorId() {
            };
            TranslatorId translatorId2 = new TranslatorId() {
            };
            Translator translator1 = Translator.builder()
                    .setTranslatorId(translatorId1)
                    .addDependency(translatorId2)
                    .setInitializer((c) -> {
                    })
                    .build();
            Translator translator2 = Translator.builder()
                    .setTranslatorId(translatorId2)
                    .addDependency(translatorId1)
                    .setInitializer((c) -> {
                    })
                    .build();

            TranslatorId translatorId3 = new TranslatorId() {
            };
            TranslatorId translatorId4 = new TranslatorId() {
            };
            Translator translator3 = Translator.builder()
                    .setTranslatorId(translatorId3)
                    .addDependency(translatorId4)
                    .setInitializer((c) -> {
                    })
                    .build();
            Translator translator4 = Translator.builder()
                    .setTranslatorId(translatorId4)
                    .addDependency(translatorId3)
                    .setInitializer((c) -> {
                    })
                    .build();

            TaskitEngineData.builder()
                    .addTranslator(translator1)
                    .addTranslator(translator2)
                    .addTranslator(translator3)
                    .addTranslator(translator4)
                    .checkTranslatorGraph(false);
        });

        assertEquals(TaskitError.CIRCULAR_TRANSLATOR_DEPENDENCIES, contractException.getErrorType());

        // no translation specs were added
        contractException = assertThrows(ContractException.class, () -> {
            TaskitEngineData.builder().build();
        });

        assertEquals(TaskitError.NO_TRANSLATION_SPECS, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TaskitEngineData.Builder.class, name = "addTranslationSpec", args = {
            ITranslationSpec.class })
    public void testAddTranslationSpec() {
        TestObjectTranslationSpec testObjectTranslationSpec = new TestObjectTranslationSpec();
        TestComplexObjectTranslationSpec testComplexObjectTranslationSpec = new TestComplexObjectTranslationSpec();

        TaskitEngineData.Builder builder = TaskitEngineData.builder();

        TaskitEngineData taskitEngineData = builder
                .addTranslationSpec(testObjectTranslationSpec)
                .addTranslationSpec(testComplexObjectTranslationSpec).build();

        // show that the translation specs are retrievable by their own app and input
        // classes
        assertEquals(testObjectTranslationSpec,
                taskitEngineData.classToTranslationSpecMap.get(testObjectTranslationSpec.getAppObjectClass()));
        assertEquals(testObjectTranslationSpec,
                taskitEngineData.classToTranslationSpecMap.get(testObjectTranslationSpec.getInputObjectClass()));

        assertEquals(testComplexObjectTranslationSpec,
                taskitEngineData.classToTranslationSpecMap.get(testComplexObjectTranslationSpec.getAppObjectClass()));
        assertEquals(testComplexObjectTranslationSpec,
                taskitEngineData.classToTranslationSpecMap.get(testComplexObjectTranslationSpec.getInputObjectClass()));

        // preconditions
        // translationSpec is null
        ContractException contractException = assertThrows(ContractException.class, () -> {
            builder.addTranslationSpec(null);
        });

        assertEquals(TaskitError.NULL_TRANSLATION_SPEC, contractException.getErrorType());

        // null translationSpecToClassMap
        contractException = assertThrows(ContractException.class, () -> {
            BadTranslationSpecNullMap badTranslationSpecNullMap = new BadTranslationSpecNullMap();
            builder.addTranslationSpec(badTranslationSpecNullMap);
        });

        assertEquals(TaskitError.NULL_TRANSLATION_SPEC_CLASS_MAP,
                contractException.getErrorType());

        // empty translationSpecToClassMap
        contractException = assertThrows(ContractException.class, () -> {
            BadTranslationSpecEmptyMap badTranslationSpecEmptyMap = new BadTranslationSpecEmptyMap();
            builder.addTranslationSpec(badTranslationSpecEmptyMap);
        });

        assertEquals(TaskitError.EMPTY_TRANSLATION_SPEC_CLASS_MAP,
                contractException.getErrorType());

        // if the translation spec has already been added (same, but different
        // instances)
        contractException = assertThrows(ContractException.class, () -> {
            TestObjectTranslationSpec testObjectTranslationSpec1 = new TestObjectTranslationSpec();
            TestObjectTranslationSpec testObjectTranslationSpec2 = new TestObjectTranslationSpec();

            TaskitEngineData.builder()
                    .addTranslationSpec(testObjectTranslationSpec1)
                    .addTranslationSpec(testObjectTranslationSpec2);
        });

        assertEquals(TaskitError.DUPLICATE_TRANSLATION_SPEC, contractException.getErrorType());

        // if the translation spec has already been added (exact same instance)
        contractException = assertThrows(ContractException.class, () -> {
            TestObjectTranslationSpec testObjectTranslationSpec1 = new TestObjectTranslationSpec();

            TaskitEngineData.builder()
                    .addTranslationSpec(testObjectTranslationSpec1)
                    .addTranslationSpec(testObjectTranslationSpec1);
        });

        assertEquals(TaskitError.DUPLICATE_TRANSLATION_SPEC, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TaskitEngineData.Builder.class, name = "addTranslator", args = { Translator.class })
    public void testAddTranslator() {
        Translator translator = TestObjectTranslator.getTranslator();
        TranslatorContext translatorContext = new TranslatorContext(TestTaskitEngine.builder());
        translator.initialize(translatorContext);

        TaskitEngineData.builder().addTranslator(translator);

        // preconditions
        // null translator
        ContractException contractException = assertThrows(ContractException.class, () -> {
            TaskitEngineData.builder().addTranslator(null);
        });

        assertEquals(TaskitError.NULL_TRANSLATOR, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TaskitEngineData.class, name = "builder", args = {})
    public void testBuilder() {
        // nothing to test
    }

    @Test
    @UnitTestMethod(target = TaskitEngineData.class, name = "hashCode", args = {})
    public void testHashCode() {
		RandomGenerator randomGenerator = RandomGeneratorProvider.getRandomGenerator(2658899674638883354L);

		// equal objects have equal hash codes
		for (int i = 0; i < 30; i++) {
			long seed = randomGenerator.nextLong();
			TaskitEngineData taskitEngineData1 = getRandomTaskitEngineData(seed);
			TaskitEngineData taskitEngineData2 = getRandomTaskitEngineData(seed);

			assertEquals(taskitEngineData1, taskitEngineData2);
			assertEquals(taskitEngineData1.hashCode(), taskitEngineData2.hashCode());
		}

		// hash codes are reasonably distributed
		Set<Integer> hashCodes = new LinkedHashSet<>();
		for (int i = 0; i < 100; i++) {
			TaskitEngineData taskitEngineData = getRandomTaskitEngineData(randomGenerator.nextLong());
			hashCodes.add(taskitEngineData.hashCode());
		}

		assertEquals(100, hashCodes.size());
    }

    @Test
    @UnitTestMethod(target = TaskitEngineData.class, name = "equals", args = { Object.class })
    public void testEquals() {
		RandomGenerator randomGenerator = RandomGeneratorProvider.getRandomGenerator(8999757418377306870L);

		// never equal to another type
		for (int i = 0; i < 30; i++) {
			TaskitEngineData taskitEngineData = getRandomTaskitEngineData(randomGenerator.nextLong());
			assertFalse(taskitEngineData.equals(new Object()));
		}

		// never equal to null
		for (int i = 0; i < 30; i++) {
			TaskitEngineData taskitEngineData = getRandomTaskitEngineData(randomGenerator.nextLong());
			assertFalse(taskitEngineData.equals(null));
		}

		// reflexive
		for (int i = 0; i < 30; i++) {
			TaskitEngineData taskitEngineData = getRandomTaskitEngineData(randomGenerator.nextLong());
			assertTrue(taskitEngineData.equals(taskitEngineData));
		}

		// symmetric, transitive, consistent
		for (int i = 0; i < 30; i++) {
			long seed = randomGenerator.nextLong();
			TaskitEngineData taskitEngineData1 = getRandomTaskitEngineData(seed);
			TaskitEngineData taskitEngineData2 = getRandomTaskitEngineData(seed);
			assertFalse(taskitEngineData1 == taskitEngineData2);
			for (int j = 0; j < 10; j++) {
				assertTrue(taskitEngineData1.equals(taskitEngineData2));
				assertTrue(taskitEngineData2.equals(taskitEngineData1));
			}
		}

		// different inputs yield unequal taskitEngineDatas
		Set<TaskitEngineData> set = new LinkedHashSet<>();
		for (int i = 0; i < 100; i++) {
			TaskitEngineData taskitEngineData = getRandomTaskitEngineData(randomGenerator.nextLong());
			set.add(taskitEngineData);
		}
		assertEquals(100, set.size());
    }



    private TaskitEngineData getRandomTaskitEngineData(long seed) {
        RandomGenerator randomGenerator = RandomGeneratorProvider.getRandomGenerator(seed);
		
		TaskitEngineData.Builder builder = TaskitEngineData.builder();

        List<DynamicTestTranslationSpec> shuffledTranslationSpecs = DynamicTestTranslationSpec.getShuffledTranslationSpecs(randomGenerator);

        int n = randomGenerator.nextInt(10) + 1;
		for (int i = 0; i < n; i++) {
            DynamicTestTranslationSpec translationSpec = shuffledTranslationSpecs.get(i);
            builder.addTranslationSpec(translationSpec.getTranslationSpec());
        }

        return builder.build();
    }
}
