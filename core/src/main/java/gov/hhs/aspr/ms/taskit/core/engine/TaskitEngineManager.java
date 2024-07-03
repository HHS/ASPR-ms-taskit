package gov.hhs.aspr.ms.taskit.core.engine;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import gov.hhs.aspr.ms.taskit.core.translation.TranslationSpec;
import gov.hhs.aspr.ms.util.errors.ContractException;
import gov.hhs.aspr.ms.util.resourcehelper.ResourceError;
import gov.hhs.aspr.ms.util.resourcehelper.ResourceHelper;

/**
 * The TaskitEngineManager allows {@link ITaskitEngine}s to be added to it, and
 * acts as a wrapper around the TaskitEngine read/write/translate methods.
 */
public final class TaskitEngineManager {
    private final Data data;

    TaskitEngineManager(Data data) {
        this.data = data;
    }

    final static class Data {
        private final Map<TaskitEngineId, ITaskitEngine> taskitEngineIdToEngineMap = new LinkedHashMap<>();

        Data() {
        }
    }

    public final static class Builder {
        Data data;

        Builder(Data data) {
            this.data = data;
        }

        private void validateTaskitEngine(ITaskitEngine taskitEngine) {
            if (taskitEngine == null) {
                throw new ContractException(TaskitCoreError.NULL_TASKIT_ENGINE);
            }

            if (!taskitEngine.getTaskitEngine().isInitialized()) {
                throw new ContractException(TaskitCoreError.UNINITIALIZED_TASKIT_ENGINE);
            }
        }

        private void validateTaskitEngineAdded() {
            if (this.data.taskitEngineIdToEngineMap.isEmpty()) {
                throw new ContractException(TaskitCoreError.NO_TASKIT_ENGINES);
            }
        }

        /**
         * Builds the TaskitEngineManager.
         * 
         * @throws ContractException
         *                           <ul>
         *                           <li>{@linkplain TaskitCoreError#NO_TASKIT_ENGINES}
         *                           if no taskit engines were added</li>
         *                           </ul>
         */
        public TaskitEngineManager build() {
            validateTaskitEngineAdded();

            return new TaskitEngineManager(this.data);
        }

        /**
         * package access for testing
         */
        TaskitEngineManager buildWithoutInitAndChecks() {
            return new TaskitEngineManager(this.data);
        }

        /**
         * Adds a {@link TaskitEngine} and also adds all of it's child -> parent class
         * ref mappings
         * <p>
         * For those mappings, it is a first come, first served basis, meaning that if
         * multiple taskit engines define a child -> parent relationship, only the first
         * relationship encountered is added to the internal mapping of this manager. It
         * is the responsibility of the user to make sure that there are no ambiguous
         * mappings, and if there are, that whichever taskitEngine that defines the
         * correct one gets added first.
         * <p>
         * In a future version of Taskit, there is a plan to use reflection to obtain
         * the list of relationships, which would negate the need for the user to define
         * such mappings. Until then, the mappings are added to the taskit engine and
         * then subsequently added to this engine manager.
         * 
         * @throws ContractException
         *                           <ul>
         *                           <li>{@linkplain TaskitCoreError#NULL_TASKIT_ENGINE}
         *                           if taskitEngine is null</li>
         *                           <li>{@linkplain TaskitCoreError#UNINITIALIZED_TASKIT_ENGINE}
         *                           if the taskit engine was not initialized prior to
         *                           adding it to the manager</li>
         *                           </ul>
         */
        public Builder addTaskitEngine(ITaskitEngine taskitEngine) {
            validateTaskitEngine(taskitEngine);

            this.data.taskitEngineIdToEngineMap.put(taskitEngine.getTaskitEngineId(), taskitEngine);

            return this;
        }
    }

    /**
     * Returns a new instance of Builder
     */
    public static Builder builder() {
        return new Builder(new Data());
    }

    private void validateTaskitEngine(ITaskitEngine taskitEngine) {
        if (taskitEngine == null) {
            throw new ContractException(TaskitCoreError.NULL_TASKIT_ENGINE);
        }
    }

    private void validatePath(Path path) {
        if (path == null) {
            throw new ContractException(TaskitCoreError.NULL_PATH);
        }

        ResourceHelper.validateFilePath(path);
    }

    private void validateClass(Class<?> classRef) {
        if (classRef == null) {
            throw new ContractException(TaskitCoreError.NULL_CLASS_REF);
        }
    }

    private void validateObject(Object object) {
        if (object == null) {
            throw new ContractException(TaskitCoreError.NULL_OBJECT_FOR_TRANSLATION);
        }
    }

    private void validateTaskitEngineId(TaskitEngineId taskitEngineId) {
        if (taskitEngineId == null) {
            throw new ContractException(TaskitCoreError.NULL_TASKIT_ENGINE_ID);
        }
    }

    /**
     * Using the given {@link TaskitEngineId}'s associated
     * {@link TaskitEngine}, reads the given file into the given classRef and then
     * translates it to it's corresponding type as defined by the provided
     * {@link TranslationSpec}s to the associated TaskitEngine
     * 
     * @param <I> the class to read the file as
     * 
     * @throws ContractException
     *                           <ul>
     *                           <li>{@linkplain TaskitCoreError#NULL_PATH}
     *                           if the path is null</li>
     *                           <li>{@linkplain ResourceError#FILE_PATH_IS_DIRECTORY}
     *                           if the path points to a directory instead of a
     *                           file</li>
     *                           <li>{@linkplain TaskitCoreError#NULL_CLASS_REF}
     *                           if the classRef is null</li>
     *                           <li>{@linkplain TaskitCoreError#NULL_TASKIT_ENGINE_ID}
     *                           if taskitEngineId is null</li>
     *                           <li>{@linkplain TaskitCoreError#NULL_TASKIT_ENGINE}
     *                           if taskitEngine is null</li>
     *                           </ul>
     * @throws RuntimeException  if the reading of the file encounters an
     *                           IOException
     */
    public <I> I read(Path path, Class<I> classRef, TaskitEngineId taskitEngineId) {
        validatePath(path);
        validateClass(classRef);
        validateTaskitEngineId(taskitEngineId);

        ITaskitEngine taskitEngine = this.data.taskitEngineIdToEngineMap.get(taskitEngineId);

        validateTaskitEngine(taskitEngine);

        try {
            return taskitEngine.read(path, classRef);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Using the given {@link TaskitEngineId}'s associated
     * {@link TaskitEngine}, reads the given file into the given classRef and then
     * translates it to it's corresponding type as defined by the provided
     * {@link TranslationSpec}s to the associated TaskitEngine
     * 
     * @param <I> the class to read the file as
     * 
     * @throws ContractException
     *                           <ul>
     *                           <li>{@linkplain TaskitCoreError#NULL_PATH}
     *                           if the path is null</li>
     *                           <li>{@linkplain ResourceError#FILE_PATH_IS_DIRECTORY}
     *                           if the path points to a directory instead of a
     *                           file</li>
     *                           <li>{@linkplain TaskitCoreError#NULL_CLASS_REF}
     *                           if the classRef is null</li>
     *                           <li>{@linkplain TaskitCoreError#NULL_TASKIT_ENGINE_ID}
     *                           if taskitEngineId is null</li>
     *                           <li>{@linkplain TaskitCoreError#NULL_TASKIT_ENGINE}
     *                           if taskitEngine is null</li>
     *                           </ul>
     * @throws RuntimeException  if the reading of the file encounters an
     *                           IOException
     */
    public <I, T> T readAndTranslate(Path path, Class<I> classRef, TaskitEngineId taskitEngineId) {
        validatePath(path);
        validateClass(classRef);
        validateTaskitEngineId(taskitEngineId);

        ITaskitEngine taskitEngine = this.data.taskitEngineIdToEngineMap.get(taskitEngineId);

        validateTaskitEngine(taskitEngine);

        try {
            return taskitEngine.readAndTranslate(path, classRef);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Using the given {@link TaskitEngineId}'s associated
     * {@link TaskitEngine}, writes the given object to a file.
     * 
     * @param <O> the class of the object to write to the outputFile
     * @param <U> the optional parent class of the object to write to the outputFile
     * 
     * @throws ContractException
     *                           <ul>
     *                           <li>{@linkplain TaskitCoreError#NULL_PATH}
     *                           if the path is null</li>
     *                           <li>{@linkplain ResourceError#FILE_PATH_IS_DIRECTORY}
     *                           if the path points to a directory instead of a
     *                           file</li>
     *                           <li>{@linkplain TaskitCoreError#NULL_OBJECT_FOR_TRANSLATION}
     *                           if the object is null</li>
     *                           <li>{@linkplain TaskitCoreError#NULL_TASKIT_ENGINE_ID}
     *                           if taskitEngineId is null</li>
     *                           <li>{@linkplain TaskitCoreError#NULL_TASKIT_ENGINE}
     *                           if taskitEngine is null</li>
     *                           </ul>
     * @throws RuntimeException  if the writing of the file encounters an
     *                           IOException
     */
    public <O> void write(Path path, O object, TaskitEngineId taskitEngineId) {
        write(path, object, Optional.empty(), taskitEngineId, false);
    }

    /**
     * Using the given {@link TaskitEngineId}'s associated
     * {@link TaskitEngine}, translates and writes the given object to a file.
     * 
     * @param <O> the class of the object to write to the outputFile
     * @param <U> the optional parent class of the object to write to the outputFile
     * 
     * @throws ContractException
     *                           <ul>
     *                           <li>{@linkplain TaskitCoreError#NULL_PATH}
     *                           if the path is null</li>
     *                           <li>{@linkplain ResourceError#FILE_PATH_IS_DIRECTORY}
     *                           if the path points to a directory instead of a
     *                           file</li>
     *                           <li>{@linkplain TaskitCoreError#NULL_OBJECT_FOR_TRANSLATION}
     *                           if the object is null</li>
     *                           <li>{@linkplain TaskitCoreError#NULL_TASKIT_ENGINE_ID}
     *                           if taskitEngineId is null</li>
     *                           <li>{@linkplain TaskitCoreError#NULL_TASKIT_ENGINE}
     *                           if taskitEngine is null</li>
     *                           </ul>
     * @throws RuntimeException  if the writing of the file encounters an
     *                           IOException
     */
    public <O> void translateAndWrite(Path path, O object, TaskitEngineId taskitEngineId) {
        write(path, object, Optional.empty(), taskitEngineId, true);
    }

    /**
     * Using the given {@link TaskitEngineId}'s associated
     * {@link TaskitEngine}, translates and writes the given object to a file,
     * using the given class as the output class rather than the class of the
     * object.
     * 
     * @param <O> the class of the object to write to the outputFile
     * @param <P> the class to write the object as
     * 
     * @throws ContractException
     *                           <ul>
     *                           <li>{@linkplain TaskitCoreError#NULL_CLASS_REF}
     *                           if the output classref is null</li>
     *                           <li>{@linkplain TaskitCoreError#NULL_PATH}
     *                           if the path is null</li>
     *                           <li>{@linkplain ResourceError#FILE_PATH_IS_DIRECTORY}
     *                           if the path points to a directory instead of a
     *                           file</li>
     *                           <li>{@linkplain TaskitCoreError#NULL_OBJECT_FOR_TRANSLATION}
     *                           if the object is null</li>
     *                           <li>{@linkplain TaskitCoreError#NULL_TASKIT_ENGINE_ID}
     *                           if taskitEngineId is null</li>
     *                           <li>{@linkplain TaskitCoreError#NULL_TASKIT_ENGINE}
     *                           if taskitEngine is null</li>
     *                           </ul>
     * @throws RuntimeException  if the writing of the file encounters an
     *                           IOException
     */
    public <O extends P, P> void translateAndWrite(Path path, O object, Class<P> outputClass,
            TaskitEngineId taskitEngineId) {
        validateClass(outputClass);

        write(path, object, Optional.of(outputClass), taskitEngineId, true);
    }

    /**
     * package access for testing
     */
    <O extends P, P> void write(Path path, O object, Optional<Class<P>> outputClass,
            TaskitEngineId taskitEngineId, boolean translate) {

        validatePath(path);
        validateObject(object);
        validateTaskitEngineId(taskitEngineId);

        ITaskitEngine taskitEngine = this.data.taskitEngineIdToEngineMap.get(taskitEngineId);

        validateTaskitEngine(taskitEngine);

        try {
            if (!translate) {
                taskitEngine.write(path, object);
                return;
            }
            if (outputClass.isEmpty()) {
                taskitEngine.translateAndWrite(path, object);
                return;
            }
            taskitEngine.translateAndWrite(path, object, outputClass.get());
            return;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
