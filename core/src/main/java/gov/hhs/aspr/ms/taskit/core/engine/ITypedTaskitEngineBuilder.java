package gov.hhs.aspr.ms.taskit.core.engine;

import gov.hhs.aspr.ms.taskit.core.translation.ITranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.TranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.Translator;

/**
 * Interface for TaskitEngine Builders
 */
public interface ITypedTaskitEngineBuilder<E extends TaskitEngine> extends ITaskitEngineBuilder {

    /**
     * Builds the TaskitEngine
     * 
     * @return the built taskit engine
     */
    public E build();

    /**
     * Adds the given {@link TranslationSpec} to the TaskitEngine
     * 
     * @param <E>             the taskit engine type
     * @param translationSpec the translationSpec to add
     * @return the builder instance
     */
    public ITypedTaskitEngineBuilder<E> addTranslationSpec(ITranslationSpec<E> translationSpec);

    /**
     * Adds the given {@link Translator} to the TaskitEngine
     * 
     * @param translator the translator to add
     * @return the builder instance
     */
    public ITypedTaskitEngineBuilder<E> addTranslator(Translator translator);
}
