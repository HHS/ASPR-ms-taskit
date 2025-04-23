package gov.hhs.aspr.ms.taskit.core.translation;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngine;
import gov.hhs.aspr.ms.taskit.core.engine.TaskitError;
import gov.hhs.aspr.ms.util.errors.ContractException;

/**
 * Core TranslationSpec that must be extended by each translationSpec
 * implementor.
 * <p>
 * The implementing spec must define the type of the TaskitEngine it intends to
 * use.
 * </p>
 */
public abstract class TranslationSpec<I, A, E extends TaskitEngine> implements ITranslationSpec {
	private boolean initialized = false;
	protected E taskitEngine;
	private final Class<E> taskitEngineClass;

	protected TranslationSpec(Class<E> taskitEngineClass) {
		this.taskitEngineClass = taskitEngineClass;
	}

	/**
	 * @throws ContractException
	 *                           <ul>
	 *                           <li>{@link TaskitError#DOUBLE_TRANSLATION_SPEC_INIT}
	 *                           if init on this translation spec has been called
	 *                           more than once</li>
	 *                           <li>{@link TaskitError#INVALID_TASKIT_ENGINE} if
	 *                           the given taskitEngine is of a different type than
	 *                           the one provided in the type parameter.</li>
	 *                           </ul>
	 */
	@Override
	public final void init(TaskitEngine taskitEngine) {
		if (this.initialized) {
			throw new ContractException(TaskitError.DOUBLE_TRANSLATION_SPEC_INIT);
		}
		// make sure assignable from E
		if (!(this.taskitEngineClass.isAssignableFrom(taskitEngine.getClass()))) {
			throw new ContractException(TaskitError.INVALID_TASKIT_ENGINE,
					"given:" + taskitEngine.getClass().getName() + " but expected " + this.taskitEngineClass.getName());
		}

		this.taskitEngine = this.taskitEngineClass.cast(taskitEngine);
		this.initialized = true;
	}

	@Override
	public final boolean isInitialized() {
		return this.initialized;
	}

	/**
	 * Given an object, translates it if the translationSpec knows how to translate
	 * it
	 * <p>
	 * It first checks if the object class is exactly equal to either the App or
	 * Input Class.
	 * </p>
	 * <p>
	 * It then checks if the the object class is assignable from either the App or
	 * Input Class.
	 * </p>
	 * <p>
	 * After the above checks, calls the appropriate method.
	 * </p>
	 * <p>
	 * If the object is cannot be translated by this spec, a contract exception is
	 * thrown.
	 * </p>
	 * 
	 * @throws ContractException
	 *                           <ul>
	 *                           <li>{@linkplain TaskitError#UNINITIALIZED_TRANSLATION_SPEC}
	 *                           if this method was called before initialization.
	 *                           <p>
	 *                           Note that under normal circumstances, this should
	 *                           never happen
	 *                           </p>
	 *                           </li>
	 *                           <li>{@linkplain TaskitError#NULL_OBJECT_FOR_TRANSLATION}
	 *                           if the given object is null</li>
	 *                           <li>{@linkplain TaskitError#UNKNOWN_OBJECT} if the
	 *                           given object cannot be translated by this
	 *                           translation spec</li>
	 *                           </ul>
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> T translate(Object object) {
		if (!this.initialized) {
			throw new ContractException(TaskitError.UNINITIALIZED_TRANSLATION_SPEC);
		}

		if (object == null) {
			throw new ContractException(TaskitError.NULL_OBJECT_FOR_TRANSLATION);
		}

		Class<?> objectClass = object.getClass();

		boolean isAppObject = this.getAppObjectClass() == objectClass;
		boolean isInObject = this.getInputObjectClass() == objectClass;

		boolean isAssignAppObject = this.getAppObjectClass().isAssignableFrom(objectClass);
		boolean isAssignInObject = this.getInputObjectClass().isAssignableFrom(objectClass);

		boolean shouldTranslateAsApp = isAppObject || (isAssignAppObject && !isAssignInObject);
		boolean shouldTranslateAsIn = isInObject || (isAssignInObject && !isAssignAppObject);

		if (shouldTranslateAsApp) {
			return (T) this.translateAppObject((A) object);
		}

		if (shouldTranslateAsIn) {
			return (T) this.translateInputObject((I) object);
		}

		throw new ContractException(TaskitError.UNKNOWN_OBJECT,
				"Object is not a " + this.getAppObjectClass().getName() + " and it is not a "
						+ this.getInputObjectClass().getName() + ". Given object is of type: " + objectClass.getName());

	}

	/**
	 * Standard implementation consistent with the {@link #equals(Object)} method
	 */
	@Override
	public int hashCode() {
		return Objects.hash(initialized, getAppObjectClass(), getInputObjectClass());
	}

	/**
	 * Two {@link TranslationSpec}s are equal if and only if they translate
	 * identical app and input types and have identical initialization states.
	 */
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
