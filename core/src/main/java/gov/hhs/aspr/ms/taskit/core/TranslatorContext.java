package gov.hhs.aspr.ms.taskit.core;

import util.errors.ContractException;

/**
 * Context that is used by {@link Translator}s
 * 
 * Note: This class exists because the subclassed TranslationEngine may have
 * different build methods than the abstract one, preventing the associated
 * consumer that this context is used in from just being a consumer of a
 * TranslationEngine.Builder
 */
public class TranslatorContext {

    private final TranslationEngine.Builder builder;

    public TranslatorContext(TranslationEngine.Builder builder) {
        this.builder = builder;
    }

    /**
     * Returns an instance of the TranslationEngine Builder
     * 
     * @param <T> the type of the TranslationEngine
     * @throws ContractException {@linkplain CoreTranslationError#INVALID_TRANSLATION_ENGINE_BUILDER_CLASS_REF}
     *                           if the given classRef does not match the class or
     *                           the translatorCoreBuilder is null
     */
    public <T extends TranslationEngine.Builder> T getTranslationEngineBuilder(Class<T> classRef) {
        if (this.builder.getClass().isAssignableFrom(classRef)) {
            return classRef.cast(this.builder);
        }

        throw new ContractException(CoreTranslationError.INVALID_TRANSLATION_ENGINE_BUILDER_CLASS_REF,
                "No Translation Engine Builder was found for the type: " + classRef.getName());

    }
}
