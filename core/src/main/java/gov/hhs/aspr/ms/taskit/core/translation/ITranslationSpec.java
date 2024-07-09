package gov.hhs.aspr.ms.taskit.core.translation;

import gov.hhs.aspr.ms.taskit.core.engine.ITaskitEngine;

/**
 * Interface for TranslationSpecifications (TranslationSpecs)
 */
public interface ITranslationSpec {

    /**
     * Initializes the translation spec with the given taskitEngine
     * 
     * @param <T>          the type of the taskitEngine
     * @param taskitEngine the taskitEngine the translationSpec should be
     *                     initialized with
     */
    public <T extends ITaskitEngine> void init(T taskitEngine);

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
}
