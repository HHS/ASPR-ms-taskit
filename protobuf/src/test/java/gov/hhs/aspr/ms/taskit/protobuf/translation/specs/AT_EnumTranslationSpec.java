package gov.hhs.aspr.ms.taskit.protobuf.translation.specs;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.google.protobuf.BoolValue;

import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestAppEnum;
import gov.hhs.aspr.ms.taskit.core.translation.TranslationSpecContext;
import gov.hhs.aspr.ms.taskit.protobuf.engine.ProtobufJsonTaskitEngine;
import gov.hhs.aspr.ms.taskit.protobuf.engine.ProtobufTaskitEngine;
import gov.hhs.aspr.ms.taskit.protobuf.objects.WrapperEnumValue;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.objects.TestInputEnum;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.translation.specs.TestProtobufEnumTranslationSpec;
import gov.hhs.aspr.ms.util.annotations.UnitTestConstructor;
import gov.hhs.aspr.ms.util.annotations.UnitTestForCoverage;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;

public class AT_EnumTranslationSpec {

    @Test
    @UnitTestConstructor(target = EnumTranslationSpec.class, args = {})
    public void testConstructor() {
        assertNotNull(new EnumTranslationSpec());
    }

    @Test
    @UnitTestForCoverage
    public void testConvertInputObject() {
        ProtobufJsonTaskitEngine protobufTaskitEngine = ProtobufJsonTaskitEngine.builder()
                .addTranslationSpec(new TestProtobufEnumTranslationSpec()).build();

                TranslationSpecContext<ProtobufTaskitEngine> translationSpecContext = new TranslationSpecContext<>(
                protobufTaskitEngine);

        EnumTranslationSpec enumTranslationSpec = new EnumTranslationSpec();
        enumTranslationSpec.init(translationSpecContext);

        TestAppEnum expectedValue = TestAppEnum.TEST1;
        WrapperEnumValue inputValue = WrapperEnumValue.newBuilder()
                .setEnumTypeUrl(TestInputEnum.TEST1.getDescriptorForType().getFullName())
                .setValue(TestInputEnum.TEST1.name()).build();

        TestAppEnum actualValue = (TestAppEnum) enumTranslationSpec.translateInputObject(inputValue);

        assertEquals(expectedValue, actualValue);

        // precondition
        // type url is well formed
        assertThrows(RuntimeException.class, () -> {
            WrapperEnumValue badInputValue = WrapperEnumValue.newBuilder()
                    .setEnumTypeUrl(BoolValue.getDefaultInstance().getDescriptorForType().getFullName())
                    .setValue(TestInputEnum.TEST1.name()).build();

            enumTranslationSpec.translateInputObject(badInputValue);
        });
    }

    @Test
    @UnitTestForCoverage
    public void testConvertAppObject() {
        ProtobufJsonTaskitEngine protobufTaskitEngine = ProtobufJsonTaskitEngine.builder()
                .addTranslationSpec(new TestProtobufEnumTranslationSpec()).build();

                TranslationSpecContext<ProtobufTaskitEngine> translationSpecContext = new TranslationSpecContext<>(
                protobufTaskitEngine);

        EnumTranslationSpec enumTranslationSpec = new EnumTranslationSpec();
        enumTranslationSpec.init(translationSpecContext);

        TestAppEnum appValue = TestAppEnum.TEST2;
        WrapperEnumValue expectedValue = WrapperEnumValue.newBuilder()
                .setEnumTypeUrl(TestInputEnum.TEST2.getDescriptorForType().getFullName())
                .setValue(TestInputEnum.TEST2.name()).build();

        WrapperEnumValue actualValue = enumTranslationSpec.translateAppObject(appValue);

        assertEquals(expectedValue, actualValue);
    }

    @Test
    @UnitTestMethod(target = EnumTranslationSpec.class, name = "getAppObjectClass", args = {})
    public void testGetAppObjectClass() {
        EnumTranslationSpec enumTranslationSpec = new EnumTranslationSpec();

        assertEquals(Enum.class, enumTranslationSpec.getAppObjectClass());
    }

    @Test
    @UnitTestMethod(target = EnumTranslationSpec.class, name = "getInputObjectClass", args = {})
    public void testGetInputObjectClass() {
        EnumTranslationSpec enumTranslationSpec = new EnumTranslationSpec();

        assertEquals(WrapperEnumValue.class, enumTranslationSpec.getInputObjectClass());
    }
}
