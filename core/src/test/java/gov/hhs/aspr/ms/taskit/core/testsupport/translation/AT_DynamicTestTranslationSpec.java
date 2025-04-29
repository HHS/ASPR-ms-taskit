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

import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;
import gov.hhs.aspr.ms.util.random.RandomGeneratorProvider;
import gov.hhs.aspr.ms.util.wrappers.MutableInteger;

public class AT_DynamicTestTranslationSpec {

    @Test
    @UnitTestMethod(target = DynamicTestTranslationSpec.class, name = "getTypeI", args = {})
    public void testGetTypeI() {
        for (DynamicTestTranslationSpec translationSpec : DynamicTestTranslationSpec.values()) {
            assertNotNull(translationSpec.getTypeI());
        }
    }

    @Test
    @UnitTestMethod(target = DynamicTestTranslationSpec.class, name = "getTypeA", args = {})
    public void testGetTypeA() {
        for (DynamicTestTranslationSpec translationSpec : DynamicTestTranslationSpec.values()) {
            assertNotNull(translationSpec.getTypeA());
        }
    }

    @Test
    @UnitTestMethod(target = DynamicTestTranslationSpec.class, name = "getRandomTranslationSpec", args = {
            RandomGenerator.class })
    public void testGetRandomTranslationSpec() {
        RandomGenerator randomGenerator = RandomGeneratorProvider.getRandomGenerator(242770195043563036L);
        Map<DynamicTestTranslationSpec, MutableInteger> idCounter = new LinkedHashMap<>();
        Set<DynamicTestTranslationSpec> setOfDynamicTestTranslationSpecs = new LinkedHashSet<>();

        for (DynamicTestTranslationSpec translationSpec : DynamicTestTranslationSpec.values()) {
            idCounter.put(translationSpec, new MutableInteger());
        }

        // show that generated values are reasonably unique
        int numIterations = 10000;
        for (int i = 0; i < numIterations; i++) {
            DynamicTestTranslationSpec translationSpec = DynamicTestTranslationSpec
                    .getRandomTranslationSpec(randomGenerator);
            setOfDynamicTestTranslationSpecs.add(translationSpec);
            idCounter.get(translationSpec).increment();
        }

        int acceptableLowerLimit = 75;
        int acceptableUpperLimit = 125;
        for (DynamicTestTranslationSpec translationSpec : idCounter.keySet()) {
            assertTrue(idCounter.get(translationSpec).getValue() >= acceptableLowerLimit
                    && idCounter.get(translationSpec).getValue() <= acceptableUpperLimit);
        }

        assertEquals(idCounter.values().stream().mapToInt(a -> a.getValue()).sum(), numIterations);
        assertEquals(setOfDynamicTestTranslationSpecs.size(), DynamicTestTranslationSpec.values().length);
    }

    @Test
    @UnitTestMethod(target = DynamicTestTranslationSpec.class, name = "getTranslationSpecs", args = {})
    public void testGetTranslationSpecs() {
        assertEquals(Arrays.asList(DynamicTestTranslationSpec.values()),
                DynamicTestTranslationSpec.getTranslationSpecs());
    }

    @Test
    @UnitTestMethod(target = DynamicTestTranslationSpec.class, name = "getShuffledTranslationSpecs", args = {
            RandomGenerator.class })
    public void testGetShuffledTranslationSpecs() {
        RandomGenerator randomGenerator = RandomGeneratorProvider.getRandomGenerator(503706833466130759L);
        List<DynamicTestTranslationSpec> dynamicTestTranslationSpecs = DynamicTestTranslationSpec.getTranslationSpecs();
        Collections.shuffle(dynamicTestTranslationSpecs, new Random(randomGenerator.nextLong()));

        assertEquals(dynamicTestTranslationSpecs, DynamicTestTranslationSpec
                .getShuffledTranslationSpecs(RandomGeneratorProvider.getRandomGenerator(503706833466130759L)));
    }

    @Test
    @UnitTestMethod(target = DynamicTestTranslationSpec.class, name = "getTranslationSpec", args = {})
    public void testGetTranslationSpec() {
        for (DynamicTestTranslationSpec translationSpec : DynamicTestTranslationSpec.values()) {

            TestTranslationSpec<?, ?> spec = translationSpec.getTranslationSpec();

            assertNotNull(spec);
            assertEquals(translationSpec.getTypeI(), spec.getInputObjectClass());
            assertEquals(translationSpec.getTypeA(), spec.getAppObjectClass());
        }
    }
}
