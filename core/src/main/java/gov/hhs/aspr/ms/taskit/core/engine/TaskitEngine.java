package gov.hhs.aspr.ms.taskit.core.engine;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import gov.hhs.aspr.ms.taskit.core.translation.ITranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.TranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.Translator;
import gov.hhs.aspr.ms.util.errors.ContractException;

/**
 * TaskitEngine Initializes all {@link TranslationSpec}s and maintains
 * a mapping between the translationSpec and it's respective classes. Each
 * serialization library must implement its own unique Taskit Engine that
 * includes this Engine in its data.
 */
public final class TaskitEngine implements ITaskitEngine {
    private final Data data;

    private boolean isInitialized = false;

    private TaskitEngine(Data data) {
        this.data = data;
    }

    private static final class Data {
        private final Map<Class<?>, ITranslationSpec> classToTranslationSpecMap = new LinkedHashMap<>();
        private final Set<ITranslationSpec> translationSpecs = new LinkedHashSet<>();
        private TaskitEngineId taskitEngineId;

        private Data() {
        }

        @Override
        public int hashCode() {
            return Objects.hash(classToTranslationSpecMap, translationSpecs);
        }

        @Override
        public boolean equals(Object obj) {
            Data other = (Data) obj;

            if (!Objects.equals(classToTranslationSpecMap, other.classToTranslationSpecMap)) {
                return false;
            }

            if (!Objects.equals(translationSpecs, other.translationSpecs)) {
                return false;
            }

            return true;
        }
    }

    // TODO update javadoc
    /**
     * This class builds a TaskitEngine.
     */
    public static final class Builder implements ITaskitEngineBuilder {
        private Data data;
        private final List<Translator> translators = new ArrayList<>();

        private Builder(Data data) {
            this.data = data;
        }

        private <I, A> void validateTranslationSpec(TranslationSpec<I, A> translationSpec) {
            if (translationSpec == null) {
                throw new ContractException(TaskitCoreError.NULL_TRANSLATION_SPEC);
            }

            if (translationSpec.getAppObjectClass() == null) {
                throw new ContractException(TaskitCoreError.NULL_TRANSLATION_SPEC_APP_CLASS);
            }

            if (translationSpec.getInputObjectClass() == null) {
                throw new ContractException(TaskitCoreError.NULL_TRANSLATION_SPEC_INPUT_CLASS);
            }

            if (this.data.translationSpecs.contains(translationSpec)) {
                throw new ContractException(TaskitCoreError.DUPLICATE_TRANSLATION_SPEC);
            }
        }

        private void validateTranslatorNotNull(Translator translator) {
            if (translator == null) {
                throw new ContractException(TaskitCoreError.NULL_TRANSLATOR);
            }
        }

        private void validateTaskitEngineIdSet() {
            if (this.data.taskitEngineId == null) {
                throw new ContractException(TaskitCoreError.UNKNOWN_TASKIT_ENGINE_ID);
            }
        }

        private void validateTaskitEngineId(TaskitEngineId taskitEngineId) {
            if (taskitEngineId == null) {
                throw new ContractException(TaskitCoreError.NULL_TASKIT_ENGINE_ID);
            }
        }

        private void validateTranslationSpecsNotEmpty() {
            if (this.data.translationSpecs.isEmpty()) {
                throw new ContractException(TaskitCoreError.NO_TRANSLATION_SPECS);
            }
        }

        private void validateTranslatorsInitialized() {
            if (!this.translators.isEmpty()) {

                List<Translator> orderedTranslators = new TaskitEngineHelper(translators).getOrderedTranslators();

                for (Translator translator : orderedTranslators) {
                    if (!translator.isInitialized()) {
                        throw new ContractException(TaskitCoreError.UNINITIALIZED_TRANSLATORS);
                    }
                }
            }

            this.translators.clear();
        }

        /**
         * Builder for the TaskitEngine
         * 
         * @throws ContractException
         *                           <ul>
         *                           <li>{@link TaskitCoreError#NULL_TASKIT_ENGINE_ID}
         *                           if the engine id was not set</li>
         *                           <li>{@link TaskitCoreError#DUPLICATE_TRANSLATOR}
         *                           if a duplicate translator is found</li>
         *                           <li>{@link TaskitCoreError#MISSING_TRANSLATOR}
         *                           if an added translator has a unmet dependency</li>
         *                           <li>{@link TaskitCoreError#CIRCULAR_TRANSLATOR_DEPENDENCIES}
         *                           if the added translators have a circular dependency
         *                           graph</li>
         *                           <li>{@link TaskitCoreError#UNINITIALIZED_TRANSLATORS}
         *                           if translators were added to the engine but their
         *                           initialized flag was still set to false</li>
         *                           <li>{@link TaskitCoreError#NO_TRANSLATION_SPECS} if
         *                           no translation specs were added to the engine</li>
         *                           </ul>
         */
        public TaskitEngine build() {
            // Engine must have an ID
            validateTaskitEngineIdSet();

            // validate the translators that were added
            // they should have an acyclic dependency tree and also all be initialized
            validateTranslatorsInitialized();

            // There should be at least 1 translation spec added
            validateTranslationSpecsNotEmpty();

            return new TaskitEngine(this.data);
        }

        /**
         * Sets the type for this Taskit Engine
         */
        public final Builder setTaskitEngineId(TaskitEngineId taskitEngineId) {
            validateTaskitEngineId(taskitEngineId);
            this.data.taskitEngineId = taskitEngineId;

            return this;
        }

        /**
         * Adds the given {@link TranslationSpec} to the TaskitEngine
         * <p>
         * there is a bidirectional mapping that arises from this. The Engine needs to
         * know that class A translates to class B and vice versa.
         * 
         * @param <I> the input object type
         * @param <A> the app object type
         * @throws ContractException
         *                           <ul>
         *                           <li>{@linkplain TaskitCoreError#NULL_TRANSLATION_SPEC}
         *                           if the given translationSpec is null</li>
         *                           <li>{@linkplain TaskitCoreError#NULL_TRANSLATION_SPEC_APP_CLASS}
         *                           if the given translationSpecs getAppClass method
         *                           returns null</li>
         *                           <li>{@linkplain TaskitCoreError#NULL_TRANSLATION_SPEC_INPUT_CLASS}
         *                           if the given translationSpecs getInputClass method
         *                           returns null</li>
         *                           <li>{@linkplain TaskitCoreError#DUPLICATE_TRANSLATION_SPEC}
         *                           if the given translationSpec is already known</li>
         *                           </ul>
         */
        public final <I, A> Builder addTranslationSpec(TranslationSpec<I, A> translationSpec) {
            validateTranslationSpec(translationSpec);

            this.data.classToTranslationSpecMap.put(translationSpec.getInputObjectClass(), translationSpec);
            this.data.classToTranslationSpecMap.put(translationSpec.getAppObjectClass(), translationSpec);

            this.data.translationSpecs.add(translationSpec);

            return this;
        }

        /**
         * Add a {@link Translator}
         * 
         * @throws ContractException {@linkplain TaskitCoreError#NULL_TRANSLATOR}
         *                           if translator is null
         */
        public final Builder addTranslator(Translator translator) {
            validateTranslatorNotNull(translator);

            this.translators.add(translator);

            return this;
        }
    }

    /**
     * Returns a new builder for a Base TaskitEngine
     */
    public static TaskitEngine.Builder builder() {
        return new Builder(new Data());
    }

    /**
     * returns the {@link TaskitEngineType} of this TaskitEngine
     * 
     * guaranteed to NOT be null
     */
    public TaskitEngineId getTaskitEngineId() {
        return this.data.taskitEngineId;
    }

    /**
     * Initializes the taskitEngine by calling init on each translationSpec
     * added in the builder
     * <p>
     * Note that this method is on {@link TaskitEngine} because it is the engine
     * that knows about translation specs. Any engine that wraps this engine need
     * not known about translation specs, but the translation specs expect to be
     * initialized with a TaskitEngine corresponding to their associated library.
     * 
     * @throws ContractException {@link TaskitCoreError#UNINITIALIZED_TRANSLATION_SPEC}
     *                           if a translation spec's initialized flag is not set
     *                           after calling it's init method
     */
    public void init(ITaskitEngine taskitEngine) {
        /*
         * Calling init on a translationSpec causes the hashCode of the translationSpec
         * to change. Because of this, before calling init, we need to remove them from
         * the translationSpecs Set then initialize them, then add them back to the set.
         * Set's aren't happy when the hash code of the objects in them change
         */
        List<ITranslationSpec> copyOfTranslationSpecs = new ArrayList<>(this.data.translationSpecs);

        this.data.translationSpecs.clear();

        for (ITranslationSpec translationSpec : copyOfTranslationSpecs) {
            translationSpec.init(taskitEngine);
            this.data.translationSpecs.add(translationSpec);

            if (!translationSpec.isInitialized()) {
                throw new ContractException(TaskitCoreError.UNINITIALIZED_TRANSLATION_SPEC);
            }
        }

        this.isInitialized = true;
    }

    /**
     * returns whether this taskitEngine is initialized or not
     */
    public boolean isInitialized() {
        return this.isInitialized;
    }

    /**
     * Returns an instance of the Base Taskit Engine
     * 
     * NOTE: for {@link TaskitEngine} it returns itself
     */
    @Override
    public TaskitEngine getTaskitEngine() {
        return this;
    }

    /**
     * Returns a set of all {@link TranslationSpec}s associated with this
     * TaskitEngine
     */
    public Set<ITranslationSpec> getTranslationSpecs() {
        return this.data.translationSpecs;
    }

    /**
     * writing to files must be defined in explicit TaskitEngines, the base taskit
     * engine knows nothing about writing to files
     * 
     * THIS METHOD SHOULD NEVER BE CALLED DIRECTLY
     */
    @Override
    public <O> void write(Path path, O object)
            throws IOException {
        throw new UnsupportedOperationException("Called 'write' on TaskitEngine");
    }

    /**
     * writing to files must be defined in explicit TaskitEngines, the base taskit
     * engine knows nothing about writing to files
     * 
     * THIS METHOD SHOULD NEVER BE CALLED DIRECTLY
     */
    @Override
    public <T, O extends T> void translateAndWrite(Path path, O object, Class<T> classRef)
            throws IOException {
        throw new UnsupportedOperationException("Called 'translateAndWrite' on TaskitEngine");
    }

    /**
     * writing to files must be defined in explicit TaskitEngines, the base taskit
     * engine knows nothing about writing to files
     * 
     * THIS METHOD SHOULD NEVER BE CALLED DIRECTLY
     */
    @Override
    public <O> void translateAndWrite(Path path, O object)
            throws IOException {
        throw new UnsupportedOperationException("Called 'translateAndWrite' on TaskitEngine");
    }

    /**
     * reading files must be defined in explicit TaskitEngines, the base taskit
     * engine knows nothing about reading files
     * 
     * THIS METHOD SHOULD NEVER BE CALLED DIRECTLY
     */
    @Override
    public <I> I read(Path path, Class<I> classRef) throws IOException {
        throw new UnsupportedOperationException("Called 'read' on TaskitEngine");
    }

    /**
     * reading files must be defined in explicit TaskitEngines, the base taskit
     * engine knows nothing about reading files
     * 
     * THIS METHOD SHOULD NEVER BE CALLED DIRECTLY
     */
    @Override
    public <T, I> T readAndTranslate(Path path, Class<I> inputClassRef) throws IOException {
        throw new UnsupportedOperationException("Called 'readAndTranslate' on TaskitEngine");
    }

    private void validateObject(Object object) {
        if (object == null) {
            throw new ContractException(TaskitCoreError.NULL_OBJECT_FOR_TRANSLATION);
        }
    }

    /**
     * Given an object, uses the class of the object to obtain the translationSpec
     * and then calls {@link TranslationSpec#translate(Object)}
     * <p>
     * this conversion method will be used approx ~90% of the time
     * </p>
     * 
     * @param <T> the return type after translating
     * @throws ContractException
     *                           <ul>
     *                           <li>{@linkplain TaskitCoreError#NULL_OBJECT_FOR_TRANSLATION}
     *                           if the passed in object is null</li>
     *                           <li>{@linkplain TaskitCoreError#UNKNOWN_TRANSLATION_SPEC}
     *                           if no translationSpec was provided for the given
     *                           objects class</li>
     *                           </ul>
     */
    public <T> T translateObject(Object object) {
        validateObject(object);

        return getTranslationSpecForClass(object.getClass()).translate(object);
    }

    /**
     * Given an object, uses the parent class of the object to obtain the
     * translationSpec and then calls {@link TranslationSpec#translate(Object)}
     * <p>
     * This method call is safe in the sense that the type parameters ensure that
     * the passed in object is actually a child of the passed in parentClassRef
     * </p>
     * <p>
     * this conversion method will be used approx ~7% of the time
     * </p>
     * 
     * @param <T> the return type after translating
     * @param <O> the type of the object; extends U
     * @param <P> the parent type of the object and the class for which
     *            translationSpec you want to use
     * @throws ContractException
     *                           <ul>
     *                           <li>{@linkplain TaskitCoreError#NULL_OBJECT_FOR_TRANSLATION}
     *                           if the passed in object is null</li>
     *                           <li>{@linkplain TaskitCoreError#NULL_CLASS_REF}
     *                           if the passed in classRef is null</li>
     *                           <li>{@linkplain TaskitCoreError#UNKNOWN_TRANSLATION_SPEC}
     *                           if no translationSpec was provided for the given
     *                           objects class</li>
     *                           </ul>
     */
    public <T, O extends P, P> T translateObjectAsClassSafe(O object, Class<P> classRef) {
        validateObject(object);

        return getTranslationSpecForClass(classRef).translate(object);
    }

    /**
     * Given an object, uses the passed in class to obtain the translationSpec and
     * then calls {@link TranslationSpec#translate(Object)}
     * <p>
     * This method call is unsafe in the sense that the type parameters do not
     * ensure any relationship between the passed in object and the passed in
     * classRef.
     * </p>
     * <p>
     * A common use case for using this conversion method would be to call a
     * translationSpec that will wrap the given object in another object.
     * </p>
     * <p>
     * this conversion method will be used approx ~3% of the time
     * </p>
     * 
     * @param <T> the return type after translating
     * @param <O> the type of the object
     * @param <P> the type of the class for which translationSpec you want to use
     * @throws ContractException
     *                           <ul>
     *                           <li>{@linkplain TaskitCoreError#NULL_OBJECT_FOR_TRANSLATION}
     *                           if the passed in object is null</li>
     *                           <li>{@linkplain TaskitCoreError#NULL_CLASS_REF}
     *                           if the passed in classRef is null</li>
     *                           <li>{@linkplain TaskitCoreError#UNINITIALIZED_TASKIT_ENGINE}
     *                           if this engine was not initialized</li>
     *                           <li>
     *                           <li>{@linkplain TaskitCoreError#UNKNOWN_TRANSLATION_SPEC}
     *                           if no translationSpec was provided for the given
     *                           objects class</li>
     *                           </ul>
     */
    public <T, O, P> T translateObjectAsClassUnsafe(O object, Class<P> classRef) {
        validateObject(object);

        return getTranslationSpecForClass(classRef).translate(object);
    }

    /**
     * Given a classRef, returns the translationSpec associated with that class, if
     * it is known
     * 
     * @throws ContractException
     *                           <ul>
     *                           <li>{@linkplain TaskitCoreError#UNINITIALIZED_TASKIT_ENGINE}
     *                           if this engine was not initialized</li>
     *                           <li>
     *                           <li>{@linkplain TaskitCoreError#NULL_CLASS_REF}
     *                           if the passed in classRef is null</li>
     *                           {@linkplain TaskitCoreError#UNKNOWN_TRANSLATION_SPEC}
     *                           if no translationSpec for the given class was
     *                           found</li>
     *                           </ul>
     */
    public <T> ITranslationSpec getTranslationSpecForClass(Class<T> classRef) {
        if (classRef == null) {
            throw new ContractException(TaskitCoreError.NULL_CLASS_REF);
        }

        if (this.data.classToTranslationSpecMap.containsKey(classRef)) {
            return this.data.classToTranslationSpecMap.get(classRef);
        }

        throw new ContractException(TaskitCoreError.UNKNOWN_TRANSLATION_SPEC, classRef.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(data, isInitialized);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TaskitEngine other = (TaskitEngine) obj;

        if (isInitialized != other.isInitialized) {
            return false;
        }
        return Objects.equals(data, other.data);
    }

}
