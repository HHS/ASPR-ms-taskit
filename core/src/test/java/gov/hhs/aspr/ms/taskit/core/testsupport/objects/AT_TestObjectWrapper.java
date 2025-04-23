package gov.hhs.aspr.ms.taskit.core.testsupport.objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.math3.random.RandomGenerator;
import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.taskit.core.testsupport.TestObjectUtil;
import gov.hhs.aspr.ms.util.annotations.UnitTestConstructor;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;
import gov.hhs.aspr.ms.util.random.RandomGeneratorProvider;

public class AT_TestObjectWrapper {

    @Test
    @UnitTestConstructor(target = TestObjectWrapper.class, args = {})
    public void testConstructor() {
        assertNotNull(new TestObjectWrapper());
    }

    @Test
    @UnitTestMethod(target = TestObjectWrapper.class, name = "setWrappedObject", args = { Object.class })
    public void testSetWrappedObject() {
        TestAppObject testAppObject = TestObjectUtil.generateTestAppObject();
        TestObjectWrapper testObjectWrapper = new TestObjectWrapper();

        testObjectWrapper.setWrappedObject(testAppObject);

        assertEquals(testAppObject, testObjectWrapper.getWrappedObject());

        // preconditions
        // cannot set the wrapped object to the TestObjectWrapper instance
        assertThrows(RuntimeException.class, () -> {
            testObjectWrapper.setWrappedObject(testObjectWrapper);
        });

        // cannot set the wrapped object to another instance of TestObjectWrapper
        assertThrows(RuntimeException.class, () -> {
            testObjectWrapper.setWrappedObject(new TestObjectWrapper());
        });
    }

    @Test
    @UnitTestMethod(target = TestObjectWrapper.class, name = "getWrappedObject", args = {})
    public void testGetWrappedObject() {
        TestInputObject testInputObject = TestObjectUtil.generateTestInputObject();
        TestObjectWrapper testObjectWrapper = new TestObjectWrapper();

        testObjectWrapper.setWrappedObject(testInputObject);

        assertEquals(testInputObject, testObjectWrapper.getWrappedObject());
    }

    @Test
    @UnitTestMethod(target = TestObjectWrapper.class, name = "hashCode", args = {})
    public void testHashCode() {
		RandomGenerator randomGenerator = RandomGeneratorProvider.getRandomGenerator(2743491444433183354L);

		// equal objects have equal hash codes
		for (int i = 0; i < 30; i++) {
			long seed = randomGenerator.nextLong();
			TestObjectWrapper testObjectWrapper1 = getRandomTestObjectWrapper(seed);
			TestObjectWrapper testObjectWrapper2 = getRandomTestObjectWrapper(seed);

			assertEquals(testObjectWrapper1, testObjectWrapper2);
			assertEquals(testObjectWrapper1.hashCode(), testObjectWrapper2.hashCode());
		}

		// hash codes are reasonably distributed
		Set<Integer> hashCodes = new LinkedHashSet<>();
		for (int i = 0; i < 100; i++) {
			TestObjectWrapper testObjectWrapper = getRandomTestObjectWrapper(randomGenerator.nextLong());
			hashCodes.add(testObjectWrapper.hashCode());
		}

		assertEquals(100, hashCodes.size());
    }

    @Test
    @UnitTestMethod(target = TestObjectWrapper.class, name = "equals", args = { Object.class })
    public void testEquals() {
		RandomGenerator randomGenerator = RandomGeneratorProvider.getRandomGenerator(8980322778377306870L);

		// never equal to another type
		for (int i = 0; i < 30; i++) {
			TestObjectWrapper testObjectWrapper = getRandomTestObjectWrapper(randomGenerator.nextLong());
			assertFalse(testObjectWrapper.equals(new Object()));
		}

		// never equal to null
		for (int i = 0; i < 30; i++) {
			TestObjectWrapper testObjectWrapper = getRandomTestObjectWrapper(randomGenerator.nextLong());
			assertFalse(testObjectWrapper.equals(null));
		}

		// reflexive
		for (int i = 0; i < 30; i++) {
			TestObjectWrapper testObjectWrapper = getRandomTestObjectWrapper(randomGenerator.nextLong());
			assertTrue(testObjectWrapper.equals(testObjectWrapper));
		}

		// symmetric, transitive, consistent
		for (int i = 0; i < 30; i++) {
			long seed = randomGenerator.nextLong();
			TestObjectWrapper testObjectWrapper1 = getRandomTestObjectWrapper(seed);
			TestObjectWrapper testObjectWrapper2 = getRandomTestObjectWrapper(seed);
			assertFalse(testObjectWrapper1 == testObjectWrapper2);
			for (int j = 0; j < 10; j++) {
				assertTrue(testObjectWrapper1.equals(testObjectWrapper2));
				assertTrue(testObjectWrapper2.equals(testObjectWrapper1));
			}
		}

		// different inputs yield unequal testObjectWrappers
		Set<TestObjectWrapper> set = new LinkedHashSet<>();
		for (int i = 0; i < 100; i++) {
			TestObjectWrapper testObjectWrapper = getRandomTestObjectWrapper(randomGenerator.nextLong());
			set.add(testObjectWrapper);
		}
		assertEquals(100, set.size());
    }

    private TestObjectWrapper getRandomTestObjectWrapper(long seed) {
        RandomGenerator randomGenerator = RandomGeneratorProvider.getRandomGenerator(seed);
        TestObjectWrapper testObjectWrapper = new TestObjectWrapper();
        testObjectWrapper.setWrappedObject(randomGenerator.nextInt());
        return testObjectWrapper;
    }
}
