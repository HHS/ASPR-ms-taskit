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

    public static TaskitObjectInput getTaskitObjectInput(Object value, ProtobufTaskitEngine taskitEngine) {
        String valClass = value.getClass().getSimpleName();

        if (value instanceof Enum) {
            valClass = "Enum";
        }

        switch (valClass) {
            case "int":
            case "Integer":
                return getIntegerTaskitInput((Integer) value);
            case "long":
            case "Long":
                return getLongTaskitInput((Long) value);
            case "double":
            case "Double":
                return getDoubleTaskitInput((Double) value);
            case "float":
            case "Float":
                return getFloatTaskitInput((Float) value);
            case "boolean":
            case "Boolean":
                return getBooleanTaskitInput((Boolean) value);
            case "String":
                return getStringTaskitInput((String) value);
            case "LocalDate":
                return getDateTaskitInput((Date) taskitEngine.translateObject(value));
            case "Enum":
            return getEnumTaskitInput(taskitEngine.translateObjectAsClassSafe(Enum.class.cast(value), Enum.class));
            default:
                return getAnyTaskitInput(taskitEngine.getAnyFromObject(value));
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
