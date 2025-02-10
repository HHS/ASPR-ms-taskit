package gov.hhs.aspr.ms.taskit.protobuf.engine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import gov.hhs.aspr.ms.util.annotations.UnitTag;
import gov.hhs.aspr.ms.util.annotations.UnitTestConstructor;
import gov.hhs.aspr.ms.util.annotations.UnitTestMethod;
import gov.hhs.aspr.ms.util.errors.ContractException;

public class AT_TaskitObject {

    @Test
    @UnitTestConstructor(target = TaskitObject.class, args = {}, tags = { UnitTag.INCOMPLETE })
    public void testObjectConstructor() {
        Object value = new Object() {};
        TaskitObject taskitObject = new TaskitObject(value);

        assertNotNull(taskitObject);

        assertTrue(taskitObject.hasObjectVal());
        assertFalse(taskitObject.hasIntegerVal());
        assertFalse(taskitObject.hasDoubleVal());
        assertFalse(taskitObject.hasBooleanVal());
        assertFalse(taskitObject.hasFloatVal());
        assertFalse(taskitObject.hasLongVal());
        assertFalse(taskitObject.hasStringVal());
        assertFalse(taskitObject.hasDateVal());
        assertFalse(taskitObject.hasEnumVal());

        assertEquals(value, taskitObject.getObjectVal());
    }

    @Test
    @UnitTestConstructor(target = TaskitObject.class, args = {}, tags = { UnitTag.INCOMPLETE })
    public void testIntConstructor() {
        Integer value = 100;
        TaskitObject taskitObject = new TaskitObject(value);

        assertNotNull(taskitObject);

        assertFalse(taskitObject.hasObjectVal());
        assertTrue(taskitObject.hasIntegerVal());
        assertFalse(taskitObject.hasDoubleVal());
        assertFalse(taskitObject.hasBooleanVal());
        assertFalse(taskitObject.hasFloatVal());
        assertFalse(taskitObject.hasLongVal());
        assertFalse(taskitObject.hasStringVal());
        assertFalse(taskitObject.hasDateVal());
        assertFalse(taskitObject.hasEnumVal());

        assertEquals(value, taskitObject.getIntegerVal());
    }

    @Test
    @UnitTestConstructor(target = TaskitObject.class, args = {}, tags = { UnitTag.INCOMPLETE })
    public void testDoubleConstructor() {
        Double value = 5.5;
        TaskitObject taskitObject = new TaskitObject(value);

        assertNotNull(taskitObject);

        assertFalse(taskitObject.hasObjectVal());
        assertFalse(taskitObject.hasIntegerVal());
        assertTrue(taskitObject.hasDoubleVal());
        assertFalse(taskitObject.hasBooleanVal());
        assertFalse(taskitObject.hasFloatVal());
        assertFalse(taskitObject.hasLongVal());
        assertFalse(taskitObject.hasStringVal());
        assertFalse(taskitObject.hasDateVal());
        assertFalse(taskitObject.hasEnumVal());

        assertEquals(value, taskitObject.getDoubleVal());
    }

    @Test
    @UnitTestConstructor(target = TaskitObject.class, args = {}, tags = { UnitTag.INCOMPLETE })
    public void testBooleanConstructor() {
        Boolean value = true;
        TaskitObject taskitObject = new TaskitObject(value);

        assertNotNull(taskitObject);

        assertFalse(taskitObject.hasObjectVal());
        assertFalse(taskitObject.hasIntegerVal());
        assertFalse(taskitObject.hasDoubleVal());
        assertTrue(taskitObject.hasBooleanVal());
        assertFalse(taskitObject.hasFloatVal());
        assertFalse(taskitObject.hasLongVal());
        assertFalse(taskitObject.hasStringVal());
        assertFalse(taskitObject.hasDateVal());
        assertFalse(taskitObject.hasEnumVal());

        assertEquals(value, taskitObject.getBooleanVal());
    }

    @Test
    @UnitTestConstructor(target = TaskitObject.class, args = {}, tags = { UnitTag.INCOMPLETE })
    public void testFloatConstructor() {
        Float value = 5.5f;
        TaskitObject taskitObject = new TaskitObject(value);

        assertNotNull(taskitObject);

        assertFalse(taskitObject.hasObjectVal());
        assertFalse(taskitObject.hasIntegerVal());
        assertFalse(taskitObject.hasDoubleVal());
        assertFalse(taskitObject.hasBooleanVal());
        assertTrue(taskitObject.hasFloatVal());
        assertFalse(taskitObject.hasLongVal());
        assertFalse(taskitObject.hasStringVal());
        assertFalse(taskitObject.hasDateVal());
        assertFalse(taskitObject.hasEnumVal());

        assertEquals(value, taskitObject.getFloatVal());
    }

    @Test
    @UnitTestConstructor(target = TaskitObject.class, args = {}, tags = { UnitTag.INCOMPLETE })
    public void testLongConstructor() {
        Long value = 100L;
        TaskitObject taskitObject = new TaskitObject(value);

        assertNotNull(taskitObject);

        assertFalse(taskitObject.hasObjectVal());
        assertFalse(taskitObject.hasIntegerVal());
        assertFalse(taskitObject.hasDoubleVal());
        assertFalse(taskitObject.hasBooleanVal());
        assertFalse(taskitObject.hasFloatVal());
        assertTrue(taskitObject.hasLongVal());
        assertFalse(taskitObject.hasStringVal());
        assertFalse(taskitObject.hasDateVal());
        assertFalse(taskitObject.hasEnumVal());

        assertEquals(value, taskitObject.getLongVal());
    }

    @Test
    @UnitTestConstructor(target = TaskitObject.class, args = {}, tags = { UnitTag.INCOMPLETE })
    public void testStringConstructor() {
        String value = "Test";
        TaskitObject taskitObject = new TaskitObject(value);

        assertNotNull(taskitObject);

        assertFalse(taskitObject.hasObjectVal());
        assertFalse(taskitObject.hasIntegerVal());
        assertFalse(taskitObject.hasDoubleVal());
        assertFalse(taskitObject.hasBooleanVal());
        assertFalse(taskitObject.hasFloatVal());
        assertFalse(taskitObject.hasLongVal());
        assertTrue(taskitObject.hasStringVal());
        assertFalse(taskitObject.hasDateVal());
        assertFalse(taskitObject.hasEnumVal());

        assertEquals(value, taskitObject.getStringVal());
    }

    @Test
    @UnitTestConstructor(target = TaskitObject.class, args = {}, tags = { UnitTag.INCOMPLETE })
    public void testDateConstructor() {
        LocalDate value = LocalDate.now();
        TaskitObject taskitObject = new TaskitObject(value);

        assertNotNull(taskitObject);

        assertFalse(taskitObject.hasObjectVal());
        assertFalse(taskitObject.hasIntegerVal());
        assertFalse(taskitObject.hasDoubleVal());
        assertFalse(taskitObject.hasBooleanVal());
        assertFalse(taskitObject.hasFloatVal());
        assertFalse(taskitObject.hasLongVal());
        assertFalse(taskitObject.hasStringVal());
        assertTrue(taskitObject.hasDateVal());
        assertFalse(taskitObject.hasEnumVal());

        assertEquals(value, taskitObject.getDateVal());
    }

    @Test
    @UnitTestConstructor(target = TaskitObject.class, args = {}, tags = { UnitTag.INCOMPLETE })
    public void testEnumConstructor() {
        Enum<?> value = ProtobufTaskitError.INVALID_INPUT_CLASS;
        TaskitObject taskitObject = new TaskitObject(value);

        assertNotNull(taskitObject);

        assertFalse(taskitObject.hasObjectVal());
        assertFalse(taskitObject.hasIntegerVal());
        assertFalse(taskitObject.hasDoubleVal());
        assertFalse(taskitObject.hasBooleanVal());
        assertFalse(taskitObject.hasFloatVal());
        assertFalse(taskitObject.hasLongVal());
        assertFalse(taskitObject.hasStringVal());
        assertFalse(taskitObject.hasDateVal());
        assertTrue(taskitObject.hasEnumVal());

        assertEquals(value, taskitObject.getEnumVal());
    }

    @Test
    @UnitTestMethod(target = TaskitObject.class, name = "hasObjectVal", args = {}, tags = { UnitTag.INCOMPLETE })
    public void testHasObjectVal() {
        Object value = new Object() {};
        TaskitObject taskitObject = new TaskitObject(value);

        assertNotNull(taskitObject);

        assertTrue(taskitObject.hasObjectVal());
        assertFalse(taskitObject.hasIntegerVal());
        assertFalse(taskitObject.hasDoubleVal());
        assertFalse(taskitObject.hasBooleanVal());
        assertFalse(taskitObject.hasFloatVal());
        assertFalse(taskitObject.hasLongVal());
        assertFalse(taskitObject.hasStringVal());
        assertFalse(taskitObject.hasDateVal());
        assertFalse(taskitObject.hasEnumVal());
    }

    @Test
    @UnitTestMethod(target = TaskitObject.class, name = "hasIntVal", args = {}, tags = { UnitTag.INCOMPLETE })
    public void testHasIntVal() {
        Integer value = 100;
        TaskitObject taskitObject = new TaskitObject(value);

        assertNotNull(taskitObject);

        assertFalse(taskitObject.hasObjectVal());
        assertTrue(taskitObject.hasIntegerVal());
        assertFalse(taskitObject.hasDoubleVal());
        assertFalse(taskitObject.hasBooleanVal());
        assertFalse(taskitObject.hasFloatVal());
        assertFalse(taskitObject.hasLongVal());
        assertFalse(taskitObject.hasStringVal());
        assertFalse(taskitObject.hasDateVal());
        assertFalse(taskitObject.hasEnumVal());
    }

    @Test
    @UnitTestMethod(target = TaskitObject.class, name = "hasDoubleVal", args = {}, tags = { UnitTag.INCOMPLETE })
    public void testHasDoubleVal() {
        Double value = 5.5;
        TaskitObject taskitObject = new TaskitObject(value);

        assertNotNull(taskitObject);

        assertFalse(taskitObject.hasObjectVal());
        assertFalse(taskitObject.hasIntegerVal());
        assertTrue(taskitObject.hasDoubleVal());
        assertFalse(taskitObject.hasBooleanVal());
        assertFalse(taskitObject.hasFloatVal());
        assertFalse(taskitObject.hasLongVal());
        assertFalse(taskitObject.hasStringVal());
        assertFalse(taskitObject.hasDateVal());
        assertFalse(taskitObject.hasEnumVal());
    }

    @Test
    @UnitTestMethod(target = TaskitObject.class, name = "hasBoolVal", args = {}, tags = { UnitTag.INCOMPLETE })
    public void testHasBoolVal() {
        Boolean value = true;
        TaskitObject taskitObject = new TaskitObject(value);

        assertNotNull(taskitObject);

        assertFalse(taskitObject.hasObjectVal());
        assertFalse(taskitObject.hasIntegerVal());
        assertFalse(taskitObject.hasDoubleVal());
        assertTrue(taskitObject.hasBooleanVal());
        assertFalse(taskitObject.hasFloatVal());
        assertFalse(taskitObject.hasLongVal());
        assertFalse(taskitObject.hasStringVal());
        assertFalse(taskitObject.hasDateVal());
        assertFalse(taskitObject.hasEnumVal());
    }

    @Test
    @UnitTestMethod(target = TaskitObject.class, name = "hasFloatVal", args = {}, tags = { UnitTag.INCOMPLETE })
    public void testHasFloatVal() {
        Float value = 5.5f;
        TaskitObject taskitObject = new TaskitObject(value);

        assertNotNull(taskitObject);

        assertFalse(taskitObject.hasObjectVal());
        assertFalse(taskitObject.hasIntegerVal());
        assertFalse(taskitObject.hasDoubleVal());
        assertFalse(taskitObject.hasBooleanVal());
        assertTrue(taskitObject.hasFloatVal());
        assertFalse(taskitObject.hasLongVal());
        assertFalse(taskitObject.hasStringVal());
        assertFalse(taskitObject.hasDateVal());
        assertFalse(taskitObject.hasEnumVal());
    }

    @Test
    @UnitTestMethod(target = TaskitObject.class, name = "hasLongVal", args = {}, tags = { UnitTag.INCOMPLETE })
    public void testHasLongVal() {
        Long value = 100L;
        TaskitObject taskitObject = new TaskitObject(value);

        assertNotNull(taskitObject);

        assertFalse(taskitObject.hasObjectVal());
        assertFalse(taskitObject.hasIntegerVal());
        assertFalse(taskitObject.hasDoubleVal());
        assertFalse(taskitObject.hasBooleanVal());
        assertFalse(taskitObject.hasFloatVal());
        assertTrue(taskitObject.hasLongVal());
        assertFalse(taskitObject.hasStringVal());
        assertFalse(taskitObject.hasDateVal());
        assertFalse(taskitObject.hasEnumVal());
    }

    @Test
    @UnitTestMethod(target = TaskitObject.class, name = "hasStringVal", args = {}, tags = { UnitTag.INCOMPLETE })
    public void testHasStringVal() {
        String value = "Test";
        TaskitObject taskitObject = new TaskitObject(value);

        assertNotNull(taskitObject);

        assertFalse(taskitObject.hasObjectVal());
        assertFalse(taskitObject.hasIntegerVal());
        assertFalse(taskitObject.hasDoubleVal());
        assertFalse(taskitObject.hasBooleanVal());
        assertFalse(taskitObject.hasFloatVal());
        assertFalse(taskitObject.hasLongVal());
        assertTrue(taskitObject.hasStringVal());
        assertFalse(taskitObject.hasDateVal());
        assertFalse(taskitObject.hasEnumVal());
    }

    @Test
    @UnitTestMethod(target = TaskitObject.class, name = "hasDateVal", args = {}, tags = { UnitTag.INCOMPLETE })
    public void testHasDateVal() {
        LocalDate value = LocalDate.now();
        TaskitObject taskitObject = new TaskitObject(value);

        assertNotNull(taskitObject);

        assertFalse(taskitObject.hasObjectVal());
        assertFalse(taskitObject.hasIntegerVal());
        assertFalse(taskitObject.hasDoubleVal());
        assertFalse(taskitObject.hasBooleanVal());
        assertFalse(taskitObject.hasFloatVal());
        assertFalse(taskitObject.hasLongVal());
        assertFalse(taskitObject.hasStringVal());
        assertTrue(taskitObject.hasDateVal());
        assertFalse(taskitObject.hasEnumVal());
    }

    @Test
    @UnitTestMethod(target = TaskitObject.class, name = "hasEnumVal", args = {}, tags = { UnitTag.INCOMPLETE })
    public void testHasEnumVal() {
        Enum<?> value = ProtobufTaskitError.INVALID_INPUT_CLASS;
        TaskitObject taskitObject = new TaskitObject(value);

        assertNotNull(taskitObject);

        assertFalse(taskitObject.hasObjectVal());
        assertFalse(taskitObject.hasIntegerVal());
        assertFalse(taskitObject.hasDoubleVal());
        assertFalse(taskitObject.hasBooleanVal());
        assertFalse(taskitObject.hasFloatVal());
        assertFalse(taskitObject.hasLongVal());
        assertFalse(taskitObject.hasStringVal());
        assertFalse(taskitObject.hasDateVal());
        assertTrue(taskitObject.hasEnumVal());
    }

    @Test
    @UnitTestMethod(target = TaskitObject.class, name = "getObjectVal", args = {}, tags = { UnitTag.INCOMPLETE })
    public void testGetObjectVal() {
        Object value = new Object() {};
        TaskitObject taskitObject = new TaskitObject(value);

        assertNotNull(taskitObject);

        assertTrue(taskitObject.hasObjectVal());
        assertFalse(taskitObject.hasIntegerVal());
        assertFalse(taskitObject.hasDoubleVal());
        assertFalse(taskitObject.hasBooleanVal());
        assertFalse(taskitObject.hasFloatVal());
        assertFalse(taskitObject.hasLongVal());
        assertFalse(taskitObject.hasStringVal());
        assertFalse(taskitObject.hasDateVal());
        assertFalse(taskitObject.hasEnumVal());

        assertEquals(value, taskitObject.getObjectVal());

        // precondition:
        // attempt to get value that was not set
        ContractException contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getIntegerVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getDoubleVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getBooleanVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getFloatVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getLongVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getStringVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getDateVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getEnumVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TaskitObject.class, name = "getIntVal", args = {}, tags = { UnitTag.INCOMPLETE })
    public void testGetIntVal() {
        Integer value = 100;
        TaskitObject taskitObject = new TaskitObject(value);

        assertNotNull(taskitObject);

        assertFalse(taskitObject.hasObjectVal());
        assertTrue(taskitObject.hasIntegerVal());
        assertFalse(taskitObject.hasDoubleVal());
        assertFalse(taskitObject.hasBooleanVal());
        assertFalse(taskitObject.hasFloatVal());
        assertFalse(taskitObject.hasLongVal());
        assertFalse(taskitObject.hasStringVal());
        assertFalse(taskitObject.hasDateVal());
        assertFalse(taskitObject.hasEnumVal());

        assertEquals(value, taskitObject.getIntegerVal());

        // precondition:
        // attempt to get value that was not set
        ContractException contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getObjectVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getDoubleVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getBooleanVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getFloatVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getLongVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getStringVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getDateVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getEnumVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TaskitObject.class, name = "getDoubleVal", args = {}, tags = { UnitTag.INCOMPLETE })
    public void testGetDoubleVal() {
        Double value = 5.5;
        TaskitObject taskitObject = new TaskitObject(value);

        assertNotNull(taskitObject);

        assertFalse(taskitObject.hasObjectVal());
        assertFalse(taskitObject.hasIntegerVal());
        assertTrue(taskitObject.hasDoubleVal());
        assertFalse(taskitObject.hasBooleanVal());
        assertFalse(taskitObject.hasFloatVal());
        assertFalse(taskitObject.hasLongVal());
        assertFalse(taskitObject.hasStringVal());
        assertFalse(taskitObject.hasDateVal());
        assertFalse(taskitObject.hasEnumVal());

        assertEquals(value, taskitObject.getDoubleVal());

        // precondition:
        // attempt to get value that was not set
        ContractException contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getObjectVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getIntegerVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getBooleanVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getFloatVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getLongVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getStringVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getDateVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getEnumVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TaskitObject.class, name = "getBooleanVal", args = {}, tags = { UnitTag.INCOMPLETE })
    public void testGetBooleanVal() {
        Boolean value = true;
        TaskitObject taskitObject = new TaskitObject(value);

        assertNotNull(taskitObject);

        assertFalse(taskitObject.hasObjectVal());
        assertFalse(taskitObject.hasIntegerVal());
        assertFalse(taskitObject.hasDoubleVal());
        assertTrue(taskitObject.hasBooleanVal());
        assertFalse(taskitObject.hasFloatVal());
        assertFalse(taskitObject.hasLongVal());
        assertFalse(taskitObject.hasStringVal());
        assertFalse(taskitObject.hasDateVal());
        assertFalse(taskitObject.hasEnumVal());

        assertEquals(value, taskitObject.getBooleanVal());

        // precondition:
        // attempt to get value that was not set
        ContractException contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getObjectVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getIntegerVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getDoubleVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getFloatVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getLongVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getStringVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getDateVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getEnumVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TaskitObject.class, name = "getFloatVal", args = {}, tags = { UnitTag.INCOMPLETE })
    public void testGetFloatVal() {
        Float value = 5.5f;
        TaskitObject taskitObject = new TaskitObject(value);

        assertNotNull(taskitObject);

        assertFalse(taskitObject.hasObjectVal());
        assertFalse(taskitObject.hasIntegerVal());
        assertFalse(taskitObject.hasDoubleVal());
        assertFalse(taskitObject.hasBooleanVal());
        assertTrue(taskitObject.hasFloatVal());
        assertFalse(taskitObject.hasLongVal());
        assertFalse(taskitObject.hasStringVal());
        assertFalse(taskitObject.hasDateVal());
        assertFalse(taskitObject.hasEnumVal());

        assertEquals(value, taskitObject.getFloatVal());

        // precondition:
        // attempt to get value that was not set
        ContractException contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getObjectVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getIntegerVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getDoubleVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getBooleanVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getLongVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getStringVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getDateVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getEnumVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TaskitObject.class, name = "getLongVal", args = {}, tags = { UnitTag.INCOMPLETE })
    public void testGetLongVal() {
        Long value = 100L;
        TaskitObject taskitObject = new TaskitObject(value);

        assertNotNull(taskitObject);

        assertFalse(taskitObject.hasObjectVal());
        assertFalse(taskitObject.hasIntegerVal());
        assertFalse(taskitObject.hasDoubleVal());
        assertFalse(taskitObject.hasBooleanVal());
        assertFalse(taskitObject.hasFloatVal());
        assertTrue(taskitObject.hasLongVal());
        assertFalse(taskitObject.hasStringVal());
        assertFalse(taskitObject.hasDateVal());
        assertFalse(taskitObject.hasEnumVal());

        assertEquals(value, taskitObject.getLongVal());

        // precondition:
        // attempt to get value that was not set
        ContractException contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getObjectVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getIntegerVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getDoubleVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getBooleanVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getFloatVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getStringVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getDateVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getEnumVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TaskitObject.class, name = "getStringVal", args = {}, tags = { UnitTag.INCOMPLETE })
    public void testGetStringVal() {
        String value = "Test";
        TaskitObject taskitObject = new TaskitObject(value);

        assertNotNull(taskitObject);

        assertFalse(taskitObject.hasObjectVal());
        assertFalse(taskitObject.hasIntegerVal());
        assertFalse(taskitObject.hasDoubleVal());
        assertFalse(taskitObject.hasBooleanVal());
        assertFalse(taskitObject.hasFloatVal());
        assertFalse(taskitObject.hasLongVal());
        assertTrue(taskitObject.hasStringVal());
        assertFalse(taskitObject.hasDateVal());
        assertFalse(taskitObject.hasEnumVal());

        assertEquals(value, taskitObject.getStringVal());

        // precondition:
        // attempt to get value that was not set
        ContractException contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getObjectVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getIntegerVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getDoubleVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getBooleanVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getFloatVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getLongVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getDateVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getEnumVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TaskitObject.class, name = "getDateVal", args = {}, tags = { UnitTag.INCOMPLETE })
    public void testGetDateVal() {
        LocalDate value = LocalDate.now();
        TaskitObject taskitObject = new TaskitObject(value);

        assertNotNull(taskitObject);

        assertFalse(taskitObject.hasObjectVal());
        assertFalse(taskitObject.hasIntegerVal());
        assertFalse(taskitObject.hasDoubleVal());
        assertFalse(taskitObject.hasBooleanVal());
        assertFalse(taskitObject.hasFloatVal());
        assertFalse(taskitObject.hasLongVal());
        assertFalse(taskitObject.hasStringVal());
        assertTrue(taskitObject.hasDateVal());
        assertFalse(taskitObject.hasEnumVal());

        assertEquals(value, taskitObject.getDateVal());

        // precondition:
        // attempt to get value that was not set
        ContractException contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getObjectVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getIntegerVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getDoubleVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getBooleanVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getFloatVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getLongVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getStringVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getEnumVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());
    }

    @Test
    @UnitTestMethod(target = TaskitObject.class, name = "getEnumVal", args = {}, tags = { UnitTag.INCOMPLETE })
    public void testGetEnumVal() {
        Enum<?> value = ProtobufTaskitError.INVALID_INPUT_CLASS;
        TaskitObject taskitObject = new TaskitObject(value);

        assertNotNull(taskitObject);

        assertFalse(taskitObject.hasObjectVal());
        assertFalse(taskitObject.hasIntegerVal());
        assertFalse(taskitObject.hasDoubleVal());
        assertFalse(taskitObject.hasBooleanVal());
        assertFalse(taskitObject.hasFloatVal());
        assertFalse(taskitObject.hasLongVal());
        assertFalse(taskitObject.hasStringVal());
        assertFalse(taskitObject.hasDateVal());
        assertTrue(taskitObject.hasEnumVal());

        assertEquals(value, taskitObject.getEnumVal());

        // precondition:
        // attempt to get value that was not set
        ContractException contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getObjectVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getIntegerVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getDoubleVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getBooleanVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getFloatVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getLongVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getStringVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());

        contractException = assertThrows(ContractException.class, () -> {
            taskitObject.getDateVal();
        });

        assertEquals(ProtobufTaskitError.INVALID_RETRIEVAL, contractException.getErrorType());
    }
}
