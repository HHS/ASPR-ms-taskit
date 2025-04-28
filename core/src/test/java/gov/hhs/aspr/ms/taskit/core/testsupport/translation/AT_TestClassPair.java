package gov.hhs.aspr.ms.taskit.core.testsupport.translation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.apache.commons.math3.random.RandomGenerator;
import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.taskit.core.testsupport.engine.TestTaskitEngine;
import gov.hhs.aspr.ms.taskit.core.translation.TranslationSpec;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;
import gov.hhs.aspr.ms.util.random.RandomGeneratorProvider;
import gov.hhs.aspr.ms.util.wrappers.MutableInteger;

public class AT_TestClassPair {

    @Test
	@UnitTestMethod(target = TestClassPair.class, name = "getTypeI", args = {})
	public void testGetTypeI() {
        for (TestClassPair testClassPair : TestClassPair.values()) {
            assertNotNull(testClassPair.getTypeI());
        }
    }

    @Test
	@UnitTestMethod(target = TestClassPair.class, name = "getTypeA", args = {})
	public void testGetTypeA() {
        for (TestClassPair testClassPair : TestClassPair.values()) {
            assertNotNull(testClassPair.getTypeA());
        }
    }

    @Test
	@UnitTestMethod(target = TestClassPair.class, name = "getRandomTestClassPair", args = { RandomGenerator.class })
	public void testGetRandomTestClassPair() {
        RandomGenerator randomGenerator = RandomGeneratorProvider.getRandomGenerator(242770195043563036L);
		Map<TestClassPair, MutableInteger> idCounter = new LinkedHashMap<>();
		Set<TestClassPair> setOfRandomTestClassPairs = new LinkedHashSet<>();

		for (TestClassPair testClassPair : TestClassPair.values()) {
			idCounter.put(testClassPair, new MutableInteger());
		}

		// show that generated values are reasonably unique
        int numIterations = 10000;
		for (int i = 0; i < numIterations; i++) {
			TestClassPair testClassPair = TestClassPair.getRandomTestClassPair(randomGenerator);
			setOfRandomTestClassPairs.add(testClassPair);
			idCounter.get(testClassPair).increment();
		}

        int acceptableLowerLimit = 75;
        int acceptableUpperLimit = 125;
		for (TestClassPair testClassPair : idCounter.keySet()) {
			assertTrue(idCounter.get(testClassPair).getValue() >= acceptableLowerLimit 
                    && idCounter.get(testClassPair).getValue() <= acceptableUpperLimit);
		}

		assertEquals(idCounter.values().stream().mapToInt(a -> a.getValue()).sum(), numIterations);
		assertEquals(setOfRandomTestClassPairs.size(), TestClassPair.values().length);
    }

    @Test
	@UnitTestMethod(target = TestClassPair.class, name = "getTestClassPairs", args = {})
	public void testGetTestClassPairs() {
        assertEquals(Arrays.asList(TestClassPair.values()), TestClassPair.getTestClassPairs());
    }

    @Test
	@UnitTestMethod(target = TestClassPair.class, name = "getShuffledTestClassPairs", args = { RandomGenerator.class })
	public void testGetShuffledTestClassPairs() {
        RandomGenerator randomGenerator = RandomGeneratorProvider.getRandomGenerator(503706833466130759L);
        List<TestClassPair> testClassPairs = TestClassPair.getTestClassPairs();
        Collections.shuffle(testClassPairs, new Random(randomGenerator.nextLong()));

        assertEquals(testClassPairs, TestClassPair.getShuffledTestClassPairs(RandomGeneratorProvider.getRandomGenerator(503706833466130759L)));
    }

    @Test
	@UnitTestMethod(target = TestClassPair.class, name = "createTranslationSpec", args = {})
	public void testCreateTranslationSpec() {
        for (TestClassPair testClassPair : TestClassPair.values()) {

            TranslationSpec<?, ?, TestTaskitEngine> spec = testClassPair.createTranslationSpec();

            assertNotNull(spec);
            assertEquals(testClassPair.getTypeI(), spec.getInputObjectClass());
            assertEquals(testClassPair.getTypeA(), spec.getAppObjectClass());
        }
    }
}
