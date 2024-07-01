package gov.hhs.aspr.ms.taskit.protobuf.translationSpecs;

import java.lang.reflect.InvocationTargetException;

import com.google.protobuf.Any;
import com.google.protobuf.ProtocolMessageEnum;

import gov.hhs.aspr.ms.taskit.protobuf.ProtobufTranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.input.WrapperEnumValue;

/**
 * TranslationSpec that defines how to convert from any Java Enum to a Protobuf
 * {@link WrapperEnumValue} type and vice versa
 * <p>
 * <b>Note: A {@link WrapperEnumValue} is specifically used to wrap a Enum into
 * a Protobuf {@link Any} type</b>
 * </p>
 * <p>
 * The Protobuf {@link Any} type does not natively support enums, only
 * primitives and other Protobuf Messages
 * </p>
 */
@SuppressWarnings("rawtypes")
public class EnumTranslationSpec extends ProtobufTranslationSpec<WrapperEnumValue, Enum> {

    @Override
    protected Enum convertInputObject(WrapperEnumValue inputObject) {
        String typeUrl = inputObject.getEnumTypeUrl();
        String value = inputObject.getValue();

        Class<?> classRef = this.taskitEngine.getClassFromTypeUrl(typeUrl);

        try {
            Enum inputInput = (Enum<?>) classRef.getMethod("valueOf", String.class).invoke(null, value);
            return this.taskitEngine.translateObject(inputInput);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
                | SecurityException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    protected WrapperEnumValue translateAppObject(Enum appObject) {
        ProtocolMessageEnum messageEnum = this.taskitEngine.translateObject(appObject);

        WrapperEnumValue wrapperEnumValue = WrapperEnumValue.newBuilder()
                .setValue(messageEnum.getValueDescriptor().getName())
                .setEnumTypeUrl(messageEnum.getDescriptorForType().getFullName()).build();

        return wrapperEnumValue;
    }

    @Override
    public Class<Enum> getAppObjectClass() {
        return Enum.class;
    }

    @Override
    public Class<WrapperEnumValue> getInputObjectClass() {
        return WrapperEnumValue.class;
    }
}
