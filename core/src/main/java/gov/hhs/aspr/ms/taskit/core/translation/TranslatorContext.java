package gov.hhs.aspr.ms.taskit.core.translation;

import gov.hhs.aspr.ms.taskit.core.engine.ITaskitEngineBuilder;
import gov.hhs.aspr.ms.taskit.core.engine.TaskitError;
import gov.hhs.aspr.ms.util.errors.ContractException;

/**
 * Context that is used by {@link Translator}s for initialization
 * <p>
 * Note: This class exists because the TaskitEngine instance used by a
 * Translator may have different build methods, preventing the associated
 * consumer using this context from just being a consumer of a
 * ITaskitEngineBuilder
 * </p>
 */
public final class TranslatorContext {

	private final ITaskitEngineBuilder builder;

	/**
	 * Constructor for the Translator Context.
	 * 
	 * @param builder the builder of the TaskitEngine
	 */
	public TranslatorContext(ITaskitEngineBuilder builder) {
		this.builder = builder;
	}

	/**
	 * Gets the Taskit engine builder for this context
	 * <p>
	 * See the note on this class for the usage of this method.
	 * </p>
	 * 
	 * @param <T> the type of the TaskitEngine
	 * @param classRef the classRef of the TaskitEngine type
	 * @return the taskitEngineBuilder
	 * @throws ContractException {@linkplain TaskitError#INVALID_TASKIT_ENGINE_BUILDER_CLASS_REF}
	 *                           if the given classRef does not match the class
	 */
	public <T extends ITaskitEngineBuilder> T getTaskitEngineBuilder(Class<T> classRef) {
		if (classRef.isAssignableFrom(this.builder.getClass())) {
			return classRef.cast(this.builder);
		}

		throw new ContractException(TaskitError.INVALID_TASKIT_ENGINE_BUILDER_CLASS_REF,
				"No Taskit Engine Builder was found for the type: " + classRef.getName());

	}
}
