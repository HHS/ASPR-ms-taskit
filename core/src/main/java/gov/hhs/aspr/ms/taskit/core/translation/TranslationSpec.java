package gov.hhs.aspr.ms.taskit.core.translation;

import java.util.Objects;

import gov.hhs.aspr.ms.taskit.core.engine.ITaskitEngine;
import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngine;
import gov.hhs.aspr.ms.taskit.core.engine.TaskitCoreError;
import gov.hhs.aspr.ms.util.errors.ContractException;

/**
 * Core implementation of the {@link ITranslationSpec} that must be
 * implemented by each needed translationSpec.
 * <p>
 * Note: No reference to a {@link TaskitEngine} exists in this class, and
 * must be implemented by the implementing class.
 */
public abstract class TranslationSpec<I, A> implements ITranslationSpec {
    private boolean initialized = false;

    /**
     * @implNote All child TranslationSpecs must call
     *           super() otherwise there will be an exception throw in the
     *           TaskitEngine
     */
    public <T extends ITaskitEngine> void init(T taskitEngine) {
        this.initialized = true;
    }

    public boolean isInitialized() {
        return this.initialized;
    }

    /**
     * @implNote The implementation of the
     *           {@link ITranslationSpec#translate(Object)} method
     *           <p>
     *           It first checks if the object class is exactly equal to either the
     *           App or
     *           Input Class and if so, calls the related method
     *           </p>
     *           <p>
     *           It then checks if the the object class is assignable from either
     *           the App or
     *           Input Class and if so, calls the related method
     *           </p>
     *           <p>
     *           If no match can be found, an exception is thrown
     *           </p>
     * @throws ContractException {@linkplain TaskitCoreError#UNKNOWN_OBJECT} if
     *                           no match can be found between the passed in object
     *                           and the given appClass and InputClass
     */
    @SuppressWarnings("unchecked")
    public <T> T translate(Object obj) {
        checkInit();

        if ((this.getAppObjectClass() == obj.getClass())) {
            return (T) this.translateAppObject((A) obj);
        }

        if ((this.getInputObjectClass() == obj.getClass())) {
            return (T) this.translateInputObject((I) obj);
        }

        if ((this.getAppObjectClass().isAssignableFrom(obj.getClass()))) {
            return (T) this.translateAppObject((A) obj);
        }

        if ((this.getInputObjectClass().isAssignableFrom(obj.getClass()))) {
            return (T) this.translateInputObject((I) obj);
        }

        throw new ContractException(TaskitCoreError.UNKNOWN_OBJECT, "Object is not a "
                + this.getAppObjectClass().getName() + " and it is not a " + this.getInputObjectClass().getName());

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

    /*
     * checks the initialized flag on this translation spec and throws an exception
     * if it has not been initialized
     */
    private void checkInit() {
        if (!this.initialized) {
            throw new ContractException(TaskitCoreError.UNINITIALIZED_TRANSLATION_SPEC);
        }
    }
}
