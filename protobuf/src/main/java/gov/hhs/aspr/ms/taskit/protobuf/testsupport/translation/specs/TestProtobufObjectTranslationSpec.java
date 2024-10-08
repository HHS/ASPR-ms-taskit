package gov.hhs.aspr.ms.taskit.protobuf.testsupport.translation.specs;

import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestAppObject;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.objects.TestComplexInputObject;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.objects.TestInputObject;
import gov.hhs.aspr.ms.taskit.protobuf.translation.ProtobufTranslationSpec;

/**
 * Translation Spec implementation for TestObject.
 */
public class TestProtobufObjectTranslationSpec extends ProtobufTranslationSpec<TestInputObject, TestAppObject> {

    @Override
    protected TestAppObject translateInputObject(TestInputObject inputObject) {
        TestAppObject testAppObject = new TestAppObject();

        testAppObject.setBool(inputObject.getBool());
        testAppObject.setInteger(inputObject.getInteger());
        testAppObject.setString(inputObject.getString());
        testAppObject
                .setTestComplexAppObject(this.taskitEngine.translateObject(inputObject.getTestComplexInputObject()));

        return testAppObject;
    }

    @Override
    protected TestInputObject translateAppObject(TestAppObject appObject) {
        TestInputObject testInputObject = TestInputObject.newBuilder().setBool(appObject.isBool())
                .setInteger(appObject.getInteger()).setString(appObject.getString())
                .setTestComplexInputObject((TestComplexInputObject) this.taskitEngine
                        .translateObject(appObject.getTestComplexAppObject()))
                .build();

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
