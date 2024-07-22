package gov.hhs.aspr.ms.taskit.core.translation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngine;
import gov.hhs.aspr.ms.taskit.core.engine.TaskitError;
import gov.hhs.aspr.ms.util.errors.ContractException;

/**
 * Core TranslationSpec that must be
 * implemented by each needed translationSpec.
 * <p>
 * The implementing spec must define the type of the TaskitEngine it intends to
 * use
 */
public abstract class TranslationSpec<I, A, E extends TaskitEngine> implements ITranslationSpec {
    private boolean initialized = false;
    protected E taskitEngine;
    private final Class<E> taskitEngineClass;

    protected TranslationSpec(Class<E> taskitEngineClass) {
        this.taskitEngineClass = taskitEngineClass;
    }

    /**
     * Initializes the translation spec with the given taskitEngine
     * All child TranslationSpecs must call
     * super() otherwise there will be an exception throw in the
     * TaskitEngine
     * 
     * @param taskitEngine the taskitEngine the translationSpec should be
     *                     initialized with
     * @throws ContractException {@link TaskitError#INVALID_TASKIT_ENGINE} if the
     *                           given taskitEngine is of a different type than the
     *                           one provided in the type parameter.
     */
    @Override
    public final void init(TaskitEngine taskitEngine) {
        // make sure assignable from E
        if (!(this.taskitEngineClass.isAssignableFrom(taskitEngine.getClass()))) {
            throw new ContractException(TaskitError.INVALID_TASKIT_ENGINE,
                    "given:" + taskitEngine.getClass().getName() + " but expected " + this.taskitEngineClass.getName());
        }

        this.taskitEngine = this.taskitEngineClass.cast(taskitEngine);
        this.initialized = true;
    }

    /**
     * @return the initialized flag
     */
    public boolean isInitialized() {
        return this.initialized;
    }

    /**
     * Given an object, translates it if the translationSpec knows how to translate
     * it
     * <p>
     * It first checks if the object class is exactly equal to either the
     * App or
     * Input Class
     * </p>
     * <p>
     * It then checks if the the object class is assignable from either
     * the App or
     * Input Class
     * </p>
     * <p>
     * after the above checks, calls the appropriate method
     * </p>
     * <p>
     * If the object is not able to be translated by this spec, the exception is
     * thrown
     * </p>
     * 
     * @param <T>    the translated type
     * @param object the object to translate
     * @return the translated object
     * @throws ContractException {@linkplain TaskitError#UNKNOWN_OBJECT} if the
     *                           given object cannot be translated by this
     *                           translation spec
     */
    @SuppressWarnings("unchecked")
    public <T> T translate(Object object) {
        Class<?> objectClass = object.getClass();

        boolean isAppObject = this.getAppObjectClass() == objectClass;
        boolean isInObject = this.getInputObjectClass() == objectClass;

        boolean isAssignAppObject = this.getAppObjectClass().isAssignableFrom(objectClass);
        boolean isAssignInObject = this.getInputObjectClass().isAssignableFrom(objectClass);

        boolean shouldTranslateAsApp = (isAppObject && !isInObject) || (isAssignAppObject && !isAssignInObject);
        boolean shouldTranslateAsIn = (isInObject && !isAppObject) || (isAssignInObject && !isAssignAppObject);

        if (shouldTranslateAsApp) {
            return (T) this.translateAppObject((A) object);
        }

        if (shouldTranslateAsIn) {
            return (T) this.translateInputObject((I) object);
        }

        throw new ContractException(TaskitError.UNKNOWN_OBJECT, "Object is not a "
                + this.getAppObjectClass().getName() + " and it is not a " + this.getInputObjectClass().getName()
                + ". Given object is of type: " + objectClass.getName());

    }

    @Override
    public int hashCode() {
        return Objects.hash(initialized, getAppObjectClass(), getInputObjectClass());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null) {
            return false;
        }

        if (!(obj instanceof TranslationSpec)) {
            return false;
        }

        @SuppressWarnings("rawtypes")
        TranslationSpec other = (TranslationSpec) obj;

        // if different app class, not equal
        if (getAppObjectClass() != other.getAppObjectClass()) {
            return false;
        }

        // if different input class, not equal
        if (getInputObjectClass() != other.getInputObjectClass()) {
            return false;
        }

        // if not both initialized, not equal
        return initialized == other.initialized;
    }

    @Override
    public final Map<Class<?>, ITranslationSpec> getTranslationSpecClassMapping() {
        final Map<Class<?>, ITranslationSpec> translationSpecClassMap = new LinkedHashMap<>();

        translationSpecClassMap.put(getAppObjectClass(), this);
        translationSpecClassMap.put(getInputObjectClass(), this);

        return translationSpecClassMap;
    }

    /**
     * Translates the given object to its corresponding app type
     * 
     * @param inputObject the input object to translate
     * @return the translated object
     */
    protected abstract A translateInputObject(I inputObject);

    /**
     * Translates the given object to its corresponding input type
     * 
     * @param appObject the app object to translate
     * @return the translated object
     */
    protected abstract I translateAppObject(A appObject);

    /**
     * @return the class of the app type
     */
    public abstract Class<A> getAppObjectClass();

    /**
     * @return the class of the input type
     */
    public abstract Class<I> getInputObjectClass();
}
