package gov.hhs.aspr.ms.taskit.core.testsupport;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.math3.random.RandomGenerator;

import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestAppChildObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestAppEnum;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestAppObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestComplexAppObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestComplexInputObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestInputChildObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestInputEnum;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestInputObject;
import gov.hhs.aspr.ms.util.random.RandomGeneratorProvider;

public class TestObjectUtil {
    static RandomGenerator randomGenerator = RandomGeneratorProvider
            .getRandomGenerator(4444833210967964206L);

    public static TestAppObject generateTestAppObject() {
        return generateTestAppObject(randomGenerator.nextLong());
    }

    public static TestAppObject generateTestAppObject(long seed) {
        RandomGenerator localRG = RandomGeneratorProvider.getRandomGenerator(seed);

        TestComplexAppObject testComplexAppObject = generateTestComplexAppObject(localRG.nextLong());

        TestAppEnum[] enumValues = TestAppEnum.values();
        TestAppEnum randomEnum = enumValues[localRG.nextInt(enumValues.length)];

        TestAppObject testAppObject = new TestAppObject();
        testAppObject.setBool(localRG.nextBoolean());
        testAppObject.setInteger(localRG.nextInt(1500));
        testAppObject.setString("readInput" + localRG.nextInt(25));
        testAppObject.setTestComplexAppObject(testComplexAppObject);
        testAppObject.setTestAppEnum(randomEnum);

        return testAppObject;
    }

    public static TestComplexAppObject generateTestComplexAppObject() {
        return generateTestComplexAppObject(randomGenerator.nextLong());
    }

    public static TestComplexAppObject generateTestComplexAppObject(long seed) {
        RandomGenerator localRG = RandomGeneratorProvider.getRandomGenerator(seed);
        
        TestComplexAppObject complexAppObject = new TestComplexAppObject();

        complexAppObject.setNumEntities(localRG.nextInt(100) + 1);
        complexAppObject.setStartTime(localRG.nextDouble() * 15);
        complexAppObject.setTestString("readInput" + localRG.nextInt(15));

        return complexAppObject;
    }

    public static List<TestAppObject> getListOfAppObjects(int num) {
        List<TestAppObject> appObjects = new ArrayList<>();

        for (int i = 0; i < num; i++) {
            appObjects.add(generateTestAppObject());
        }

        return appObjects;
    }

    public static TestInputObject generateTestInputObject() {
        return generateTestInputObject(randomGenerator.nextLong());
    }
    public static TestInputObject generateTestInputObject(long seed) {
        RandomGenerator localRG = RandomGeneratorProvider.getRandomGenerator(seed);

        TestComplexInputObject testComplexInputObject = generateTestComplexInputObject(localRG.nextLong());

        TestInputEnum[] enumValues = TestInputEnum.values();
        TestInputEnum randomEnum = enumValues[localRG.nextInt(enumValues.length)];

        TestInputObject testInputObject = new TestInputObject();
        testInputObject.setBool(localRG.nextBoolean());
        testInputObject.setInteger(localRG.nextInt(1500));
        testInputObject.setString("readInput" + localRG.nextInt(25));
        testInputObject.setTestComplexInputObject(testComplexInputObject);
        testInputObject.setTestInputEnum(randomEnum);

        return testInputObject;
    }

    public static TestComplexInputObject generateTestComplexInputObject() {
        return generateTestComplexInputObject(randomGenerator.nextLong());
    }

    public static TestComplexInputObject generateTestComplexInputObject(long seed) {
        RandomGenerator localRG = RandomGeneratorProvider.getRandomGenerator(seed);

        TestComplexInputObject complexInputObject = new TestComplexInputObject();

        complexInputObject.setNumEntities(localRG.nextInt(100)+1);
        complexInputObject.setStartTime(localRG.nextDouble() * 15);
        complexInputObject.setTestString("readInput" + localRG.nextInt(15));

        return complexInputObject;
    }

    public static List<TestInputObject> getListOfInputObjects(int num) {
        List<TestInputObject> inputObjects = new ArrayList<>();

        for (int i = 0; i < num; i++) {
            inputObjects.add(generateTestInputObject());
        }

        return inputObjects;
    }

    public static TestAppObject getAppFromInput(TestInputObject inputObject) {
        TestAppObject appObject = new TestAppObject();
        TestComplexAppObject complexAppObject = getComplexAppFromComplexInput(inputObject.getTestComplexInputObject());

        appObject.setTestComplexAppObject(complexAppObject);
        appObject.setBool(inputObject.isBool());
        appObject.setInteger(inputObject.getInteger());
        appObject.setString(inputObject.getString());
        appObject.setTestAppEnum(getAppEnumFromInputEnum(inputObject.getTestInputEnum()));

        return appObject;
    }

    public static TestInputObject getInputFromApp(TestAppObject appObject) {
        TestInputObject inputObject = new TestInputObject();
        TestComplexInputObject complexInputObject = getComplexInputFromComplexApp(appObject.getTestComplexAppObject());

        inputObject.setTestComplexInputObject(complexInputObject);
        inputObject.setBool(appObject.isBool());
        inputObject.setInteger(appObject.getInteger());
        inputObject.setString(appObject.getString());
        inputObject.setTestInputEnum(getInputEnumFromAppEnum(appObject.getTestAppEnum()));

        return inputObject;
    }

    public static TestAppChildObject getChildAppFromApp(TestAppObject appObject) {
        TestAppChildObject childAppObject = new TestAppChildObject();

        childAppObject.setTestComplexAppObject(appObject.getTestComplexAppObject());
        childAppObject.setBool(appObject.isBool());
        childAppObject.setInteger(appObject.getInteger());
        childAppObject.setString(appObject.getString());
        childAppObject.setTestAppEnum(appObject.getTestAppEnum());

        return childAppObject;
    }

    public static TestInputChildObject getChildInputFromInput(TestInputObject inputObject) {
        TestInputChildObject childInputObject = new TestInputChildObject();

        childInputObject.setTestComplexInputObject(inputObject.getTestComplexInputObject());
        childInputObject.setBool(inputObject.isBool());
        childInputObject.setInteger(inputObject.getInteger());
        childInputObject.setString(inputObject.getString());
        childInputObject.setTestInputEnum(inputObject.getTestInputEnum());

        return childInputObject;
    }

    public static TestComplexAppObject getComplexAppFromComplexInput(TestComplexInputObject inputObject) {
        TestComplexAppObject complexAppObject = new TestComplexAppObject();

        complexAppObject.setNumEntities(inputObject.getNumEntities());
        complexAppObject.setStartTime(inputObject.getStartTime());
        complexAppObject.setTestString(inputObject.getTestString());

        return complexAppObject;
    }

    public static TestComplexInputObject getComplexInputFromComplexApp(TestComplexAppObject appObject) {
        TestComplexInputObject complexInputObject = new TestComplexInputObject();

        complexInputObject.setNumEntities(appObject.getNumEntities());
        complexInputObject.setStartTime(appObject.getStartTime());
        complexInputObject.setTestString(appObject.getTestString());

        return complexInputObject;
    }

    public static TestAppEnum getAppEnumFromInputEnum(TestInputEnum testInputEnum) {
        return TestAppEnum.valueOf(testInputEnum.name());
    }

    public static TestInputEnum getInputEnumFromAppEnum(TestAppEnum testAppEnum) {
        return TestInputEnum.valueOf(testAppEnum.name());
    }
}
