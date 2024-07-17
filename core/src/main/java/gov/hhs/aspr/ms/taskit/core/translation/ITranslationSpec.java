package gov.hhs.aspr.ms.taskit.core.translation;

import java.util.Map;

import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngine;

/**
 * Interface for TranslationSpecifications (TranslationSpecs)
 */
public interface ITranslationSpec<E extends TaskitEngine> {

    /**
     * Initializes the translation spec with the given taskitEngine
     * 
     * @param taskitEngine the taskitEngine the translationSpec should be
     *                     initialized with
     */
    public void init(TranslationSpecContext<? extends TaskitEngine> translationSpecContext);

    /**
     * Given an object, translates it if the translationSpec knows how to translate
     * it
     * 
     * @param <T>    the translated type
     * @param object the object to translate
     * @return the translated object
     */
    public <T> T translate(Object object);

    /**
     * @return the initialized flag of this translation spec
     */
    public boolean isInitialized();

    Map<Class<?>, ITranslationSpec<E>> getTranslationSpecClassMapping();
}
