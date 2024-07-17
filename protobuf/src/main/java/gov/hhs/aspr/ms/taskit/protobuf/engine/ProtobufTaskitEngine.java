package gov.hhs.aspr.ms.taskit.protobuf.engine;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

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
    protected final Map<String, Class<?>> typeUrlToClassMap;

    protected ProtobufTaskitEngine(Map<String, Class<?>> typeUrlToClassMap, TaskitEngineData taskitEngineData,
            TaskitEngineId taskitEngineId) {
        super(taskitEngineData, taskitEngineId);
        this.typeUrlToClassMap = new LinkedHashMap<>(typeUrlToClassMap);

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
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Objects.hash(typeUrlToClassMap);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!super.equals(obj)) {
            return false;
        }

        if (!(obj instanceof ProtobufTaskitEngine)) {
            return false;
        }
        
        ProtobufTaskitEngine other = (ProtobufTaskitEngine) obj;
        return Objects.equals(typeUrlToClassMap, other.typeUrlToClassMap);
    }

}
