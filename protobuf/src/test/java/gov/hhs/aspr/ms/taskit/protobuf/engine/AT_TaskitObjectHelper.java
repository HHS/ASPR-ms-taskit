package gov.hhs.aspr.ms.taskit.protobuf.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.google.protobuf.Any;
import com.google.type.Date;

import gov.hhs.aspr.ms.taskit.protobuf.testsupport.TestObjectUtil;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestAppEnum;
import gov.hhs.aspr.ms.taskit.core.testsupport.objects.TestAppObject;
import gov.hhs.aspr.ms.taskit.protobuf.objects.TaskitObjectInput;
import gov.hhs.aspr.ms.taskit.protobuf.objects.WrapperEnumValue;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.translation.specs.TestProtobufComplexObjectTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.translation.specs.TestProtobufEnumTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.testsupport.translation.specs.TestProtobufObjectTranslationSpec;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;
import gov.hhs.aspr.ms.util.errors.ContractException;

public class AT_TaskitObjectHelper {

    @Test
    @UnitTestMethod(target = TaskitObjectHelper.class, name = "getValue", args = {
            TaskitObjectInput.class })
    public void testHasObjectVal() {
        Boolean expectedBoolean = false;
        TaskitObjectInput input = TaskitObjectInput.newBuilder().setBVal(expectedBoolean).build();

        Object actualObject = TaskitObjectHelper.getValue(input);

        assertEquals(expectedBoolean, actualObject);

        Date expectedDate = Date.getDefaultInstance();
        input = TaskitObjectInput.newBuilder().setDateVal(expectedDate).build();

        actualObject = TaskitObjectHelper.getValue(input);

        assertEquals(expectedDate, actualObject);

        Double expectedDouble = 0.0;
        input = TaskitObjectInput.newBuilder().setDVal(expectedDouble).build();

        actualObject = TaskitObjectHelper.getValue(input);

        assertEquals(expectedDouble, actualObject);

        WrapperEnumValue expectedEnum = WrapperEnumValue.getDefaultInstance();
        input = TaskitObjectInput.newBuilder().setEnumVal(expectedEnum).build();

        actualObject = TaskitObjectHelper.getValue(input);

        assertEquals(expectedEnum, actualObject);

        Float expectedFloat = 0.0f;
        input = TaskitObjectInput.newBuilder().setFVal(expectedFloat).build();

        actualObject = TaskitObjectHelper.getValue(input);

        assertEquals(expectedFloat, actualObject);

        Integer expectedInt = 0;
        input = TaskitObjectInput.newBuilder().setI32Val(expectedInt).build();

        actualObject = TaskitObjectHelper.getValue(input);

        assertEquals(expectedInt, actualObject);

        Long expectedLong = 0L;
        input = TaskitObjectInput.newBuilder().setI64Val(expectedLong).build();

        actualObject = TaskitObjectHelper.getValue(input);

        assertEquals(expectedLong, actualObject);

        Any expectedAny = Any.pack(WrapperEnumValue.getDefaultInstance());
        input = TaskitObjectInput.newBuilder().setMVal(expectedAny).build();

        actualObject = TaskitObjectHelper.getValue(input);

        assertEquals(expectedAny, actualObject);

        String expectedString = "";
        input = TaskitObjectInput.newBuilder().setSVal(expectedString).build();

        actualObject = TaskitObjectHelper.getValue(input);

        assertEquals(expectedString, actualObject);

        // preconditions:
        // value was not set
        ContractException contractException = assertThrows(ContractException.class, () -> {
            TaskitObjectHelper.getValue(TaskitObjectInput.newBuilder().build());
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TaskitObjectHelper.class, name = "getTaskitObjectInput", args = { Object.class,
            ProtobufTaskitEngine.class })
    public void testGetTaskitObjectInput() {

        ProtobufTaskitEngine taskitEngine = ProtobufJsonTaskitEngine.builder()
        .addTranslationSpec(new TestProtobufComplexObjectTranslationSpec())
        .addTranslationSpec(new TestProtobufEnumTranslationSpec())
        .addTranslationSpec(new TestProtobufObjectTranslationSpec())
        .build();

        Boolean expectedBoolean = false;
        TaskitObjectInput expectedInput = TaskitObjectInput.newBuilder().setBVal(expectedBoolean).build();
        TaskitObjectInput actualInput = TaskitObjectHelper.getTaskitObjectInput(expectedBoolean, taskitEngine);

        assertEquals(expectedInput, actualInput);

        boolean expectedBoolean2 = false;
        expectedInput = TaskitObjectInput.newBuilder().setBVal(expectedBoolean2).build();
        actualInput = TaskitObjectHelper.getTaskitObjectInput(expectedBoolean2, taskitEngine);

        assertEquals(expectedInput, actualInput);

        LocalDate date = LocalDate.now();
        Date expectedDate = taskitEngine.translateObject(date);
        expectedInput = TaskitObjectInput.newBuilder().setDateVal(expectedDate).build();
        actualInput = TaskitObjectHelper.getTaskitObjectInput(date, taskitEngine);

        assertEquals(expectedInput, actualInput);

        Double expectedDouble = 0.0;
        expectedInput = TaskitObjectInput.newBuilder().setDVal(expectedDouble).build();
        actualInput = TaskitObjectHelper.getTaskitObjectInput(expectedDouble, taskitEngine);

        assertEquals(expectedInput, actualInput);

        WrapperEnumValue expectedEnum = taskitEngine.translateObjectAsClassSafe(TestAppEnum.TEST1, Enum.class);
        expectedInput = TaskitObjectInput.newBuilder().setEnumVal(expectedEnum).build();
        actualInput = TaskitObjectHelper.getTaskitObjectInput(TestAppEnum.TEST1, taskitEngine);

        assertEquals(expectedInput, actualInput);

        Float expectedFloat = 0.0f;
        expectedInput = TaskitObjectInput.newBuilder().setFVal(expectedFloat).build();
        actualInput = TaskitObjectHelper.getTaskitObjectInput(expectedFloat, taskitEngine);

        assertEquals(expectedInput, actualInput);

        float expectedFloat2 = 0.0f;
        expectedInput = TaskitObjectInput.newBuilder().setFVal(expectedFloat2).build();
        actualInput = TaskitObjectHelper.getTaskitObjectInput(expectedFloat2, taskitEngine);

        assertEquals(expectedInput, actualInput);

        Integer expectedInt = 0;
        expectedInput = TaskitObjectInput.newBuilder().setI32Val(expectedInt).build();
        actualInput = TaskitObjectHelper.getTaskitObjectInput(expectedInt, taskitEngine);

        assertEquals(expectedInput, actualInput);

        int expectedInt2 = 0;
        expectedInput = TaskitObjectInput.newBuilder().setI32Val(expectedInt2).build();
        actualInput = TaskitObjectHelper.getTaskitObjectInput(expectedInt2, taskitEngine);

        assertEquals(expectedInput, actualInput);

        Long expectedLong = 0L;
        expectedInput = TaskitObjectInput.newBuilder().setI64Val(expectedLong).build();
        actualInput = TaskitObjectHelper.getTaskitObjectInput(expectedLong, taskitEngine);

        assertEquals(expectedInput, actualInput);

        long expectedLong2 = 0L;
        expectedInput = TaskitObjectInput.newBuilder().setI64Val(expectedLong2).build();
        actualInput = TaskitObjectHelper.getTaskitObjectInput(expectedLong2, taskitEngine);

        assertEquals(expectedInput, actualInput);

        TestAppObject obj = TestObjectUtil.generateTestAppObject();
        Any expectedAny = taskitEngine.getAnyFromObject(obj);
        expectedInput = TaskitObjectInput.newBuilder().setMVal(expectedAny).build();
        actualInput = TaskitObjectHelper.getTaskitObjectInput(obj, taskitEngine);

        assertEquals(expectedInput, actualInput);

        String expectedString = "";
        expectedInput = TaskitObjectInput.newBuilder().setSVal(expectedString).build();
        actualInput = TaskitObjectHelper.getTaskitObjectInput(expectedString, taskitEngine);

        assertEquals(expectedInput, actualInput);
    }
}
