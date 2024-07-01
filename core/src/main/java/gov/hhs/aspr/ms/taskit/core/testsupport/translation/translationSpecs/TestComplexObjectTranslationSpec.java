package gov.hhs.aspr.ms.taskit.core.testsupport.translation.translationSpecs;

import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestComplexAppObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestComplexInputObject;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.TestTranslationSpec;

public class TestComplexObjectTranslationSpec
        extends TestTranslationSpec<TestComplexInputObject, TestComplexAppObject> {

    @Override
    protected TestComplexAppObject translateInputObject(TestComplexInputObject inputObject) {
        TestComplexAppObject testAppObject = new TestComplexAppObject();

        testAppObject.setNumEntities(inputObject.getNumEntities());
        testAppObject.setStartTime(inputObject.getStartTime());
        testAppObject.setTestString(inputObject.getTestString());

        return testAppObject;
    }

    @Override
    protected TestComplexInputObject translateAppObject(TestComplexAppObject appObject) {
        TestComplexInputObject testInputObject = new TestComplexInputObject();

        testInputObject.setNumEntities(appObject.getNumEntities());
        testInputObject.setStartTime(appObject.getStartTime());
        testInputObject.setTestString(appObject.getTestString());

        return testInputObject;
    }

    @Override
    public Class<TestComplexAppObject> getAppObjectClass() {
        return TestComplexAppObject.class;
    }

    @Override
    public Class<TestComplexInputObject> getInputObjectClass() {
        return TestComplexInputObject.class;
    }

}
