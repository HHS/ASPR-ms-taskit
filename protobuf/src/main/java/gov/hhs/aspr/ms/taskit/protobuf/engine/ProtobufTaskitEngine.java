package gov.hhs.aspr.ms.taskit.protobuf.engine;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.protobuf.Any;

import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngine;
import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngineData;
import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngineId;
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

    /**
     * Translate a object of type {@link Any} to the wrapped object's corresponding
     * app type
     * <p>
     * this will call {@link AnyTranslationSpec#translate(Object)} via
     * {@link #translateObject(Object)}
     * 
     * @param <T>      the translated type
     * @param anyValue the object to translate
     * @return the translate object
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
     * @return the translated object translated into an Any type
     *         K
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

    @Override
    public int hashCode() {
        /*
         * Note that we don't include the type url map as part of the hash code contract because it is
         * directly linked to the added translationSpecs, which are already part of the
         * hash code contract.
         * Meaning that if the specs are the same, so is the map. There is never a case
         * where the map would be different outside of the specs being different.
         * However, child classes of this class are free to use the type url map as part
         * of their hash code contract if they so wish.
         */
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        /*
         * Note that we don't include the type url map as part of the equals contract because it is
         * directly linked to the added translationSpecs, which are already part of the
         * equals contract.
         * Meaning that if the specs are the same, so is the map. There is never a case
         * where the map would be different outside of the specs being different.
         * However, child classes of this class are free to use the type url map as part
         * of their equals contract if they so wish.
         */
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof ProtobufTaskitEngine)) {
            return false;
        }

        if (!super.equals(obj)) {
            return false;
        }
        return true;
    }

}
