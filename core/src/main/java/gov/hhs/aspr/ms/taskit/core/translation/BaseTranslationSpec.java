package gov.hhs.aspr.ms.taskit.core.translation;

import gov.hhs.aspr.ms.taskit.core.engine.ITaskitEngine;

/**
 * Base interface for TranslationSpecifications (TranslationSpecs) Package level
 * access
 */
public interface BaseTranslationSpec {
    <T extends ITaskitEngine> void init(T taskitEngine);

    <T> T convert(Object object);

    boolean isInitialized();
}
