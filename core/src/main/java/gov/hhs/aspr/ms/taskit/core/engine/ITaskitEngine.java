package gov.hhs.aspr.ms.taskit.core.engine;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Set;

import gov.hhs.aspr.ms.taskit.core.translation.ITranslationSpec;

public interface ITaskitEngine {

    public TaskitEngine getTaskitEngine();

    public TaskitEngineType getTaskitEngineType();

    public Set<ITranslationSpec> getTranslationSpecs();

    public <M> void write(Path path, M object) throws IOException;

    public <M> void translateAndWrite(Path path, M object) throws IOException;

    public <U, M extends U> void translateAndWrite(Path path, M object, Class<U> classRef)
            throws IOException;

    public <U> U read(Path path, Class<U> classRef) throws IOException;

    public <T, U> T readAndTranslate(Path path, Class<U> classRef) throws IOException;

    public <T> T translateObject(Object object);

    public <T, M extends U, U> T translateObjectAsClassSafe(M object, Class<U> classRef);

    public <T, M, U> T translateObjectAsClassUnsafe(M object, Class<U> classRef);
}
