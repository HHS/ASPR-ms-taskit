package gov.hhs.aspr.ms.taskit.protobuf.engine;

import java.util.LinkedHashMap;
import java.util.Map;

import com.google.protobuf.Any;

import gov.hhs.aspr.ms.taskit.core.engine.ITaskitEngine;
import gov.hhs.aspr.ms.taskit.core.engine.TaskitCoreError;
import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngine;
import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngineId;
import gov.hhs.aspr.ms.taskit.core.translation.TranslationSpec;
import gov.hhs.aspr.ms.taskit.protobuf.translation.specs.AnyTranslationSpec;
import gov.hhs.aspr.ms.util.errors.ContractException;

public abstract class ProtobufTaskitEngine implements ITaskitEngine {

    // this is used specifically for Any message types to pack and unpack them
    protected final Map<String, Class<?>> typeUrlToClassMap;

    protected final TaskitEngine taskitEngine;

    protected ProtobufTaskitEngine(Map<String, Class<?>> typeUrlToClassMap, TaskitEngine taskitEngine) {
        this.typeUrlToClassMap = new LinkedHashMap<>(typeUrlToClassMap);
        this.taskitEngine = taskitEngine;
    }

    /**
     * Given an object of type {@link Any}, will translate it to the resulting
     * object
     * <p>
     * Will ultimately use the {@link AnyTranslationSpec} to accomplish this
     * </p>
     * 
     * @param <T> the return type
     */
    public final <T> T getObjectFromAny(Any anyValue) {
        return translateObject(anyValue);
    }

    /**
     * Given an object , will translate it to an {@link Any} type
     * <p>
     * Will use the {@link AnyTranslationSpec} to accomplish this
     * </p>
     */
    public final Any getAnyFromObject(Object object) {
        return translateObjectAsClassUnsafe(object, Any.class);
    }

    /**
     * Given an object , will translate it to an {@link Any} type
     * <p>
     * This method call differs from {@link #getAnyFromObject(Object)} in that it
     * will first translate the object using the safe parent class by calling
     * {@link #translateObjectAsClassSafe(Object, Class)} and will then use the
     * {@link AnyTranslationSpec} to wrap the resulting translated object in an
     * {@link Any}
     * </p>
     * 
     * @param <U> the parent Class
     * @param <M> the object class
     * @throws ContractException {@linkplain TaskitCoreError#UNKNOWN_TRANSLATION_SPEC}
     *                           if no translationSpec was provided for the given
     *                           parentClassRef
     */
    public final <U, M extends U> Any getAnyFromObjectAsClassSafe(M object, Class<U> parentClassRef) {
        U translatedObject = translateObjectAsClassSafe(object, parentClassRef);

        return translateObjectAsClassUnsafe(translatedObject, Any.class);
    }

    /**
     * Given an object, uses the class of the object to obtain the translationSpec
     * and then calls {@link TranslationSpec#translate(Object)}
     * <p>
     * this conversion method will be used approx ~90% of the time
     * </p>
     * 
     * @param <T> the return type after translating
     * @throws ContractException
     *                           <ul>
     *                           <li>{@linkplain TaskitCoreError#NULL_OBJECT_FOR_TRANSLATION}
     *                           if the passed in object is null</li>
     *                           <li>{@linkplain TaskitCoreError#UNKNOWN_TRANSLATION_SPEC}
     *                           if no translationSpec was provided for the given
     *                           objects class</li>
     *                           </ul>
     */
    @Override
    public final <T> T translateObject(Object object) {
        return this.taskitEngine.translateObject(object);
    }

    /**
     * Given an object, uses the parent class of the object to obtain the
     * translationSpec and then calls {@link TranslationSpec#translate(Object)}
     * <p>
     * This method call is safe in the sense that the type parameters ensure that
     * the passed in object is actually a child of the passed in parentClassRef
     * </p>
     * <p>
     * this conversion method will be used approx ~7% of the time
     * </p>
     * 
     * @param <T> the return type after translating
     * @param <O> the type of the object; extends U
     * @param <P> the parent type of the object and the class for which
     *            translationSpec you want to use
     * @throws ContractException
     *                           <ul>
     *                           <li>{@linkplain TaskitCoreError#NULL_OBJECT_FOR_TRANSLATION}
     *                           if the passed in object is null</li>
     *                           <li>{@linkplain TaskitCoreError#NULL_CLASS_REF}
     *                           if the passed in parentClassRef is null</li>
     *                           <li>{@linkplain TaskitCoreError#UNKNOWN_TRANSLATION_SPEC}
     *                           if no translationSpec was provided for the given
     *                           objects class</li>
     *                           </ul>
     */
    @Override
    public final <T, O extends P, P> T translateObjectAsClassSafe(O object, Class<P> classRef) {
        return this.taskitEngine.translateObjectAsClassSafe(object, classRef);
    }

    /**
     * Given an object, uses the passed in class to obtain the translationSpec and
     * then calls {@link TranslationSpec#translate(Object)}
     * <p>
     * This method call is unsafe in the sense that the type parameters do not
     * ensure any relationship between the passed in object and the passed in
     * classRef.
     * </p>
     * <p>
     * A common use case for using this conversion method would be to call a
     * translationSpec that will wrap the given object in another object.
     * </p>
     * <p>
     * this conversion method will be used approx ~3% of the time
     * </p>
     * 
     * @param <T> the return type after translating
     * @param <O> the type of the object
     * @param <P> the type of the class for which translationSpec you want to use
     * @throws ContractException
     *                           <ul>
     *                           <li>{@linkplain TaskitCoreError#NULL_OBJECT_FOR_TRANSLATION}
     *                           if the passed in object is null</li>
     *                           <li>{@linkplain TaskitCoreError#NULL_CLASS_REF}
     *                           if the passed in objectClassRef is null</li>
     *                           <li>{@linkplain TaskitCoreError#UNKNOWN_TRANSLATION_SPEC}
     *                           if no translationSpec was provided for the given
     *                           objects class</li>
     *                           </ul>
     */
    @Override
    public final <T, O, P> T translateObjectAsClassUnsafe(O object, Class<P> classRef) {
        return this.taskitEngine.translateObjectAsClassUnsafe(object, classRef);
    }

    /**
     * Given a typeUrl, returns the associated Protobuf Message type Class, if it
     * has been previously provided
     * 
     * @throws ContractException {@linkplain ProtobufTaskitError#UNKNOWN_TYPE_URL}
     *                           if the given type url does not exist. This could be
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

    /**
     * Returns an instance of the BaseTaskitEngine for this translation engine
     */
    @Override
    public final TaskitEngine getTaskitEngine() {
        return this.taskitEngine;
    }

    /**
     * returns the {@link TaskitEngineId} of this TaskitEngine
     * 
     * guaranteed to NOT be {@link TaskitEngineId#UNKNOWN}
     */
    @Override
    public final TaskitEngineId getTaskitEngineId() {
        return this.taskitEngine.getTaskitEngineId();
    }
}
