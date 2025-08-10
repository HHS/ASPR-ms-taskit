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

public class AT_TestComplexInputObject {

    @Test
    @UnitTestConstructor(target = TestComplexInputObject.class, args = {})
    public void testConstructor() {
        assertNotNull(new TestComplexInputObject());
    }

    @Test
    @UnitTestMethod(target = TestComplexInputObject.class, name = "setNumEntities", args = { int.class })
    public void testSetNumEntities() {
        TestComplexInputObject testComplexInputObject = new TestComplexInputObject();

        testComplexInputObject.setNumEntities(15);

        assertEquals(15, testComplexInputObject.getNumEntities());
    }

    @Test
    @UnitTestMethod(target = TestComplexInputObject.class, name = "getNumEntities", args = {})
    public void testGetNumEntities() {
        TestComplexInputObject testComplexInputObject = new TestComplexInputObject();

        testComplexInputObject.setNumEntities(150);

        assertEquals(150, testComplexInputObject.getNumEntities());
    }

    @Test
    @UnitTestMethod(target = TestComplexInputObject.class, name = "setStartTime", args = { double.class })
    public void testSetStartTime() {
        TestComplexInputObject testComplexInputObject = new TestComplexInputObject();

        testComplexInputObject.setStartTime(0.0);

        assertEquals(0.0, testComplexInputObject.getStartTime());
    }

    @Test
    @UnitTestMethod(target = TestComplexInputObject.class, name = "getStartTime", args = {})
    public void testIsStartTime() {
        TestComplexInputObject testComplexInputObject = new TestComplexInputObject();

        testComplexInputObject.setStartTime(150.0);

        assertEquals(150.0, testComplexInputObject.getStartTime());
    }

    @Test
    @UnitTestMethod(target = TestComplexInputObject.class, name = "setTestString", args = { String.class })
    public void testSetTestString() {
        TestComplexInputObject testComplexInputObject = new TestComplexInputObject();

        testComplexInputObject.setTestString("test");

        assertEquals("test", testComplexInputObject.getTestString());
    }

    @Test
    @UnitTestMethod(target = TestComplexInputObject.class, name = "getTestString", args = {})
    public void testGetString() {
        TestComplexInputObject testComplexInputObject = new TestComplexInputObject();

        testComplexInputObject.setTestString("test2");

        assertEquals("test2", testComplexInputObject.getTestString());
    }

    @Test
    @UnitTestMethod(target = TestComplexInputObject.class, name = "hashCode", args = {})
    public void testHashCode() {
        RandomGenerator randomGenerator = RandomGeneratorProvider.getRandomGenerator(2653491508433183354L);

        // equal objects have equal hash codes
        for (int i = 0; i < 30; i++) {
            long seed = randomGenerator.nextLong();
            TestComplexInputObject testComplexInputObject1 = TestObjectUtil.generateTestComplexInputObject(seed);
            TestComplexInputObject testComplexInputObject2 = TestObjectUtil.generateTestComplexInputObject(seed);

            assertEquals(testComplexInputObject1, testComplexInputObject2);
            assertEquals(testComplexInputObject1.hashCode(), testComplexInputObject2.hashCode());
        }

        // hash codes are reasonably distributed
        Set<Integer> hashCodes = new LinkedHashSet<>();
        for (int i = 0; i < 100; i++) {
            TestComplexInputObject testComplexInputObject = TestObjectUtil
                    .generateTestComplexInputObject(randomGenerator.nextLong());
            hashCodes.add(testComplexInputObject.hashCode());
        }

        assertEquals(100, hashCodes.size());
    }

    @Test
    @UnitTestMethod(target = TestComplexInputObject.class, name = "equals", args = { Object.class })
    public void testEquals() {
        RandomGenerator randomGenerator = RandomGeneratorProvider.getRandomGenerator(8980223418377306870L);

        // never equal to another type
        for (int i = 0; i < 30; i++) {
            TestComplexInputObject testComplexInputObject = TestObjectUtil
                    .generateTestComplexInputObject(randomGenerator.nextLong());
            assertFalse(testComplexInputObject.equals(new Object()));
        }

        // never equal to null
        for (int i = 0; i < 30; i++) {
            TestComplexInputObject testComplexInputObject = TestObjectUtil
                    .generateTestComplexInputObject(randomGenerator.nextLong());
            assertFalse(testComplexInputObject.equals(null));
        }

        // reflexive
        for (int i = 0; i < 30; i++) {
            TestComplexInputObject testComplexInputObject = TestObjectUtil
                    .generateTestComplexInputObject(randomGenerator.nextLong());
            assertTrue(testComplexInputObject.equals(testComplexInputObject));
        }

        // symmetric, transitive, consistent
        for (int i = 0; i < 30; i++) {
            long seed = randomGenerator.nextLong();
            TestComplexInputObject testComplexInputObject1 = TestObjectUtil.generateTestComplexInputObject(seed);
            TestComplexInputObject testComplexInputObject2 = TestObjectUtil.generateTestComplexInputObject(seed);
            assertFalse(testComplexInputObject1 == testComplexInputObject2);
            for (int j = 0; j < 10; j++) {
                assertTrue(testComplexInputObject1.equals(testComplexInputObject2));
                assertTrue(testComplexInputObject2.equals(testComplexInputObject1));
            }
        }

        // small changes result in different objects
        for (int i = 0; i < 30; i++) {
            TestComplexInputObject testComplexInputObject2 = TestObjectUtil
                    .generateTestComplexInputObject(randomGenerator.nextLong());

            for (int j = 0; j < 10; j++) {
                TestComplexInputObject testComplexInputObject1 = new TestComplexInputObject();

                assertFalse(testComplexInputObject1.equals(testComplexInputObject2));

                testComplexInputObject1.setTestString(testComplexInputObject2.getTestString());
                assertFalse(testComplexInputObject1.equals(testComplexInputObject2));

                testComplexInputObject1.setStartTime(testComplexInputObject2.getStartTime());
                assertFalse(testComplexInputObject1.equals(testComplexInputObject2));

                testComplexInputObject1.setNumEntities(testComplexInputObject2.getNumEntities());
                assertTrue(testComplexInputObject1.equals(testComplexInputObject2));
            }
        }

        // different inputs yield unequal testComplexInputObjects
        Set<TestComplexInputObject> set = new LinkedHashSet<>();
        for (int i = 0; i < 100; i++) {
            TestComplexInputObject testComplexInputObject = TestObjectUtil
                    .generateTestComplexInputObject(randomGenerator.nextLong());
            set.add(testComplexInputObject);
        }
        assertEquals(100, set.size());
    }
}
