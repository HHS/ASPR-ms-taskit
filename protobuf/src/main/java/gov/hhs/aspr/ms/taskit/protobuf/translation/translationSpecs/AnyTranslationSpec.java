package gov.hhs.aspr.ms.taskit.protobuf.translation.translationSpecs;

import com.google.protobuf.Any;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;

import gov.hhs.aspr.ms.taskit.protobuf.translation.ProtobufTranslationSpec;

/**
 * TranslationSpec that defines how to translate from any Java Object to a
 * Protobuf {@link Any} type and vice versa
 */
public class AnyTranslationSpec extends ProtobufTranslationSpec<Any, Object> {

    @Override
    protected Object translateInputObject(Any inputObject) {
        String fullTypeUrl = inputObject.getTypeUrl();
        String[] parts = fullTypeUrl.split("/");

        if (parts.length != 2) {
            throw new RuntimeException("Malformed type url");
        }

        String typeUrl = parts[1];
        Class<?> classRef = this.taskitEngine.getClassFromTypeUrl(typeUrl);
        Class<? extends Message> messageClassRef;

        if (!(Message.class.isAssignableFrom(classRef))) {
            throw new RuntimeException("Message is not assignable from " + classRef.getName());
        }

        messageClassRef = classRef.asSubclass(Message.class);

        return unpackMessage(inputObject, messageClassRef);
    }

    protected <U extends Message> Object unpackMessage(Any inputObject, Class<U> messageClassRef) {
        try {
            Message unpackedMessage = inputObject.unpack(messageClassRef);

            return this.taskitEngine.translateObject(unpackedMessage);
        } catch (InvalidProtocolBufferException e) {
            throw new RuntimeException("Unable To unpack any type to given class: " + messageClassRef.getName(), e);
        }
    }

    @Override
    protected Any translateAppObject(Object appObject) {

        Message message;

        if (Enum.class.isAssignableFrom(appObject.getClass())) {
            message = this.taskitEngine.translateObjectAsClassSafe(Enum.class.cast(appObject), Enum.class);
        }

        // in the event that the object was translateed BEFORE calling this
        // translationSpec, there is no need to translate it again.
        else if (Message.class.isAssignableFrom(appObject.getClass())) {
            message = Message.class.cast(appObject);
        } else {
            message = this.taskitEngine.translateObject(appObject);
        }

        return Any.pack(message);
    }

    @Override
    public Class<Object> getAppObjectClass() {
        return Object.class;
    }

    @Override
    public Class<Any> getInputObjectClass() {
        return Any.class;
    }
}
