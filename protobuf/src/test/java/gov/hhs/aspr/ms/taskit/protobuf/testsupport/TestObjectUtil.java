package gov.hhs.aspr.ms.taskit.protobuf.testsupport;

import java.util.ArrayList;
import java.util.List;

import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestAppChildObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestAppEnum;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestAppObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestComplexAppObject;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.objects.TestComplexInputObject;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.objects.TestInputEnum;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.objects.TestInputObject;
import gov.hhs.aspr.ms.util.random.RandomGeneratorProvider;

public class TestObjectUtil {
    static org.apache.commons.math3.random.RandomGenerator randomGenerator = RandomGeneratorProvider
            .getRandomGenerator(4444833210967964206L);

    public static TestAppObject generateTestAppObject() {

        TestAppObject appObject = new TestAppObject();

        appObject.setTestComplexAppObject(generateTestComplexAppObject());
        appObject.setBool(randomGenerator.nextBoolean());
        appObject.setInteger(randomGenerator.nextInt(1500));
        appObject.setString("readInput" + randomGenerator.nextInt(25));
        appObject.setTestAppEnum(TestAppEnum.values()[randomGenerator.nextInt(1)]);

        return appObject;
    }

    public static TestComplexAppObject generateTestComplexAppObject() {
        TestComplexAppObject complexAppObject = new TestComplexAppObject();

        complexAppObject.setNumEntities(randomGenerator.nextInt(100));
        complexAppObject.setStartTime(randomGenerator.nextDouble() * 15);
        complexAppObject.setTestString("readInput" + randomGenerator.nextInt(15));

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

        TestInputObject inputObject = TestInputObject.newBuilder()
                .setTestComplexInputObject(generateTestComplexInputObject()).setBool(randomGenerator.nextBoolean())
                .setInteger(randomGenerator.nextInt(1500)).setString("readInput" + randomGenerator.nextInt(25))
                .setEnum(TestInputEnum.values()[randomGenerator.nextInt(1)]).build();

        return inputObject;
    }

    public static TestComplexInputObject generateTestComplexInputObject() {
        TestComplexInputObject complexInputObject = TestComplexInputObject.newBuilder()
                .setNumEntities(randomGenerator.nextInt(100)).setStartTime(randomGenerator.nextDouble() * 15)
                .setTestString("readInput" + randomGenerator.nextInt(15)).build();

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
        TestAppEnum testAppEnum = getTestAppEnumFromTestInputEnum(inputObject.getEnum());

        appObject.setTestComplexAppObject(complexAppObject);
        appObject.setBool(inputObject.getBool());
        appObject.setInteger(inputObject.getInteger());
        appObject.setString(inputObject.getString());
        appObject.setTestAppEnum(testAppEnum);

        return appObject;
    }

    public static TestInputObject getInputFromApp(TestAppObject appObject) {
        TestComplexInputObject complexInputObject = getComplexInputFromComplexApp(appObject.getTestComplexAppObject());
        TestInputEnum testInputEnum = getTestInputEnumFromTestAppEnum(appObject.getTestAppEnum());

        TestInputObject inputObject = TestInputObject.newBuilder().setTestComplexInputObject(complexInputObject)
                .setBool(appObject.isBool()).setInteger(appObject.getInteger()).setString(appObject.getString())
                .setEnum(testInputEnum).build();

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

    public static TestComplexAppObject getComplexAppFromComplexInput(TestComplexInputObject inputObject) {
        TestComplexAppObject complexAppObject = new TestComplexAppObject();

        complexAppObject.setNumEntities(inputObject.getNumEntities());
        complexAppObject.setStartTime(inputObject.getStartTime());
        complexAppObject.setTestString(inputObject.getTestString());

        return complexAppObject;
    }

    public static TestComplexInputObject getComplexInputFromComplexApp(TestComplexAppObject appObject) {
        TestComplexInputObject complexInputObject = TestComplexInputObject.newBuilder()
                .setNumEntities(appObject.getNumEntities()).setStartTime(appObject.getStartTime())
                .setTestString(appObject.getTestString()).build();

        return complexInputObject;
    }

    public static TestAppEnum getTestAppEnumFromTestInputEnum(TestInputEnum testInputEnum) {
        return TestAppEnum.valueOf(testInputEnum.name());
    }

    public static TestInputEnum getTestInputEnumFromTestAppEnum(TestAppEnum testAppEnum) {
        return TestInputEnum.valueOf(testAppEnum.name());
    }
}
