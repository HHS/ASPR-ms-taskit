package gov.hhs.aspr.ms.taskit.protobuf.engine;

import com.google.protobuf.Any;
import com.google.type.Date;

import gov.hhs.aspr.ms.taskit.protobuf.objects.TaskitObjectInput;
import gov.hhs.aspr.ms.taskit.protobuf.objects.WrapperEnumValue;
import gov.hhs.aspr.ms.util.errors.ContractException;

public final class TaskitObjectHelper {

    private TaskitObjectHelper() {
    }

    public static Object getValue(TaskitObjectInput taskitObjectInput) {
        TaskitObjectInput.ValueCase valueCase = taskitObjectInput.getValueCase();

        switch (valueCase) {
            case BVAL:
                return taskitObjectInput.getBVal();
            case DATEVAL:
                return taskitObjectInput.getDateVal();
            case DVAL:
                return taskitObjectInput.getDVal();
            case ENUMVAL:
                return taskitObjectInput.getEnumVal();
            case FVAL:
                return taskitObjectInput.getFVal();
            case I32VAL:
                return taskitObjectInput.getI32Val();
            case I64VAL:
                return taskitObjectInput.getI64Val();
            case MVAL:
                return taskitObjectInput.getMVal();
            case SVAL:
                return taskitObjectInput.getSVal();
            case VALUE_NOT_SET:
            default:
                throw new ContractException(ProtobufTaskitError.INVALID_RETRIEVAL);
        }
    }

    public static TaskitObjectInput getIntegerTaskitInput(Integer value) {
        return TaskitObjectInput.newBuilder().setI32Val(value).build();
    }

    public static TaskitObjectInput getAnyTaskitInput(Any value) {
        return TaskitObjectInput.newBuilder().setMVal(value).build();
    }

    public static TaskitObjectInput getLongTaskitInput(Long value) {
        return TaskitObjectInput.newBuilder().setI64Val(value).build();
    }

    public static TaskitObjectInput getFloatTaskitInput(Float value) {
        return TaskitObjectInput.newBuilder().setFVal(value).build();
    }

    public static TaskitObjectInput getDoubleTaskitInput(Double value) {
        return TaskitObjectInput.newBuilder().setDVal(value).build();
    }

    public static TaskitObjectInput getDateTaskitInput(Date value) {
        return TaskitObjectInput.newBuilder().setDateVal(value).build();
    }

    public static TaskitObjectInput getStringTaskitInput(String value) {
        return TaskitObjectInput.newBuilder().setSVal(value).build();
    }

    public static TaskitObjectInput getEnumTaskitInput(WrapperEnumValue value) {
        return TaskitObjectInput.newBuilder().setEnumVal(value).build();
    }

    public static TaskitObjectInput getBooleanTaskitInput(Boolean value) {
        return TaskitObjectInput.newBuilder().setBVal(value).build();
    }
}
