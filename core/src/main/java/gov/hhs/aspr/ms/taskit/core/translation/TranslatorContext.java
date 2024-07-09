package gov.hhs.aspr.ms.taskit.core.translation;

import gov.hhs.aspr.ms.taskit.core.engine.ITaskitEngineBuilder;
import gov.hhs.aspr.ms.taskit.core.engine.TaskitCoreError;
import gov.hhs.aspr.ms.util.errors.ContractException;

/**
 * Context that is used by {@link Translator}s for initialization
 * <p>
 * Note: This class exists because the TaskitEngine instance used by a
 * Translator may have
 * different build methods, preventing the associated
 * consumer that this context is used in from just being a consumer of a
 * ITaskitEngineBuilder
 */
public final class TranslatorContext {

    private final ITaskitEngineBuilder builder;

    /**
     * Constructor for the Translator Context
     * 
     * @param builder the builder of the TaskitEngine
     */
    public TranslatorContext(ITaskitEngineBuilder builder) {
        this.builder = builder;
    }

    /**
     * Gets the taskit engine builder for this context
     * <p>
     * see note on this class for the usage of this method
     * 
     * @param <T>      the type of the TaskitEngine
     * @param classRef the classRef of the TaskitEngine type
     * @return the taskitEngineBuilder
     * @throws ContractException {@linkplain TaskitCoreError#INVALID_TASKIT_ENGINE_BUILDER_CLASS_REF}
     *                           if the given classRef does not match the class or
     *                           the taskitEngineBuilder is null
     */
    public <T extends ITaskitEngineBuilder> T getTaskitEngineBuilder(final Class<T> classRef) {
        if (classRef.isAssignableFrom(this.builder.getClass())) {
            return classRef.cast(this.builder);
        }

        throw new ContractException(TaskitCoreError.INVALID_TASKIT_ENGINE_BUILDER_CLASS_REF,
                "No Taskit Engine Builder was found for the type: " + classRef.getName());

    }
}
