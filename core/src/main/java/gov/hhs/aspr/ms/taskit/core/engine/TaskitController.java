package gov.hhs.aspr.ms.taskit.core.engine;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import gov.hhs.aspr.ms.taskit.core.translation.TranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.Translator;
import gov.hhs.aspr.ms.util.errors.ContractException;
import gov.hhs.aspr.ms.util.resourcehelper.ResourceError;
import gov.hhs.aspr.ms.util.resourcehelper.ResourceHelper;

/**
 * The TaskitController allows {@link ITaskitEngine}s to be added to it, and
 * acts as a wrapper around the TaskitEngine read/write/translate methods.
 */
public final class TaskitController {
    protected final Data data;
    protected final Map<TaskitEngineType, ITaskitEngine> taskitEngines = new LinkedHashMap<>();
    protected final Map<Class<? extends ITaskitEngine>, TaskitEngineType> taskitEngineClassToTypeMap = new LinkedHashMap<>();

    TaskitController(Data data) {
        this.data = data;
    }

    final static class Data {
        protected Set<ITaskitEngine> taskitEngines = new LinkedHashSet<>();
        protected final List<Translator> translators = new ArrayList<>();
        protected final Map<Class<?>, Class<?>> parentChildClassRelationshipMap = new LinkedHashMap<>();

        Data() {
        }
    }

    public final static class Builder {
        Data data;

        Builder(Data data) {
            this.data = data;
        }

        private void validateClassRefNotNull(Class<?> classRef) {
            if (classRef == null) {
                throw new ContractException(TaskitError.NULL_CLASS_REF);
            }
        }

        private void validateTaskitEngineNotNull(ITaskitEngine taskitEngine) {
            if (taskitEngine == null) {
                throw new ContractException(TaskitError.NULL_TASKIT_ENGINE);
            }
        }

        private void validateTaskitEnginesNotNull() {
            if (this.data.taskitEngines.isEmpty()) {
                throw new ContractException(TaskitError.NULL_TASKIT_ENGINE,
                        "No TaskitEngine Builders were added");
            }
            for (ITaskitEngine engine : this.data.taskitEngines) {
                validateTaskitEngineNotNull(engine);
            }
        }

        /**
         * Builds the TaskitController. Calls the initializer on each added
         * {@link Translator}
         * 
         * @throws ContractException
         *                           <ul>
         *                           <li>{@linkplain TaskitError#NULL_TASKIT_ENGINE}
         *                           if taskitEngineBuilder has not been set</li>
         *                           </ul>
         */
        public TaskitController build() {
            validateTaskitEnginesNotNull();

            TaskitController translatorController = new TaskitController(this.data);

            translatorController.initTaskitEngines();
            translatorController.validateTaskitEngines();

            return translatorController;
        }

        TaskitController buildWithoutInitAndChecks() {
            return new TaskitController(this.data);
        }

        /**
         * Adds the given classRef parent class mapping.
         * <p>
         * explicitly used when calling {@link TaskitController#write} with a need to
         * output the given class as the parent class instead of the concrete class
         * 
         * @param <M> the childClass
         * @param <U> the parentClass/MarkerInterfaceClass
         * @throws ContractException
         *                           <ul>
         *                           <li>{@linkplain TaskitError#NULL_CLASS_REF}
         *                           if classRef is null or if markerInterface is
         *                           null</li>
         *                           <li>{@linkplain TaskitError#DUPLICATE_CLASSREF}
         *                           if child parent relationship has already been
         *                           added</li>
         *                           </ul>
         */
        public <M extends U, U> Builder addParentChildClassRelationship(Class<M> classRef, Class<U> parentClassRef) {
            validateClassRefNotNull(classRef);
            validateClassRefNotNull(parentClassRef);

            if (this.data.parentChildClassRelationshipMap.containsKey(classRef)) {
                throw new ContractException(TaskitError.DUPLICATE_CLASSREF);
            }

            this.data.parentChildClassRelationshipMap.put(classRef, parentClassRef);
            return this;
        }

        /**
         * Adds a {@link TaskitEngine.Builder}
         * 
         * @throws ContractException
         *                           <ul>
         *                           <li>{@linkplain TaskitError#NULL_TASKIT_ENGINE}
         *                           if taskitEngineBuilder is null</li>
         *                           </ul>
         */
        public Builder addTaskitEngine(TaskitEngine taskitEngine) {
            validateTaskitEngineNotNull(taskitEngine);

            this.data.taskitEngines.add(taskitEngine);

            Map<Class<?>, Class<?>> childToParentClassMap = taskitEngine.getChildParentClassMap();

            for (Class<?> childClassRef : childToParentClassMap.keySet()) {
                // Need to duplicate code here because the map doesn't provide the type safety
                // that is required by the addParentChildClassRelationship method
                Class<?> parentClassRef = childToParentClassMap.get(childClassRef);

                // Note: no 'class is not null' validation here because it was validated prior
                // to being put into the engine
                if (this.data.parentChildClassRelationshipMap.containsKey(childClassRef)) {
                    throw new ContractException(TaskitError.DUPLICATE_CLASSREF);
                }

                this.data.parentChildClassRelationshipMap.put(childClassRef, parentClassRef);
            }

            return this;
        }
    }

    /**
     * Returns a new instance of Builder
     */
    public static Builder builder() {
        return new Builder(new Data());
    }

    void initTaskitEngines() {
        for (ITaskitEngine taskitEngine : this.data.taskitEngines) {
            TaskitEngine baseTaskitEngine = taskitEngine.getTaskitEngine();

            baseTaskitEngine.translationSpecsAreInitialized();

            this.taskitEngines.put(taskitEngine.getTaskitEngineType(), taskitEngine);
            this.taskitEngineClassToTypeMap.put(taskitEngine.getClass(),
                    taskitEngine.getTaskitEngineType());
        }

        // since we are making a new mapping, clear the original set in the data
        this.data.taskitEngines.clear();
    }

    void validateTaskitEngine(ITaskitEngine taskitEngine) {
        if (taskitEngine == null) {
            throw new ContractException(TaskitError.NULL_TASKIT_ENGINE);
        }

        /*
         * Because the taskitEngine's init method is called within the
         * initTranslators() method, this should never happen, thus it is a
         * RuntimeException and not a ContractException
         */
        if (!taskitEngine.getTaskitEngine().isInitialized()) {
            throw new RuntimeException("TaskitEngine has been built but has not been initialized.");
        }
    }

    void validateTaskitEngines() {
        Set<Class<? extends ITaskitEngine>> taskitEngineClasses = new HashSet<>();

        if (this.taskitEngines.keySet().isEmpty()) {
            throw new ContractException(TaskitError.NO_TASKIT_ENGINES);
        }

        // validate each engine that exists irrespective of any mapping
        for (ITaskitEngine taskitEngine : this.taskitEngines.values()) {
            validateTaskitEngine(taskitEngine);
            taskitEngineClasses.add(taskitEngine.getClass());
        }

        // if the class to type map doesn't contain all of the classes of the engines in
        // the engine map
        // and
        // if the engine map doesn't contain all of the types from the class to type map
        // this ensures that every engine has a valid class -> type -> engine mapping
        if (!(this.taskitEngineClassToTypeMap.keySet().containsAll(taskitEngineClasses)
                && this.taskitEngines.keySet().containsAll(this.taskitEngineClassToTypeMap.values()))) {
            throw new RuntimeException(
                    "Not all Taskit Engines have an associated Class -> Type -> Engine Mapping. Something went very wrong.");
        }
    }

    /**
     * Using the given {@link TaskitEngineType}'s associated
     * {@link TaskitEngine}, reads the given file into the given classRef and then
     * translates it to it's corresponding type as defined by the provided
     * {@link TranslationSpec}s to the associated TaskitEngine
     * 
     * @param <I> the class to read the file as
     * 
     * @throws ContractException
     *                           <ul>
     *                           <li>{@linkplain TaskitError#NULL_PATH}
     *                           if the path is null</li>
     *                           <li>{@linkplain ResourceError#FILE_PATH_IS_DIRECTORY}
     *                           if the path points to a directory instead of a
     *                           file</li>
     *                           <li>{@linkplain TaskitError#NULL_CLASS_REF}
     *                           if the object is null</li>
     *                           <li>{@linkplain TaskitError#NULL_TASKIT_ENGINE_TYPE}
     *                           if taskitEngineType is null</li>
     *                           <li>{@linkplain TaskitError#NULL_TASKIT_ENGINE}
     *                           if taskitEngine is null</li>
     *                           </ul>
     * @throws RuntimeException  if the reading of the file encounters an
     *                           IOException
     */
    public <I> I read(Path path, Class<I> inputClass, TaskitEngineType taskitEngineType) {
        if (path == null) {
            throw new ContractException(TaskitError.NULL_PATH);
        }

        ResourceHelper.validateFilePath(path);

        if (inputClass == null) {
            throw new ContractException(TaskitError.NULL_CLASS_REF);
        }

        if (taskitEngineType == null) {
            throw new ContractException(TaskitError.NULL_TASKIT_ENGINE_TYPE);
        }

        ITaskitEngine taskitEngine = this.taskitEngines.get(taskitEngineType);

        if (taskitEngine == null) {
            throw new ContractException(TaskitError.NULL_TASKIT_ENGINE);
        }

        try {
            return taskitEngine.read(path, inputClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Using the given {@link TaskitEngineType}'s associated
     * {@link TaskitEngine}, writes the given object to a file.
     * 
     * @param <O> the class of the object to write to the outputFile
     * @param <U> the optional parent class of the object to write to the outputFile
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
     *                           <li>{@linkplain TaskitError#NULL_TASKIT_ENGINE_TYPE}
     *                           if taskitEngineType is null</li>
     *                           <li>{@linkplain TaskitError#NULL_TASKIT_ENGINE}
     *                           if taskitEngine is null</li>
     *                           </ul>
     * @throws RuntimeException  if the writing of the file encounters an
     *                           IOException
     */
    public <O> void write(Path path, O object, TaskitEngineType taskitEngineType) {
        write(path, object, Optional.empty(), taskitEngineType, false);
    }

    /**
     * Using the given {@link TaskitEngineType}'s associated
     * {@link TaskitEngine}, translates and writes the given object to a file.
     * 
     * @param <O> the class of the object to write to the outputFile
     * @param <U> the optional parent class of the object to write to the outputFile
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
     *                           <li>{@linkplain TaskitError#NULL_TASKIT_ENGINE_TYPE}
     *                           if taskitEngineType is null</li>
     *                           <li>{@linkplain TaskitError#NULL_TASKIT_ENGINE}
     *                           if taskitEngine is null</li>
     *                           </ul>
     * @throws RuntimeException  if the writing of the file encounters an
     *                           IOException
     */
    public <O> void translateAndWrite(Path path, O object, TaskitEngineType taskitEngineType) {
        write(path, object, Optional.empty(), taskitEngineType, false);
    }

    /**
     * Using the given {@link TaskitEngineType}'s associated
     * {@link TaskitEngine}, translates and writes the given object to a file,
     * using the given class as the output class rather than the class of the
     * object.
     * 
     * @param <O> the class of the object to write to the outputFile
     * @param <P> the class to write the object as
     * 
     * @throws ContractException
     *                           <ul>
     *                           <li>{@linkplain TaskitError#NULL_CLASS_REF}
     *                           if the output classref is null</li>
     *                           <li>{@linkplain TaskitError#INVALID_PARENT_OUTPUT_CLASS}
     *                           if the output classref is not known to be a parent
     *                           of the given object's class</li>
     *                           <li>{@linkplain TaskitError#NULL_PATH}
     *                           if the path is null</li>
     *                           <li>{@linkplain ResourceError#FILE_PATH_IS_DIRECTORY}
     *                           if the path points to a directory instead of a
     *                           file</li>
     *                           <li>{@linkplain TaskitError#NULL_OBJECT_FOR_TRANSLATION}
     *                           if the object is null</li>
     *                           <li>{@linkplain TaskitError#NULL_TASKIT_ENGINE_TYPE}
     *                           if taskitEngineType is null</li>
     *                           <li>{@linkplain TaskitError#NULL_TASKIT_ENGINE}
     *                           if taskitEngine is null</li>
     *                           </ul>
     * @throws RuntimeException  if the writing of the file encounters an
     *                           IOException
     */
    public <O extends P, P> void translateAndWrite(Path path, O object, Class<P> outputClass, TaskitEngineType taskitEngineType) {
        if (outputClass == null) {
            throw new ContractException(TaskitError.NULL_CLASS_REF);
        }

        if (!this.data.parentChildClassRelationshipMap.values().contains(outputClass)) {
            throw new ContractException(TaskitError.INVALID_PARENT_OUTPUT_CLASS);
        }

        write(path, object, Optional.of(outputClass), taskitEngineType, true);
    }

    <O extends P, P> void write(Path path, O object, Optional<Class<P>> outputClass,
            TaskitEngineType taskitEngineType, boolean translate) {

        if (path == null) {
            throw new ContractException(TaskitError.NULL_PATH);
        }

        ResourceHelper.validateFilePath(path);

        if (object == null) {
            throw new ContractException(TaskitError.NULL_OBJECT_FOR_TRANSLATION);
        }

        if (taskitEngineType == null) {
            throw new ContractException(TaskitError.NULL_TASKIT_ENGINE_TYPE);
        }

        ITaskitEngine taskitEngine = this.taskitEngines.get(taskitEngineType);

        if (taskitEngine == null) {
            throw new ContractException(TaskitError.NULL_TASKIT_ENGINE);
        }

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
