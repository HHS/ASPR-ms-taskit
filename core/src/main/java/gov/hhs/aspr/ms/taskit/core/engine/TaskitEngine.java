package gov.hhs.aspr.ms.taskit.core.engine;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import gov.hhs.aspr.ms.taskit.core.translation.ITranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.TranslationSpec;
import gov.hhs.aspr.ms.taskit.core.translation.Translator;
import gov.hhs.aspr.ms.taskit.core.translation.TranslatorContext;
import gov.hhs.aspr.ms.taskit.core.translation.TranslatorId;
import gov.hhs.aspr.ms.util.errors.ContractException;
import gov.hhs.aspr.ms.util.graph.Graph;
import gov.hhs.aspr.ms.util.graph.GraphDepthEvaluator;
import gov.hhs.aspr.ms.util.graph.Graphs;
import gov.hhs.aspr.ms.util.graph.MutableGraph;

/**
 * Main Translator Class Initializes all {@link TranslationSpec}s and maintains
 * a mapping between the translationSpec and it's respective classes This is an
 * Abstract class, meaning that for a given translation library (Fasterxml,
 * Protobuf, etc) must have a custom implemented TaskitEngine
 */
public final class TaskitEngine implements ITaskitEngine {

    private final Data data;
    protected boolean debug = false;
    protected boolean isInitialized = false;

    protected TaskitEngine(Data data) {
        this.data = data;
    }

    protected static final class Data {
        protected final Map<Class<?>, ITranslationSpec> classToTranslationSpecMap = new LinkedHashMap<>();
        protected final Set<ITranslationSpec> translationSpecs = new LinkedHashSet<>();
        protected Map<Class<?>, Class<?>> childToParentClassMap = new LinkedHashMap<>();
        protected TaskitEngineType taskitEngineType = TaskitEngineType.UNKNOWN;
        protected boolean translatorsInitialized = false;

        protected Data() {
        }

        @Override
        public int hashCode() {
            return Objects.hash(classToTranslationSpecMap, translationSpecs);
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

    // TODO update
    /**
     * This class contains protected final methods for all of its abstract methods.
     * All descendant classes of this class MUST call these if you want it to
     * function properly.
     */
    public static final class Builder implements ITaskitEngineBuilder {
        protected Data data;
        protected final List<Translator> translators = new ArrayList<>();

        protected Builder(Data data) {
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

        private void validateClassRefNotNull(Class<?> classRef) {
            if (classRef == null) {
                throw new ContractException(TaskitCoreError.NULL_CLASS_REF);
            }
        }

        void clearBuilder() {
            this.data = new Data();
        }

        /**
         * Builder for the TaskitEngine
         */
        public TaskitEngine build() {
            initTranslators();

            return new TaskitEngine(data);
        }

        protected final void initTranslators() {
            TranslatorContext translatorContext = new TranslatorContext(this);

            List<Translator> orderedTranslators = this.getOrderedTranslators();

            for (Translator translator : orderedTranslators) {
                translator.getInitializer().accept(translatorContext);
            }

            this.data.translatorsInitialized = true;
            this.translators.clear();
        }

        /**
         * Sets the type for this Taskit Engine
         */
        public final Builder setTaskitEngineType(TaskitEngineType taskitEngineType) {
            this.data.taskitEngineType = taskitEngineType;

            return this;
        }

        /**
         * Adds the given {@link TranslationSpec} to the internal
         * classToTranslationSpecMap
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
         * @throws ContractException
         *                           <ul>
         *                           <li>{@linkplain TaskitCoreError#NULL_TRANSLATOR}
         *                           if translator is null</li>
         *                           <li>{@linkplain TaskitCoreError#DUPLICATE_TRANSLATOR}
         *                           if translator has already been added</li>
         *                           </ul>
         */
        public final Builder addTranslator(Translator translator) {
            validateTranslatorNotNull(translator);

            if (this.translators.contains(translator)) {
                throw new ContractException(TaskitCoreError.DUPLICATE_TRANSLATOR);
            }

            this.translators.add(translator);

            return this;
        }

        /**
         * Adds the given classRef markerInterface mapping.
         * <p>
         * explicitly used when calling {@link TaskitController#writeOutput} with a
         * class for which a classRef ScenarioId pair does not exist and/or the need to
         * output the given class as the markerInterface instead of the concrete class
         * 
         * @param <M> the childClass
         * @param <U> the parentClass/MarkerInterfaceClass
         * @throws ContractException
         *                           <ul>
         *                           <li>{@linkplain TaskitCoreError#NULL_CLASS_REF}
         *                           if classRef is null or if markerInterface is
         *                           null</li>
         *                           <li>{@linkplain TaskitCoreError#DUPLICATE_CLASSREF}
         *                           if child parent relationship has already been
         *                           added</li>
         *                           </ul>
         */
        public final <M extends U, U> Builder addParentChildClassRelationship(Class<M> classRef,
                Class<U> parentClassRef) {
            validateClassRefNotNull(classRef);
            validateClassRefNotNull(parentClassRef);

            if (this.data.childToParentClassMap.containsKey(classRef)) {
                throw new ContractException(TaskitCoreError.DUPLICATE_CLASSREF);
            }

            this.data.childToParentClassMap.put(classRef, parentClassRef);

            return this;
        }

        /*
         * Goes through the list of translators and orders them based on their
         * dependencies
         */
        List<Translator> getOrderedTranslators() {
            return this.getOrderedTranslators(new MutableGraph<>(), new LinkedHashMap<>());
        }

        /*
         * Goes through the list of translators and orders them based on their
         * dependencies
         */
        List<Translator> getOrderedTranslators(MutableGraph<TranslatorId, Object> mutableGraph,
                Map<TranslatorId, Translator> translatorMap) {

            /*
             * Add the nodes to the graph, check for duplicate ids, build the mapping from
             * plugin id back to plugin
             */
            this.addNodes(mutableGraph, translatorMap);

            // Add the edges to the graph
            this.addEdges(mutableGraph);

            /*
             * Check for missing plugins from the plugin dependencies that were collected
             * from the known plugins.
             */
            checkForMissingTranslators(mutableGraph, translatorMap);

            /*
             * Determine whether the graph is acyclic and generate a graph depth evaluator
             * for the graph so that we can determine the order of initialization.
             */
            checkForCyclicGraph(mutableGraph);

            // the graph is acyclic, so the depth evaluator is present
            GraphDepthEvaluator<TranslatorId> graphDepthEvaluator = GraphDepthEvaluator
                    .getGraphDepthEvaluator(mutableGraph.toGraph()).get();

            List<TranslatorId> orderedTranslatorIds = graphDepthEvaluator.getNodesInRankOrder();

            List<Translator> orderedTranslators = new ArrayList<>();
            for (TranslatorId translatorId : orderedTranslatorIds) {
                orderedTranslators.add(translatorMap.get(translatorId));
            }

            return orderedTranslators;
        }

        void addNodes(MutableGraph<TranslatorId, Object> mutableGraph, Map<TranslatorId, Translator> translatorMap) {
            TranslatorId focalTranslatorId = null;
            for (Translator translator : this.translators) {
                focalTranslatorId = translator.getTranslatorId();
                translatorMap.put(focalTranslatorId, translator);
                // ensure that there are no duplicate plugins
                if (mutableGraph.containsNode(focalTranslatorId)) {
                    throw new ContractException(TaskitCoreError.DUPLICATE_TRANSLATOR);
                }
                mutableGraph.addNode(focalTranslatorId);
                focalTranslatorId = null;
            }
        }

        void addEdges(MutableGraph<TranslatorId, Object> mutableGraph) {
            TranslatorId focalTranslatorId = null;
            for (Translator translator : this.translators) {
                focalTranslatorId = translator.getTranslatorId();
                for (TranslatorId translatorId : translator.getTranslatorDependencies()) {
                    mutableGraph.addEdge(new Object(), focalTranslatorId, translatorId);
                }
                focalTranslatorId = null;
            }
        }

        void checkForMissingTranslators(MutableGraph<TranslatorId, Object> mutableGraph,
                Map<TranslatorId, Translator> translatorMap) {
            for (TranslatorId translatorId : mutableGraph.getNodes()) {
                if (!translatorMap.containsKey(translatorId)) {
                    List<Object> inboundEdges = mutableGraph.getInboundEdges(translatorId);
                    StringBuilder sb = new StringBuilder();
                    sb.append("cannot locate instance of ");
                    sb.append(translatorId);
                    sb.append(" needed for ");
                    boolean first = true;
                    for (Object edge : inboundEdges) {
                        if (first) {
                            first = false;
                        } else {
                            sb.append(", ");
                        }
                        TranslatorId dependentTranslatorId = mutableGraph.getOriginNode(edge);
                        sb.append(dependentTranslatorId);
                    }
                    throw new ContractException(TaskitCoreError.MISSING_TRANSLATOR, sb.toString());
                }
            }
        }

        void checkForCyclicGraph(MutableGraph<TranslatorId, Object> mutableGraph) {
            Optional<GraphDepthEvaluator<TranslatorId>> optional = GraphDepthEvaluator
                    .getGraphDepthEvaluator(mutableGraph.toGraph());

            if (!optional.isPresent()) {
                /*
                 * Explain in detail why there is a circular dependency
                 */

                Graph<TranslatorId, Object> g = mutableGraph.toGraph();
                g = Graphs.getSourceSinkReducedGraph(g);
                g = Graphs.getEdgeReducedGraph(g);
                g = Graphs.getSourceSinkReducedGraph(g);

                List<Graph<TranslatorId, Object>> cutGraphs = Graphs.cutGraph(g);
                StringBuilder sb = new StringBuilder();
                String lineSeparator = System.getProperty("line.separator");
                sb.append(lineSeparator);
                boolean firstCutGraph = true;

                for (Graph<TranslatorId, Object> cutGraph : cutGraphs) {
                    if (firstCutGraph) {
                        firstCutGraph = false;
                    } else {
                        sb.append(lineSeparator);
                    }
                    sb.append("Dependency group: ");
                    sb.append(lineSeparator);
                    Set<TranslatorId> nodes = cutGraph.getNodes().stream()
                            .collect(Collectors.toCollection(LinkedHashSet::new));

                    for (TranslatorId node : nodes) {
                        sb.append("\t");
                        sb.append(node);
                        sb.append(" requires:");
                        sb.append(lineSeparator);
                        for (Object edge : cutGraph.getInboundEdges(node)) {
                            TranslatorId dependencyNode = cutGraph.getOriginNode(edge);
                            sb.append("\t");
                            sb.append("\t");
                            sb.append(dependencyNode);
                            sb.append(lineSeparator);
                        }
                    }
                }
                throw new ContractException(TaskitCoreError.CIRCULAR_TRANSLATOR_DEPENDENCIES, sb.toString());
            }
        }
    }

    /**
     * Returns a new builder for a Base TaskitEngine
     */
    public static TaskitEngine.Builder builder() {
        return new Builder(new Data());
    }

    private void validateTaskitEngineType() {
        if (this.data.taskitEngineType == TaskitEngineType.UNKNOWN) {
            throw new ContractException(TaskitCoreError.UNKNOWN_TASKIT_ENGINE_TYPE);
        }
    }

    private void validateTranslatorsInitialized() {
        if (!this.data.translatorsInitialized) {
            throw new ContractException(TaskitCoreError.UNINITIALIZED_TRANSLATORS);
        }
    }

    // This is package access so the TranslationController can access it but nothing
    // else.
    Map<Class<?>, Class<?>> getChildParentClassMap() {
        Map<Class<?>, Class<?>> copyMap = new LinkedHashMap<>(this.data.childToParentClassMap);

        return copyMap;
    }

    /**
     * returns the {@link TaskitEngineType} of this TaskitEngine
     * 
     * guaranteed to NOT be {@link TaskitEngineType#UNKNOWN}
     */
    public TaskitEngineType getTaskitEngineType() {
        return this.data.taskitEngineType;
    }

    /**
     * Initializes the taskitEngine by calling init on each translationSpec
     * added in the builder
     */
    public void initTranslationSpecs(ITaskitEngine taskitEngine) {
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
        }

        this.isInitialized = true;
    }

    public void validateInit() {
        validateTaskitEngineType();
        validateTranslatorsInitialized();
    }

    /**
     * returns whether this taskitEngine is initialized or not
     */
    public boolean isInitialized() {
        return this.isInitialized;
    }

    /**
     * checks to verify all the translationSpecs have been initialized.
     * 
     * @throws RuntimeException There should not be a case where all
     *                          translationSpecs are initialized, so if one of them
     *                          isn't, something went very wrong.
     */
    public void translationSpecsAreInitialized() {

        for (ITranslationSpec translationSpec : this.data.translationSpecs) {
            if (!translationSpec.isInitialized()) {
                throw new RuntimeException(translationSpec.getClass().getName()
                        + " was not properly initialized, be sure to call super()");
            }
        }

    }

    /**
     * Returns an instance of the Base Taskit Engine
     * 
     * NOTE: for {@link TaskitEngine} it returns itself
     */
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
    public <M> void write(Path path, M object)
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
    public <U, M extends U> void translateAndWrite(Path path, M appObject, Class<U> outputClass)
            throws IOException {
        throw new UnsupportedOperationException("Called 'translateAndWriteAs' on TaskitEngine");
    }

    /**
     * writing to files must be defined in explicit TaskitEngines, the base taskit
     * engine knows nothing about writing to files
     * 
     * THIS METHOD SHOULD NEVER BE CALLED DIRECTLY
     */
    @Override
    public <M> void translateAndWrite(Path path, M appObject)
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
    public <U> U read(Path path, Class<U> inputClassRef) throws IOException {
        throw new UnsupportedOperationException("Called 'read' on TaskitEngine");
    }

    /**
     * reading files must be defined in explicit TaskitEngines, the base taskit
     * engine knows nothing about reading files
     * 
     * THIS METHOD SHOULD NEVER BE CALLED DIRECTLY
     */
    @Override
    public <T, U> T readAndTranslate(Path path, Class<U> inputClassRef) throws IOException {
        throw new UnsupportedOperationException("Called 'readAndConvert' on TaskitEngine");
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
        if (object == null) {
            throw new ContractException(TaskitCoreError.NULL_OBJECT_FOR_TRANSLATION);
        }
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
     * @param <M> the type of the object; extends U
     * @param <U> the parent type of the object and the class for which
     *            translationSpec you want to use
     * @throws ContractException
     *                           <ul>
     *                           <li>{@linkplain TaskitCoreError#NULL_OBJECT_FOR_TRANSLATION}
     *                           if the passed in object is null</li>
     *                           <li>{@linkplain TaskitCoreError#NULL_CLASS_REF}
     *                           if the passed in parentClassRef is null</li>
     *                           <li>{@linkplain TaskitCoreError#UNKNOWN_TRANSLATION_SPEC}
     *                           if no translationSpec was provided for the given
     *                           objects class</li>
     *                           </ul>
     */
    public <T, M extends U, U> T translateObjectAsClassSafe(M object, Class<U> parentClassRef) {
        if (object == null) {
            throw new ContractException(TaskitCoreError.NULL_OBJECT_FOR_TRANSLATION);
        }

        if (parentClassRef == null) {
            throw new ContractException(TaskitCoreError.NULL_CLASS_REF);
        }

        return getTranslationSpecForClass(parentClassRef).translate(object);
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
     * @param <M> the type of the object
     * @param <U> the type of the class for which translationSpec you want to use
     * @throws ContractException
     *                           <ul>
     *                           <li>{@linkplain TaskitCoreError#NULL_OBJECT_FOR_TRANSLATION}
     *                           if the passed in object is null</li>
     *                           <li>{@linkplain TaskitCoreError#NULL_CLASS_REF}
     *                           if the passed in objectClassRef is null</li>
     *                           <li>{@linkplain TaskitCoreError#UNKNOWN_TRANSLATION_SPEC}
     *                           if no translationSpec was provided for the given
     *                           objects class</li>
     *                           </ul>
     */
    public <T, M, U> T translateObjectAsClassUnsafe(M object, Class<U> objectClassRef) {
        if (object == null) {
            throw new ContractException(TaskitCoreError.NULL_OBJECT_FOR_TRANSLATION);
        }

        if (objectClassRef == null) {
            throw new ContractException(TaskitCoreError.NULL_CLASS_REF);
        }

        return getTranslationSpecForClass(objectClassRef).translate(object);
    }

    /**
     * Given a classRef, returns the translationSpec associated with that class, if
     * it is known
     * 
     * @throws ContractException {@linkplain TaskitCoreError#UNKNOWN_TRANSLATION_SPEC}
     *                           if no translationSpec for the given class was found
     */
    public ITranslationSpec getTranslationSpecForClass(Class<?> classRef) {
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
