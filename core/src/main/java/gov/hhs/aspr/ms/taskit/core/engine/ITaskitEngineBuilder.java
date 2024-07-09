package gov.hhs.aspr.ms.taskit.core.engine;

import gov.hhs.aspr.ms.taskit.core.translation.TranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.Translator;

/**
 * Interface for TaskitEngine Builders
 */
public interface ITaskitEngineBuilder {
    /**
     * @return the built TaskitEngine
     */
    public ITaskitEngine build();

    /**
     * Adds the given {@link TranslationSpec} to the TaskitEngine
     * 
     * @param <I>             the input type
     * @param <A>             the app type
     * @param translationSpec the translationSpec to add
     * @return the builder instance
     */
    public <I, A> ITaskitEngineBuilder addTranslationSpec(TranslationSpec<I, A> translationSpec);

    /**
     * Adds the given {@link Translator} to the TaskitEngine
     * 
     * @param translator the translator to add
     * @return the builder instance
     */
    public ITaskitEngineBuilder addTranslator(Translator translator);
}
