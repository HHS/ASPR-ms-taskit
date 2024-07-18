package gov.hhs.aspr.ms.taskit.core.engine;

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
import gov.hhs.aspr.ms.taskit.core.translation.TranslatorId;
import gov.hhs.aspr.ms.util.errors.ContractException;
import gov.hhs.aspr.ms.util.graph.Graph;
import gov.hhs.aspr.ms.util.graph.GraphDepthEvaluator;
import gov.hhs.aspr.ms.util.graph.Graphs;
import gov.hhs.aspr.ms.util.graph.MutableGraph;

public final class TaskitEngineData {
    // package access for use in TaskitEngine
    final Map<Class<?>, ITranslationSpec> classToTranslationSpecMap = new LinkedHashMap<>();
    final Set<ITranslationSpec> translationSpecs = new LinkedHashSet<>();

    private TaskitEngineData(Map<Class<?>, ITranslationSpec> classToTranslationSpecMap,
            Set<ITranslationSpec> translationSpecs) {
        this.classToTranslationSpecMap.putAll(classToTranslationSpecMap);
        this.translationSpecs.addAll(translationSpecs);
    }

    public static class Builder {
        private Map<Class<?>, ITranslationSpec> classToTranslationSpecMap = new LinkedHashMap<>();
        private Set<ITranslationSpec> translationSpecs = new LinkedHashSet<>();

        private List<Translator> translators = new ArrayList<>();

        private void validateTranslationSpec(ITranslationSpec translationSpec) {
            if (translationSpec == null) {
                throw new ContractException(TaskitError.NULL_TRANSLATION_SPEC);
            }

            if (translationSpec.getTranslationSpecClassMapping() == null) {
                // TODO: throw exception
            }

            if (translationSpec.getTranslationSpecClassMapping().isEmpty()) {
                // TODO: throw exception
            }

            if (this.translationSpecs.contains(translationSpec)) {
                throw new ContractException(TaskitError.DUPLICATE_TRANSLATION_SPEC);
            }
        }

        private void validateTranslatorNotNull(Translator translator) {
            if (translator == null) {
                throw new ContractException(TaskitError.NULL_TRANSLATOR);
            }
        }

        private void validateTranslationSpecsNotEmpty() {
            if (this.translationSpecs.isEmpty()) {
                throw new ContractException(TaskitError.NO_TRANSLATION_SPECS);
            }
        }

        /**
         * Builder for the TaskitEngineData
         * 
         * @throws ContractException
         *                           <ul>
         *                           <li>{@link TaskitError#UNINITIALIZED_TRANSLATORS}
         *                           if translators were added to the engine but their
         *                           initialized flag was still set to false</li>
         *                           <li>{@link TaskitError#DUPLICATE_TRANSLATOR}
         *                           if a duplicate translator is found</li>
         *                           <li>{@link TaskitError#MISSING_TRANSLATOR}
         *                           if an added translator has a unmet dependency</li>
         *                           <li>{@link TaskitError#CIRCULAR_TRANSLATOR_DEPENDENCIES}
         *                           if the added translators have a circular dependency
         *                           graph</li>
         *                           <li>{@link TaskitError#NO_TRANSLATION_SPECS} if
         *                           no translation specs were added to the engine</li>
         *                           </ul>
         */
        public TaskitEngineData build() {
            // validate the translators that were added
            // they should have an acyclic dependency tree and also all be initialized
            if (!this.translators.isEmpty()) {
                checkTranslatorGraph(true);
                this.translators.clear();
            }

            // There should be at least 1 translation spec added
            validateTranslationSpecsNotEmpty();

            return new TaskitEngineData(classToTranslationSpecMap, translationSpecs);
        }

        // package access for testing
        TaskitEngineData buildWithoutInit() {
            // validate the translators that were added
            // they should have an acyclic dependency tree
            if (!this.translators.isEmpty()) {
                checkTranslatorGraph(false);
                this.translators.clear();
            }

            // There should be at least 1 translation spec added
            validateTranslationSpecsNotEmpty();

            return new TaskitEngineData(classToTranslationSpecMap, translationSpecs);
        }

        /**
         * Adds the given {@link TranslationSpec} to the TaskitEngine
         * <p>
         * there is a bidirectional mapping that arises from this. The Engine needs to
         * know that class A translates to class B and vice versa.
         * 
         * @throws ContractException
         *                           <ul>
         *                           <li>{@linkplain TaskitError#NULL_TRANSLATION_SPEC}
         *                           if the given translationSpec is null</li>
         *                           <li>{@linkplain TaskitError#DUPLICATE_TRANSLATION_SPEC}
         *                           if the given translationSpec is already known</li>
         *                           </ul>
         */
        public Builder addTranslationSpec(ITranslationSpec translationSpec) {
            validateTranslationSpec(translationSpec);

            this.classToTranslationSpecMap.putAll(translationSpec.getTranslationSpecClassMapping());

            this.translationSpecs.add(translationSpec);

            return this;
        }

        /**
         * @throws ContractException
         *                           <ul>
         *                           <li>{@linkplain TaskitError#NULL_TRANSLATOR}
         *                           if translator is null</li>
         *                           </ul>
         */
        public Builder addTranslator(Translator translator) {
            validateTranslatorNotNull(translator);

            this.translators.add(translator);

            return this;
        }

        /*
         * Goes through the list of translators and orders them based on their
         * dependencies
         */
        private void checkTranslatorGraph(boolean checkInit) {
            MutableGraph<TranslatorId, Object> mutableGraph = new MutableGraph<>();
            Map<TranslatorId, Translator> translatorMap = new LinkedHashMap<>();
            /*
             * Add the nodes to the graph, check for duplicate ids, build the mapping from
             * plugin id back to plugin
             */
            this.addNodes(mutableGraph, translatorMap, checkInit);

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

        }

        private void addNodes(MutableGraph<TranslatorId, Object> mutableGraph,
                Map<TranslatorId, Translator> translatorMap, boolean checkInit) {
            TranslatorId focalTranslatorId = null;
            for (Translator translator : this.translators) {
                if (checkInit && !translator.isInitialized()) {
                    throw new ContractException(TaskitError.UNINITIALIZED_TRANSLATORS);
                }
                focalTranslatorId = translator.getTranslatorId();
                translatorMap.put(focalTranslatorId, translator);
                // ensure that there are no duplicate plugins
                if (mutableGraph.containsNode(focalTranslatorId)) {
                    throw new ContractException(TaskitError.DUPLICATE_TRANSLATOR);
                }
                mutableGraph.addNode(focalTranslatorId);
                focalTranslatorId = null;
            }
        }

        private void addEdges(MutableGraph<TranslatorId, Object> mutableGraph) {
            TranslatorId focalTranslatorId = null;
            for (Translator translator : this.translators) {
                focalTranslatorId = translator.getTranslatorId();
                for (TranslatorId translatorId : translator.getTranslatorDependencies()) {
                    mutableGraph.addEdge(new Object(), focalTranslatorId, translatorId);
                }
                focalTranslatorId = null;
            }
        }

        private void checkForMissingTranslators(MutableGraph<TranslatorId, Object> mutableGraph,
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
                    throw new ContractException(TaskitError.MISSING_TRANSLATOR, sb.toString());
                }
            }
        }

        private void checkForCyclicGraph(MutableGraph<TranslatorId, Object> mutableGraph) {
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
                throw new ContractException(TaskitError.CIRCULAR_TRANSLATOR_DEPENDENCIES, sb.toString());
            }
        }
    }

    /**
     * @return a new builder for a TaskitEngine
     */
    public static Builder builder() {
        return new Builder();
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

        if (!(obj instanceof TaskitEngineData)) {
            return false;
        }

        TaskitEngineData other = (TaskitEngineData) obj;
        return Objects.equals(classToTranslationSpecMap, other.classToTranslationSpecMap)
                && Objects.equals(translationSpecs, other.translationSpecs);
    }

}
