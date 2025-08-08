package gov.hhs.aspr.ms.taskit.core.testsupport.objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.LinkedHashSet;
import java.util.Set;

import org.apache.commons.math3.random.RandomGenerator;
import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.taskit.core.testsupport.TestObjectUtil;
import gov.hhs.aspr.ms.util.annotations.UnitTestConstructor;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;
import gov.hhs.aspr.ms.util.random.RandomGeneratorProvider;

public class AT_TestComplexAppObject {

    @Test
    @UnitTestConstructor(target = TestComplexAppObject.class, args = {})
    public void testConstructor() {
        assertNotNull(new TestComplexAppObject());
    }

    @Test
    @UnitTestMethod(target = TestComplexAppObject.class, name = "setNumEntities", args = { int.class })
    public void testSetNumEntities() {
        TestComplexAppObject testComplexAppObject = new TestComplexAppObject();

        testComplexAppObject.setNumEntities(15);

        assertEquals(15, testComplexAppObject.getNumEntities());
    }

    @Test
    @UnitTestMethod(target = TestComplexAppObject.class, name = "getNumEntities", args = {})
    public void testGetNumEntities() {
        TestComplexAppObject testComplexAppObject = new TestComplexAppObject();

        testComplexAppObject.setNumEntities(150);

        assertEquals(150, testComplexAppObject.getNumEntities());
    }

    @Test
    @UnitTestMethod(target = TestComplexAppObject.class, name = "setStartTime", args = { double.class })
    public void testSetStartTime() {
        TestComplexAppObject testComplexAppObject = new TestComplexAppObject();

        testComplexAppObject.setStartTime(0.0);

        assertEquals(0.0, testComplexAppObject.getStartTime());
    }

    @Test
    @UnitTestMethod(target = TestComplexAppObject.class, name = "getStartTime", args = {})
    public void testGetStartTime() {
        TestComplexAppObject testComplexAppObject = new TestComplexAppObject();

        testComplexAppObject.setStartTime(150.0);

        assertEquals(150.0, testComplexAppObject.getStartTime());
    }

    @Test
    @UnitTestMethod(target = TestComplexAppObject.class, name = "setTestString", args = { String.class })
    public void testSetTestString() {
        TestComplexAppObject testComplexAppObject = new TestComplexAppObject();

        testComplexAppObject.setTestString("test");

        assertEquals("test", testComplexAppObject.getTestString());
    }

    @Test
    @UnitTestMethod(target = TestComplexAppObject.class, name = "getTestString", args = {})
    public void testGetTestString() {
        TestComplexAppObject testComplexAppObject = new TestComplexAppObject();

        testComplexAppObject.setTestString("test2");

        assertEquals("test2", testComplexAppObject.getTestString());
    }

    @Test
    @UnitTestMethod(target = TestComplexAppObject.class, name = "hashCode", args = {})
    public void testHashCode() {
        RandomGenerator randomGenerator = RandomGeneratorProvider.getRandomGenerator(2653491444433183354L);

        // equal objects have equal hash codes
        for (int i = 0; i < 30; i++) {
            long seed = randomGenerator.nextLong();
            TestComplexAppObject testComplexAppObject1 = TestObjectUtil.generateTestComplexAppObject(seed);
            TestComplexAppObject testComplexAppObject2 = TestObjectUtil.generateTestComplexAppObject(seed);

            assertEquals(testComplexAppObject1, testComplexAppObject2);
            assertEquals(testComplexAppObject1.hashCode(), testComplexAppObject2.hashCode());
        }

        // hash codes are reasonably distributed
        Set<Integer> hashCodes = new LinkedHashSet<>();
        for (int i = 0; i < 100; i++) {
            TestComplexAppObject testComplexAppObject = TestObjectUtil
                    .generateTestComplexAppObject(randomGenerator.nextLong());
            hashCodes.add(testComplexAppObject.hashCode());
        }

        assertEquals(100, hashCodes.size());
    }

    @Test
    @UnitTestMethod(target = TestComplexAppObject.class, name = "equals", args = { Object.class })
    public void testEquals() {
        RandomGenerator randomGenerator = RandomGeneratorProvider.getRandomGenerator(8980322418377306870L);

        // never equal to another type
        for (int i = 0; i < 30; i++) {
            TestComplexAppObject testComplexAppObject = TestObjectUtil
                    .generateTestComplexAppObject(randomGenerator.nextLong());
            assertFalse(testComplexAppObject.equals(new Object()));
        }

        // never equal to null
        for (int i = 0; i < 30; i++) {
            TestComplexAppObject testComplexAppObject = TestObjectUtil
                    .generateTestComplexAppObject(randomGenerator.nextLong());
            assertFalse(testComplexAppObject.equals(null));
        }

        // reflexive
        for (int i = 0; i < 30; i++) {
            TestComplexAppObject testComplexAppObject = TestObjectUtil
                    .generateTestComplexAppObject(randomGenerator.nextLong());
            assertTrue(testComplexAppObject.equals(testComplexAppObject));
        }

        // symmetric, transitive, consistent
        for (int i = 0; i < 30; i++) {
            long seed = randomGenerator.nextLong();
            TestComplexAppObject testComplexAppObject1 = TestObjectUtil.generateTestComplexAppObject(seed);
            TestComplexAppObject testComplexAppObject2 = TestObjectUtil.generateTestComplexAppObject(seed);
            assertFalse(testComplexAppObject1 == testComplexAppObject2);
            for (int j = 0; j < 10; j++) {
                assertTrue(testComplexAppObject1.equals(testComplexAppObject2));
                assertTrue(testComplexAppObject2.equals(testComplexAppObject1));
            }
        }

        // small changes result in different objects
        for (int i = 0; i < 30; i++) {
            TestComplexAppObject testComplexAppObject2 = TestObjectUtil
                    .generateTestComplexAppObject(randomGenerator.nextLong());

            for (int j = 0; j < 10; j++) {
                TestComplexAppObject testComplexAppObject1 = new TestComplexAppObject();

                assertFalse(testComplexAppObject1.equals(testComplexAppObject2));

                testComplexAppObject1.setTestString(testComplexAppObject2.getTestString());
                assertFalse(testComplexAppObject1.equals(testComplexAppObject2));

                testComplexAppObject1.setStartTime(testComplexAppObject2.getStartTime());
                assertFalse(testComplexAppObject1.equals(testComplexAppObject2));

                testComplexAppObject1.setNumEntities(testComplexAppObject2.getNumEntities());
                assertTrue(testComplexAppObject1.equals(testComplexAppObject2));
            }
        }

        // different inputs yield unequal testComplexAppObjects
        Set<TestComplexAppObject> set = new LinkedHashSet<>();
        for (int i = 0; i < 100; i++) {
            TestComplexAppObject testComplexAppObject = TestObjectUtil
                    .generateTestComplexAppObject(randomGenerator.nextLong());
            set.add(testComplexAppObject);
        }
        assertEquals(100, set.size());
    }
}
