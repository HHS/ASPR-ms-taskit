package gov.hhs.aspr.ms.taskit.core.engine;

import java.io.IOException;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import gov.hhs.aspr.ms.util.errors.ContractException;
import gov.hhs.aspr.ms.util.resourcehelper.ResourceError;
import gov.hhs.aspr.ms.util.resourcehelper.ResourceHelper;

/**
 * The TaskitEngineManager allows {@link TaskitEngine}s to be added to it, and
 * acts as a wrapper around the TaskitEngine read/write/translate methods.
 */
public final class TaskitEngineManager {
    private final Data data;

    private TaskitEngineManager(Data data) {
        this.data = data;
    }

    private final static class Data {
        private final Map<TaskitEngineId, TaskitEngine> taskitEngineIdToEngineMap = new LinkedHashMap<>();

        Data() {
        }
    }

    /**
     * Builder for the TaskitEngineManager
     */
    public final static class Builder {
        Data data;

        Builder(Data data) {
            this.data = data;
        }

        private void validateTaskitEngine(TaskitEngine taskitEngine) {
            if (taskitEngine == null) {
                throw new ContractException(TaskitError.NULL_TASKIT_ENGINE);
            }

            if (!taskitEngine.isInitialized()) {
                throw new ContractException(TaskitError.UNINITIALIZED_TASKIT_ENGINE);
            }
        }

        private void validateTaskitEngineAdded() {
            if (this.data.taskitEngineIdToEngineMap.isEmpty()) {
                throw new ContractException(TaskitError.NO_TASKIT_ENGINES);
            }
        }

        /**
         * Builds the TaskitEngineManager.
         * 
         * @throws ContractException
         *                           <ul>
         *                           <li>{@linkplain TaskitError#NO_TASKIT_ENGINES}
         *                           if no taskit engines were added</li>
         *                           </ul>
         */
        public TaskitEngineManager build() {
            // at least 1 engine must be added
            validateTaskitEngineAdded();

            return new TaskitEngineManager(this.data);
        }

        /**
         * Adds a {@link TaskitEngine} to this TaskitEngineManager
         * 
         * @param taskitEngine the taskit engine to add
         * @return the builder instance
         * 
         * @throws ContractException
         *                           <ul>
         *                           <li>{@linkplain TaskitError#NULL_TASKIT_ENGINE}
         *                           if taskitEngine is null</li>
         *                           <li>{@linkplain TaskitError#UNINITIALIZED_TASKIT_ENGINE}
         *                           if the taskit engine was not initialized prior to
         *                           adding it to the manager</li>
         *                           </ul>
         */
        public Builder addTaskitEngine(TaskitEngine taskitEngine) {
            validateTaskitEngine(taskitEngine);

            this.data.taskitEngineIdToEngineMap.put(taskitEngine.getTaskitEngineId(), taskitEngine);

            return this;
        }
    }

    /**
     * Returns a new instance of TaskitEngineManager Builder
     */
    public static Builder builder() {
        return new Builder(new Data());
    }

    private void validateTaskitEngine(TaskitEngine taskitEngine) {
        if (taskitEngine == null) {
            throw new ContractException(TaskitError.NULL_TASKIT_ENGINE);
        }
    }

    private void validatePath(Path path) {
        if (path == null) {
            throw new ContractException(TaskitError.NULL_PATH);
        }

        ResourceHelper.validateFilePath(path);
    }

    private void validateClass(Class<?> classRef) {
        if (classRef == null) {
            throw new ContractException(TaskitError.NULL_CLASS_REF);
        }
    }

    private void validateObject(Object object) {
        if (object == null) {
            throw new ContractException(TaskitError.NULL_OBJECT_FOR_TRANSLATION);
        }
    }

    private void validateTaskitEngineId(TaskitEngineId taskitEngineId) {
        if (taskitEngineId == null) {
            throw new ContractException(TaskitError.NULL_TASKIT_ENGINE_ID);
        }
    }

    // TODO: add the 3 translate methods from the TaskitEngine

    /**
     * Using the given {@link TaskitEngineId}'s associated
     * {@link TaskitEngine}, reads the given file into the provided class type
     * 
     * @param <I>            the input type
     * @param path           the path of the file to read
     * @param classRef       the to read the file as
     * @param taskitEngineId the taskitEngineId to use to read the file
     * @return the resulting object from reading the file as the class
     * 
     * @throws ContractException
     *                           <ul>
     *                           <li>{@linkplain TaskitError#NULL_PATH}
     *                           if the path is null</li>
     *                           <li>{@linkplain ResourceError#FILE_PATH_IS_DIRECTORY}
     *                           if the path points to a directory instead of a
     *                           file</li>
     *                           <li>{@linkplain TaskitError#NULL_CLASS_REF}
     *                           if the classRef is null</li>
     *                           <li>{@linkplain TaskitError#NULL_TASKIT_ENGINE_ID}
     *                           if taskitEngineId is null</li>
     *                           <li>{@linkplain TaskitError#NULL_TASKIT_ENGINE}
     *                           if taskitEngine is null</li>
     *                           </ul>
     * @throws RuntimeException  if the reading of the file encounters an
     *                           IOException
     */
    public <I> I read(Path path, Class<I> classRef, TaskitEngineId taskitEngineId) {
        validatePath(path);
        validateClass(classRef);
        validateTaskitEngineId(taskitEngineId);

        TaskitEngine taskitEngine = this.data.taskitEngineIdToEngineMap.get(taskitEngineId);

        validateTaskitEngine(taskitEngine);

        try {
            return taskitEngine.read(path, classRef);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Using the given {@link TaskitEngineId}'s associated
     * {@link TaskitEngine}, reads the given file into the provided class type and
     * then translates it to
     * the corresponding type associated with the input type
     * 
     * @param <I>            the input type
     * @param <T>            the translated type
     * @param path           the path of the file to read
     * @param classRef       the to read the file as
     * @param taskitEngineId the taskitEngineId to use to read the file
     * @return the resulting object from reading the file as the class
     * 
     * @throws ContractException
     *                           <ul>
     *                           <li>{@linkplain TaskitError#NULL_PATH}
     *                           if the path is null</li>
     *                           <li>{@linkplain ResourceError#FILE_PATH_IS_DIRECTORY}
     *                           if the path points to a directory instead of a
     *                           file</li>
     *                           <li>{@linkplain TaskitError#NULL_CLASS_REF}
     *                           if the classRef is null</li>
     *                           <li>{@linkplain TaskitError#NULL_TASKIT_ENGINE_ID}
     *                           if taskitEngineId is null</li>
     *                           <li>{@linkplain TaskitError#NULL_TASKIT_ENGINE}
     *                           if taskitEngine is null</li>
     *                           </ul>
     * @throws RuntimeException  if the reading of the file encounters an
     *                           IOException
     */
    public <I, T> T readAndTranslate(Path path, Class<I> classRef, TaskitEngineId taskitEngineId) {
        validatePath(path);
        validateClass(classRef);
        validateTaskitEngineId(taskitEngineId);

        TaskitEngine taskitEngine = this.data.taskitEngineIdToEngineMap.get(taskitEngineId);

        validateTaskitEngine(taskitEngine);

        try {
            return taskitEngine.readAndTranslate(path, classRef);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Using the given {@link TaskitEngineId}'s associated
     * {@link TaskitEngine}, Writes the object to the file referenced by the Path.
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

    /**
     * Using the given {@link TaskitEngineId}'s associated
     * {@link TaskitEngine}, writes the object to the file referenced by the Path.
     * 
     * @param <O>            the type of the object to write
     * @param path           the path of the file to write to
     * @param object         the object to write
     * @param taskitEngineId the taskitEngineId to use to write the object
     * 
     * @throws ContractException
     *                           <ul>
     *                           <li>{@linkplain TaskitError#NULL_PATH}
     *                           if the path is null</li>
     *                           <li>{@linkplain ResourceError#FILE_PATH_IS_DIRECTORY}
     *                           if the path points to a directory instead of a
     *                           file</li>
     *                           <li>{@linkplain TaskitError#NULL_OBJECT_FOR_TRANSLATION}
     *                           if the object is null</li>
     *                           <li>{@linkplain TaskitError#NULL_TASKIT_ENGINE_ID}
     *                           if taskitEngineId is null</li>
     *                           <li>{@linkplain TaskitError#NULL_TASKIT_ENGINE}
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
     * {@link TaskitEngine}, translates the object and then writes the translated
     * object to the file
     * reference by the Path
     * 
     * @param <O>            the type of the object to write
     * @param path           the path of the file to write to
     * @param object         the object to write
     * @param taskitEngineId the taskitEngineId to use to write the object
     * 
     * @throws ContractException
     *                           <ul>
     *                           <li>{@linkplain TaskitError#NULL_PATH}
     *                           if the path is null</li>
     *                           <li>{@linkplain ResourceError#FILE_PATH_IS_DIRECTORY}
     *                           if the path points to a directory instead of a
     *                           file</li>
     *                           <li>{@linkplain TaskitError#NULL_OBJECT_FOR_TRANSLATION}
     *                           if the object is null</li>
     *                           <li>{@linkplain TaskitError#NULL_TASKIT_ENGINE_ID}
     *                           if taskitEngineId is null</li>
     *                           <li>{@linkplain TaskitError#NULL_TASKIT_ENGINE}
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
     * {@link TaskitEngine}, translates the object as the provided class and then
     * writes the translated
     * object to the file referenced by the Path
     * <p>
     * The type params ensure that the object can be written as the provided class
     * 
     * @param <C>            the type to translate the object as
     * @param <O>            the type of the object
     * @param path           the path of the file to write to
     * @param object         the object to write
     * @param classRef       the class to translate the object as
     * @param taskitEngineId the taskitEngineId to use to write the object
     * 
     * @throws ContractException
     *                           <ul>
     *                           <li>{@linkplain TaskitError#NULL_CLASS_REF}
     *                           if the output classref is null</li>
     *                           <li>{@linkplain TaskitError#NULL_PATH}
     *                           if the path is null</li>
     *                           <li>{@linkplain ResourceError#FILE_PATH_IS_DIRECTORY}
     *                           if the path points to a directory instead of a
     *                           file</li>
     *                           <li>{@linkplain TaskitError#NULL_OBJECT_FOR_TRANSLATION}
     *                           if the object is null</li>
     *                           <li>{@linkplain TaskitError#NULL_TASKIT_ENGINE_ID}
     *                           if taskitEngineId is null</li>
     *                           <li>{@linkplain TaskitError#NULL_TASKIT_ENGINE}
     *                           if taskitEngine is null</li>
     *                           </ul>
     * @throws RuntimeException  if the writing of the file encounters an
     *                           IOException
     */
    public <C, O extends C> void translateAndWrite(Path path, O object, Class<C> classRef,
            TaskitEngineId taskitEngineId) {
        validateClass(classRef);

        write(path, object, Optional.of(classRef), taskitEngineId, true);
    }

    /**
     * package access for testing
     * 
     * calls the associated TaskitEngine write method depending on whether the
     * translate flag is set and whether there is a classRef provided to translate
     * the object as
     */
    <C, O extends C> void write(Path path, O object, Optional<Class<C>> classRef,
            TaskitEngineId taskitEngineId, boolean translate) {

        validatePath(path);
        validateObject(object);
        validateTaskitEngineId(taskitEngineId);

        TaskitEngine taskitEngine = this.data.taskitEngineIdToEngineMap.get(taskitEngineId);

        validateTaskitEngine(taskitEngine);

        try {
            if (!translate) {
                taskitEngine.write(path, object);
                return;
            }
            if (classRef.isEmpty()) {
                taskitEngine.translateAndWrite(path, object);
                return;
            }
            taskitEngine.translateAndWrite(path, object, classRef.get());
            return;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
