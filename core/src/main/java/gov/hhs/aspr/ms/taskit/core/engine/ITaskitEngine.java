package gov.hhs.aspr.ms.taskit.core.engine;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;

import gov.hhs.aspr.ms.taskit.core.translation.BaseTranslationSpec;

public interface ITaskitEngine {

    public TaskitEngine getBaseTaskitEngine();

    public TaskitEngineType getTaskitEngineType();

    public Set<BaseTranslationSpec> getTranslationSpecs();

    public <U, M extends U> void write(Path outputPath, M objectToWrite, Optional<Class<U>> outputClassRefOverride)
            throws IOException;

    public <T, U> T read(Path inputPath, Class<U> inputClassRef) throws IOException;

    public <T> T convertObject(Object object);

    public <T, M extends U, U> T convertObjectAsSafeClass(M object, Class<U> classRef);

    public <T, M, U> T convertObjectAsUnsafeClass(M object, Class<U> classRef);
}
