package gov.hhs.aspr.ms.taskit.core.testsupport.translation.object.specs;

import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestAppObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestInputObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.TestTranslationSpec;

/** 
 * Translation Specification for the TestObject
 */
public class TestObjectTranslationSpec extends TestTranslationSpec<TestInputObject, TestAppObject> {

    @Override
    protected TestAppObject translateInputObject(TestInputObject inputObject) {
        TestAppObject testAppObject = new TestAppObject();

        testAppObject.setBool(inputObject.isBool());
        testAppObject.setInteger(inputObject.getInteger());
        testAppObject.setString(inputObject.getString());
        testAppObject
                .setTestComplexAppObject(this.taskitEngine.translateObject(inputObject.getTestComplexInputObject()));
        testAppObject.setTestAppEnum(this.taskitEngine.translateObject(inputObject.getTestInputEnum()));

        return testAppObject;
    }

    @Override
    protected TestInputObject translateAppObject(TestAppObject appObject) {
        TestInputObject testInputObject = new TestInputObject();

        testInputObject.setBool(appObject.isBool());
        testInputObject.setInteger(appObject.getInteger());
        testInputObject.setString(appObject.getString());
        testInputObject
                .setTestComplexInputObject(this.taskitEngine.translateObject(appObject.getTestComplexAppObject()));
        testInputObject.setTestInputEnum(this.taskitEngine.translateObject(appObject.getTestAppEnum()));

        return testInputObject;
    }

    @Override
    public Class<TestAppObject> getAppObjectClass() {
        return TestAppObject.class;
    }

    @Override
    public Class<TestInputObject> getInputObjectClass() {
        return TestInputObject.class;
    }

}
