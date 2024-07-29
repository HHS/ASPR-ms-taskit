package gov.hhs.aspr.ms.taskit.protobuf.testsupport.translation.specs;

import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestAppEnum;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.objects.TestInputEnum;
import gov.hhs.aspr.ms.taskit.protobuf.translation.ProtobufTranslationSpec;

/** 
 * Translation Spec implementation for TestEnum.
 */
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
