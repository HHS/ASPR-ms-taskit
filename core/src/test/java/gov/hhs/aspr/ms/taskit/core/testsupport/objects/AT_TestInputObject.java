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

public class AT_TestInputObject {

    @Test
    @UnitTestConstructor(target = TestInputObject.class, args = {})
    public void testConstructor() {
        assertNotNull(new TestInputObject());
    }

    @Test
    @UnitTestMethod(target = TestInputObject.class, name = "setInteger", args = { int.class })
    public void testSetInteger() {
        TestInputObject testInputObject = new TestInputObject();

        testInputObject.setInteger(15);

        assertEquals(15, testInputObject.getInteger());
    }

    @Test
    @UnitTestMethod(target = TestInputObject.class, name = "getInteger", args = {})
    public void testGetInteger() {
        TestInputObject testInputObject = new TestInputObject();

        testInputObject.setInteger(150);

        assertEquals(150, testInputObject.getInteger());
    }

    @Test
    @UnitTestMethod(target = TestInputObject.class, name = "setBool", args = { boolean.class })
    public void testSetBool() {
        TestInputObject testInputObject = new TestInputObject();

        testInputObject.setBool(false);

        assertEquals(false, testInputObject.isBool());
    }

    @Test
    @UnitTestMethod(target = TestInputObject.class, name = "isBool", args = {})
    public void testIsBool() {
        TestInputObject testInputObject = new TestInputObject();

        testInputObject.setBool(true);

        assertEquals(true, testInputObject.isBool());
    }

    @Test
    @UnitTestMethod(target = TestInputObject.class, name = "setString", args = { String.class })
    public void testSetString() {
        TestInputObject testInputObject = new TestInputObject();

        testInputObject.setString("test");

        assertEquals("test", testInputObject.getString());
    }

    @Test
    @UnitTestMethod(target = TestInputObject.class, name = "getString", args = {})
    public void testGetString() {
        TestInputObject testInputObject = new TestInputObject();

        testInputObject.setString("test2");

        assertEquals("test2", testInputObject.getString());
    }

    @Test
    @UnitTestMethod(target = TestInputObject.class, name = "setTestComplexInputObject", args = {
            TestComplexInputObject.class })
    public void testSetTestComplexInputObject() {
        TestInputObject testInputObject = new TestInputObject();
        TestComplexInputObject testComplexInputObject = TestObjectUtil.generateTestComplexInputObject();

        testInputObject.setTestComplexInputObject(testComplexInputObject);

        assertEquals(testComplexInputObject, testInputObject.getTestComplexInputObject());
    }

    @Test
    @UnitTestMethod(target = TestInputObject.class, name = "getTestComplexInputObject", args = {})
    public void testGetTestComplexInputObject() {
        TestInputObject testInputObject = new TestInputObject();
        TestComplexInputObject testComplexInputObject = TestObjectUtil.generateTestComplexInputObject();

        testInputObject.setTestComplexInputObject(testComplexInputObject);

        assertEquals(testComplexInputObject, testInputObject.getTestComplexInputObject());
    }

    @Test
    @UnitTestMethod(target = TestInputObject.class, name = "setTestInputEnum", args = { TestInputEnum.class })
    public void testSetTestInputEnum() {
        TestInputObject testInputObject = new TestInputObject();

        testInputObject.setTestInputEnum(TestInputEnum.TEST1);

        assertEquals(TestInputEnum.TEST1, testInputObject.getTestInputEnum());
    }

    @Test
    @UnitTestMethod(target = TestInputObject.class, name = "getTestInputEnum", args = {})
    public void testGetTestInputEnum() {
        TestInputObject testInputObject = new TestInputObject();

        testInputObject.setTestInputEnum(TestInputEnum.TEST2);

        assertEquals(TestInputEnum.TEST2, testInputObject.getTestInputEnum());
    }

    @Test
    @UnitTestMethod(target = TestInputObject.class, name = "hashCode", args = {})
    public void testHashCode() {
		RandomGenerator randomGenerator = RandomGeneratorProvider.getRandomGenerator(2653491890433183354L);

		// equal objects have equal hash codes
		for (int i = 0; i < 30; i++) {
			long seed = randomGenerator.nextLong();
			TestInputObject testInputObject1 = TestObjectUtil.generateTestInputObject(seed);
			TestInputObject testInputObject2 = TestObjectUtil.generateTestInputObject(seed);

			assertEquals(testInputObject1, testInputObject2);
			assertEquals(testInputObject1.hashCode(), testInputObject2.hashCode());
		}

		// hash codes are reasonably distributed
		Set<Integer> hashCodes = new LinkedHashSet<>();
		for (int i = 0; i < 100; i++) {
			TestInputObject testInputObject = TestObjectUtil.generateTestInputObject(randomGenerator.nextLong());
			hashCodes.add(testInputObject.hashCode());
		}

		assertEquals(100, hashCodes.size());
    }

    @Test
    @UnitTestMethod(target = TestInputObject.class, name = "equals", args = { Object.class })
    public void testEquals() {
		RandomGenerator randomGenerator = RandomGeneratorProvider.getRandomGenerator(8980234518377306870L);

		// never equal to another type
		for (int i = 0; i < 30; i++) {
			TestInputObject testInputObject = TestObjectUtil.generateTestInputObject(randomGenerator.nextLong());
			assertFalse(testInputObject.equals(new Object()));
		}

		// never equal to null
		for (int i = 0; i < 30; i++) {
			TestInputObject testInputObject = TestObjectUtil.generateTestInputObject(randomGenerator.nextLong());
			assertFalse(testInputObject.equals(null));
		}

		// reflexive
		for (int i = 0; i < 30; i++) {
			TestInputObject testInputObject = TestObjectUtil.generateTestInputObject(randomGenerator.nextLong());
			assertTrue(testInputObject.equals(testInputObject));
		}

		// symmetric, transitive, consistent
		for (int i = 0; i < 30; i++) {
			long seed = randomGenerator.nextLong();
			TestInputObject testInputObject1 = TestObjectUtil.generateTestInputObject(seed);
			TestInputObject testInputObject2 = TestObjectUtil.generateTestInputObject(seed);
			assertFalse(testInputObject1 == testInputObject2);
			for (int j = 0; j < 10; j++) {
				assertTrue(testInputObject1.equals(testInputObject2));
				assertTrue(testInputObject2.equals(testInputObject1));
			}
		}

		// different inputs yield unequal testInputObjects
		Set<TestInputObject> set = new LinkedHashSet<>();
		for (int i = 0; i < 100; i++) {
			TestInputObject testInputObject = TestObjectUtil.generateTestInputObject(randomGenerator.nextLong());
			set.add(testInputObject);
		}
		assertEquals(100, set.size());
    }
}
