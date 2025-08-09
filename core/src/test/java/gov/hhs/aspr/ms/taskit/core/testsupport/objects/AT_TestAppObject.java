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

public class AT_TestAppObject {

    @Test
    @UnitTestConstructor(target = TestAppObject.class, args = {})
    public void testConstructor() {
        assertNotNull(new TestAppObject());
    }

    @Test
    @UnitTestMethod(target = TestAppObject.class, name = "setInteger", args = { int.class })
    public void testSetInteger() {
        TestAppObject testAppObject = new TestAppObject();

        testAppObject.setInteger(15);

        assertEquals(15, testAppObject.getInteger());
    }

    @Test
    @UnitTestMethod(target = TestAppObject.class, name = "getInteger", args = {})
    public void testGetInteger() {
        TestAppObject testAppObject = new TestAppObject();

        testAppObject.setInteger(150);

        assertEquals(150, testAppObject.getInteger());
    }

    @Test
    @UnitTestMethod(target = TestAppObject.class, name = "setBool", args = { boolean.class })
    public void testSetBool() {
        TestAppObject testAppObject = new TestAppObject();

        testAppObject.setBool(false);

        assertEquals(false, testAppObject.isBool());
    }

    @Test
    @UnitTestMethod(target = TestAppObject.class, name = "isBool", args = {})
    public void testIsBool() {
        TestAppObject testAppObject = new TestAppObject();

        testAppObject.setBool(true);

        assertEquals(true, testAppObject.isBool());
    }

    @Test
    @UnitTestMethod(target = TestAppObject.class, name = "setString", args = { String.class })
    public void testSetString() {
        TestAppObject testAppObject = new TestAppObject();

        testAppObject.setString("test");

        assertEquals("test", testAppObject.getString());
    }

    @Test
    @UnitTestMethod(target = TestAppObject.class, name = "getString", args = {})
    public void testGetString() {
        TestAppObject testAppObject = new TestAppObject();

        testAppObject.setString("test2");

        assertEquals("test2", testAppObject.getString());
    }

    @Test
    @UnitTestMethod(target = TestAppObject.class, name = "setTestComplexAppObject", args = {
            TestComplexAppObject.class })
    public void testSetTestComplexAppObject() {
        TestAppObject testAppObject = new TestAppObject();
        TestComplexAppObject testComplexAppObject = TestObjectUtil.generateTestComplexAppObject();

        testAppObject.setTestComplexAppObject(testComplexAppObject);

        assertEquals(testComplexAppObject, testAppObject.getTestComplexAppObject());
    }

    @Test
    @UnitTestMethod(target = TestAppObject.class, name = "getTestComplexAppObject", args = {})
    public void testGetTestComplexAppObject() {
        TestAppObject testAppObject = new TestAppObject();
        TestComplexAppObject testComplexAppObject = TestObjectUtil.generateTestComplexAppObject();

        testAppObject.setTestComplexAppObject(testComplexAppObject);

        assertEquals(testComplexAppObject, testAppObject.getTestComplexAppObject());
    }

    @Test
    @UnitTestMethod(target = TestAppObject.class, name = "setTestAppEnum", args = { TestAppEnum.class })
    public void testSetTestAppEnum() {
        TestAppObject testAppObject = new TestAppObject();

        testAppObject.setTestAppEnum(TestAppEnum.TEST1);

        assertEquals(TestAppEnum.TEST1, testAppObject.getTestAppEnum());
    }

    @Test
    @UnitTestMethod(target = TestAppObject.class, name = "getTestAppEnum", args = {})
    public void testGetTestAppEnum() {
        TestAppObject testAppObject = new TestAppObject();

        testAppObject.setTestAppEnum(TestAppEnum.TEST2);

        assertEquals(TestAppEnum.TEST2, testAppObject.getTestAppEnum());
    }

    @Test
    @UnitTestMethod(target = TestAppObject.class, name = "hashCode", args = {})
    public void testHashCode() {
		RandomGenerator randomGenerator = RandomGeneratorProvider.getRandomGenerator(2653491444438883354L);

		// equal objects have equal hash codes
		for (int i = 0; i < 30; i++) {
			long seed = randomGenerator.nextLong();
			TestAppObject testAppObject1 = TestObjectUtil.generateTestAppObject(seed);
			TestAppObject testAppObject2 = TestObjectUtil.generateTestAppObject(seed);

			assertEquals(testAppObject1, testAppObject2);
			assertEquals(testAppObject1.hashCode(), testAppObject2.hashCode());
		}

		// hash codes are reasonably distributed
		Set<Integer> hashCodes = new LinkedHashSet<>();
		for (int i = 0; i < 100; i++) {
			TestAppObject testAppObject = TestObjectUtil.generateTestAppObject(randomGenerator.nextLong());
			hashCodes.add(testAppObject.hashCode());
		}

		assertEquals(100, hashCodes.size());
    }

    @Test
    @UnitTestMethod(target = TestAppObject.class, name = "equals", args = { Object.class })
    public void testEquals() {
		RandomGenerator randomGenerator = RandomGeneratorProvider.getRandomGenerator(8980322418377306870L);

		// never equal to another type
		for (int i = 0; i < 30; i++) {
			TestAppObject testAppObject = TestObjectUtil.generateTestAppObject(randomGenerator.nextLong());
			assertFalse(testAppObject.equals(new Object()));
		}

		// never equal to null
		for (int i = 0; i < 30; i++) {
			TestAppObject testAppObject = TestObjectUtil.generateTestAppObject(randomGenerator.nextLong());
			assertFalse(testAppObject.equals(null));
		}

		// reflexive
		for (int i = 0; i < 30; i++) {
			TestAppObject testAppObject = TestObjectUtil.generateTestAppObject(randomGenerator.nextLong());
			assertTrue(testAppObject.equals(testAppObject));
		}

		// symmetric, transitive, consistent
		for (int i = 0; i < 30; i++) {
			long seed = randomGenerator.nextLong();
			TestAppObject testAppObject1 = TestObjectUtil.generateTestAppObject(seed);
			TestAppObject testAppObject2 = TestObjectUtil.generateTestAppObject(seed);
			assertFalse(testAppObject1 == testAppObject2);
			for (int j = 0; j < 10; j++) {
				assertTrue(testAppObject1.equals(testAppObject2));
				assertTrue(testAppObject2.equals(testAppObject1));
			}
		}

        // small changes result in different objects
        for (int i = 0; i < 30; i++) {
			TestAppObject testAppObject1 = new TestAppObject();
            TestAppObject testAppObject2 = TestObjectUtil.generateTestAppObject(randomGenerator.nextLong());

			for (int j = 0; j < 10; j++) {
				assertFalse(testAppObject1.equals(testAppObject2));

                testAppObject1.setInteger(testAppObject2.getInteger());
                assertFalse(testAppObject1.equals(testAppObject2));

				testAppObject1.setBool(testAppObject2.isBool());
                assertFalse(testAppObject1.equals(testAppObject2));

                testAppObject1.setString(testAppObject2.getString());
                 assertFalse(testAppObject1.equals(testAppObject2));

                testAppObject1.setTestComplexAppObject(testAppObject2.getTestComplexAppObject());
                assertFalse(testAppObject1.equals(testAppObject2));

                testAppObject1.setTestAppEnum(testAppObject2.getTestAppEnum());
                assertTrue(testAppObject1.equals(testAppObject2));

                testAppObject1 = new TestAppObject();
			}
		}

		// different inputs yield unequal testAppObjects
		Set<TestAppObject> set = new LinkedHashSet<>();
		for (int i = 0; i < 100; i++) {
			TestAppObject testAppObject = TestObjectUtil.generateTestAppObject(randomGenerator.nextLong());
			set.add(testAppObject);
		}
		assertEquals(100, set.size());
    }
}
