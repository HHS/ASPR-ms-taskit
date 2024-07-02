package gov.hhs.aspr.ms.taskit.core.engine;

import java.io.IOException;
import java.nio.file.Path;

public interface ITaskitEngine {

    public TaskitEngine getTaskitEngine();

    public TaskitEngineId getTaskitEngineId();

    public <O> void write(Path path, O object) throws IOException;

    public <O> void translateAndWrite(Path path, O object) throws IOException;

    public <OC, O extends OC> void translateAndWrite(Path path, O object, Class<OC> classRef)
            throws IOException;

    public <I> I read(Path path, Class<I> classRef) throws IOException;

    public <T, I> T readAndTranslate(Path path, Class<I> classRef) throws IOException;

    public <T> T translateObject(Object object);

    public <T, O extends C, C> T translateObjectAsClassSafe(O object, Class<C> classRef);

    public <T, O, C> T translateObjectAsClassUnsafe(O object, Class<C> classRef);
}
