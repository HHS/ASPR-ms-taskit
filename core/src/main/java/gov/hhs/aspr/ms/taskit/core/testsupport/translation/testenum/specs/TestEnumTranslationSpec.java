package gov.hhs.aspr.ms.taskit.core.testsupport.translation.testenum.specs;

import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestAppEnum;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestInputEnum;
import gov.hhs.aspr.ms.taskit.core.testsupport.translation.TestTranslationSpec;

/** 
 * Translation Specification for the TestEnum
 */
public class TestEnumTranslationSpec extends TestTranslationSpec<TestInputEnum, TestAppEnum> {

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
