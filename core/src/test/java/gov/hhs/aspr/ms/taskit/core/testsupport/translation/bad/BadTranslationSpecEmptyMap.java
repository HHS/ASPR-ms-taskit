package gov.hhs.aspr.ms.taskit.core.testsupport.translation.bad;

import java.util.LinkedHashMap;
import java.util.Map;

import gov.hhs.aspr.ms.taskit.core.engine.TaskitEngine;
import gov.hhs.aspr.ms.taskit.core.translation.ITranslationSpec;

public class BadTranslationSpecEmptyMap implements ITranslationSpec {

    @Override
    public void init(TaskitEngine taskitEngine) {
        throw new UnsupportedOperationException("Unimplemented method 'init'");
    }

    @Override
    public <T> T translate(Object object) {
        throw new UnsupportedOperationException("Unimplemented method 'translate'");
    }

    @Override
    public boolean isInitialized() {
        return false;
    }

    @Override
    public Map<Class<?>, ITranslationSpec> getTranslationSpecClassMapping() {
        return new LinkedHashMap<>();
    }
}
