package gov.hhs.aspr.ms.taskit.core.engine;

import gov.hhs.aspr.ms.taskit.core.translation.ITranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.TranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.Translator;

/** 
 * Interface for TaskitEngine Builders
 */
public interface ITaskitEngineBuilder {
    /**
     * Adds the given {@link TranslationSpec} to the TaskitEngine
     * @param <E> the taskit engine type
     * @param translationSpec the translationSpec to add
     * @return the builder instance
     */
    public <E extends TaskitEngine> ITaskitEngineBuilder addTranslationSpec(ITranslationSpec<E> translationSpec);

    /**
     * Adds the given {@link Translator} to the TaskitEngine
     * @param translator the translator to add
     * @return the builder instance
     */
    public ITaskitEngineBuilder addTranslator(Translator translator);
}
