package gov.hhs.aspr.ms.taskit.core;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.math3.util.Pair;

import gov.hhs.aspr.ms.util.errors.ContractException;

/**
 * The TranslatorController serves as the master of cerimonies for translating
 * between two types of objects. Additionally, it has the ability to distribute
 * Input/Output files for reading and writing.
 */
public final class TranslationController {
    protected final Data data;
    protected final Map<TranslationEngineType, TranslationEngine> translationEngines = new LinkedHashMap<>();
    protected final Map<Class<? extends TranslationEngine>, TranslationEngineType> translationEngineClassToTypeMap = new LinkedHashMap<>();
    protected final List<Object> objects = Collections.synchronizedList(new ArrayList<>());

    TranslationController(Data data) {
        this.data = data;
    }

    final static class Data {
        protected Map<Class<? extends TranslationEngine>, TranslationEngine> translationEngines = new LinkedHashMap<>();
        protected final List<Translator> translators = new ArrayList<>();
        protected final Map<Path, Class<?>> inputFilePathMap = new LinkedHashMap<>();
        protected final Map<Path, TranslationEngineType> inputFilePathEngine = new LinkedHashMap<>();
        protected final Map<Pair<Class<?>, Integer>, Path> outputFilePathMap = new LinkedHashMap<>();
        protected final Map<Path, TranslationEngineType> outputFilePathEngine = new LinkedHashMap<>();
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
                throw new ContractException(CoreTranslationError.NULL_CLASS_REF);
            }
        }

        private void validateFilePathNotNull(Path filePath) {
            if (filePath == null) {
                throw new ContractException(CoreTranslationError.NULL_PATH);
            }
        }

        private void validatePathNotDuplicate(Path filePath, boolean in, boolean out) {
            if (in && this.data.inputFilePathMap.containsKey(filePath)) {
                throw new ContractException(CoreTranslationError.DUPLICATE_INPUT_PATH);
            }

            if (out && this.data.outputFilePathMap.values().contains(filePath)) {
                throw new ContractException(CoreTranslationError.DUPLICATE_OUTPUT_PATH);
            }
        }

        private void validateTranslationEngineNotNull(TranslationEngine translationEngine) {
            if (translationEngine == null) {
                throw new ContractException(CoreTranslationError.NULL_TRANSLATION_ENGINE);
            }
        }

        private void validateTranslationEnginesNotNull() {
            if (this.data.translationEngines.isEmpty()) {
                throw new ContractException(CoreTranslationError.NULL_TRANSLATION_ENGINE,
                        "No TranslationEngine Builders were added");
            }
            for (TranslationEngine engine : this.data.translationEngines.values()) {
                validateTranslationEngineNotNull(engine);
            }
        }

        /**
         * Builds the TranslatorController. Calls the initializer on each added
         * {@link Translator}
         * 
         * @throws ContractException
         *                           <ul>
         *                           <li>{@linkplain CoreTranslationError#NULL_TRANSLATION_ENGINE}
         *                           if translationEngineBuilder has not been set</li>
         *                           </ul>
         */
        public TranslationController build() {
            validateTranslationEnginesNotNull();

            TranslationController translatorController = new TranslationController(this.data);

            translatorController.initTranslationEngines();
            translatorController.validateTranslationEngines();

            return translatorController;
        }

        TranslationController buildWithoutInitAndChecks() {
            return new TranslationController(this.data);
        }

        /**
         * Adds the path and class ref to be read from after building via
         * {@link TranslationController#readInput()}
         * 
         * @throws ContractException
         *                           <ul>
         *                           <li>{@linkplain CoreTranslationError#NULL_PATH} if
         *                           filePath is null</li>
         *                           <li>{@linkplain CoreTranslationError#NULL_CLASS_REF}
         *                           if classRef is null</li>
         *                           <li>{@linkplain CoreTranslationError#DUPLICATE_INPUT_PATH}
         *                           if filePath has already been added</li>
         *                           <li>{@linkplain CoreTranslationError#INVALID_INPUT_PATH}
         *                           if filePath does not exist on the system</li>
         *                           </ul>
         */
        public Builder addInputFilePath(Path filePath, Class<?> classRef, TranslationEngineType translationEngineType) {
            validateFilePathNotNull(filePath);
            validateClassRefNotNull(classRef);
            validatePathNotDuplicate(filePath, true, false);

            if (!filePath.toFile().exists()) {
                throw new ContractException(CoreTranslationError.INVALID_INPUT_PATH);
            }

            this.data.inputFilePathMap.put(filePath, classRef);
            this.data.inputFilePathEngine.put(filePath, translationEngineType);
            return this;
        }

        /**
         * Adds the path and class ref to be written to after building via
         * {@link TranslationController#writeOutput} with a scenario id of 0
         * 
         * @throws ContractException
         *                           <ul>
         *                           <li>{@linkplain CoreTranslationError#NULL_PATH} if
         *                           filePath is null</li>
         *                           <li>{@linkplain CoreTranslationError#NULL_CLASS_REF}
         *                           if classRef is null</li>
         *                           <li>{@linkplain CoreTranslationError#DUPLICATE_OUTPUT_PATH}
         *                           if filePath has already been added</li>
         *                           <li>{@linkplain CoreTranslationError#DUPLICATE_CLASSREF_SCENARIO_PAIR}
         *                           if the classRef and scenarioId pair has already
         *                           been added</li>
         *                           <li>{@linkplain CoreTranslationError#INVALID_OUTPUT_PATH}
         *                           if filePath does not exist on the system</li>
         *                           </ul>
         */
        public Builder addOutputFilePath(Path filePath, Class<?> classRef,
                TranslationEngineType translationEngineType) {
            return this.addOutputFilePath(filePath, classRef, 0, translationEngineType);
        }

        /**
         * Adds the path and class ref to be written to after building via
         * {@link TranslationController#writeOutput} with the given scenarioId
         * 
         * @throws ContractException
         *                           <ul>
         *                           <li>{@linkplain CoreTranslationError#NULL_PATH} if
         *                           filePath is null</li>
         *                           <li>{@linkplain CoreTranslationError#NULL_CLASS_REF}
         *                           if classRef is null</li>
         *                           <li>{@linkplain CoreTranslationError#DUPLICATE_OUTPUT_PATH}
         *                           if filePath has already been added</li>
         *                           <li>{@linkplain CoreTranslationError#DUPLICATE_CLASSREF_SCENARIO_PAIR}
         *                           if the classRef and scenarioId pair has already
         *                           been added</li>
         *                           <li>{@linkplain CoreTranslationError#INVALID_OUTPUT_PATH}
         *                           if filePath does not exist on the system</li>
         *                           </ul>
         */
        public Builder addOutputFilePath(Path filePath, Class<?> classRef, Integer scenarioId,
                TranslationEngineType translationEngineType) {
            validateFilePathNotNull(filePath);
            validateClassRefNotNull(classRef);
            validatePathNotDuplicate(filePath, false, true);

            Pair<Class<?>, Integer> key = new Pair<>(classRef, scenarioId);

            if (this.data.outputFilePathMap.containsKey(key)) {
                throw new ContractException(CoreTranslationError.DUPLICATE_CLASSREF_SCENARIO_PAIR);
            }

            if (!filePath.getParent().toFile().exists()) {
                throw new ContractException(CoreTranslationError.INVALID_OUTPUT_PATH);
            }

            this.data.outputFilePathMap.put(key, filePath);
            this.data.outputFilePathEngine.put(filePath, translationEngineType);
            return this;
        }

        /**
         * Adds the given classRef markerInterace mapping.
         * <p>
         * explicitly used when calling {@link TranslationController#writeOutput} with a
         * class for which a classRef ScenarioId pair does not exist and/or the need to
         * output the given class as the markerInterface instead of the concrete class
         * 
         * @param <M> the childClass
         * @param <U> the parentClass/MarkerInterfaceClass
         * @throws ContractException
         *                           <ul>
         *                           <li>{@linkplain CoreTranslationError#NULL_CLASS_REF}
         *                           if classRef is null or if markerInterface is
         *                           null</li>
         *                           <li>{@linkplain CoreTranslationError#DUPLICATE_CLASSREF}
         *                           if child parent relationship has already been
         *                           added</li>
         *                           </ul>
         */
        public <M extends U, U> Builder addParentChildClassRelationship(Class<M> classRef, Class<U> markerInterface) {
            validateClassRefNotNull(classRef);
            validateClassRefNotNull(markerInterface);

            if (this.data.parentChildClassRelationshipMap.containsKey(classRef)) {
                throw new ContractException(CoreTranslationError.DUPLICATE_CLASSREF);
            }

            this.data.parentChildClassRelationshipMap.put(classRef, markerInterface);
            return this;
        }

        /**
         * Adds a {@link TranslationEngine.Builder}
         * 
         * @throws ContractException
         *                           <ul>
         *                           <li>{@linkplain CoreTranslationError#NULL_TRANSLATION_ENGINE}
         *                           if translationEngineBuilder is null</li>
         *                           </ul>
         */
        public Builder addTranslationEngine(TranslationEngine translationEngine) {
            validateTranslationEngineNotNull(translationEngine);

            this.data.translationEngines.put(translationEngine.getClass(), translationEngine);

            Map<Class<?>, Class<?>> childToParentClassMap = translationEngine.getChildParentClassMap();

            for (Class<?> childClassRef : childToParentClassMap.keySet()) {
                // Need to duplicate code here because the map doesn't provide the type safety
                // that is required by the addParentChildClassRelationship method
                Class<?> parentClassRef = childToParentClassMap.get(childClassRef);

                // Note: no 'class is not null' validation here because it was validated prior
                // to being put into the engine
                if (this.data.parentChildClassRelationshipMap.containsKey(childClassRef)) {
                    throw new ContractException(CoreTranslationError.DUPLICATE_CLASSREF);
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

    /**
     * Passes the given reader and inputClassRef to the built
     * {@link TranslationEngine} to read, parse and translate the inputData.
     * 
     * @param <U> the classType associated with the reader
     */
    <U> void readInput(Path path, Class<U> inputClassRef, TranslationEngine translationEngine) {
        Object appObject;
        try {
            appObject = translationEngine.readInput(path, inputClassRef);
            this.objects.add(appObject);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Passes the given writer object and optional superClass to the built
     * {@link TranslationEngine} to translate and write to the outputFile
     * 
     * @param <M> the class of the object to write to the outputFile
     * @param <U> the optional parent class of the object to write to the outputFile
     */
    <M extends U, U> void writeOutput(Path path, M object, Optional<Class<U>> superClass,
            TranslationEngine translationEngine) {
        try {
            translationEngine.writeOutput(path, object, superClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    void initTranslationEngines() {
        for (TranslationEngine translationEngine : this.data.translationEngines.values()) {
            translationEngine.translationSpecsAreInitialized();

            this.translationEngines.put(translationEngine.getTranslationEngineType(), translationEngine);
            this.translationEngineClassToTypeMap.put(translationEngine.getClass(),
                    translationEngine.getTranslationEngineType());
        }
    }

    void validateTranslationEngine(TranslationEngine translationEngine) {
        if (translationEngine == null) {
            throw new ContractException(CoreTranslationError.NULL_TRANSLATION_ENGINE);
        }

        /*
         * Because the translationEngine's init method is called within the
         * initTranslators() method, this should never happen, thus it is a
         * RuntimeException and not a ContractException
         */
        if (!translationEngine.isInitialized()) {
            throw new RuntimeException("TranslationEngine has been built but has not been initialized.");
        }
    }

    void validateTranslationEngines() {
        Set<Class<? extends TranslationEngine>> translationEngineClasses = new HashSet<>();

        if (this.translationEngines.keySet().isEmpty()) {
            throw new ContractException(CoreTranslationError.NO_TRANSLATION_ENGINES);
        }

        // validate each engine that exists irrespective of any mapping
        for (TranslationEngine translationEngine : this.translationEngines.values()) {
            validateTranslationEngine(translationEngine);
            translationEngineClasses.add(translationEngine.getClass());
        }

        // if the class to type map doesn't contain all of the classes of the engines in
        // the engine map
        // and
        // if the engine map doesn't contain all of the types from the class to type map
        // this ensures that every engine has a valid class -> type -> engine mapping
        if (!(this.translationEngineClassToTypeMap.keySet().containsAll(translationEngineClasses)
                && this.translationEngines.keySet().containsAll(this.translationEngineClassToTypeMap.values()))) {
            throw new RuntimeException(
                    "Not all Translation Engines have an associated Class -> Type -> Engine Mapping. Something went very wrong.");
        }
    }


    /**
     * passes every input path and classRef to
     * the TranslationEngine via
     * {@link TranslationController#readInput(Path, Class, TranslationEngine)}
     */
    public TranslationController readInput() {
        for (Path path : this.data.inputFilePathMap.keySet()) {
            Class<?> classRef = this.data.inputFilePathMap.get(path);
            TranslationEngineType type = this.data.inputFilePathEngine.get(path);
            TranslationEngine translationEngine = this.translationEngines.get(type);

            this.readInput(path, classRef, translationEngine);
        }

        return this;
    }

    /**
     * Given the classRef and scenarioId, find the given outputFilePath. If the
     * classRef Scenario pair has been added, that is returned. Otherwise, checks to
     * see if the classRef exists in the markerInterfaceClassMap and if so, returns
     * the resulting classRef scenarioId pair
     * 
     * @param <M> the childClass
     * @param <U> the optional parentClass/MarkerInterfaceClass
     */
    <M extends U, U> Pair<Path, Optional<Class<U>>> getOutputPath(Class<M> classRef, Integer scenarioId) {
        Pair<Class<?>, Integer> key = new Pair<>(classRef, scenarioId);

        if (this.data.outputFilePathMap.containsKey(key)) {
            return new Pair<>(this.data.outputFilePathMap.get(key), Optional.empty());
        }

        if (this.data.parentChildClassRelationshipMap.containsKey(classRef)) {
            // can safely cast because of type checking when adding to the
            // markerInterfaceClassMap
            @SuppressWarnings("unchecked")
            Class<U> parentClass = (Class<U>) this.data.parentChildClassRelationshipMap.get(classRef);

            key = new Pair<>(parentClass, scenarioId);

            if (this.data.outputFilePathMap.containsKey(key)) {
                return new Pair<>(this.data.outputFilePathMap.get(key), Optional.of(parentClass));
            }
        }

        throw new ContractException(CoreTranslationError.INVALID_OUTPUT_CLASSREF,
                "No path was provided for " + classRef.getName());
    }

    /**
     * takes the list of objects and writes each object out to it's corresponding
     * outputFilePath, if it exists
     * <p>
     * internally calls {@link TranslationController#writeOutput(Object)}
     * 
     * @param <T> the type of the list of obects to write to output
     * @throws ContractException
     *                           <ul>
     *                           <li>{@linkplain CoreTranslationError#INVALID_OUTPUT_CLASSREF}
     *                           if the class of the object paired with the
     *                           scenarioId does not have a associated
     *                           outputFilePath</li>
     *                           <li>{@linkplain CoreTranslationError#NULL_TRANSLATION_ENGINE}
     *                           if translationEngine is null</li>
     *                           </ul>
     */
    public <T> void writeOutput(List<T> objects) {
        for (T object : objects) {
            this.writeOutput(object);
        }
    }

    /**
     * takes the list of objects with the specified scenarioId and writes each
     * object out to it's corresponding outputFilePath, if it exists
     * <p>
     * internally calls {@link TranslationController#writeOutput(Object, Integer)}
     * 
     * @param <T> the type of the list of obects to write to output
     * @throws ContractException
     *                           <ul>
     *                           <li>{@linkplain CoreTranslationError#INVALID_OUTPUT_CLASSREF}
     *                           if the class of the object paired with the
     *                           scenarioId does not have a associated
     *                           outputFilePath</li>
     *                           <li>{@linkplain CoreTranslationError#NULL_TRANSLATION_ENGINE}
     *                           if translationEngine is null</li>
     *                           </ul>
     */
    public <T> void writeOutput(List<T> objects, Integer scenarioId) {
        for (T object : objects) {
            this.writeOutput(object, scenarioId);
        }
    }

    /**
     * takes the given object and writes it out to it's corresponding
     * outputFilePath, if it exists
     * <p>
     * internally calls {@link TranslationController#writeOutput(Object, Integer)}
     * with a scenarioId of 0
     * 
     * @param <T> the type of the list of obects to write to output
     * @throws ContractException
     *                           <ul>
     *                           <li>{@linkplain CoreTranslationError#INVALID_OUTPUT_CLASSREF}
     *                           if the class of the object paired with the
     *                           scenarioId does not have a associated
     *                           outputFilePath</li>
     *                           <li>{@linkplain CoreTranslationError#NULL_TRANSLATION_ENGINE}
     *                           if translationEngine is null</li>
     *                           </ul>
     */
    public <T> void writeOutput(T object) {
        this.writeOutput(object, 0);
    }

    /**
     * takes the given object and scenarioId pair and writes it out to it's
     * corresponding outputFilePath, if it exists
     * 
     * @param <M> the classType of the object
     * @param <U> the optional type of the parent class of the object
     * @throws ContractException
     *                           <ul>
     *                           <li>{@linkplain CoreTranslationError#INVALID_OUTPUT_CLASSREF}
     *                           if the class of the object paired with the
     *                           scenarioId does not have a associated
     *                           outputFilePath</li>
     *                           <li>{@linkplain CoreTranslationError#NULL_TRANSLATION_ENGINE}
     *                           if translationEngine is null</li>
     *                           </ul>
     */
    @SuppressWarnings("unchecked")
    public <M extends U, U> void writeOutput(M object, Integer scenarioId) {
        // this gives an unchecked warning, surprisingly
        Class<M> classRef = (Class<M>) object.getClass();

        Pair<Path, Optional<Class<U>>> pathPair = getOutputPath(classRef, scenarioId);
        Path path = pathPair.getFirst();
        TranslationEngineType type = this.data.outputFilePathEngine.get(path);
        TranslationEngine translationEngine = this.translationEngines.get(type);

        this.writeOutput(path, object, pathPair.getSecond(), translationEngine);
    }

    /**
     * Searches the list of read in objects and returns the first Object found of
     * the given classRef
     * 
     * @param <T> the type of the obect to get
     * @throws ContractException
     *                           <ul>
     *                           <li>{@linkplain CoreTranslationError#UNKNOWN_CLASSREF}
     *                           if no object with the specified class is found</li>
     *                           </ul>
     */
    public <T> T getFirstObject(Class<T> classRef) {
        for (Object object : this.objects) {
            if (classRef.isAssignableFrom(object.getClass())) {
                return classRef.cast(object);
            }
        }

        throw new ContractException(CoreTranslationError.UNKNOWN_CLASSREF);
    }

    /**
     * Searches the list of read in objects and returns all Objects found with the
     * given classRef
     * 
     * @param <T> the type of the obect to get
     */
    public <T> List<T> getObjects(Class<T> classRef) {
        List<T> objects = new ArrayList<>();
        for (Object object : this.objects) {
            if (classRef.isAssignableFrom(object.getClass())) {
                objects.add(classRef.cast(object));
            }
        }

        return objects;
    }

    /**
     * Returns the entire list of read in objects
     */
    public List<Object> getObjects() {
        return this.objects;
    }

}
