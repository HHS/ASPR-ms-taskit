package gov.hhs.aspr.ms.taskit.core.engine;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import gov.hhs.aspr.ms.taskit.core.translation.Translator;
import gov.hhs.aspr.ms.taskit.core.translation.TranslatorId;
import gov.hhs.aspr.ms.util.errors.ContractException;
import gov.hhs.aspr.ms.util.graph.Graph;
import gov.hhs.aspr.ms.util.graph.GraphDepthEvaluator;
import gov.hhs.aspr.ms.util.graph.Graphs;
import gov.hhs.aspr.ms.util.graph.MutableGraph;

/**
 * package access because it should only ever be used by TaskitEngine
 * internally.
 * methods are package access for testing
 */
final class TaskitEngineHelper {
    private List<Translator> translators;

    TaskitEngineHelper(List<Translator> translators) {
        this.translators = translators;
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
