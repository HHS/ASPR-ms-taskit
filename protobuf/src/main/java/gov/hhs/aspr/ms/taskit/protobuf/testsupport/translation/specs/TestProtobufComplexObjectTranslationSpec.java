package gov.hhs.aspr.ms.taskit.protobuf.testsupport.translation.specs;

import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestComplexAppObject;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.objects.TestComplexInputObject;
import gov.hhs.aspr.ms.taskit.protobuf.translation.ProtobufTranslationSpec;

/** 
 * Translation spec implementation for the TestComplexObject.
 */
public class TestProtobufComplexObjectTranslationSpec
        extends ProtobufTranslationSpec<TestComplexInputObject, TestComplexAppObject> {

    @Override
    protected TestComplexAppObject translateInputObject(TestComplexInputObject inputObject) {
        TestComplexAppObject testComplexAppObject = new TestComplexAppObject();

        testComplexAppObject.setNumEntities(inputObject.getNumEntities());
        testComplexAppObject.setStartTime(inputObject.getStartTime());
        testComplexAppObject.setTestString(inputObject.getTestString());

        return testComplexAppObject;
    }

    @Override
    protected TestComplexInputObject translateAppObject(TestComplexAppObject appObject) {
        return TestComplexInputObject.newBuilder().setNumEntities(appObject.getNumEntities())
                .setStartTime(appObject.getStartTime()).setTestString(appObject.getTestString()).build();
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
