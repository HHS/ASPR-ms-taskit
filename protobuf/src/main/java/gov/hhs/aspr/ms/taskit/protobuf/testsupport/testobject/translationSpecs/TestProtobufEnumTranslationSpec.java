package gov.hhs.aspr.ms.taskit.protobuf.testsupport.testobject.translationSpecs;

import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestAppEnum;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.testobject.input.TestInputEnum;
import gov.hhs.aspr.ms.taskit.protobuf.translation.ProtobufTranslationSpec;

public class TestProtobufEnumTranslationSpec extends ProtobufTranslationSpec<TestInputEnum, TestAppEnum> {
    @Override
    protected TestAppEnum translateInputObject(TestInputEnum inputObject) {
        return TestAppEnum.valueOf(inputObject.name());
    }

    @Override
    protected TestInputEnum translateAppObject(TestAppEnum appObject) {
        return TestInputEnum.valueOf(appObject.name());
    }

    @Override
    public Class<TestAppEnum> getAppObjectClass() {
        return TestAppEnum.class;
    }

    @Override
    public Class<TestInputEnum> getInputObjectClass() {
        return TestInputEnum.class;
    }
}
