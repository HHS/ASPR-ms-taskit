package gov.hhs.aspr.ms.taskit.protobuf.testsupport.testobject.translationSpecs;

import gov.hhs.aspr.ms.taskit.core.testsupport.testobject.TestAppObject;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.testcomplexobject.input.TestComplexInputObject;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.testobject.input.TestInputObject;
import gov.hhs.aspr.ms.taskit.protobuf.translation.ProtobufTranslationSpec;

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
