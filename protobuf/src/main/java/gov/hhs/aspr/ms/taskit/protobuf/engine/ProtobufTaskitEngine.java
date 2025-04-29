package gov.hhs.aspr.ms.taskit.protobuf.engine;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.protobuf.Any;
import com.google.protobuf.Message;

import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngine;
import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngineData;
import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngineId;
import gov.hhs.aspr.ms.taskit.core.engine.TaskitError;
import gov.hhs.aspr.ms.taskit.protobuf.translation.specs.AnyTranslationSpec;
import gov.hhs.aspr.ms.util.errors.ContractException;

/**
 * Abstract ProtobufTaskitEngine that defines the common methods that are shared
 * between a JSON engine and a Binary Engine
 */
public abstract class ProtobufTaskitEngine extends TaskitEngine {

    // this is used specifically for Any message types to pack and unpack them
    protected final Map<String, Class<?>> typeUrlToClassMap = new LinkedHashMap<>();

    protected ProtobufTaskitEngine(Map<String, Class<?>> typeUrlToClassMap, TaskitEngineData taskitEngineData,
            TaskitEngineId taskitEngineId) {
        super(taskitEngineData, taskitEngineId);
        this.typeUrlToClassMap.putAll(typeUrlToClassMap);

    }

    @Override
    protected final <O> void writeToFile(File file, O outputObject) throws IOException {
        if (!Message.class.isAssignableFrom(outputObject.getClass())) {
            throw new ContractException(TaskitError.INVALID_OUTPUT_CLASS, Message.class.getName());
        }

        this.writeToFile(file, Message.class.cast(outputObject));
    }

    @Override
    protected final <I> I readFile(File file, Class<I> inputClassRef) throws IOException {
        if (!Message.class.isAssignableFrom(inputClassRef)) {
            throw new ContractException(TaskitError.INVALID_INPUT_CLASS, Message.class.getName());
        }

        Message.Builder builder = ProtobufTaskitEngineHelper
                .getBuilderForMessage(inputClassRef.asSubclass(Message.class));

        Message message = this.readFile(file, builder);

        return inputClassRef.cast(message);
    }

    protected abstract void writeToFile(File file, Message message) throws IOException;

    protected abstract Message readFile(File file, Message.Builder builder) throws IOException;

    /**
     * Translate a object of type {@link Any} to the wrapped object's corresponding
     * app type
     * <p>
     * this will call {@link AnyTranslationSpec#translate(Object)} via
     * {@link #translateObject(Object)}
     * 
     * @param <T>      the object type
     * @param anyValue the any value to get an object from
     * @return the object that was wrapped in the any
     * 
     * @throws ContractException
     *                           <ul>
     *                           <li>{@linkplain TaskitError#NULL_OBJECT_FOR_TRANSLATION}
     *                           if the passed in object is null</li>
     *                           </ul>
     */
    public final <T> T getObjectFromAny(Any anyValue) {
        return this.translateObject(anyValue);
    }

    /**
     * Translate an Object into a {@link Any} type
     * <p>
     * this will call {@link AnyTranslationSpec#translate(Object)} via
     * {@link #translateObjectAsClassUnsafe(Object, Class)}
     * 
     * @param object the object to translate
     * @return the resulting Any
     * 
     * @throws ContractException
     *                           <ul>
     *                           <li>{@linkplain TaskitError#NULL_OBJECT_FOR_TRANSLATION}
     *                           if the passed in object is null</li>
     *                           </ul>
     */
    public final Any getAnyFromObject(Object object) {
        return this.translateObjectAsClassUnsafe(object, Any.class);
    }

    /**
     * Translate an object into an {@link Any} type
     * <p>
     * This method call differs from {@link #getAnyFromObject(Object)} in that it
     * will first translate the object using the classRef by calling
     * {@link #translateObjectAsClassSafe(Object, Class)} and will then call
     * {@link #translateObjectAsClassUnsafe(Object, Class)} to translate the
     * translated object into an {@link Any} type using
     * {@link AnyTranslationSpec#translate(Object)}
     * </p>
     * 
     * @param <O>      the type of the object
     * @param <C>      the type to translate the object as
     * @param object   the object to translate
     * @param classRef the classRef of the type to translate the object as
     * @return the translated object wrapped into an Any type
     * 
     * @throws ContractException
     *                           <ul>
     *                           <li>{@linkplain TaskitError#NULL_OBJECT_FOR_TRANSLATION}
     *                           if the passed in object is null</li>
     *                           <li>{@linkplain TaskitError#NULL_CLASS_REF}
     *                           if the passed in classRef is null</li>
     *                           <li>{@linkplain TaskitError#UNKNOWN_TRANSLATION_SPEC}
     *                           if no translationSpec was provided for the given
     *                           objects class</li>
     *                           </ul>
     */
    public final <C, O extends C> Any getAnyFromObjectAsClassSafe(O object, Class<C> classRef) {
        C translatedObject = translateObjectAsClassSafe(object, classRef);

        return this.translateObjectAsClassUnsafe(translatedObject, Any.class);
    }

    /**
     * Obtain a classRef from a given typeUrl
     * 
     * @param typeUrl the typeUrl to use to obtain a classRef for
     * @return the classRef associated with the given typeUrl
     * @throws ContractException {@linkplain ProtobufTaskitError#UNKNOWN_TYPE_URL}
     *                           if the given type url does not exist.
     *                           <p>
     *                           This could be
     *                           because the type url was never provided or the type
     *                           url itself is malformed
     */
    public final Class<?> getClassFromTypeUrl(String typeUrl) {
        if (this.typeUrlToClassMap.containsKey(typeUrl)) {
            return this.typeUrlToClassMap.get(typeUrl);
        }

        throw new ContractException(ProtobufTaskitError.UNKNOWN_TYPE_URL,
                "Unable to find corresponding class for: " + typeUrl);
    }

    /*
     * Note that we don't include the type url map as part of the hash code contract
     * or the equals contract because it is
     * directly linked to the added translationSpecs, which are already part of the
     * hash code contract and equals contract.
     * Meaning that if the specs are the same, so is the map. There is never a case
     * where the map would be different outside of the specs being different.
     * However, child classes of this class are free to use the type url map as part
     * of their hash code contract and equals contract if they so wish.
     */
}
