package gov.hhs.aspr.ms.taskit.core.translation;

import gov.hhs.aspr.ms.taskit.core.engine.ITaskitEngine;

/**
 * Interface for TranslationSpecifications (TranslationSpecs)
 */
public interface ITranslationSpec {
    <T extends ITaskitEngine> void init(T taskitEngine);

    <T> T translate(Object object);

    boolean isInitialized();
}
