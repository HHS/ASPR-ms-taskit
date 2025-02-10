package gov.hhs.aspr.ms.taskit.protobuf.engine;

import java.time.LocalDate;
import java.util.Optional;

import gov.hhs.aspr.ms.util.errors.ContractException;

public final class TaskitObject {

    private final Optional<Object> objectVal;
    private final Optional<Integer> intVal;
    private final Optional<Double> doubleVal;
    private final Optional<Boolean> booleanVal;
    private final Optional<Float> floatVal;
    private final Optional<Long> longVal;
    private final Optional<String> stringVal;
    private final Optional<LocalDate> dateVal;
    private final Optional<Enum<?>> enumVal;

    private TaskitObject(Optional<Object> objectVal,
            Optional<Integer> i32Val,
            Optional<Double> doubleVal,
            Optional<Boolean> bVal,
            Optional<Float> floatVal,
            Optional<Long> i64Val,
            Optional<String> stringVal,
            Optional<LocalDate> dateVal,
            Optional<Enum<?>> enumVal) {
        this.objectVal = objectVal;
        this.intVal = i32Val;
        this.doubleVal = doubleVal;
        this.booleanVal = bVal;
        this.floatVal = floatVal;
        this.longVal = i64Val;
        this.stringVal = stringVal;
        this.dateVal = dateVal;
        this.enumVal = enumVal;
    }

    public TaskitObject(Object objectVal) {
        this(Optional.of(objectVal), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    }

    public TaskitObject(Integer intVal) {
        this(Optional.empty(), Optional.of(intVal), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    }

    public TaskitObject(Double doubleVal) {
        this(Optional.empty(), Optional.empty(), Optional.of(doubleVal), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    }

    public TaskitObject(Boolean booleanVal) {
        this(Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(booleanVal), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    }

    public TaskitObject(Float floatVal) {
        this(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.of(floatVal),
                Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty());
    }

    public TaskitObject(Long longVal) {
        this(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.of(longVal), Optional.empty(), Optional.empty(), Optional.empty());
    }

    public TaskitObject(String stringVal) {
        this(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.of(stringVal), Optional.empty(), Optional.empty());
    }

    public TaskitObject(LocalDate dateVal) {
        this(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.of(dateVal), Optional.empty());
    }

    public TaskitObject(Enum<?> enumVal) {
        this(Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(), Optional.empty(),
                Optional.empty(), Optional.empty(), Optional.of(enumVal));
    }

    public boolean hasObjectVal() {
        return objectVal.isPresent();
    }

    public boolean hasIntegerVal() {
        return intVal.isPresent();
    }

    public boolean hasDoubleVal() {
        return doubleVal.isPresent();
    }

    public boolean hasBooleanVal() {
        return booleanVal.isPresent();
    }

    public boolean hasFloatVal() {
        return floatVal.isPresent();
    }

    public boolean hasLongVal() {
        return longVal.isPresent();
    }

    public boolean hasStringVal() {
        return stringVal.isPresent();
    }

    public boolean hasDateVal() {
        return dateVal.isPresent();
    }

    public boolean hasEnumVal() {
        return enumVal.isPresent();
    }

    public Object getObjectVal() {
        if (!hasObjectVal()) {
            throw new ContractException(ProtobufTaskitError.INVALID_RETRIEVAL);
        }

        return objectVal.get();
    }

    public Integer getIntegerVal() {
        if (!hasIntegerVal()) {
            throw new ContractException(ProtobufTaskitError.INVALID_RETRIEVAL);
        }
        return intVal.get();
    }

    public Double getDoubleVal() {
        if (!hasDoubleVal()) {
            throw new ContractException(ProtobufTaskitError.INVALID_RETRIEVAL);
        }
        return doubleVal.get();
    }

    public Boolean getBooleanVal() {
        if (!hasBooleanVal()) {
            throw new ContractException(ProtobufTaskitError.INVALID_RETRIEVAL);
        }
        return booleanVal.get();
    }

    public Float getFloatVal() {
        if (!hasFloatVal()) {
            throw new ContractException(ProtobufTaskitError.INVALID_RETRIEVAL);
        }
        return floatVal.get();
    }

    public Long getLongVal() {
        if (!hasLongVal()) {
            throw new ContractException(ProtobufTaskitError.INVALID_RETRIEVAL);
        }
        return longVal.get();
    }

    public String getStringVal() {
        if (!hasStringVal()) {
            throw new ContractException(ProtobufTaskitError.INVALID_RETRIEVAL);
        }
        return stringVal.get();
    }

    public LocalDate getDateVal() {
        if (!hasDateVal()) {
            throw new ContractException(ProtobufTaskitError.INVALID_RETRIEVAL);
        }
        return dateVal.get();
    }

    public Enum<?> getEnumVal() {
        if (!hasEnumVal()) {
            throw new ContractException(ProtobufTaskitError.INVALID_RETRIEVAL);
        }
        return enumVal.get();
    }
}
