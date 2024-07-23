package gov.hhs.aspr.ms.taskit.core.engine;

import gov.hhs.aspr.ms.taskit.core.translation.ITranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.Translator;

/**
 * Interface for TaskitEngine Builders
 */
public interface ITaskitEngineBuilder {

    /**
     * Adds the given {@link ITranslationSpec} to the TaskitEngine
     * 
     * @param translationSpec the translationSpec to add
     * @return the builder instance
     */
    public ITaskitEngineBuilder addTranslationSpec(ITranslationSpec translationSpec);

    /**
     * Adds the given {@link Translator} to the TaskitEngine
     * 
     * @param translator the translator to add
     * @return the builder instance
     */
    public ITaskitEngineBuilder addTranslator(Translator translator);
}
