package gov.hhs.aspr.ms.taskit.protobuf.translation.specs;

import java.time.LocalDate;

import com.google.protobuf.Any;
import com.google.type.Date;

import gov.hhs.aspr.ms.taskit.protobuf.engine.ProtobufTaskitError;
import gov.hhs.aspr.ms.taskit.protobuf.engine.TaskitObject;
import gov.hhs.aspr.ms.taskit.protobuf.objects.TaskitObjectInput;
import gov.hhs.aspr.ms.taskit.protobuf.objects.WrapperEnumValue;
import gov.hhs.aspr.ms.taskit.protobuf.translation.ProtobufTranslationSpec;
import gov.hhs.aspr.ms.util.errors.ContractException;

/**
 * TranslationSpec that defines how to translate from a {@link TaskitObject} to
 * a
 * {@link TaskitObjectInput} type and vice versa.
 */
public class TaskitObjectTranslationSpec extends ProtobufTranslationSpec<TaskitObjectInput, TaskitObject> {

    @Override
    protected TaskitObject translateInputObject(TaskitObjectInput inputObject) {
        if (inputObject.hasI32Val()) {
            return new TaskitObject(inputObject.getI32Val());
        }

        if (inputObject.hasI64Val()) {
            return new TaskitObject(inputObject.getI64Val());
        }

        if (inputObject.hasDVal()) {
            return new TaskitObject(inputObject.getDVal());
        }

        if (inputObject.hasFVal()) {
            return new TaskitObject(inputObject.getFVal());
        }

        if (inputObject.hasSVal()) {
            return new TaskitObject(inputObject.getSVal());
        }

        if (inputObject.hasDateVal()) {
            return new TaskitObject((LocalDate) this.taskitEngine.translateObject(inputObject.getDateVal()));
        }

        if (inputObject.hasEnumVal()) {
            return new TaskitObject((Enum<?>) this.taskitEngine.translateObject(inputObject.getEnumVal()));
        }

        if (inputObject.hasMVal()) {
            Object o = this.taskitEngine.getObjectFromAny(inputObject.getMVal());
            return new TaskitObject(o);
        }

        throw new ContractException(ProtobufTaskitError.MALFORMED_TASKIT_OBJECT);
    }

    @Override
    protected TaskitObjectInput translateAppObject(TaskitObject appObject) {
        if (appObject.hasIntVal()) {
            return TaskitObjectInput.newBuilder().setI32Val(appObject.getIntVal()).build();
        }

        if (appObject.hasLongVal()) {
            return TaskitObjectInput.newBuilder().setI64Val(appObject.getLongVal()).build();
        }

        if (appObject.hasDoubleVal()) {
            return TaskitObjectInput.newBuilder().setDVal(appObject.getDoubleVal()).build();
        }

        if (appObject.hasFloatVal()) {
            return TaskitObjectInput.newBuilder().setFVal(appObject.getFloatVal()).build();
        }

        if (appObject.hasStringVal()) {
            return TaskitObjectInput.newBuilder().setSVal(appObject.getStringVal()).build();
        }

        if (appObject.hasDateVal()) {
            return TaskitObjectInput.newBuilder()
                    .setDateVal((Date) this.taskitEngine.translateObject(appObject.getDateVal())).build();
        }

        if (appObject.hasEnumVal()) {
            return TaskitObjectInput.newBuilder()
                    .setEnumVal((WrapperEnumValue) this.taskitEngine
                            .translateObjectAsClassSafe(Enum.class.cast(appObject.getEnumVal()), Enum.class))
                    .build();
        }

        if (appObject.hasObjectVal()) {
            Any any = this.taskitEngine.getAnyFromObject(appObject.getObjectVal());
            return TaskitObjectInput.newBuilder().setMVal(any).build();
        }

        throw new ContractException(ProtobufTaskitError.MALFORMED_TASKIT_OBJECT);
    }

    @Override
    public Class<TaskitObject> getAppObjectClass() {
        return TaskitObject.class;
    }

    @Override
    public Class<TaskitObjectInput> getInputObjectClass() {
        return TaskitObjectInput.class;
    }
}
